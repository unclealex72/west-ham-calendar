/**
 * Copyright 2010-2012 Alex Jones
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

import com.typesafe.scalalogging.slf4j.StrictLogging
import dao.GameDao
import dates.NowService
import html.{DatePlayedLocator, GameKeyLocator, GameLocator, GameUpdateCommand}
import logging.RemoteStream
import model.Game
import models.Location
import Location.HOME
import update.fixtures.FixturesGameScanner
import update.tickets.TicketsGameScanner

import scala.concurrent.{ExecutionContext, Future}

/**
 * The Class MainUpdateServiceImpl.
 *
 * @author alex
 */
class MainUpdateServiceImpl(
  /**
   * The {@link GameDao} for getting persisted {@link Game} information.
   */
  gameDao: GameDao,
  /**
   * The {@link GamesScanner} for getting game information.
   */
  fixturesGameScanner: FixturesGameScanner,
  /**
   * The {@link GamesScanner} for getting game information.
   */
  ticketsGameScanner: TicketsGameScanner,

  /**
   * The {@link LastUpdated} used to notify the application when calendars were last updated.
   */
  lastUpdated: LastUpdated
  /**
   * The {@link NowService} used to get the current date and time.
   */
  )(implicit ec: ExecutionContext, nowService: NowService) extends MainUpdateService with StrictLogging {

  /**
   * Process all updates required in the database.
   *
   * @throws IOException
   */
  override def processDatabaseUpdates()(implicit remoteStream: RemoteStream): Future[Int] = {
    for {
      allGames <- gameDao.getAll
      latestSeason <- gameDao.getLatestSeason
      newGames <- processUpdates("fixture", fixturesGameScanner, latestSeason, allGames)
      _ <- processUpdates("ticket", ticketsGameScanner, latestSeason, allGames ++ newGames)
    } yield {
      lastUpdated at nowService.now
      (allGames ++ newGames).size
    }
  }

  /**
   * Process updates.
   *
   * @param updatesType
   *          the updates type
   * @param uri
   *          the uri
   * @param scanner
   *          the scanner
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  def processUpdates(updatesType: String, scanner: GameScanner, latestSeason: Option[Int], allGames: List[Game])(implicit remoteStream: RemoteStream): Future[List[Game]] = {
    logger info s"Scanning for $updatesType changes."
    scanner.scan(latestSeason).flatMap { allGameUpdateCommands =>
      val updatesByGameLocator = allGameUpdateCommands.groupBy(_.gameLocator)
      updatesByGameLocator.foldRight(Future.successful(List.empty[Game])) { (gl, fGames) =>
        val (gameLocator, updates) = gl
        fGames.flatMap { games =>
          updateAndStoreGame(allGames, gameLocator, updates).map {
            case Some(game) => game :: games
            case _ => games
          }
        }
      }
    }
  }

  def updateAndStoreGame(allGames: List[Game], gameLocator: GameLocator, updates: List[GameUpdateCommand]): Future[Option[Game]] = {
    val (isNew, oGame) = findGame(allGames, gameLocator) match {
      case Some(game) => (false, Some(game))
      case _ => (true, createNewGame(gameLocator))
    }
    val oUpdate = for {
      game <- oGame
      updatedGame <- updateGame(game, updates)
    } yield {
      gameDao.store(updatedGame).map { persistedGame =>
        if (isNew) Some(persistedGame) else None
      }
    }
    oUpdate.getOrElse(Future.successful(None))
  }
  /**
   * Find a game from its game locator.
   */
  def findGame(allGames: List[Game], gameLocator: GameLocator): Option[Game] = {
    def gameFinder: Game => Boolean = { (game: Game) =>
      gameLocator match {
        case GameKeyLocator(gameKey) => game.gameKey == gameKey
        case DatePlayedLocator(datePlayed) => game.at.contains(datePlayed)
      }
    }
    allGames find gameFinder
  }

  /**
   * Create a new game if the game locator permits it (i.e. is a gameKey locator) or return None otherwise.
   */
  def createNewGame(gameLocator: GameLocator): Option[Game] = {
    gameLocator match {
      case GameKeyLocator(gameKey) =>
        logger info s"Creating new game $gameKey"
        Some(Game.gameKey(gameKey))
      case DatePlayedLocator(datePlayed) =>
        logger info s"Tickets were found for a non-existent game played at $datePlayed. Ignoring."
        None
    }
  }

  /**
   * Update a game with a list of updates.
   */
  def updateGame(game: Game, updates: Traversable[GameUpdateCommand]): Option[Game] = {
    case class UpdatedGame(game: Game, updated: Boolean = false) {
      def update(newGame: Game) = UpdatedGame(newGame, updated = true)
    }
    val updatedGame: UpdatedGame = updates.foldLeft(UpdatedGame(game)) { (updatedGame, gameUpdateCommand) =>
      gameUpdateCommand.update(updatedGame.game) match {
        case Some(newGame) => updatedGame.update(newGame)
        case _ => updatedGame
      }
    }
    if (updatedGame.updated) {
      Some(updatedGame.game)
    }
    else {
      logger info s"Ignoring game ${game.gameKey}"
      None
    }
  }

  def attendGame(gameId: Long): Future[Option[Game]] = attendOrUnattendGame(gameId, attend = true)

  def unattendGame(gameId: Long): Future[Option[Game]] = attendOrUnattendGame(gameId, attend = false)

  def attendOrUnattendGame(gameId: Long, attend: Boolean): Future[Option[Game]] =
    attendOrUnattendGames(gameDao.findById(gameId).map(_.toList), attend).map(_.headOption)

  def attendAllHomeGamesForSeason(season: Int) =
    attendOrUnattendGames(gameDao.getAllForSeasonAndLocation(season, HOME), attend = true)

  def attendOrUnattendGames(fGames: Future[List[Game]], attend: Boolean): Future[List[Game]] = {
    fGames.flatMap { games =>
      games.foldRight(Future.successful(List.empty[Game])) { (game, fGames) =>
        for {
          newGame <- gameDao.store(game.copy(attended = Some(attend)))
          games <- fGames
        } yield newGame :: games
      }
    }
  }
}
