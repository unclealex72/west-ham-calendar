package controllers

import javax.inject.Inject

import dates.ZonedDateTimeFactory
import location.LocationService
import play.api.mvc.{AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class Location @Inject() (
                           val locationService: LocationService,
                           override val controllerComponents: ControllerComponents,
                           override val zonedDateTimeFactory: ZonedDateTimeFactory,
                           override implicit val ec: ExecutionContext) extends AbstractController(controllerComponents, zonedDateTimeFactory, ec) {

  def location(gameId: Long) = Action.async { implicit request: Request[AnyContent] =>
    fo(locationService.location(gameId)) { url =>
      TemporaryRedirect(url.toString)
    }
  }
}