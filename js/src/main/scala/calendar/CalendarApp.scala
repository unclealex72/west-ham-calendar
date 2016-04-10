package calendar

/**
  * Created by alex on 07/02/16.
  */

import calendar.controllers.AppController
import calendar.directives._
import calendar.filters.{CustomDateFilter, JsonDecodeFilter, OpponentsFilter}
import calendar.services.{AjaxServiceFactory, AttendanceServiceFactory, WatcherServiceFactory}
import com.greencatsoft.angularjs.{Angular, Config}
import com.greencatsoft.angularjs.core.{LocationProvider, Route, RouteProvider}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object CalendarApp extends JSApp {

  override def main() {
    val module = Angular.module("hammersCalendar", Seq("ui.materialize"))
    module
      // Directives
      .directive[AppDirective]
      .directive[NavDirective]
      .directive[ContentDirective]
      .directive[GameDirective]
      .directive[TeamNameDirective]
      .directive[TeamLogoDirective]
      .directive[ResultDirective]
      // Controllers
      .controller[CalendarController]
      .controller[AppController]
      // Services
      .factory[AjaxServiceFactory]
      .factory[AttendanceServiceFactory]
      .factory[DropdownProviderFactory]
      .factory[WatcherServiceFactory]
      // Filters
      .filter[CustomDateFilter]
      .filter[OpponentsFilter]
      .filter[JsonDecodeFilter]
      // Config
      .config[CalendarConfig]
  }
}

class CalendarConfig(locationProvider: LocationProvider) extends Config {
  locationProvider.html5Mode(true)
}