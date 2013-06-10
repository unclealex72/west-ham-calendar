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
package uk.co.unclealex.hammers.calendar.dao

import org.specs2.mutable.Specification
import org.specs2.specification.Fragment
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter
import SquerylGameDao._
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Location
import java.sql.SQLException
import org.squeryl.SquerylSQLException
import uk.co.unclealex.hammers.calendar.Date._
import uk.co.unclealex.hammers.calendar.September
import uk.co.unclealex.hammers.calendar.Instant
import uk.co.unclealex.hammers.calendar.January
import uk.co.unclealex.hammers.calendar.Date
import uk.co.unclealex.hammers.calendar.May
import uk.co.unclealex.hammers.calendar.February
import uk.co.unclealex.hammers.calendar.April
import uk.co.unclealex.hammers.calendar.March
import scala.collection.SortedSet
import scala.math.Ordering
import uk.co.unclealex.hammers.calendar.search.LocationSearchOption
import uk.co.unclealex.hammers.calendar.search.AttendedSearchOption
import uk.co.unclealex.hammers.calendar.search.GameOrTicketSearchOption
import scala.collection.mutable.Buffer
import uk.co.unclealex.hammers.calendar.August
import org.joda.time.DateTime
import scala.collection.mutable.Map
import uk.co.unclealex.hammers.calendar.search.GameOrTicketSearchOption

/**
 * @author alex
 *
 */
class SquerylGameDaoTest extends Specification {

  "A minimally stored game" should txn {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    store(game)
    val persistedGame = findByBusinessKey(Competition.FACP, Location.HOME, "Opponents", 2013)
    persistedGame map { persistedGame =>
      "have a non-zero id" in {
        persistedGame.id must not be equalTo(0)
      }
      "have the correct business key" in {
        persistedGame.gameKey must be equalTo (GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
      }
    }
    "be retrievable" in {
      persistedGame must not be equalTo(None)
    }
  }

  "Game keys" should txn {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    store(game)
    val unique = {
      try {
        store(Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013)))
        false
      } catch {
        case _: SquerylSQLException => true
      }
    }
    "be unique" in {
      unique must be equalTo (true)
    }
  }

  "Games with dates" should txn {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    game.dateTimePlayed = Some(September(5, 2013) at (15, 0))
    store(game)
    val persistedGame = findByDatePlayed(September(5, 2013) at (15, 0))
    "should be searchable by date" in {
      persistedGame match {
        case Some(persistedGame) => persistedGame.dateTimePlayed must be equalTo (Some(September(5, 2013) at (15, 0)))
        case None => persistedGame must not be equalTo(None)
      }
    }
  }

  "Looking for all games in a season" should txn {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val gamesFor2013 = getAllForSeason(2013)
    "return only games for that season in date played order" in {
      gamesFor2013 must be equalTo (List(spurs, arsenal, chelsea))
    }
  }

  "Retrieving all known seasons" should txn {
    val chelsea = "Chelsea" home May(5, 2013)
    val reading = "Reading" away September(7, 2011)
    val everton = "Everton" home March(15, 2011)
    val seasons = getAllSeasons
    "retrieve every season a game has been played, earliest first" in {
      seasons must be equalTo (SortedSet(2011, 2013))
    }
  }

  "Getting the latest season" should txn {
    val emptyLastSeason = getLatestSeason
    "be None for when there are no games at all" in {
      emptyLastSeason must be equalTo (None)
    }
    val chelsea = "Chelsea" home May(5, 2013)
    val reading = "Reading" away September(7, 2011)
    val everton = "Everton" home March(15, 2011)
    val lastSeason = getLatestSeason
    "be equal to the last season with a game" in {
      lastSeason must be equalTo (Some(2013))
    }
  }

  "Getting all games for a given season and location" should txn {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val homeGamesFor2013 = getAllForSeasonAndLocation(2013, Location.HOME)
    "return only games for that season in date played order" in {
      homeGamesFor2013 must be equalTo (List(arsenal, chelsea))
    }
  }

  "Getting all games" should txn {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val allGames = getAll
    "return all games in chronological order" in {
      allGames must be equalTo (List(fulham, spurs, arsenal, chelsea))
    }
  }

  "Searching for games" should txn {
    val allGames = Buffer.empty[Game]
    var day = 1
    var index = 0
    // Generate a game for each possible search option
    for (
      location <- List(LocationSearchOption.HOME, LocationSearchOption.AWAY);
      attended <- List(AttendedSearchOption.ATTENDED, AttendedSearchOption.UNATTENDED);
      ticket <- GameOrTicketSearchOption.values
    ) {
      val opponents = String.format("Opponents %02d", new Integer(index))
      val game = location match {
        case LocationSearchOption.HOME => opponents home September(day, 2013)
        case LocationSearchOption.AWAY => opponents away September(day, 2013)
      }
      game.attended = attended match {
        case AttendedSearchOption.ATTENDED => Some(true)
        case AttendedSearchOption.UNATTENDED => Some(false)
      }
      val tickets: Option[DateTime] = Some(August(day, 2013) at (9, 0))
      ticket match {
        case GameOrTicketSearchOption.BONDHOLDERS => game.dateTimeBondholdersAvailable = tickets
        case GameOrTicketSearchOption.PRIORITY_POINT => game.dateTimePriorityPointPostAvailable = tickets
        case GameOrTicketSearchOption.SEASON => game.dateTimeSeasonTicketsAvailable = tickets
        case GameOrTicketSearchOption.ACADEMY => game.dateTimeAcademyMembersAvailable = tickets
        case GameOrTicketSearchOption.GENERAL_SALE => game.dateTimeGeneralSaleAvailable = tickets
        case GameOrTicketSearchOption.GAME =>
      }
      allGames += store(game)
      day = day + 1
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
      case GameOrTicketSearchOption.BONDHOLDERS => g.dateTimeBondholdersAvailable.isDefined
      case GameOrTicketSearchOption.PRIORITY_POINT => g.dateTimePriorityPointPostAvailable.isDefined
      case GameOrTicketSearchOption.SEASON => g.dateTimeSeasonTicketsAvailable.isDefined
      case GameOrTicketSearchOption.ACADEMY => g.dateTimeAcademyMembersAvailable.isDefined
      case GameOrTicketSearchOption.GENERAL_SALE => g.dateTimeGeneralSaleAvailable.isDefined
      case GameOrTicketSearchOption.GAME => true
    }
    // Search for each possible option
    val expectedSearchesByPredicates =
      Map.empty[Tuple3[LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption], List[Game]]
    val actualSearchesByPredicates =
      Map.empty[Tuple3[LocationSearchOption, AttendedSearchOption, GameOrTicketSearchOption], List[Game]]
    for (lso <- LocationSearchOption.values; aso <- AttendedSearchOption.values; gtso <- GameOrTicketSearchOption.values) {
      val key = (lso, aso, gtso)
      actualSearchesByPredicates += key -> search(aso, lso, gtso)
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
  }

  /**
   * Wrap tests with database creation and transactions
   */
  def txn[B](block: => B) = {
    Class forName "org.h2.Driver"
    SessionFactory.concreteFactory = Some(() =>
      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:mem:", "", ""),
        new H2Adapter))
    tx { gd =>
      create
      block
    }
  }

  /**
   * A simple implicit class that allows games to be created as "Opponents home date" or "Opponents away date"
   */
  implicit class StringImplicit(opponents: String) {
    def home(implicit date: Date) = on(Location.HOME)
    def away(implicit date: Date) = on(Location.AWAY)
    def on(location: Location)(implicit date: Date): Game = {
      val game = Game(GameKey(Competition.FACP, location, opponents, date.year))
      game.dateTimePlayed = Some(date at (15, 0))
      store(game)
    }
  }
}