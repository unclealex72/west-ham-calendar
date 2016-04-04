package calendar

import dates.SharedDate
import models.GameRowRel.{LOCATION, MATCH_REPORT, _}
import models.TicketingInformationRel.FORM
import models._

import scala.scalajs.js
import scala.scalajs.js.{Date, JSON}
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.JSConverters._

/**
  * Created by alex on 01/04/16.
  */
package object views {

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
      val date = new js.Date(month.date.year - 1900, month.date.month - 1, 1)
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
                  var homeTeam: TeamView,
                  var awayTeam: TeamView,
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
        gameRow.competitionLogoClass.orUndefined,
        gameRow.opponents,
        gameRow.result.isDefined,
        gameRow.result.flatMap(_.shootoutScore).isDefined,
        TeamView(gameRow, _.isHome, gameRow.homeTeamLogoClass, _.home),
        TeamView(gameRow, _.isAway, gameRow.awayTeamLogoClass, _.away),
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
  class TeamView(var name: js.UndefOr[String],
                 var logo: js.UndefOr[String],
                 var score: js.UndefOr[Int],
                 var shootout: js.UndefOr[Int]) extends js.Object {
    val isWestHam: Boolean = name.isEmpty
  }

  object TeamView {
    def apply(gameRow: GameRow, homeFactory: Location => Boolean, logo: Option[String], scoreFactory: Score => Int): TeamView = {
      val optionalScore = gameRow.result.map(result => scoreFactory(result.score))
      val optionalShootoutScore = gameRow.result.flatMap(result => result.shootoutScore.map(scoreFactory))
      new TeamView(
        Some(gameRow.opponents).filterNot(_ => homeFactory(gameRow.location)).orUndefined,
        logo.orUndefined,
        optionalScore.orUndefined,
        optionalShootoutScore.orUndefined)
    }
  }
}