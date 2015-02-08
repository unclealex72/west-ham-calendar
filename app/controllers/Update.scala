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
package controllers

import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject
import javax.inject.Named
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.Results._
import play.mvc.Controller
import securesocial.core.Authorization
import uk.co.unclealex.hammers.calendar.update.MainUpdateService
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.iteratee.Concurrent
import play.api.mvc.ChunkedResult
import play.api.mvc.ResponseHeader
import uk.co.unclealex.hammers.calendar.logging.RemoteStream
import json.JsonResults
import uk.co.unclealex.hammers.calendar.model.Game
import services.GameRowFactory

/**
 * @author alex
 *
 */
class Update @Inject() (
  /**
   * The secret used to protect the update path.
   */
  @Named("secret") val secret: String,
  /**
   * The authorization object used to check a user is authorised.
   */
  authorization: Authorization,
  /**
   * The main update service used to scrape the West Ham site and update game information.
   */
  mainUpdateService: MainUpdateService,
  /**
   * The game row factory used to get game row models.
   */
  gameRowFactory: GameRowFactory) extends Controller with Secure with Secret with JsonResults {

  implicit val implicitAuthorization = authorization

  /**
   * Update all games in the database from the web.
   */
  def update(secretPayload: String) = Secret(secretPayload) {
    Action { implicit request =>
      val (enumerator, channel) = Concurrent.broadcast[String]
      implicit val remoteStream = new RemoteStream() {

        def logToRemote(message: String): Unit = {
          channel.push(message)
        }
      }
      scala.concurrent.Future {
        val gameCount = mainUpdateService.processDatabaseUpdates
        channel.push(s"There are now ${gameCount} games.\n")
        channel.eofAndEnd
      }
      Ok.chunked(enumerator)
    }
  }

  /**
   * Attend or unattend a game.
   */
  def attendOrUnattend(gameUpdater: Long => Option[Game], gameId: Long) =
    SecuredAction(true, authorization) {
      json(gameUpdater(gameId).map(gameRowFactory.toRow(true)))
    }

  /**
   * Attend a game.
   */
  def attend(gameId: Long) = attendOrUnattend(mainUpdateService.attendGame, gameId)

  /**
   * Unattend a game.
   */
  def unattend(gameId: Long) = attendOrUnattend(mainUpdateService.unattendGame, gameId)

}