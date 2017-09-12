/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package dao

import com.typesafe.config.{Config, ConfigFactory}
import dates.Date._
import dates._
import model.{Game, GameKey}
import models.{Competition, Location}
import java.time.{Clock, Instant, ZoneId, ZonedDateTime}
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors

import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach
import org.specs2.specification.core.Fragments
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import cats.instances.future._

import scala.util.Try

/**
 * @author alex
 *
 */
class SlickGameDaoSpec extends Specification with DatabaseContext {

  sequential

  "A minimally stored game" should {
    val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013)
    "have a non-zero id" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013).value
        } yield pg
      persistedGame.map(_.map(_.id)) must beSome[Long](be_!=(0)).await
    }
    "have the correct business key" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013).value
        } yield pg
      persistedGame.map(_.map(_.gameKey)) must beSome(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013)).await
    }
    "have the correct creation date" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013).value
        } yield pg
      persistedGame.map(_.map(_.dateCreated)) must beSome(nowA).await
    }
    "have the correct update date" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013).value
        } yield pg
      persistedGame.map(_.map(_.lastUpdated)) must beSome(nowA).await
    }
  }

  "Game keys" should {
    "be unique" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013)
      val unique = for {
        _ <- db.gameDao.store(game)
        result <- db.gameDao.store(game).map(_ => false).recover { case _ => true }
      } yield result
      unique must beTrue.await
    }
  }

  "Games with dates" should {
      "should be searchable by date" in { db: Db =>
        implicit val ee: ExecutionEnv = db.ee
        val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013).copy(at = Some(September(5, 2013) at (15, 0)))
        val foundGame = for {
          _ <- db.gameDao.store(game)
          persistedGame <- db.gameDao.findByDatePlayed(September(5, 2013) at (15, 0)).value
        } yield persistedGame
        foundGame.map(_.flatMap(_.at)) must beSome(September(5, 2013) at (15, 0)).await
        foundGame.map(_.map(_.competition)) must beSome[Competition](Competition.FACP).await
        foundGame.map(_.map(_.location)) must beSome[Location](Location.HOME).await
        foundGame.map(_.map(_.opponents)) must beSome("Opponents").await
        foundGame.map(_.map(_.season)) must beSome(2013).await
      }
  }

  "Looking for all games in a season" should {
      "return only games for that season in date played order" in { db: Db =>
        case class Expectation(spursArsenalChelsea: List[Game], gamesFor2013: List[Game])
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val expectation = for {
          chelsea <- "Chelsea" home May(5, 2013)
          spurs <- "Spurs" away January(9, 2013)
          arsenal <- "Arsenal" home February(12, 2013)
          fulham <- "Fulham" away April(1, 2012)
          gamesFor2013 <- db.gameDao.getAllForSeason(2013)
        } yield Expectation(List(spurs, arsenal, chelsea), gamesFor2013)
        expectation.map(_.gamesFor2013) must be_===(Await.result(expectation.map(_.spursArsenalChelsea), 1.second)).await
      }
  }

  "Retrieving all known seasons" should {
      "retrieve every season a game has been played, earliest first" in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val seasons = for {
          _ <- "Chelsea" home May(5, 2013)
          _ <- "Reading" away September(7, 2011)
          _ <- "Everton" home March(15, 2011)
          seasons <- db.gameDao.getAllSeasons
        } yield seasons
        seasons must be_===(SortedSet(2011, 2013)).await
      }
  }

  "Getting the latest season" should {
      "be None for when there are no games at all" in { db: Db =>
        implicit val ee: ExecutionEnv = db.ee
        val emptyLastSeason = db.gameDao.getLatestSeason.value
        emptyLastSeason must beNone.await
      }
      "be equal to the last season with a game" in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val lastSeason = for {
          _ <- "Chelsea" home May(5, 2013)
          _ <- "Reading" away September(7, 2011)
          _ <- "Everton" home March(15, 2011)
          lastSeason <- db.gameDao.getLatestSeason.value
        } yield lastSeason
        lastSeason must beSome(2013).await
      }
  }

  "Getting all games for a given season and location" should {
      "return only games for that season in date played order" in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        case class Expectation(arsenalChelsea: List[Game], homeGamesFor2013: List[Game])
        val expectation = for {
          chelsea <- "Chelsea" home May(5, 2013)
          spurs <- "Spurs" away January(9, 2013)
          arsenal <- "Arsenal" home February(12, 2013)
          fulham <- "Fulham" away April(1, 2012)
          homeGamesFor2013 <- db.gameDao.getAllForSeasonAndLocation(2013, Location.HOME)
        } yield Expectation(List(arsenal, chelsea), homeGamesFor2013)
        expectation.map(_.homeGamesFor2013) must be_===(Await.result(expectation.map(_.arsenalChelsea), 1.second)).await
      }
  }

  "Getting all games" should {
      "return all games in chronological order" in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        case class Expectation(fulhamSpursArsenalChelsea: List[Game], allGames: List[Game])
        val expectation = for {
          chelsea <- "Chelsea" home May(5, 2013)
          spurs <- "Spurs" away January(9, 2013)
          arsenal <- "Arsenal" home February(12, 2013)
          fulham <- "Fulham" away April(1, 2012)
          allGames <- db.gameDao.getAll
        } yield Expectation(List(fulham, spurs, arsenal, chelsea), allGames)
        expectation.map(_.allGames) must be_===(Await.result(expectation.map(_.fulhamSpursArsenalChelsea), 1.second)).await
      }
  }

  "Searching for games" should {
    val nonPersistedGames: List[Game] = {
      def locationFactory(lso: LocationSearchOption): Option[Location] = lso match {
        case LocationSearchOption.HOME => Some(Location.HOME)
        case LocationSearchOption.AWAY => Some(Location.AWAY)
        case _ => None
      }
      def gameAttendedFactory(aso: AttendedSearchOption): Option[Boolean] = aso match {
        case AttendedSearchOption.ATTENDED => Some(true)
        case AttendedSearchOption.UNATTENDED => Some(false)
        case _ => None
      }
      val gameGenerators: List[(LocationSearchOption, AttendedSearchOption, Location, Boolean, GameOrTicketSearchOption)] = for {
        lso <- List(LocationSearchOption.HOME, LocationSearchOption.AWAY)
        aso <- List(AttendedSearchOption.ATTENDED, AttendedSearchOption.UNATTENDED)
        location <- locationFactory(lso).toList
        attended <- gameAttendedFactory(aso).toList
        ticket <- GameOrTicketSearchOption.values
      } yield (lso, aso, location, attended, ticket)
      gameGenerators.zipWithIndex.flatMap { case ((lso, aso, location, gameAttended, gtso), idx) =>
        val opponents = f"Opponents $idx%02d"
        val date = September(1, 2013).plusDays(idx)
        val game = Game.gameKey(
          Competition.FACP,
          location,
          opponents,
          date.getYear).copy(
          attended = gameAttended,
          at = Some(date.withHour(15).withMinute(0).truncatedTo(ChronoUnit.MINUTES)))
        val tickets: Option[ZonedDateTime] = Some((August(1, 2013) at(9, 0)).plusDays(idx))
        gtso match {
          case GameOrTicketSearchOption.BONDHOLDERS => Some(game.copy(bondholdersAvailable = tickets))
          case GameOrTicketSearchOption.PRIORITY_POINT => Some(game.copy(priorityPointAvailable = tickets))
          case GameOrTicketSearchOption.SEASON => Some(game.copy(seasonTicketsAvailable = tickets))
          case GameOrTicketSearchOption.ACADEMY => Some(game.copy(academyMembersAvailable = tickets))
          case GameOrTicketSearchOption.GENERAL_SALE => Some(game.copy(generalSaleAvailable = tickets))
          case GameOrTicketSearchOption.GAME => None
        }
      }
    }
    val allSearchOptions = for {
      lso <- LocationSearchOption.values
      aso <- AttendedSearchOption.values
      gtso <- GameOrTicketSearchOption.values
    } yield (lso, aso, gtso)
    Fragments.foreach(allSearchOptions) { searchOptions =>
      val (lso, aso, gtso) = searchOptions
      s"return the correct results for $searchOptions " in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val locationPredicateFactory = (g: Game) => lso match {
          case LocationSearchOption.HOME => g.location == Location.HOME
          case LocationSearchOption.AWAY => g.location == Location.AWAY
          case LocationSearchOption.ANY => true
        }
        val attendedPredicateFactory = (g: Game) => aso match {
          case AttendedSearchOption.ATTENDED => g.attended
          case AttendedSearchOption.UNATTENDED => !g.attended
          case AttendedSearchOption.ANY => true
        }
        val gameOrTicketPredicateFactory = (g: Game) => gtso match {
          case GameOrTicketSearchOption.BONDHOLDERS => g.bondholdersAvailable.isDefined
          case GameOrTicketSearchOption.PRIORITY_POINT => g.priorityPointAvailable.isDefined
          case GameOrTicketSearchOption.SEASON => g.seasonTicketsAvailable.isDefined
          case GameOrTicketSearchOption.ACADEMY => g.academyMembersAvailable.isDefined
          case GameOrTicketSearchOption.GENERAL_SALE => g.generalSaleAvailable.isDefined
          case GameOrTicketSearchOption.GAME => true
        }
        val futureGames = nonPersistedGames.foldRight(Future.successful(List.empty[Game])) { (game, fGames) =>
          for {
            persistedGame <- db.gameDao.store(game)
            games <- fGames
          } yield persistedGame :: games
        }
        val expectedGames = futureGames.map { games =>
          games.filter { game => locationPredicateFactory(game) && attendedPredicateFactory(game) && gameOrTicketPredicateFactory(game)
          }
        }
        val actualGames = futureGames.flatMap { _ => gameDao.search(aso, lso, gtso) }
        actualGames must containTheSameElementsAs(Await.result(expectedGames, 5.seconds)).await
      }
    }
  }

  /**
   * A simple implicit class that allows games to be created as "Opponents home date" or "Opponents away date"
   */
  implicit class StringImplicit(opponents: String) {
    def home(date: Date)(implicit gameDao: GameDao): Future[Game] = on(Location.HOME, date)
    def home(date: ZonedDateTime)(implicit gameDao: GameDao): Future[Game] = on(Location.HOME, date)
    def away(date: Date)(implicit gameDao: GameDao): Future[Game] = on(Location.AWAY, date)
    def away(date: ZonedDateTime)(implicit gameDao: GameDao): Future[Game] = on(Location.AWAY, date)
    def on(location: Location, date: ZonedDateTime)(implicit gameDao: GameDao): Future[Game] = {
      val game = Game.gameKey(Competition.FACP, location, opponents, date.getYear).copy(
        at = Some(date.withHour(15).truncatedTo(ChronoUnit.HOURS)))
      gameDao.store(game)
    }
  }
}

case class Db(gameDao: GameDao, ee: ExecutionEnv)

trait DatabaseContext extends ForEach[Db] {

  val nowA: ZonedDateTime = September(5, 1972).at(9, 0)
  val nowB: ZonedDateTime = September(7, 1972).at(9, 0)
  val nows: Stream[ZonedDateTime] = List(nowA, nowB).toStream #::: nows

  implicit val zonedDateTimeFactory: ZonedDateTimeFactory = new ZonedDateTimeFactoryImpl() {
    override val clock: Clock = new Clock {
      val nowsIterator: Iterator[ZonedDateTime] = nows.iterator
      override def withZone(zone: ZoneId): Clock = throw new UnsupportedOperationException("withZone")

      override def getZone: ZoneId = zoneId

      override def instant(): Instant = nowsIterator.next().toInstant
    }
  }

  // you need to define the "foreach" method
  def foreach[R: AsResult](f: Db => R): Result = {
    val threadPoolExecutor = Executors.newFixedThreadPool(10)
    val ee: ExecutionEnv = ExecutionEnv.fromExecutionContext(ExecutionContext.fromExecutor(threadPoolExecutor))
    implicit val ec = ee.ec
    val result = for {
      gameDao <- openDatabaseTransaction
      result <- Future.successful(Try(AsResult(f(Db(gameDao, ee)))))
      _ <- closeDatabaseTransaction(gameDao)
    } yield result.get
    Await.result(result, 1.minute)
  }

  // create and close a transaction
  def openDatabaseTransaction(implicit ec: ExecutionContext): Future[SlickGameDao] = {
    val dbConfigFactory = new DatabaseConfigFactory {
      val config: Config = ConfigFactory.parseString(
        """
          |slick.dbs.default.driver="slick.driver.HsqldbDriver$"
          |slick.dbs.default.db.driver="org.hsqldb.jdbc.JDBCDriver"
          |slick.dbs.default.db.url="jdbc:hsqldb:mem:hammers"
          |slick.dbs.default.db.user=""
          |slick.dbs.default.db.password=""
        """.stripMargin)
      override def apply: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.default", config)
    }
    implicit val zonedDateTimeFactory = new ZonedDateTimeFactoryImpl {
      val n: Iterator[ZonedDateTime] = nows.iterator
      override def now: ZonedDateTime = n.next()
    }
    val gameDao = new SlickGameDao(dbConfigFactory, zonedDateTimeFactory)
    gameDao.dbConfig.db.run(gameDao.create).map(_ => gameDao)
  }

  def closeDatabaseTransaction(gameDao: SlickGameDao)(implicit ec: ExecutionContext): Future[Unit] = {
    gameDao.dbConfig.db.run(gameDao.drop).flatMap(_ => gameDao.dbConfig.db.shutdown)
  }
}