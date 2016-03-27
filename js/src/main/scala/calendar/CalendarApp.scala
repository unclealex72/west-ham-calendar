package calendar

/**
  * Created by alex on 07/02/16.
  */

import com.greencatsoft.angularjs.{Config, Angular}
import com.greencatsoft.angularjs.core.{Route, RouteProvider, LocationProvider}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object CalendarApp extends JSApp {

  override def main() {
    val dependencies = Seq(
      "mgcrea.ngStrap" -> Seq("", ".helpers.dimensions", ".helpers.debounce"),
      "ng" -> Seq("Animate"))
    val module =
      Angular.module("hammersCalendar",
        dependencies.flatMap { case (prefix, suffices) => suffices.map(suffix => s"$prefix$suffix") })
    module
      .controller[CalendarController]
      .factory[AjaxServiceFactory]
      .factory[AttendanceServiceFactory]
      .factory[DropdownProviderFactory]
      .filter[CustomDateFilter]
      .filter[OpponentsFilter]
      .filter[JsonDecodeFilter]
      .config[CalendarConfig]
  }
}

class CalendarConfig(locationProvider: LocationProvider) extends Config {
  locationProvider.html5Mode(true)
}