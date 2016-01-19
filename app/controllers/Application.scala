package controllers

import javax.inject.Inject

import dao.Transactional
import json.JsonResults
import models.Globals
import play.api.i18n.MessagesApi
import play.api.mvc._
import security.Definitions._
import services.GameRowFactory

case class Application @Inject() (
                              /**
   * The authorization object to check for authorised users.
   */
                              authorization: Auth,
                              /**
   * The transactional object used to get games and seasons.
   */
                              tx: Transactional,
                              /**
   * The game row factory used to get game row models.
   */
                              gameRowFactory: GameRowFactory,
                              messagesApi: MessagesApi, env: Env) extends Secure with TicketForms with JsonResults {

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
  def constants = UserAwareAction { implicit request =>
    val constants = tx { gameDao =>
      val seasons = gameDao.getAllSeasons
      val username = emailAndUsername.map(_.name)
      Globals(seasons.toList, username)
    }
    Ok(views.js.constants(constants)).withHeaders(
      PRAGMA -> "no-cache", CACHE_CONTROL -> "no-cache, no-store, must-revalidate", EXPIRES -> "0")
  }

  def games(season: Int) = UserAwareAction { implicit request =>
    val includeAttended = request.identity.isDefined
    json {
      tx { gameDao =>
        Map("games" -> gameDao.getAllForSeason(season).map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory))) }
    }
  }

  def game(id: Long) = UserAwareAction { implicit request =>
    val includeAttended = request.identity.isDefined
    json {
      tx { gameDao =>
        gameDao.findById(id).map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory(request))) }
    }
  }
}