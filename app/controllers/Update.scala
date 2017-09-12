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

import akka.stream.scaladsl.{Source => AkkaSource}
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.typesafe.scalalogging.StrictLogging
import dao.GameDao
import dates.ZonedDateTimeFactory
import logging.{Fatal, RemoteStream}
import model.{FatalError, Game}
import models.Competition.FRIENDLY
import models.{Competition, FatalErrorReportRel}
import monads.FO.FutureOption
import play.api.i18n.MessagesApi
import play.api.mvc._
import security.Definitions._
import services.GameRowFactory
import update.MainUpdateService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
/**
 * @author alex
 *
 */
class Update @javax.inject.Inject() (val secret: SecretToken,
                                     val mainUpdateService: MainUpdateService,
                                     val gameDao: GameDao,
                                     val gameRowFactory: GameRowFactory,
                                     override val messagesApi: MessagesApi,
                                     override val controllerComponents: ControllerComponents,
                                     override val zonedDateTimeFactory: ZonedDateTimeFactory,
                                     val silhouette: DefaultSilhouette,
                                     val auth: Auth,
                                     val fatal: Fatal,
                                     override implicit val ec: ExecutionContext
                                    ) extends AbstractController(controllerComponents, zonedDateTimeFactory, ec) with Secure with Secret with LinkFactories with StrictLogging {

  /**
   * Update all games in the database from the web.
   */
  def update(secretPayload: String) = Secret(secretPayload) {
    Action { implicit request: Request[AnyContent] =>
      chunked(mainUpdateService.processDatabaseUpdates) { count =>
        s"There are now $count games."
      }
    }
  }

  /**
   * Attend or unattend a game.
   */
  def attendOrUnattend(gameUpdater: Long => FutureOption[Game], gameId: Long) =
    silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      json(gameUpdater(gameId)) { game =>
        gameRowFactory.toRow(includeAttended = true, gameRowLinksFactory(includeUpdates = true))(game)
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