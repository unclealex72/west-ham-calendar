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
package update

import java.net.URI

import dao.{GameDao, Transactional}
import dates.DateTimeImplicits._
import dates.{Instant, NowService, September}
import html.{AttendenceUpdateCommand, DatePlayedLocator, DatePlayedUpdateCommand, GameKeyLocator, GameUpdateCommand, SeasonTicketsUpdateCommand}
import logging.SimpleRemoteStream
import model.Competition._
import model.Location._
import model.{Game, GameKey}
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

/**
 * @author alex
 *
 */
class MainUpdateServiceImplTest extends Specification with Mockito with SimpleRemoteStream {

  val TICKETS_URL = new URI("http://tickets")
  val FIXTURES_URL = new URI("http://fixtures")
  val SOUTHAMPTON = GameKey(FACP, HOME, "Southampton", 2014)
  val LIVERPOOL = GameKey(FACP, HOME, "Liverpool", 2014)

  "Adding a completely new game" should {
    "create and update a new game" in {
      val s = new Services()
      s.gameDao.getAll returns List.empty[Game]
      s.fixturesGameScanner.scan(remoteStream) returns List(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at(15, 0)))
      s.ticketsGameScanner.scan(remoteStream) returns List.empty[GameUpdateCommand]
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      s.gameDao.store(expectedStoredGame) returns expectedStoredGame
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates()(remoteStream) must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with new information" should {
    "update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      s.gameDao.getAll returns List(existingStoredGame)
      s.fixturesGameScanner.scan(remoteStream) returns List(
        AttendenceUpdateCommand(SOUTHAMPTON, 34966))
      s.ticketsGameScanner.scan(remoteStream) returns List.empty[GameUpdateCommand]
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.attendence = Some(34966)
      }
      s.gameDao.store(expectedStoredGame) returns expectedStoredGame
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with no extra information" should {
    "not update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.attendence = Some(34966)
      }
      s.gameDao.getAll returns List(existingStoredGame)
      s.fixturesGameScanner.scan(remoteStream) returns List(
        AttendenceUpdateCommand(SOUTHAMPTON, 34966))
      s.ticketsGameScanner.scan(remoteStream) returns List.empty[GameUpdateCommand]
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with new ticket information" should {
    "update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      s.gameDao.getAll returns List(existingStoredGame)
      s.fixturesGameScanner.scan(remoteStream) returns List.empty
      s.ticketsGameScanner.scan(remoteStream) returns List(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      val expectedStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      s.gameDao.store(expectedStoredGame) returns expectedStoredGame
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with no new ticket information" should {
    "not update the game" in {
      val s = new Services()
      val existingStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      s.gameDao.getAll returns List(existingStoredGame)
      s.fixturesGameScanner.scan(remoteStream) returns List.empty
      s.ticketsGameScanner.scan(remoteStream) returns List(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Creating a new game and also updating its ticket information" should {
    "create and update the game" in {
      val s = new Services()
      s.gameDao.getAll returns List.empty
      s.fixturesGameScanner.scan(remoteStream) returns List(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at(15, 0)))
      s.ticketsGameScanner.scan(remoteStream) returns List(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      val firstStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
      }
      val secondStoredGame = game(SOUTHAMPTON) { g =>
        g.at = Some(September(5, 2013) at (15, 0))
        g.seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      }
      List(firstStoredGame, secondStoredGame) foreach { game =>
        s.gameDao.store(game) returns game
      }
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 1
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "html.Tickets for a non-existing game" should {
    "be ignored" in {
      val s = new Services()
      s.gameDao.getAll returns List.empty
      s.fixturesGameScanner.scan(remoteStream) returns List.empty
      s.ticketsGameScanner.scan(remoteStream) returns List(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      s.nowService.now returns s.now
      s.mainUpdateService.processDatabaseUpdates() must be equalTo 0
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Attending a game" should {
    "persist its attended flag to true" in {
      val s = new Services()
      val unattendedGame = Game(SOUTHAMPTON)
      val attendedGame = game(SOUTHAMPTON)(_.attended = Some(true))

      s.gameDao.findById(1l) returns Some(unattendedGame)
      s.gameDao.store(attendedGame) returns attendedGame
      val changedGame = s.mainUpdateService.attendGame(1l)
      changedGame.map(_.attended) must be equalTo Some(Some(true))
    }
  }

  "Unattending a game" should {
    "persist its attended flag to false" in {
      val s = new Services()
      val unattendedGame = Game(SOUTHAMPTON)
      val attendedGame = game(SOUTHAMPTON)(_.attended = Some(true))

      s.gameDao.findById(1l) returns Some(attendedGame)
      s.gameDao.store(unattendedGame) returns unattendedGame
      val changedGame = s.mainUpdateService.unattendGame(1l)
      changedGame.map(_.attended) must be equalTo Some(Some(false))
    }
  }

  "Attending all home games in a season" should {
    "persist all home games attended flag to true" in {
      val s = new Services()
      val homeGames2013 = List(SOUTHAMPTON, LIVERPOOL)
      val unattendedGames = homeGames2013 map (Game(_))
      val attendedGames = homeGames2013 map (game(_)(_.attended = Some(true)))

      s.gameDao.getAllForSeasonAndLocation(2013, HOME) returns unattendedGames
      attendedGames foreach (attendedGame => s.gameDao.store(attendedGame) returns attendedGame)
      s.mainUpdateService.attendAllHomeGamesForSeason(2013).map(_.attended) must be equalTo List(Some(true), Some(true))
    }
  }

  // Game locator implicits

  implicit val instantToDatePlayedLocator: Instant => DatePlayedLocator = DatePlayedLocator(_)

  implicit val gameKeyToGameKeyLocator: GameKey => GameKeyLocator = GameKeyLocator
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
    val ticketsGameScanner = mock[GameScanner]
    val fixturesGameScanner = mock[GameScanner]
    val transactional = new Transactional {
      def tx[T](block: GameDao => T): T = block(gameDao)
    }
    val mainUpdateService = new MainUpdateServiceImpl(transactional, fixturesGameScanner, ticketsGameScanner, lastUpdated, nowService)
  }
}

