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

    implicit class JsTicketTypeImplicits(jsTicketType: JsTicketType) {
      def is(ticketType: TicketType): Boolean = {
        jsTicketType.key == ticketType.entryName
      }
    }
  }

  @ScalaJSDefined
  trait HasOpponents extends js.Object {
    def hasOpponents(prefix: String): Boolean
  }

  @ScalaJSDefined
  class MonthView(
                   val id: String,
                   val date: js.Date,
                   var games: js.Array[GameView]) extends js.Object with HasOpponents {

    def hasOpponents(prefix: String): Boolean = games.exists(_.hasOpponents(prefix))
  }

  object MonthView {
    def apply(idFactory: Date => String, ticketType: TicketType)(month: Month): MonthView = {
      val date = new js.Date(month.date.year - 1900, month.date.month - 1, 1)
      val monthId = idFactory(date)
      val gameViews = month.games.toSeq.zipWithIndex.map {
        gameRowAndIndex => GameView(ticketType)(gameRowAndIndex._1, Some(monthId).filter(_ => gameRowAndIndex._2 == 0)) }
      new MonthView(monthId, date, gameViews.toJSArray)
    }
  }

  @ScalaJSDefined
  class GameView(
                  val monthId: js.UndefOr[String],
                  val datePlayed: js.Date,
                  val competition: String,
                  val competitionLogo: js.UndefOr[String],
                  val opponents: String,
                  val hasResult: Boolean,
                  val hasShootout: Boolean,
                  val homeTeam: TeamView,
                  val awayTeam: TeamView,
                  var attended: Boolean,
                  val showAttended: Boolean,
                  val attendUrl: js.UndefOr[String],
                  val unattendUrl: js.UndefOr[String],
                  val ticketsDate: js.UndefOr[js.Date],
                  val ticketsUrl: js.UndefOr[String],
                  val matchReport: js.UndefOr[String],
                  val locationUrl: js.UndefOr[String]) extends js.Object with HasOpponents {

    def hasOpponents(prefix: String): Boolean = opponents.toLowerCase.startsWith(prefix.toLowerCase)
  }

  object GameView {
    def apply(ticketType: TicketType)(gameRow: GameRow, monthId: Option[String]): GameView = {
      val attendUrl = gameRow.links(ATTEND)
      val unattendUrl = gameRow.links(UNATTEND)
      val ticketingInfo = gameRow.tickets.get(ticketType).map { ticketingInformation =>
        (ticketingInformation.at, ticketingInformation.links(FORM))
      }
      def sharedDateToJsDate(sd: SharedDate): js.Date = new js.Date(js.Date.parse(sd.toString))
      new GameView(
        monthId.orUndefined,
        sharedDateToJsDate(gameRow.at),
        gameRow.competition.name,
        gameRow.competitionLogoClass.orUndefined,
        gameRow.opponents,
        gameRow.result.isDefined,
        gameRow.result.flatMap(_.shootoutScore).isDefined,
        TeamView(gameRow, _.isHome, gameRow.homeTeamLogoClass, _.home),
        TeamView(gameRow, _.isAway, gameRow.awayTeamLogoClass, _.away),
        gameRow.attended.getOrElse(false),
        attendUrl.isDefined && unattendUrl.isDefined,
        attendUrl.orUndefined,
        unattendUrl.orUndefined,
        ticketingInfo.map(_._1).map(sharedDateToJsDate).orUndefined,
        ticketingInfo.flatMap(_._2).map(JSON.stringify(_)).orUndefined,
        gameRow.links(MATCH_REPORT).map(JSON.stringify(_)).orUndefined,
        gameRow.links(LOCATION).map(JSON.stringify(_)).orUndefined)
    }
  }

  @ScalaJSDefined
  class TeamView(val name: js.UndefOr[String],
                 val logo: js.UndefOr[String],
                 val score: js.UndefOr[Int],
                 val shootout: js.UndefOr[Int]) extends js.Object {
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