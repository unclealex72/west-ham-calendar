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

/**
 * @author alex
 *
 */
class SquerylGameDaoTest extends Specification {

  "A minimally stored game" should tx {
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

  "Game keys" should tx {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    store(game)
    val unique = {
      try {
        store(Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013)))
        false
      }
      catch {
        case _: SquerylSQLException => true
      }
    }
    "be unique" in {
      unique must be equalTo(true)
    }
  }

  "Games with dates" should tx {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Opponents", 2013))
    game.dateTimePlayed = Some(September(5, 2013) at (15, 0))
    store(game)
    val persistedGame = findByDatePlayed(September(5, 2013) at (15, 0))
    "should be searchable by date" in {
      persistedGame match {
        case Some(persistedGame) => persistedGame.dateTimePlayed must be equalTo(Some(September(5, 2013) at (15, 0)))
        case None => persistedGame must not be equalTo(None)
      }
    }
  }
  
  "Looking for all games in a season" should tx {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val gamesFor2013 = getAllForSeason(2013)
    "return only games for that season in date played order" in {
      gamesFor2013 must be equalTo(List(spurs, arsenal, chelsea))
    }
  }
  
  "Retrieving all known seasons" should tx {
    val chelsea = "Chelsea" home May(5, 2013)
    val reading = "Reading" away September(7, 2011)
    val everton = "Everton" home March(15, 2011)
    val seasons = getAllSeasons
    "retrieve every season a game has been played, earliest first" in {
      seasons must be equalTo (SortedSet(2011, 2013))
    }
  }
  
  "Getting the latest season" should tx {
    val emptyLastSeason = getLatestSeason
    "be None for when there are no games at all" in {
      emptyLastSeason must be equalTo(None)
    }
    val chelsea = "Chelsea" home May(5, 2013)
    val reading = "Reading" away September(7, 2011)
    val everton = "Everton" home March(15, 2011)
    val lastSeason = getLatestSeason
    "be equal to the last season with a game" in {
      lastSeason must be equalTo(Some(2013))
    }
  }
  
  "Getting all games for a given season and location" should tx {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val homeGamesFor2013 = getAllForSeasonAndLocation(2013, Location.HOME)
    "return only games for that season in date played order" in {
      homeGamesFor2013 must be equalTo(List(arsenal, chelsea))
    }
  }

  "Getting all games" should tx {
    val chelsea = "Chelsea" home May(5, 2013)
    val spurs = "Spurs" away January(9, 2013)
    val arsenal = "Arsenal" home February(12, 2013)
    val fulham = "Fulham" away April(1, 2012)
    val allGames = getAll.sortBy(_.id)
    "return all games in no given order" in {
      allGames must be equalTo(List(arsenal, chelsea, spurs, fulham).sortBy(_.id))
    }
  }

  /**
   * Wrap tests with database creation and transactions
   */
  def tx[B](block: => B) = {
    Class forName "org.h2.Driver"
    SessionFactory.concreteFactory = Some(() =>
      Session.create(
        java.sql.DriverManager.getConnection("jdbc:h2:mem:", "", ""),
        new H2Adapter))
    transaction {
      create
      block
    }
  }
  
  /**
   * A simple implicit class that allows games to be created as "Opponents home date" or "Opponents away date"
   */
  implicit class StringImplicit(opponents: String) {
    def home(implicit date:Date) = on(Location.HOME)
    def away(implicit date:Date) = on(Location.AWAY)
    def on(location: Location)(implicit date: Date): Game = {
    val game = Game(GameKey(Competition.FACP, location, opponents, date.year))
    game.dateTimePlayed = Some(date at (15, 0))
    store(game)
  }}
}