package calendar.controllers

import calendar.services.{AjaxService, WatcherService}
import calendar.views.{GameView, JsTicketType, MonthView}
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, FilterService, injectable}
import models.EntryRel.{LOGIN, LOGOUT}
import models.TicketType.PriorityPointTicketType
import models.{Entry, TicketType}
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
@injectable("CalendarController")
class CalendarController(
                          $scope: CalendarScope,
                          filterService: FilterService,
                          ajax: AjaxService,
                          watcher: WatcherService) extends AbstractController[CalendarScope]($scope) {

  for {
    entry <- FL <~ ajax.get[Entry]("/entry")
    seasons <- FL <~ entry.seasons
    season <- FL <~ seasons.lastOption.toList
  } yield {
    val months = season.months
    def monthIdFactory(date: Date): String = {
      s"Month${filterService("date").call($scope, date, "MMMM")}"
    }
    val monthViews = months.toSeq.map(MonthView.apply(monthIdFactory, PriorityPointTicketType)).toJSArray
    $scope.$apply {
      $scope.user = entry.user.orUndefined
      $scope.authenticationLink = entry.links(LOGIN).orElse(entry.links(LOGOUT)).orUndefined
      $scope.currentSeason = season.season
      $scope.seasons = seasons.map(_.season).toJSArray
      val ticketTypes = TicketType.values.map(JsTicketType(_))
      $scope.currentTicketType = ticketTypes.filter(_.is(PriorityPointTicketType)).head
      $scope.ticketTypes = ticketTypes.toJSArray
      $scope.months = monthViews
      $scope.games = monthViews.flatMap(monthView => monthView.games)
      $scope.alterSeason = (season: Int) => {
        $scope.currentSeason = season
      }
      $scope.alterTicketType = (ticketType: JsTicketType) => {
        $scope.currentTicketType = ticketType
      }
      watcher.onEach($scope).listen(_.currentSeason).fire { newSeason => oldSeason =>
        println(s"$newSeason $oldSeason")
      }.listen(_.currentTicketType).fire { newTicketType => oldTicketType =>
        println(s"${newTicketType.key} ${oldTicketType.key}")
      }.go()
    }
  }
}

@js.native
trait CalendarScope extends Scope {

  var months: js.Array[MonthView] = js.native
  var games: js.Array[GameView] = js.native
  var seasons: js.Array[Int] = js.native
  var currentSeason: Int = js.native
  var user: js.UndefOr[String] = js.native
  var authenticationLink: js.UndefOr[String] = js.native
  var alterAttendance: js.Function2[MonthView, Int, Future[Unit]] = js.native
  var alterSeason: js.Function1[Int, Unit] = js.native
  var alterTicketType: js.Function1[JsTicketType, Unit] = js.native
  var ticketTypes: js.Array[JsTicketType] = js.native
  var currentTicketType: JsTicketType = js.native
}