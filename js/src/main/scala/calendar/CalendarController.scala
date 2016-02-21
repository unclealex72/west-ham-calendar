package calendar

import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, injectable}
import models.EntryRel._
import models.GameRowRel._
import models.SeasonRel.MONTHS
import models._
import monads.FL

import scala.concurrent.ExecutionContext.Implicits.global
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
class CalendarController(scope: CalendarScope, ajax: AjaxService) extends AbstractController[CalendarScope](scope){

  FL {
    for {
      entry <- FL <~ ajax.get[Entry]("/entry")
      seasonsLink <- FL <~? entry.links(SEASONS)
      seasons <- FL <~ ajax.get[Seasons](seasonsLink)
      latestSeason <- FL <~? seasons.lastOption
      monthsUrl <- FL <~? latestSeason.links(MONTHS)
      months <- FL <~ ajax.get[Months](monthsUrl)
    } yield {
      val monthViews = months.toSeq.map { month =>
        val date = new js.Date(month.year - 1900, month.month - 1, 1)
        val gameViews = month.games.toSeq.map { game =>
          GameView(
            new Date(game.at.toString),
            game.competition.name,
            game.links(COMPETITION_LOGO).orUndefined,
            Some(game.opponents).filter(_ => game.location.isAway).orUndefined,
            game.result.map(_.score.home).orUndefined,
            game.links(HOME_LOGO).orUndefined,
            Some(game.opponents).filter(_ => game.location.isHome).orUndefined,
            game.result.map(_.score.away).orUndefined,
            game.links(AWAY_LOGO).orUndefined
          )
        }
        new MonthView(date, gameViews.toJSArray)
      }
      scope.$apply {
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
}

@JSExport
case class MonthView(val date: js.Date, val games: js.Array[GameView])

@JSExport
case class GameView(
                     val datePlayed: js.Date,
                     val competition: String,
                     val competitionLogo: js.UndefOr[String],
                     val homeTeam: js.UndefOr[String],
                     val homeScore: js.UndefOr[Int],
                     val homeTeamLogo: js.UndefOr[String],
                     val awayTeam: js.UndefOr[String],
                     val awayScore: js.UndefOr[Int],
                     val awayTeamLogo: js.UndefOr[String]
                   )