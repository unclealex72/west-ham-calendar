package calendar

import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, injectable}
import dates.SharedDate
import models.EntryRel._
import models.GameRowRel._
import models.SeasonRel.MONTHS
import models._
import monads.{FO, FL}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.{ScalaJSDefined, JSExport}
import scalaz.Scalaz._
import scala.scalajs.js.JSConverters._

/**
  * Created by alex on 17/02/16.
  */
@JSExport
@injectable("CalendarController")
class CalendarController(scope: CalendarScope, ajax: AjaxService, attendanceService: AttendanceService) extends AbstractController[CalendarScope](scope){

  FL {
    for {
      entry <- FL <~ ajax.get[Entry]("/entry")
      seasonsLink <- FL <~? entry.links(SEASONS)
      seasons <- FL <~ ajax.get[Seasons](seasonsLink)
      latestSeason <- FL <~? seasons.lastOption
      monthsUrl <- FL <~? latestSeason.links(MONTHS)
      months <- FL <~ ajax.get[Months](monthsUrl)
    } yield {
      val monthViews = months.toSeq.map(MonthView.apply).toJSArray
      scope.$apply {
        scope.alterAttendance = (monthView: MonthView, idx: Int) => FO.unit {
          for {
            url <- FO <~ monthView.games(idx).attendedUrl.toOption
            gameRow <- FO <~< attendanceService.alterAttendance(url)
          } yield {
            scope.$apply {
              monthView.games.update(idx, GameView(gameRow, idx))
            }
          }
        }
        scope.user = entry.user.orUndefined
        scope.authenticationLink = entry.links(LOGIN).orElse(entry.links(LOGOUT)).orUndefined
        scope.season = latestSeason.season
        scope.months = monthViews.toJSArray
      }
    }
  }

}

@js.native
trait CalendarScope extends Scope {

  var months: js.Array[MonthView] = js.native
  var season: Int = js.native
  var user: js.UndefOr[String] = js.native
  var authenticationLink: js.UndefOr[String] = js.native
  var alterAttendance: js.Function2[MonthView, Int, Future[Unit]]
}

@ScalaJSDefined
trait HasOpponents extends js.Object {
  def hasOpponents(prefix: String): Boolean
}

@ScalaJSDefined
class MonthView(
  var date: js.Date,
  var games: js.Array[GameView]) extends js.Object with HasOpponents {

  def hasOpponents(prefix: String): Boolean = games.exists(_.hasOpponents(prefix))
}
object MonthView {
  def apply(month: Month): MonthView = {
    val date = new js.Date(month.year - 1900, month.month - 1, 1)
    val gameViews = month.games.toSeq.zipWithIndex.map { gameRowAndIndex => GameView(gameRowAndIndex._1, gameRowAndIndex._2) }
    new MonthView(date, gameViews.toJSArray)
  }
}

@ScalaJSDefined
class GameView(
                var idx: Int,
                var datePlayed: js.Date,
                var competition: String,
                var competitionLogo: js.UndefOr[String],
                var opponents: String,
                var homeTeam: js.UndefOr[String],
                var homeScore: js.UndefOr[Int],
                var homeTeamLogo: js.UndefOr[String],
                var awayTeam: js.UndefOr[String],
                var awayScore: js.UndefOr[Int],
                var awayTeamLogo: js.UndefOr[String],
                var attended: Boolean,
                var showAttended: Boolean,
                var attendedUrl: js.UndefOr[String]) extends js.Object with HasOpponents {

  def hasOpponents(prefix: String): Boolean = opponents.toLowerCase.startsWith(prefix.toLowerCase)
}
object GameView {
  def apply(gameRow: GameRow, idx: Int): GameView = {
    val attendedUrl = gameRow.links(ATTEND).orElse(gameRow.links(UNATTEND))
    new GameView(
      idx,
      new js.Date(js.Date.parse(gameRow.at.toString)),
      gameRow.competition.name,
      gameRow.links(COMPETITION_LOGO).orUndefined,
      gameRow.opponents,
      Some(gameRow.opponents).filter(_ => gameRow.location.isAway).orUndefined,
      gameRow.result.map(_.score.home).orUndefined,
      gameRow.links(HOME_LOGO).orUndefined,
      Some(gameRow.opponents).filter(_ => gameRow.location.isHome).orUndefined,
      gameRow.result.map(_.score.away).orUndefined,
      gameRow.links(AWAY_LOGO).orUndefined,
      gameRow.attended.getOrElse(false),
      attendedUrl.isDefined,
      attendedUrl.orUndefined)
  }
}