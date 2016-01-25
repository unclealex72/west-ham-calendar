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

import javax.inject.{Inject, Named}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dispatch.Future
import json.JsonResults
import logging.RemoteStream
import model.Game
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Concurrent
import play.api.mvc.Action
import security.Definitions._
import services.GameRowFactory
import update.MainUpdateService

import scala.concurrent.ExecutionContext
import scala.util.Failure

/**
 * @author alex
 *
 */
case class Update @Inject() (
                         /**
   * The secret used to protect the update path.
   */
                         @Named("secret") val secret: String,
                         /**
   * The authorization object used to check a user is authorised.
   */
                         val authorization: Auth,
                         /**
   * The main update service used to scrape the West Ham site and update game information.
   */
                         mainUpdateService: MainUpdateService,
                         /**
   * The game row factory used to get game row models.
   */
                         gameRowFactory: GameRowFactory,
                         messagesApi: MessagesApi, env: Env)(implicit ec: ExecutionContext) extends Secure with Secret with TicketForms with JsonResults {

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
      mainUpdateService.processDatabaseUpdates.map { gameCount =>
        channel.push(s"There are now $gameCount games.\n")
      }.andThen {
        case Failure(t) => {
          logger.error("Updating all games failed.", t)
          remoteStream.log("Updating all games failed.", Some(t))
        }
      }.andThen {
        case _ => channel.eofAndEnd()
      }
      Ok.chunked(enumerator)
    }
  }

  /**
   * Attend or unattend a game.
   */
  def attendOrUnattend(gameUpdater: Long => Future[Option[Game]], gameId: Long) =
    SecuredAction(authorization).async { implicit request =>
      gameUpdater(gameId).map {
        case Some(game) => json(gameRowFactory.toRow(includeAttended = true, ticketFormUrlFactory)(game))
        case _ => NotFound
      }
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