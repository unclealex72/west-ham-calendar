package controllers

import javax.inject.Inject

import json.JsonResults
import models.Globals
import play.api.mvc._
import securesocial.core.Authorization
import services.GameRowFactory
import uk.co.unclealex.hammers.calendar.dao.Transactional
class Application @Inject() (
  /**
   * The authorization object to check for authorised users.
   */
  val authorization: Authorization,
  /**
   * The transactional object used to get games and seasons.
   */
  tx: Transactional,
  /**
   * The game row factory used to get game row models.
   */
  gameRowFactory: GameRowFactory) extends Controller with Secure with TicketForms with JsonResults {

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
      val username = emailAndName.map(_._2)
      Globals(seasons.toList, username)
    }
    Ok(views.js.constants(constants)).withHeaders(PRAGMA -> "no-cache")
  }

  def games(season: Int) = UserAwareAction { implicit request =>
    val includeAttended = emailAndName.isDefined
    json {
      tx { gameDao =>
        Map("games" -> gameDao.getAllForSeason(season).map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory))) }
    }
  }

  def game(id: Long) = UserAwareAction { implicit request =>
    val includeAttended = emailAndName.isDefined
    json {
      tx { gameDao =>
        gameDao.findById(id).map(gameRowFactory.toRow(includeAttended, ticketFormUrlFactory(request))) }
    }
  }
}