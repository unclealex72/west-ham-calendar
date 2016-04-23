package calendar.controllers

import calendar.services.{AjaxService, WatcherService}
import calendar.views.{GameView, JsTicketType, MonthView, SeasonView}
import com.greencatsoft.angularjs.core.{Scope, Timeout}
import com.greencatsoft.angularjs.{AbstractController, FilterService, injectable}
import models.EntryRel.{LOGIN, LOGOUT}
import models.TicketType.PriorityPointTicketType
import models.{Entry, TicketType}
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport

/**
  * Created by alex on 01/04/16.
  */
@JSExport
@injectable("CalendarController")
class CalendarController(
                          $scope: CalendarScope,
                          filterService: FilterService,
                          ajax: AjaxService,
                          timeout: Timeout,
                          watcher: WatcherService) extends AbstractController[CalendarScope]($scope) {

  for {
    entry <- ajax.get[Entry]("/entry")
  } yield {
    val seasonViews = entry.seasons.toSeq.reverse.map(SeasonView(_))
    $scope.$apply {
      $scope.user = entry.user.orUndefined
      $scope.authenticationLink = entry.links(if (entry.user.isDefined) LOGOUT else LOGIN).orUndefined
      val ticketTypes = TicketType.values.map(JsTicketType(_))
      $scope.currentTicketType = ticketTypes.filter(_.is(PriorityPointTicketType)).head
      $scope.ticketTypes = ticketTypes.toJSArray
      $scope.seasons = seasonViews.toJSArray
      $scope.currentSeason = seasonViews.head
      $scope.currentMonth = $scope.currentSeason.months.headOption.orUndefined
      $scope.currentOpponents = None.orUndefined
      populateOpponents($scope)
      populateGames($scope)
      $scope.alterMonth = (month: MonthView) => {
        $scope.currentMonth = month
        $scope.currentMonth.foreach { month =>
          $scope.currentOpponents = None.orUndefined
          populateGames($scope)
        }
      }
      $scope.alterOpponents = (opponents: String) => {
        $scope.currentOpponents = opponents
        $scope.currentOpponents.foreach { opponents =>
          $scope.currentMonth = None.orUndefined
        }
        populateGames($scope)

      }
      $scope.alterSeason = (season: SeasonView) => {
        $scope.currentSeason = season
        populateOpponents($scope)
        $scope.alterMonth($scope.currentSeason.months.head)
      }
      $scope.alterTicketType = (ticketType: JsTicketType) => {
        $scope.currentTicketType = ticketType
      }
    }
  }

  def populateOpponents($scope: CalendarScope): Unit = {
    val possibleOpponents = for {
      month <- $scope.currentSeason.months.toSet[MonthView]
      game <- month.games.toSet[GameView]
    } yield {
      game.opponents
    }
    $scope.possibleOpponents = possibleOpponents.toJSArray
  }

  def populateGames($scope: CalendarScope): Unit = {
    $scope.currentMonth.foreach { currentMonth =>
      $scope.currentGames = currentMonth.games
    }
    $scope.currentOpponents.foreach { currentOpponents =>
      $scope.currentGames = for {
        month <- $scope.currentSeason.months
        game <- month.games if game.opponents == currentOpponents
      } yield {
        game
      }
    }
  }
}

@js.native
trait CalendarScope extends Scope {

  var seasons: js.Array[SeasonView] = js.native
  var possibleOpponents: js.Array[String] = js.native
  var ticketTypes: js.Array[JsTicketType] = js.native
  var currentMonth: js.UndefOr[MonthView] = js.native
  var currentSeason: SeasonView = js.native
  var currentGames: js.Array[GameView] = js.native
  var currentOpponents: js.UndefOr[String] = js.native
  var currentTicketType: JsTicketType = js.native

  var user: js.UndefOr[String] = js.native

  var authenticationLink: js.UndefOr[String] = js.native

  var alterSeason: js.Function1[SeasonView, Unit] = js.native
  var alterMonth: js.Function1[MonthView, Unit] = js.native
  var alterOpponents: js.Function1[String, Unit] = js.native
  var alterTicketType: js.Function1[JsTicketType, Unit] = js.native
}