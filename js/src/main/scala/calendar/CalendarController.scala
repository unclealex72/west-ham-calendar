package calendar

import com.greencatsoft.angularjs.core.{Location => AngularLocation, AnchorScroll, RouteParams, Route, Scope}
import com.greencatsoft.angularjs.{FilterService, AbstractController, injectable}
import dates.SharedDate
import models.EntryRel._
import models.GameRowRel._
import models.SeasonRel.MONTHS
import models.TicketType.PriorityPointTicketType
import models.TicketingInformationRel.FORM
import models._
import monads.{FO, FL}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.{JSON, Date}
import scala.scalajs.js.annotation.{ScalaJSDefined, JSExport}
import scalaz.Scalaz._
import scala.scalajs.js.JSConverters._
/**
  * Created by alex on 17/02/16.
  */
@JSExport
@injectable("CalendarController")
class CalendarController(
                          scope: CalendarScope,
                          ajax: AjaxService,
                          angularLocation: AngularLocation,
                          attendanceService: AttendanceService,
                          location: AngularLocation,
                          anchorScroll: AnchorScroll,
                          filterService: FilterService) extends AbstractController[CalendarScope](scope){

  loadGames(None, None)

  def loadGames(oTicketTypeName: Option[String], oSeason: Option[Int]) = FL {
    // Nasty little hack to get the parameters of the URL
    //val oParams = angularLocation.asInstanceOf[js.Dictionary[js.Any]].get("$$search").map(_.asInstanceOf[js.Dictionary[String]])
    def selectedSeasonFactory(seasons: Seasons): Option[Season] = {
      for {
        year <- oSeason
        season <- seasons.find(_.season == year)
      } yield season
    }.orElse(seasons.lastOption)
    val ticketType: TicketType = (for {
      ticketTypeName <- oTicketTypeName
      ticketType <- TicketType.withNameOption(ticketTypeName)
    } yield ticketType).getOrElse(PriorityPointTicketType)
    for {
      entry <- FL <~ ajax.get[Entry]("/entry")
      seasonsLink <- FL <~? entry.links(SEASONS)
      seasons <- FL <~ ajax.get[Seasons](seasonsLink)
      selectedSeason <- FL <~? selectedSeasonFactory(seasons)
      monthsUrl <- FL <~? selectedSeason.links(MONTHS)
      months <- FL <~ ajax.get[Months](monthsUrl)
    } yield {
      def monthIdFactory(date: Date): String = {
        filterService("date").call(scope, date, "MMMM").toString
      }
      val monthViews = months.toSeq.map(MonthView.apply(monthIdFactory, ticketType)).toJSArray
      scope.$apply {
        scope.alterAttendance = (monthView: MonthView, idx: Int) => FO.unit {
          for {
            url <- FO <~ monthView.games(idx).attendedUrl.toOption
            gameRow <- FO <~< attendanceService.alterAttendance(url)
          } yield {
            scope.$apply {
              monthView.games.update(idx, GameView(ticketType)(gameRow, idx))
            }
          }
        }
        scope.user = entry.user.orUndefined
        scope.authenticationLink = entry.links(LOGIN).orElse(entry.links(LOGOUT)).orUndefined
        scope.season = selectedSeason.season
        scope.months = monthViews.toJSArray
        scope.seasonsDropdown = seasons.toSeq.reverse.map { season =>
          new Dropdown(season.season.toString, () => changeSeason(season.season))
        }.toJSArray
        scope.ticketType = JsTicketType(ticketType)
        scope.ticketTypesDropdown = TicketType.values.map { ticketType =>
          new Dropdown(ticketType.label, () => changeTicketType(ticketType.name))
        }.toJSArray
        scope.scrollTo = (id: String) => {
          location.hash(id)
          anchorScroll()
        }
      }
    }
  }

  @JSExport
  def changeTicketType(ticketTypeName: String): Unit = {
    loadGames(Some(ticketTypeName), Some(scope.season))
  }

  @JSExport
  def changeSeason(season: Int): Unit = {
    loadGames(Some(scope.ticketType.key), Some(season))
  }
}

@js.native
trait CalendarScope extends Scope {

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

@ScalaJSDefined
class JsTicketType(val ordering: Int, val label: String, val key: String) extends js.Object
object JsTicketType {
  def apply(tt: TicketType): JsTicketType = new JsTicketType(TicketType.indexOf(tt), tt.label, tt.entryName)
}
@ScalaJSDefined
trait HasOpponents extends js.Object {
  def hasOpponents(prefix: String): Boolean
}

@ScalaJSDefined
class MonthView(
  var id: String,
  var date: js.Date,
  var games: js.Array[GameView]) extends js.Object with HasOpponents {

  def hasOpponents(prefix: String): Boolean = games.exists(_.hasOpponents(prefix))
}
object MonthView {
  def apply(idFactory: Date => String, ticketType: TicketType)(month: Month): MonthView = {
    val date = new js.Date(month.year - 1900, month.month - 1, 1)
    val gameViews = month.games.toSeq.zipWithIndex.map { gameRowAndIndex => GameView(ticketType)(gameRowAndIndex._1, gameRowAndIndex._2) }
    new MonthView(idFactory(date), date, gameViews.toJSArray)
  }
}

@ScalaJSDefined
class GameView(
                var idx: Int,
                var datePlayed: js.Date,
                var competition: String,
                var competitionLogo: js.UndefOr[String],
                var opponents: String,
                var hasResult: Boolean,
                var hasShootout: Boolean,
                var homeTeam: js.UndefOr[String],
                var homeScore: js.UndefOr[Int],
                var homeShootout: js.UndefOr[Int],
                var homeTeamLogo: js.UndefOr[String],
                var awayTeam: js.UndefOr[String],
                var awayScore: js.UndefOr[Int],
                var awayShootout: js.UndefOr[Int],
                var awayTeamLogo: js.UndefOr[String],
                var attended: Boolean,
                var showAttended: Boolean,
                var attendedUrl: js.UndefOr[String],
                var ticketsDate: js.UndefOr[js.Date],
                var ticketsUrl: js.UndefOr[String],
                var matchReport: js.UndefOr[String],
                var locationUrl: js.UndefOr[String]) extends js.Object with HasOpponents {

  def hasOpponents(prefix: String): Boolean = opponents.toLowerCase.startsWith(prefix.toLowerCase)
}
object GameView {
  def apply(ticketType: TicketType)(gameRow: GameRow, idx: Int): GameView = {
    val attendedUrl = gameRow.links(ATTEND).orElse(gameRow.links(UNATTEND))
    val ticketingInfo = gameRow.tickets.get(ticketType).map { ticketingInformation =>
      (ticketingInformation.at, ticketingInformation.links(FORM))
    }
    def sharedDateToJsDate(sd: SharedDate): js.Date = new js.Date(js.Date.parse(sd.toString))
    new GameView(
        idx,
        sharedDateToJsDate(gameRow.at),
        gameRow.competition.name,
        gameRow.links(COMPETITION_LOGO).orUndefined,
        gameRow.opponents,
        gameRow.result.isDefined,
        gameRow.result.flatMap(_.shootoutScore).isDefined,
        Some(gameRow.opponents).filter(_ => gameRow.location.isAway).orUndefined,
        gameRow.result.map(_.score.home).orUndefined,
        gameRow.result.flatMap(_.shootoutScore).map(_.home).orUndefined,
        gameRow.links(HOME_LOGO).map(JSON.stringify(_)).orUndefined,
        Some(gameRow.opponents).filter(_ => gameRow.location.isHome).orUndefined,
        gameRow.result.map(_.score.away).orUndefined,
        gameRow.result.flatMap(_.shootoutScore).map(_.away).orUndefined,
        gameRow.links(AWAY_LOGO).map(JSON.stringify(_)).orUndefined,
        gameRow.attended.getOrElse(false),
        attendedUrl.isDefined,
        attendedUrl.orUndefined,
        ticketingInfo.map(_._1).map(sharedDateToJsDate).orUndefined,
        ticketingInfo.flatMap(_._2).map(JSON.stringify(_)).orUndefined,
        gameRow.links(MATCH_REPORT).map(JSON.stringify(_)).orUndefined,
        gameRow.links(LOCATION).map(JSON.stringify(_)).orUndefined)
  }
}

@ScalaJSDefined
class Dropdown(var text: String, var click: js.Function0[Unit]) extends js.Object