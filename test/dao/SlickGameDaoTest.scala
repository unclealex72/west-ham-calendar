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

import dates.Date._
import dates._
import model.{Competition, Game, GameKey, Location}
import org.joda.time.DateTime
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.{BeforeAfter, Specification}
import org.specs2.specification.Scope
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeApplication
import slick.backend.DatabaseConfig
import slick.profile.BasicProfile

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * @author alex
 *
 */
class SlickGameDaoTest extends Specification {

  "A minimally stored game" should new db {
    val game = Game.gameKey(Competition.FACP, Location.HOME, "Opponents", 2013)
    val persistedGame = await {
      for {
        _ <- gameDao.store(game)
        persistedGame <- gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
      } yield persistedGame
    }
    "have a non-zero id" in new db {
      persistedGame.map(_.id) must beSome[Long](be_!=(0))
    }
    "have the correct business key" in {
      persistedGame.map(_.gameKey) must beSome(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    }
    "have the correct creation date" in {
      persistedGame.map(_.dateCreated) must beSome(nowA)
    }
    "have the correct update date" in {
      persistedGame.map(_.lastUpdated) must beSome(nowB)
    }
  }

  /*

  "Game keys" should txn { gameDao =>
    nowService =>
      val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
      gameDao store game
      val unique = {
        try {
          gameDao store Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
          false
        } catch {
          case _: Exception => true
        }
      }
      "be unique" in {
        unique must be equalTo (true)
      }
  }

  "Creating a new game" should txn { gameDao =>
    nowService =>
      val game = gameDao store Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
      "have the same update and create date" in {
        game.dateCreated must be equalTo (DEFAULT_UPDATE_DATE)
        game.lastUpdated must be equalTo (DEFAULT_UPDATE_DATE)
      }
  }

  "Updating a game" should txn { gameDao =>
    nowService =>
      val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
      gameDao store game
      val originalDateCreated = game.dateCreated
      val originalLastUpdated = game.lastUpdated
      gameDao store game
      val newDateCreated = game.dateCreated
      val newLastUpdated = game.lastUpdated
      "only update the update date and not the creation date" in {
        originalDateCreated must be equalTo (DEFAULT_UPDATE_DATE)
        originalLastUpdated must be equalTo (DEFAULT_UPDATE_DATE)
        newDateCreated must be equalTo (DEFAULT_UPDATE_DATE)
        newLastUpdated must be equalTo (DEFAULT_UPDATE_DATE plusDays (1))
      }
  }(new IncreasingNowService)

  "Games with dates" should txn { implicit gameDao =>
    nowService =>
      val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
      game.at = Some(September(5, 2013) at (15, 0))
      gameDao store game
      val persistedGame = gameDao findByDatePlayed (September(5, 2013) at (15, 0))
      "should be searchable by date" in {
        persistedGame match {
          case Some(persistedGame) => persistedGame.at must be equalTo (Some(September(5, 2013) at (15, 0)))
          case None => persistedGame must not be equalTo(None)
        }
      }
  }

  "Looking for all games in a season" should txn { implicit gameDao =>
    nowService =>
      val chelsea = "Chelsea" home May(5, 2013)
      val spurs = "Spurs" away January(9, 2013)
      val arsenal = "Arsenal" home February(12, 2013)
      val fulham = "Fulham" away April(1, 2012)
      val gamesFor2013 = gameDao getAllForSeason (2013)
      "return only games for that season in date played order" in {
        gamesFor2013 must be equalTo (List(spurs, arsenal, chelsea))
      }
  }

  "Retrieving all known seasons" should txn { implicit gameDao =>
    nowService =>
      val chelsea = "Chelsea" home May(5, 2013)
      val reading = "Reading" away September(7, 2011)
      val everton = "Everton" home March(15, 2011)
      val seasons = gameDao.getAllSeasons
      "retrieve every season a game has been played, earliest first" in {
        seasons must be equalTo (SortedSet(2011, 2013))
      }
  }

  "Getting the latest season" should txn { implicit gameDao =>
    nowService =>
      val emptyLastSeason = gameDao.getLatestSeason
      "be None for when there are no games at all" in {
        emptyLastSeason must be equalTo (None)
      }
      val chelsea = "Chelsea" home May(5, 2013)
      val reading = "Reading" away September(7, 2011)
      val everton = "Everton" home March(15, 2011)
      val lastSeason = gameDao.getLatestSeason
      "be equal to the last season with a game" in {
        lastSeason must be equalTo (Some(2013))
      }
  }

  "Getting all games for a given season and location" should txn { implicit gameDao =>
    nowService =>
      val chelsea = "Chelsea" home May(5, 2013)
      val spurs = "Spurs" away January(9, 2013)
      val arsenal = "Arsenal" home February(12, 2013)
      val fulham = "Fulham" away April(1, 2012)
      val homeGamesFor2013 = gameDao getAllForSeasonAndLocation (2013, Location.HOME)
      "return only games for that season in date played order" in {
        homeGamesFor2013 must be equalTo (List(arsenal, chelsea))
      }
  }

  "Getting all games" should txn { implicit gameDao =>
    nowService =>
      val chelsea = "Chelsea" home May(5, 2013)
      val spurs = "Spurs" away January(9, 2013)
      val arsenal = "Arsenal" home February(12, 2013)
      val fulham = "Fulham" away April(1, 2012)
      val allGames = gameDao.getAll
      "return all games in chronological order" in {
        allGames must be equalTo (List(fulham, spurs, arsenal, chelsea))
      }
  }

  "Searching for games" should txn { implicit gameDao =>
    nowService =>
      val allGames = Buffer.empty[Game]
      var index = 0
      // Generate a game for each possible search option
      for (
        location <- List(LocationSearchOption.HOME, LocationSearchOption.AWAY);
        attended <- List(AttendedSearchOption.ATTENDED, AttendedSearchOption.UNATTENDED);
        ticket <- GameOrTicketSearchOption.values
      ) {
        val opponents = String.format("Opponents %02d", new Integer(index))
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
      expectedSearchesByPredicates.foreach {
        case (key, expectedSearchResults) =>
          val size = expectedSearchResults.size
          s"return ${size} result${if (size == 1) "" else "s"} for search key $key" in {
            actualSearchesByPredicates.get(key) must be equalTo (Some(expectedSearchResults))
          }
      }
      "nothing else" in {
        1 must be equalTo(1)
      }
  }
*/

  trait db extends Scope with BeforeAfter {

    val nowA: DateTime = September(5, 1972).at(9, 0)
    val nowB: DateTime = September(7, 1972).at(9, 0)
    val nows: Stream[DateTime] = List(nowA, nowB).toStream #::: nows

    implicit var gameDao: GameDao = null
    implicit var ee: ExecutionEnv = null
    implicit val nowService: NowService = new NowService {
      val n = nows.iterator
      override def now: DateTime = n.next()
    }
    var dbConfig = {

    }
    def await[E](f: Future[E]): E = Await.result(f, 1.second)
    def before = {
      val app: Application = FakeApplication(
        additionalConfiguration = Map(
          "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
          "slick.dbs.default.db.driver" -> "org.h2.Driver",
          "slick.dbs.default.db.url" -> "jdbc:h2:mem:",
          "slick.dbs.default.db.user" -> "",
          "slick.dbs.default.db.password" -> ""
        ),
        withoutPlugins = Seq("evolution"))
      val dbConfigProvider = new DatabaseConfigProvider {
        override def get[P <: BasicProfile]: DatabaseConfig[P] = DatabaseConfigProvider.get[P](app)
      }
      gameDao = new SlickGameDao(dbConfigProvider)
    }

    def after: Any = {}
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