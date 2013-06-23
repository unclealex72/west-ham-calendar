package controllers

import play.api._
import play.api.mvc._
import javax.inject.Inject
import uk.co.unclealex.hammers.calendar.dao.Transactional
import services.GameRowFactory
import uk.co.unclealex.hammers.calendar.update.MainUpdateService
import securesocial.core.SecureSocial
import securesocial.core.Authorization
import securesocial.core.RequestWithUser

class Application @Inject() (
  /**
   * The authorization object to check for authorised users.
   */
  authorization: Authorization,
  /**
   * The transactional object used to get games and seasons.
   */
  tx: Transactional,
  /**
   * The game row factory used to get game row models.
   */
  gameRowFactory: GameRowFactory) extends Controller with Secure with Json {

  implicit val implicitAuthorization = authorization

  /**
   * Redirect to the  homepage.
   */
  def index = Action {
    Ok(views.html.index())
  }

  def yearMapper(parameters: Pair[String, Any]*): Int => Map[String, Any] =
    year => Map("year" -> year) ++ parameters

  /**
   * Get all seasons
   */
  def seasons = json {
    tx { gameDao => gameDao.getAllSeasons.toList.map(yearMapper()) }
  }

  /**
   * Get the base information required: the latest season and the email of the logged in user if present.
   */
  def base = UserAwareAction { implicit request =>
    val jsonFactory = emailAndName match {
      case Some((email, name)) => yearMapper("name" -> name)
      case None => yearMapper()
    }
    json {
      tx { gameDao => gameDao.getLatestSeason.map(jsonFactory) }
    }(request)
  }

  def games(season: Int) = UserAwareAction { implicit request =>
    val includeAttended = emailAndName.isDefined
    json {
      tx { gameDao => gameDao.getAllForSeason(season).map(gameRowFactory.toRow(includeAttended)) }
    }(request)
  }
}