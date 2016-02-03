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

import com.typesafe.config.ConfigFactory
import dates.Date._
import dates._
import model.{Competition, Game, GameKey, Location}
import org.joda.time.DateTime
import org.specs2.concurrent.ExecutionEnv
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Specification
import org.specs2.specification.ForEach
import org.specs2.specification.core.Fragments
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * @author alex
 *
 */
class SlickGameDaoTest extends Specification with DatabaseContext {

  sequential

  "A minimally stored game" should {
    val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013)
    "have a non-zero id" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
        } yield pg
      persistedGame.map(_.map(_.id)) must beSome[Long](be_!=(0)).await
    }
    "have the correct business key" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
        } yield pg
      persistedGame.map(_.map(_.gameKey)) must beSome(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013)).await
    }
    "have the correct creation date" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
        } yield pg
      persistedGame.map(_.map(_.dateCreated)) must beSome(nowA).await
    }
    "have the correct update date" in { db: Db =>
      implicit val ee: ExecutionEnv = db.ee
      val persistedGame =
        for {
          _ <- db.gameDao.store(game)
          pg <- db.gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
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

  "Updating a game" should {
      "only update the update date and not the creation date" in { db: Db =>
        val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013)(NowService(nowA))
        implicit val ee: ExecutionEnv = db.ee
        case class Dates(originalDateCreated: DateTime, originalDateUpdated: DateTime, newDateCreated: DateTime, newDateUpdated: DateTime)
        val dates = for {
          gameA <- db.gameDao.store(game)(NowService(nowA))
          gameB <- db.gameDao.store(gameA)(NowService(nowB))
        } yield Dates(gameA.dateCreated, gameA.lastUpdated, gameB.dateCreated, gameB.lastUpdated)
        dates.map(_.originalDateCreated) must be_===(nowA).await
        dates.map(_.originalDateUpdated) must be_===(nowA).await
        dates.map(_.newDateCreated) must be_===(nowA).await
        dates.map(_.newDateUpdated) must be_===(nowB).await
      }
  }

  "Games with dates" should {
      "should be searchable by date" in { db: Db =>
        implicit val ee: ExecutionEnv = db.ee
        val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013).copy(at = Some(September(5, 2013) at (15, 0)))
        val foundGame = for {
          _ <- db.gameDao.store(game)
          persistedGame <- db.gameDao.findByDatePlayed(September(5, 2013) at (15, 0))
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
        val emptyLastSeason = db.gameDao.getLatestSeason
        emptyLastSeason must beNone.await
      }
      "be equal to the last season with a game" in { db: Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val lastSeason = for {
          _ <- "Chelsea" home May(5, 2013)
          _ <- "Reading" away September(7, 2011)
          _ <- "Everton" home March(15, 2011)
          lastSeason <- db.gameDao.getLatestSeason
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
      val gameGenerators: List[(LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption)] = for {
        location <- List(LocationSearchOption.HOME, LocationSearchOption.AWAY)
        attended <- List(AttendedSearchOption.ATTENDED, AttendedSearchOption.UNATTENDED)
        ticket <- GameOrTicketSearchOption.values
      } yield (location, attended, ticket)
      gameGenerators.zipWithIndex.flatMap { case ((lso, aso, gtso), idx) =>
        val opponents = f"Opponents $idx%02d"
        val location: Location = lso match {
          case LocationSearchOption.HOME => Location.HOME
          case LocationSearchOption.AWAY => Location.AWAY
        }
        val gameAttended = Some {
          aso match {
            case AttendedSearchOption.ATTENDED => true
            case AttendedSearchOption.UNATTENDED => false
          }
        }
        val date = September(1, 2013).plusDays(idx)
        val game = Game.gameKey(
          Competition.FACP,
          location,
          opponents,
          date.getYear).copy(
            attended = gameAttended,
            at = Some(date.withHourOfDay(15).withMinuteOfHour(0).withMillisOfSecond(0)))
        val tickets: Option[DateTime] = Some((August(1, 2013) at(9, 0)).plusDays(idx))
        gtso match {
          case GameOrTicketSearchOption.BONDHOLDERS => Some(game.copy(bondholdersAvailable = tickets))
          case GameOrTicketSearchOption.PRIORITY_POINT => Some(game.copy(priorityPointAvailable = tickets))
          case GameOrTicketSearchOption.SEASON => Some(game.copy(seasonTicketsAvailable = tickets))
          case GameOrTicketSearchOption.ACADEMY => Some(game.copy(academyMembersAvailable = tickets))
          case GameOrTicketSearchOption.ACADEMY_POSTAL => Some(game.copy(academyMembersPostalAvailable = tickets))
          case GameOrTicketSearchOption.GENERAL_SALE => Some(game.copy(generalSaleAvailable = tickets))
          case GameOrTicketSearchOption.GENERAL_SALE_POSTAL => Some(game.copy(generalSalePostalAvailable = tickets))
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
      s"return the correct results for $searchOptions " in { db : Db =>
        implicit val (gameDao, ee): (GameDao, ExecutionEnv) = (db.gameDao, db.ee)
        val locationPredicateFactory = (g: Game) => lso match {
          case LocationSearchOption.HOME => g.location == Location.HOME
          case LocationSearchOption.AWAY => g.location == Location.AWAY
          case LocationSearchOption.ANY => true
        }
        val attendedPredicateFactory = (g: Game) => aso match {
          case AttendedSearchOption.ATTENDED => g.attended.contains(true)
          case AttendedSearchOption.UNATTENDED => g.attended.contains(false)
          case AttendedSearchOption.ANY => true
        }
        val gameOrTicketPredicateFactory = (g: Game) => gtso match {
          case GameOrTicketSearchOption.BONDHOLDERS => g.bondholdersAvailable.isDefined
          case GameOrTicketSearchOption.PRIORITY_POINT => g.priorityPointAvailable.isDefined
          case GameOrTicketSearchOption.SEASON => g.seasonTicketsAvailable.isDefined
          case GameOrTicketSearchOption.ACADEMY => g.academyMembersAvailable.isDefined
          case GameOrTicketSearchOption.ACADEMY_POSTAL => g.academyMembersPostalAvailable.isDefined
          case GameOrTicketSearchOption.GENERAL_SALE => g.generalSaleAvailable.isDefined
          case GameOrTicketSearchOption.GENERAL_SALE_POSTAL => g.generalSalePostalAvailable.isDefined
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
    /*
      Fragments.foreach(generators) { searchOptions =>
        "x" in {
          val (lso, aso, gtso) = searchOptions
          1 must be equalTo(1)
        }
      }
    }
    */
  }
    /*
  "Searching for games" should {
      // Generate a game for each possible search option
      val generators: List[(LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption)] = for {
        location <- List(LocationSearchOption.HOME, LocationSearchOption.AWAY)
        attended <- List(AttendedSearchOption.ATTENDED, AttendedSearchOption.UNATTENDED)
        ticket <- GameOrTicketSearchOption.values
      } yield (location, attended, ticket)
      generators.zipWithIndex
        val game = location match {
          case LocationSearchOption.HOME => opponents home (September(1, 2013).plusDays(index))
          case LocationSearchOption.AWAY => opponents away (September(1, 2013).plusDays(index))
        }
        game.attended = attended match {
          case AttendedSearchOption.ATTENDED => Some(true)
          case AttendedSearchOption.UNATTENDED => Some(false)
        }
        val tickets: Option[DateTime] = Some((August(1, 2013) at (9, 0)).plusDays(index))
        ticket match {
          case GameOrTicketSearchOption.BONDHOLDERS => game.bondholdersAvailable = tickets
          case GameOrTicketSearchOption.PRIORITY_POINT => game.priorityPointAvailable = tickets
          case GameOrTicketSearchOption.SEASON => game.seasonTicketsAvailable = tickets
          case GameOrTicketSearchOption.ACADEMY => game.academyMembersAvailable = tickets
          case GameOrTicketSearchOption.ACADEMY_POSTAL => game.academyMembersPostalAvailable = tickets
          case GameOrTicketSearchOption.GENERAL_SALE => game.generalSaleAvailable = tickets
          case GameOrTicketSearchOption.GENERAL_SALE_POSTAL => game.generalSalePostalAvailable = tickets
          case GameOrTicketSearchOption.GAME =>
        }
        allGames += gameDao store game
        index = index + 1
      }
      // Create predicates for each possible search option
      val locationPredicateFactory = (lso: LocationSearchOption) => (g: Game) => lso match {
        case LocationSearchOption.HOME => g.location == Location.HOME
        case LocationSearchOption.AWAY => g.location == Location.AWAY
        case LocationSearchOption.ANY => true
      }

      val attendedPredicateFactory = (aso: AttendedSearchOption) => (g: Game) => aso match {
        case AttendedSearchOption.ATTENDED => g.attended == Some(true)
        case AttendedSearchOption.UNATTENDED => g.attended == Some(false)
        case AttendedSearchOption.ANY => true
      }
      val gameOrTicketPredicateFactory = (gtso: GameOrTicketSearchOption) => (g: Game) => gtso match {
        case GameOrTicketSearchOption.BONDHOLDERS => g.bondholdersAvailable.isDefined
        case GameOrTicketSearchOption.PRIORITY_POINT => g.priorityPointAvailable.isDefined
        case GameOrTicketSearchOption.SEASON => g.seasonTicketsAvailable.isDefined
        case GameOrTicketSearchOption.ACADEMY => g.academyMembersAvailable.isDefined
        case GameOrTicketSearchOption.ACADEMY_POSTAL => g.academyMembersPostalAvailable.isDefined
        case GameOrTicketSearchOption.GENERAL_SALE => g.generalSaleAvailable.isDefined
        case GameOrTicketSearchOption.GENERAL_SALE_POSTAL => g.generalSalePostalAvailable.isDefined
        case GameOrTicketSearchOption.GAME => true
      }
      // Search for each possible option
      val expectedSearchesByPredicates =
        Map.empty[Tuple3[LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption], List[Game]]
      val actualSearchesByPredicates =
        Map.empty[Tuple3[LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption], List[Game]]
      for (lso <- LocationSearchOption.values; aso <- AttendedSearchOption.values; gtso <- GameOrTicketSearchOption.values) {
        val key = (lso, aso, gtso)
        actualSearchesByPredicates += key -> gameDao.search(aso, lso, gtso)
        val searchPredicate = (g: Game) =>
          locationPredicateFactory(lso)(g) && attendedPredicateFactory(aso)(g) && gameOrTicketPredicateFactory(gtso)(g)
        expectedSearchesByPredicates += key -> allGames.filter(searchPredicate).toList
      }
      Fragments.foreach(expectedSearchesByPredicates) { case (key, expectedSearchResults) =>
        val size = expectedSearchResults.size
        s"return ${size} result${if (size == 1) "" else "s"} for search key $key" in { db: Db =>
          actualSearchesByPredicates.get(key) must be equalTo (Some(expectedSearchResults))
        }
      }
      "nothing else" in {
        1 must be equalTo(1)
      }
  }
*/
  val nowA: DateTime = September(5, 1972).at(9, 0)
  val nowB: DateTime = September(7, 1972).at(9, 0)
  val nows: Stream[DateTime] = List(nowA, nowB).toStream #::: nows

  implicit val nowService: NowService = new NowService {
    val n = nows.iterator
    override def now: DateTime = n.next()
  }

  /**
   * A simple implicit class that allows games to be created as "Opponents home date" or "Opponents away date"
   */
  implicit class StringImplicit(opponents: String) {
    def home(date: Date)(implicit gameDao: GameDao, nowService: NowService) = on(Location.HOME, date)
    def home(date: DateTime)(implicit gameDao: GameDao, nowService: NowService) = on(Location.HOME, date)
    def away(date: Date)(implicit gameDao: GameDao, nowService: NowService) = on(Location.AWAY, date)
    def away(date: DateTime)(implicit gameDao: GameDao, nowService: NowService) = on(Location.AWAY, date)
    def on(location: Location, date: DateTime)(implicit gameDao: GameDao, nowService: NowService): Future[Game] = {
      val game = Game.gameKey(Competition.FACP, location, opponents, date.getYear).copy(
        at = Some(date.withHourOfDay(15).withMinuteOfHour(0).withMillisOfSecond(0)))
      gameDao.store(game)
    }
  }
}

case class Db(gameDao: GameDao, ee: ExecutionEnv)

trait DatabaseContext extends ForEach[Db] {
  // you need to define the "foreach" method
  def foreach[R: AsResult](f: Db => R): Result = {
    val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
    implicit val ec = ee.ec
    val result = for {
      gameDao <- openDatabaseTransaction
      result <- Future.successful {
        try {
          AsResult(f(Db(gameDao, ee)))
        }
        finally {
          closeDatabaseTransaction(gameDao)
        }
      }
    } yield result
    Await.result(result, 1.minute)
  }

  // create and close a transaction
  def openDatabaseTransaction(implicit ec: ExecutionContext): Future[SlickGameDao] = {
    val dbConfigFactory = new DatabaseConfigFactory {
      val config = ConfigFactory.parseString(
        """
          |slick.dbs.default.driver="slick.driver.HsqldbDriver$"
          |slick.dbs.default.db.driver="org.hsqldb.jdbc.JDBCDriver"
          |slick.dbs.default.db.url="jdbc:hsqldb:mem:hammers"
          |slick.dbs.default.db.user=""
          |slick.dbs.default.db.password=""
        """.stripMargin)
      override def apply = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.default", config)
    }
    val gameDao = new SlickGameDao(dbConfigFactory)
    gameDao.dbConfig.db.run(gameDao.create).map(_ => gameDao)
  }

  def closeDatabaseTransaction(gameDao: SlickGameDao)(implicit ec: ExecutionContext): Future[Unit] = {
    gameDao.dbConfig.db.run(gameDao.drop)
  }
}