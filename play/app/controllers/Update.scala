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

import com.typesafe.scalalogging.slf4j.StrictLogging
import dao.GameDao
import dates.NowService
import logging.{Fatal, RemoteStream}
import model.{FatalError, Game}
import models.Competition.FRIENDLY
import models.GameRow._
import models.{Competition, FatalErrorReportRel}
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Concurrent
import play.api.mvc.Action
import security.Definitions._
import services.GameRowFactory
import update.MainUpdateService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
import scalaz._

/**
 * @author alex
 *
 */
class Update @javax.inject.Inject() (val secret: SecretToken,
                                     val mainUpdateService: MainUpdateService,
                                     val gameDao: GameDao,
                                     val gameRowFactory: GameRowFactory,
                                     val messagesApi: MessagesApi,
                                     val silhouette: DefaultSilhouette,
                                     val fatal: Fatal,
                                     implicit val nowService: NowService,
                                     implicit val ec: ExecutionContext
                                    ) extends Secure with Secret with LinkFactories with JsonResults with StrictLogging {

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
      val linkBuilder: FatalError => String = fe => fatalErrorReportLinksFactory(request)(fe).required(FatalErrorReportRel.MESSAGE)
      mainUpdateService.processDatabaseUpdates.map {
        case \/-(gameCount) =>
        channel.push(s"There are now $gameCount games.\n")
        case -\/(errors) =>
          fatal.fail(errors, linkBuilder)
      }.andThen {
        case Failure(t) =>
          fatal.fail("Updating all games failed", t, linkBuilder)
          logger.error("Updating all games failed.", t)
          remoteStream.log("Updating all games failed.", Some(t))
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
    silhouette.SecuredAction.async { implicit request =>
      gameUpdater(gameId).map {
        case Some(game) => json {
          gameRowFactory.toRow(includeAttended = true, gameRowLinksFactory(includeUpdates = true), ticketLinksFactory)(game)
        }
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

  def updateLogos(secretPayload: String) = Secret(secretPayload) {
    def findLogos(games: List[Game]): (Map[Option[String], String], Map[Competition, String]) = {
      val empty: (Map[Option[String], String], Map[Competition, String]) = (Map.empty, Map.empty)
      games.foldLeft(empty) { (result, game) =>
        val (logosByTeam, logosByCompetition) = result
        val newCompetitionLogo = game.competitionImageLink.map { competitionLogo =>
          game.competition -> competitionLogo
        }.filterNot(_ => game.competition == FRIENDLY)
        val newAwayLogo = game.awayTeamImageLink.map { awayTeamLogo =>
          val awayTeam = if (game.location.isHome) Some(game.opponents) else None
          awayTeam -> awayTeamLogo
        }
        val newHomeLogo = game.homeTeamImageLink.map { homeTeamLogo =>
          val homeTeam = if (game.location.isAway) Some(game.opponents) else None
          homeTeam -> homeTeamLogo
        }
        (logosByTeam ++ newAwayLogo ++ newHomeLogo, logosByCompetition ++ newCompetitionLogo)
      }
    }
    Action.async { implicit request =>
      gameDao.getAll.flatMap { games =>
        val (teamLogos, competitionLogos) = findLogos(games)
        val updatedGames = games.flatMap { game =>
          def logo(original: Option[String], newLogo: Option[String]): Option[String] = (original, newLogo) match {
            case (None, Some(logo)) => Some(logo)
            case _ => None
          }
          val newCompetitionLogo = logo(game.competitionImageLink, competitionLogos.get(game.competition))
          val teams: (Option[String], Option[String]) = (None, Some(game.opponents))
          val (homeTeam, awayTeam) = if (game.location.isHome) teams else teams.swap
          val newHomeLogo = logo(game.homeTeamImageLink, teamLogos.get(homeTeam))
          val newAwayLogo = logo(game.awayTeamImageLink, teamLogos.get(awayTeam))
          if (newCompetitionLogo.isDefined || newHomeLogo.isDefined || newAwayLogo.isDefined) {
            Some(game.copy(
              competitionImageLink = newCompetitionLogo.orElse(game.competitionImageLink),
              homeTeamImageLink = newHomeLogo.orElse(game.homeTeamImageLink),
              awayTeamImageLink = newAwayLogo.orElse(game.awayTeamImageLink)
            ))
          }
          else {
            None
          }
        }
        val updates = updatedGames.foldLeft(Future.successful[Any](0)) { (result, game) =>
          result.flatMap { _ => gameDao.store(game) }
        }
        updates.map { _ => Ok }
      }
    }
  }
}