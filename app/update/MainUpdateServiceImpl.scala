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

package update;

import javax.inject.Inject

import com.google.inject.name.Named
import com.typesafe.scalalogging.slf4j.Logging
import dao.{GameDao, Transactional}
import dates.NowService
import html.{DatePlayedLocator, GameKeyLocator, GameLocator, GameUpdateCommand}
import logging.RemoteStream
import model.Game
import model.Location.HOME

/**
 * The Class MainUpdateServiceImpl.
 *
 * @author alex
 */
class MainUpdateServiceImpl @Inject() (
  /**
   * The {@link GameDao} for getting persisted {@link Game} information.
   */
  tx: Transactional,
  /**
   * The {@link GamesScanner} for getting game information.
   */
  @Named("fixturesGameScanner")
  fixturesGameScanner: GameScanner,
  /**
   * The {@link GamesScanner} for getting game information.
   */
  @Named("ticketsGameScanner")
  ticketsGameScanner: GameScanner,

  /**
   * The {@link LastUpdated} used to notify the application when calendars were last updated.
   */
  lastUpdated: LastUpdated,
  /**
   * The {@link NowService} used to get the current date and time.
   */
  nowService: NowService) extends MainUpdateService with Logging {

  /**
   * Process all updates required in the database.
   *
   * @throws IOException
   */
  override def processDatabaseUpdates()(implicit remoteStream: RemoteStream): Int = {
    val allGames = tx { _ getAll }
    val newGames = processUpdates("fixture", fixturesGameScanner, allGames)
    val allAndNewGames = allGames ++ newGames
    processUpdates("ticket", ticketsGameScanner, allAndNewGames)
    lastUpdated at nowService.now
    allAndNewGames.size
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
  def processUpdates(updatesType: String, scanner: GameScanner, allGames: List[Game])(implicit remoteStream: RemoteStream): List[Game] = {
    logger info s"Scanning for $updatesType changes."
    val allGameUpdateCommands = scanner.scan(remoteStream)
    val updatesByGameLocator = allGameUpdateCommands.groupBy(_.gameLocator)
    updatesByGameLocator.toList flatMap {
      case (gameLocator, updates) =>
        val game = findGame(allGames, gameLocator)
        game match {
          case Some(game) => {
            updateGame(game, updates)
            None
          }
          case None => {
            val newGame = createNewGame(gameLocator)
            newGame foreach { game => updateGame(game, updates) }
            newGame
          }
        }
    }
  }

  /**
   * Find a game from its game locator.
   */
  def findGame(allGames: List[Game], gameLocator: GameLocator): Option[Game] = {
    def gameFinder: Game => Boolean = { (game: Game) =>
      gameLocator match {
        case GameKeyLocator(gameKey) => game.gameKey == gameKey
        case DatePlayedLocator(datePlayed) => Some(datePlayed) == game.at
      }
    }
    allGames find gameFinder
  }

  /**
   * Create a new game if the game locator permits it (i.e. is a gameKey locator) or return None otherwise.
   */
  def createNewGame(gameLocator: GameLocator): Option[Game] = {
    gameLocator match {
      case GameKeyLocator(gameKey) => {
        logger info s"Creating new game $gameKey"
        Some(Game(gameKey))
      }
      case DatePlayedLocator(datePlayed) => {
        logger info s"Tickets were found for a non-existent game played at $datePlayed. Ignoring."
        None
      }
    }
  }

  /**
   * Update a game with a list of updates.
   */
  def updateGame(game: Game, updates: Traversable[GameUpdateCommand]): Unit = {
    val updateCount = updates.foldLeft(0) { (updateCount, gameUpdateCommand) =>
      updateCount + (if (gameUpdateCommand update game) 1 else 0)
    }
    if (updateCount == 0) {
      logger info s"Ignoring game ${game.gameKey}"
    } else {
      tx { _ store game }
    }
  }

  def attendGame(gameId: Long): Option[Game] = attendOrUnattendGame(gameId, true)

  def unattendGame(gameId: Long): Option[Game] = attendOrUnattendGame(gameId, false)

  def attendOrUnattendGame(gameId: Long, attend: Boolean) =
    attendOrUnattendGames(_ findById gameId, attend).headOption

  def attendAllHomeGamesForSeason(season: Int) = attendOrUnattendGames(_ getAllForSeasonAndLocation (season, HOME), true)

  def attendOrUnattendGames(gamesFactory: GameDao => Traversable[Game], attend: Boolean): List[Game] = {
    tx { gameDao =>
      gamesFactory(gameDao).foldRight(List.empty[Game]) { (game, games) =>
        game.attended = Some(attend)
        gameDao.store(game)
        game :: games
      }
    }
  }
}
