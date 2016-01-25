package controllers

import javax.inject.Inject

import dao.GameDao
import json.JsonResults
import models.Globals
import play.api.i18n.MessagesApi
import play.api.mvc._
import security.Definitions._
import services.GameRowFactory

import scala.concurrent.ExecutionContext

case class Application @Inject() (
                              /**
   * The authorization object to check for authorised users.
   */
                              authorization: Auth,
                              /**
   * The transactional object used to get games and seasons.
   */
                              gameDao: GameDao,
                              /**
   * The game row factory used to get game row models.
   */
                              gameRowFactory: GameRowFactory,
                              messagesApi: MessagesApi, env: Env)(implicit ec: ExecutionContext) extends Secure with TicketForms with JsonResults {

  implicit val implicitAuthorization = authorization

  /**
   * Redirect to the  homepage.
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
   * Get the global information required: all seasons, all ticket types and the email of the logged in user if present.
   */
  def constants = UserAwareAction.async { implicit request =>
    val futureGlobals = gameDao.getAllSeasons.map { seasons =>
      val username = emailAndUsername.map(_.name)
      Globals(seasons.toList, username)
    }
    futureGlobals.map { globals =>
      Ok(views.js.constants(globals)).withHeaders(
        PRAGMA -> "no-cache", CACHE_CONTROL -> "no-cache, no-store, must-revalidate", EXPIRES -> "0")
    }
  }

  def games(season: Int) = UserAwareAction.async { implicit request =>
    val includeAttended = request.identity.isDefined
    gameDao.getAllForSeason(season).map { games =>
      json {
        Map("games" -> games.map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory)))
      }
    }
  }

  def game(id: Long) = UserAwareAction.async { implicit request =>
    val includeAttended = request.identity.isDefined
    gameDao.findById(id).map { game =>
      json {
          game.map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory(request)))
      }
    }
  }
}