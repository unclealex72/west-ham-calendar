package calendar

import dates.SharedDate
import models.GameRowRel.{LOCATION, MATCH_REPORT, _}
import models.TicketingInformationRel.FORM
import models._

import scala.scalajs.js
import scala.scalajs.js.{Date, Dictionary, JSON}
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.scalajs.js.JSConverters._

/**
  * Created by alex on 01/04/16.
  */
package object views {

  private def sharedDateToJsDate(sd: SharedDate): js.Date = new js.Date(js.Date.parse(sd.toString))

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
  abstract class Identifiable extends js.Object {
    val id: Int
  }

  @ScalaJSDefined
  class SeasonView(val season: Int, val months: js.Array[MonthView]) extends Identifiable {
    val id = season
  }
  object SeasonView {
    def apply(season: Season): SeasonView = {
      new SeasonView(season.season, season.months.toSeq.zipWithIndex.map { mi => MonthView(mi._1, mi._2) }.toJSArray)
    }
  }


  @ScalaJSDefined
  class MonthView(
                   val idx: Int,
                   val date: js.Date,
                   var games: js.Array[GameView]) extends Identifiable {
    val id = idx
  }

  object MonthView {
    def apply(month: Month, idx: Int): MonthView = {
      val date = new js.Date(month.date.year - 1900, month.date.month - 1, 1)
      new MonthView(idx, date, month.games.map(GameView(_)).toJSArray)
    }
  }

  @ScalaJSDefined
  class GameView(
                  val id: Int,
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
                  val tickets: js.Dictionary[TicketsView],
                  val matchReport: js.UndefOr[String],
                  val locationUrl: js.UndefOr[String]) extends Identifiable

  object GameView {
    def apply(gameRow: GameRow): GameView = {
      val attendUrl = gameRow.links(ATTEND)
      val unattendUrl = gameRow.links(UNATTEND)
      val tickets = TicketType.values.foldLeft(Dictionary.empty[TicketsView]) { (dict, ticketType) =>
        gameRow.tickets.get(ticketType).foreach { ticketingInformation =>
          dict.update(ticketType.entryName, TicketsView(ticketingInformation))
        }
        dict
      }
      new GameView(
        gameRow.id.toInt,
        sharedDateToJsDate(gameRow.at),
        gameRow.competition.name,
        gameRow.competitionLogo.orUndefined,
        gameRow.opponents,
        gameRow.result.isDefined,
        gameRow.result.flatMap(_.shootoutScore).isDefined,
        TeamView(gameRow, _.isHome, gameRow.homeTeamLogo, _.home),
        TeamView(gameRow, _.isAway, gameRow.awayTeamLogo, _.away),
        gameRow.attended.getOrElse(false),
        attendUrl.isDefined && unattendUrl.isDefined,
        attendUrl.orUndefined,
        unattendUrl.orUndefined,
        tickets,
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

  @ScalaJSDefined
  class TicketsView(val date: js.Date, val url: js.UndefOr[String]) extends js.Object
  object TicketsView {
    def apply(ticketingInformation: TicketingInformation): TicketsView = {
      new TicketsView(
        sharedDateToJsDate(ticketingInformation.at),
        ticketingInformation.links(TicketingInformationRel.FORM).map(JSON.stringify(_)).orUndefined)
    }
  }
}