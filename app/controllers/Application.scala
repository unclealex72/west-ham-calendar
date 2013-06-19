package controllers

import play.api._
import play.api.mvc._
import javax.inject.Inject
import uk.co.unclealex.hammers.calendar.dao.Transactional
import services.GameRowFactory
import uk.co.unclealex.hammers.calendar.update.MainUpdateService

class Application @Inject() (
  /**
   * The transactional object used to get games and seasons.
   */
  tx: Transactional,
  /**
   * The game row factory used to get game row models.
   */
  gameRowFactory: GameRowFactory) extends Controller with Json {

  /**
   * Redirect to the  homepage.
   */
  def index = Action {
    Ok(views.html.index())
  }

  val yearMapper: Int => Map[String, Int] = year => Map("year" -> year)

  /**
   * Get all seasons
   */
  def seasons = json {
    tx { gameDao => gameDao.getAllSeasons.toList.map(yearMapper) }
  }

  def latestSeason = json {
    tx { gameDao => gameDao.getLatestSeason.map(yearMapper) }
  }

  def games(season: Int) = json {
    tx { gameDao => gameDao.getAllForSeason(season).map(gameRowFactory.toRow _) }
  }
}