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
package uk.co.unclealex.hammers.calendar.update

import org.specs2.mutable.Specification
import org.scalamock.specs2.MockFactory
import uk.co.unclealex.hammers.calendar.dao.GameDao
import uk.co.unclealex.hammers.calendar.html.MainPageService
import uk.co.unclealex.hammers.calendar.html.HtmlGamesScanner
import java.net.URI
import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Competition._
import uk.co.unclealex.hammers.calendar.model.Location._
import uk.co.unclealex.hammers.calendar.html.GameUpdateCommand
import uk.co.unclealex.hammers.calendar.dao.Transactional
import uk.co.unclealex.hammers.calendar.html.DatePlayedUpdateCommand
import uk.co.unclealex.hammers.calendar.html.GameKeyLocator
import uk.co.unclealex.hammers.calendar.September
import uk.co.unclealex.hammers.calendar.html.AttendenceUpdateCommand
import uk.co.unclealex.hammers.calendar.html.SeasonTicketsUpdateCommand
import uk.co.unclealex.hammers.calendar.html.DatePlayedLocator
import uk.co.unclealex.hammers.calendar.Instant
import uk.co.unclealex.hammers.calendar.log.SimpleRemoteStream
import uk.co.unclealex.hammers.calendar.dates.NowService
import org.joda.time.DateTime

/**
 * @author alex
 *
 */
class MainUpdateServiceImplTest extends Specification with MockFactory with SimpleRemoteStream {

  val TICKETS_URL = new URI("http://tickets")
  val FIXTURES_URL = new URI("http://fixtures")
  val SOUTHAMPTON = GameKey(FACP, HOME, "Southampton", 2014)
  val LIVERPOOL = GameKey(FACP, HOME, "Liverpool", 2014)

  "Adding a completely new game" should {
    "create and update a new game" in {
      val s = new Services()
      (s.gameDao.getAll _) expects () returning (List.empty[Game])
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at (15, 0))))
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List.empty[GameUpdateCommand])
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      (s.gameDao.store _) expects (where(expectedStoredGame)) returning (expectedStoredGame)
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates()(remoteStream) must be equalTo (1)
    }
  }

  "Updating an existing game with new information" should {
    "update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      (s.gameDao.getAll _) expects () returning (List(existingStoredGame))
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List(
        AttendenceUpdateCommand(SOUTHAMPTON, 34966)))
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List.empty[GameUpdateCommand])
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.attendence = Some(34966)
      }
      (s.gameDao.store _) expects (where(expectedStoredGame)) returning (expectedStoredGame)
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (1)
    }
  }

  "Updating an existing game with no extra information" should {
    "not update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.attendence = Some(34966)
      }
      (s.gameDao.getAll _) expects () returning (List(existingStoredGame))
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List(
        AttendenceUpdateCommand(SOUTHAMPTON, 34966)))
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List.empty[GameUpdateCommand])
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.attendence = Some(34966)
      }
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (1)
    }
  }

  "Updating an existing game with new ticket information" should {
    "update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      (s.gameDao.getAll _) expects () returning (List(existingStoredGame))
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List.empty)
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List(
        SeasonTicketsUpdateCommand(September(5, 2013) at (15, 0), September(3, 2013) at (9, 0))))
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      (s.gameDao.store _) expects (where(expectedStoredGame)) returning (expectedStoredGame)
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (1)
    }
  }

  "Updating an existing game with no new ticket information" should {
    "not update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      (s.gameDao.getAll _) expects () returning (List(existingStoredGame))
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List.empty)
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List(
        SeasonTicketsUpdateCommand(September(5, 2013) at (15, 0), September(3, 2013) at (9, 0))))
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (1)
    }
  }

  "Creating a new game and also updating its ticket information" should {
    "create and update the game" in {
      val s = new Services()
      (s.gameDao.getAll _) expects () returning (List.empty)
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at (15, 0))))
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List(
        SeasonTicketsUpdateCommand(September(5, 2013) at (15, 0), September(3, 2013) at (9, 0))))
      val firstStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      val secondStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      List(firstStoredGame, secondStoredGame) foreach { game =>
        (s.gameDao.store _) expects (where(game)) returning (game)
      }
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (1)
    }
  }

  "Tickets for a non-existing game" should {
    "be ignored" in {
      val s = new Services()
      (s.gameDao.getAll _) expects () returning (List.empty)
      (s.fixturesHtmlGamesScanner.scan _) expects (remoteStream, FIXTURES_URL) returning (List.empty)
      (s.ticketsHtmlGamesScanner.scan _) expects (remoteStream, TICKETS_URL) returning (List(
        SeasonTicketsUpdateCommand(September(5, 2013) at (15, 0), September(3, 2013) at (9, 0))))
      (s.nowService.now _) expects() returning (s.now)
      (s.lastUpdated.at _ ) expects(s.now)
      s.mainUpdateService.processDatabaseUpdates() must be equalTo (0)
    }
  }

  "Attending a game" should {
    "persist its attended flag to true" in {
      val s = new Services()
      val unattendedGame = Game(SOUTHAMPTON)
      val attendedGame = game(SOUTHAMPTON)(_.attended = Some(true))

      (s.gameDao.findById _) expects (1l) returning (Some(unattendedGame))
      (s.gameDao.store _) expects (where(attendedGame)) returning (attendedGame)
      s.mainUpdateService.attendGame(1l)
    }
  }

  "Unattending a game" should {
    "persist its attended flag to false" in {
      val s = new Services()
      val unattendedGame = Game(SOUTHAMPTON)
      val attendedGame = game(SOUTHAMPTON)(_.attended = Some(true))

      (s.gameDao.findById _) expects (1l) returning (Some(attendedGame))
      (s.gameDao.store _) expects (where(unattendedGame)) returning (unattendedGame)
      s.mainUpdateService.unattendGame(1l)
    }
  }

  "Attending all home games in a season" should {
    "persist all home games attended flag to true" in {
      val s = new Services()
      val homeGames2013 = List(SOUTHAMPTON, LIVERPOOL)
      val unattendedGames = homeGames2013 map (Game(_))
      val attendedGames = homeGames2013 map (game(_)(_.attended = Some(true)))

      (s.gameDao.getAllForSeasonAndLocation _) expects (2013, HOME) returning (unattendedGames)
      attendedGames foreach (attendedGame => (s.gameDao.store _) expects (where(attendedGame)) returning (attendedGame))
      s.mainUpdateService.attendAllHomeGamesForSeason(2013)
    }
  }

  // Game locator implicits

  implicit val instantToDatePlayedLocator: Instant => DatePlayedLocator = DatePlayedLocator(_)

  implicit val gameKeyToGameKeyLocator: GameKey => GameKeyLocator = GameKeyLocator(_)
  /**
   * A matcher that checks that two games are equal.
   */
  implicit val gameMatcher: Game => (Game => Boolean) = { g1 =>
    g2 =>
      g1.gameKey == g2.gameKey &&
        g1.attended == g2.attended &&
        g1.attendence == g2.attendence &&
        g1.academyMembersAvailable == g2.academyMembersAvailable &&
        g1.bondholdersAvailable == g2.bondholdersAvailable &&
        g1.generalSaleAvailable == g2.generalSaleAvailable &&
        g1.at == g2.at &&
        g1.priorityPointAvailable == g2.priorityPointAvailable &&
        g1.seasonTicketsAvailable == g2.seasonTicketsAvailable &&
        g1.id == g2.id &&
        g1.location == g2.location &&
        g1.matchReport == g2.matchReport &&
        g1.result == g2.result &&
        g1.televisionChannel == g2.televisionChannel
  }

  /**
   * Create a new game from a game key and  alter its properties in a code block
   */
  def game: GameKey => (Game => Unit) => Game = { gameKey =>
    f =>
      val game = Game(gameKey)
      f(game)
      game
  }

  /**
   * A class that holds all the mocked services
   */
  class Services {
    val now = new DateTime
    val nowService = mock[NowService]
    val gameDao = mock[GameDao]
    val lastUpdated = mock[LastUpdated]
    val mainPageService = new MainPageService {
      val ticketsUri = TICKETS_URL
      val fixturesUri = FIXTURES_URL
    }
    val ticketsHtmlGamesScanner = mock[HtmlGamesScanner]
    val fixturesHtmlGamesScanner = mock[HtmlGamesScanner]
    val ticketsHtmlGamesScannerFactory = new TicketsHtmlGamesScannerFactory() {
      def get = Some(ticketsHtmlGamesScanner)
    }
    val transactional = new Transactional {
      def tx[T](block: GameDao => T): T = block(gameDao)
    }
    val mainUpdateService = new MainUpdateServiceImpl(transactional, mainPageService, ticketsHtmlGamesScannerFactory, fixturesHtmlGamesScanner, lastUpdated, nowService)
  }
}

