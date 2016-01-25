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

import java.io.IOException
import logging.RemoteStream
import model.Game

import scala.concurrent.Future

/**
 * The service used to combine reading game information from the web and
 * transforming that information into google calendars.
 *
 * @author alex
 *
 */
trait MainUpdateService {

  /**
   * Move a game to the attended calendar.
   *
   * @param gameId
   *          The the id of the game to move.
   * @throws GoogleAuthenticationFailedException
   *           Thrown if authentication with the Google servers fails.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  def attendGame(gameId: Long): Future[Option[Game]]

  /**
   * Move a game to the unattendend calendar.
   *
   * @param gameId
   *          The id of the game to move.
   * @throws GoogleAuthenticationFailedException
   *           Thrown if authentication with the Google servers fails.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  def unattendGame(gameId: Long): Future[Option[Game]]

  /**
   * Attend all home games in a season.
   *
   * @param season
   *          the season
   * @throws GoogleAuthenticationFailedException
   *           Thrown if authentication with the Google servers fails.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  def attendAllHomeGamesForSeason(season: Int): Future[List[Game]]

  /**
   * Update a list of game update commands from an HTML scan.
    *
    * @return The number of games processed.
   */
  def processDatabaseUpdates()(implicit remoteStream: RemoteStream): Future[Int]
}
