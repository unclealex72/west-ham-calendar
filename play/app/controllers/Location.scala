package controllers

import location.LocationService
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scaldi.{Injector, Injectable}

class Location(implicit injector: Injector) extends Controller with Injectable {

  val locationService: LocationService = inject[LocationService]

  def location(gameId: Long) = Action.async {
    locationService.location(gameId).map {
      case Some(url) => TemporaryRedirect(url.toString)
      case _ => NotFound
    }
  }

  /**
   * Redirect to the  homepage.
   */
  def index = Action {
    Ok(views.html.index(true))
  }

}