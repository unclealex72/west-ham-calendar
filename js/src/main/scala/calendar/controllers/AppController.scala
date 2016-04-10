package calendar.controllers

import calendar.views.{JsTicketType, MonthView}
import calendar.Dropdown
import calendar.services.AjaxService
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, FilterService, injectable}
import models.EntryRel.{LOGIN, LOGOUT, SEASONS}
import models.SeasonRel.MONTHS
import models.TicketType.PriorityPointTicketType
import models.{Entry, Months, Seasons}
import monads.FL
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport
import scalaz.Scalaz._

/**
  * Created by alex on 01/04/16.
  */
@JSExport
@injectable("AppController")
class AppController(scope: AppScope, filterService: FilterService, ajax: AjaxService) extends AbstractController[AppScope](scope) {

  for {
    entry <- FL <~ ajax.get[Entry]("/entry")
    seasonsLink <- FL <~? entry.links(SEASONS)
    seasons <- FL <~ ajax.get[Seasons](seasonsLink)
    selectedSeason <- FL <~? seasons.lastOption
    monthsUrl <- FL <~? selectedSeason.links(MONTHS)
    months <- FL <~ ajax.get[Months](monthsUrl)
  } yield {
    def monthIdFactory(date: Date): String = {
      s"Month${filterService("date").call(scope, date, "MMMM")}"
    }
    val monthViews = months.toSeq.map(MonthView.apply(monthIdFactory, PriorityPointTicketType)).toJSArray
    scope.$apply {
      scope.user = entry.user.orUndefined
      scope.authenticationLink = entry.links(LOGIN).orElse(entry.links(LOGOUT)).orUndefined
      scope.season = selectedSeason.season
      scope.months = monthViews.toJSArray
    }
  }
}

@js.native
trait AppScope extends Scope {

  var months: js.Array[MonthView] = js.native
  var season: Int = js.native
  var user: js.UndefOr[String] = js.native
  var authenticationLink: js.UndefOr[String] = js.native
  var alterAttendance: js.Function2[MonthView, Int, Future[Unit]]
  var seasonsDropdown: js.Array[Dropdown] = js.native
  var ticketTypesDropdown: js.Array[Dropdown] = js.native
  var ticketType: JsTicketType = js.native
  var scrollTo: js.Function1[String, Unit]
}