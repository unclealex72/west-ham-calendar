/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package models

import dates.SharedDate
import json.JsonConverters
import upickle.Js

import scala.math.{Ordering => SOrdering}
import scalaz.Scalaz._
import scalaz._

/**
 * @author alex
 *
 */
object GameTimeType extends Enumeration {
  type GameTimeType = Value
  val ThreePmSaturday, Weekend, Weekday = Value
}

/**
 * The types of tickets available from West Ham Utd.
 */

/**
 * A game row allows a game to be shown as a row in a table.
 */
case class GameRow(
  id: Long,
  at: SharedDate,
  season: Int,
  opponents: String,
  competition: Competition,
  location: Location,
  result: Option[GameResult],
  tickets: Map[TicketType, TicketingInformation],
  attended: Option[Boolean],
  homeTeamLogoClass: Option[String],
  awayTeamLogoClass: Option[String],
  competitionLogoClass: Option[String],
  links: Links[GameRowRel]) {

  def updateAttendance(newAttendance: Boolean): GameRow = this.copy(attended = Some(newAttendance))
}

sealed trait GameRowRel extends Rel
object GameRowRel extends RelEnum[GameRowRel] {
  val values = findValues

  object ATTEND extends Rel_("attend") with GameRowRel
  object UNATTEND extends Rel_("unattend") with GameRowRel
  object LOCATION extends Rel_("location") with GameRowRel
  object MATCH_REPORT extends Rel_("match_report") with GameRowRel
  object HOME_LOGO extends Rel_("home_logo") with GameRowRel
  object AWAY_LOGO extends Rel_("away_logo") with GameRowRel
  object COMPETITION_LOGO extends Rel_("competition_logo") with GameRowRel

}

case class TicketingInformation(at: SharedDate, links: Links[TicketingInformationRel])

sealed trait TicketingInformationRel extends Rel
object TicketingInformationRel extends RelEnum[TicketingInformationRel] {
  val values = findValues

  object FORM extends Rel_("form") with TicketingInformationRel
}

object GameRow extends JsonConverters[GameRow] {

  private def ticketingInformationToJson(ti: TicketingInformation): Js.Value = {
    val fields: Seq[(String, Js.Value)] = Seq("at" -> dateToJson(ti.at), "links" -> Links.linksToJson(ti.links))
    Js.Obj(fields :_*)
  }

  private def jsonToTicketingInformation(value: Js.Value): ValidationNel[String, TicketingInformation] = {
    value.jsObj("TicketingInformation") { fields =>
      (fields.mandatory("at")(_.jsDate) |@|
        fields.mandatory("links")(Links.jsonToLinks(TicketingInformationRel))
        )(TicketingInformation.apply)
    }
  }

  private def jsonToTicketingInformationMap(value: Js.Value): ValidationNel[String, Map[TicketType, TicketingInformation]] = {
    value.jsObj("TicketingInformationMap") { fields =>
      val ticketingInformations = fields.fields.map { case (tt, ti) =>
        (TicketType.jsonToEnum(Js.Str(tt)) |@| jsonToTicketingInformation(ti)).tupled
      }
      val empty = Map.empty[TicketType, TicketingInformation].successNel[String]
      ticketingInformations.foldLeft(empty) { (result, vttti) =>
        (result |@| vttti)(_ + _)
      }
    }
  }

  def serialise(gr: GameRow): Js.Value = {
    val tickets = gr.tickets.map { case (tt, ti) =>
      (tt.entryName, ticketingInformationToJson(ti))
    }
    val fields: Seq[(String, Js.Value)] = Seq(
      "id" -> Js.Num(gr.id),
      "at" -> dateToJson(gr.at),
      "season" -> Js.Num(gr.season),
      "opponents" -> Js.Str(gr.opponents),
      "competition" -> Competition.enumToJson(gr.competition),
      "location" -> Location.enumToJson(gr.location),
      "tickets" -> Js.Obj(tickets.toSeq :_*),
      "links" -> Links.linksToJson(gr.links)
    ) ++
      gr.homeTeamLogoClass.map(homeTeamLogoClass => "homeTeamLogoClass" -> Js.Str(homeTeamLogoClass)) ++
      gr.awayTeamLogoClass.map(awayTeamLogoClass => "awayTeamLogoClass" -> Js.Str(awayTeamLogoClass)) ++
      gr.competitionLogoClass.map(competitionLogoClass => "competitionLogoClass" -> Js.Str(competitionLogoClass)) ++
      gr.result.map(result => "result" -> GameResult.serialise(result)) ++
      gr.attended.map(attended => "attended" -> (if (attended) Js.True else Js.False))
    Js.Obj(fields :_*)
  }

  def deserialise(value: Js.Value): ValidationNel[String, GameRow] = value.jsObj("GameRow") {
    fields =>
      val id = fields.mandatory("id")(_.jsLong)
      val at = fields.mandatory("at")(_.jsDate)
      val season = fields.mandatory("season")(_.jsInt)
      val opponents = fields.mandatory("opponents")(_.jsStr)
      val competition = fields.mandatory("competition")(Competition.jsonToEnum)
      val location = fields.mandatory("location")(Location.jsonToEnum)
      val result = fields.optional("result")(GameResult.deserialise)
      val tickets = fields.mandatory("tickets")(jsonToTicketingInformationMap)
      val attended = fields.optional("attended")(_.jsBool)
      val homeTeamLogoClass = fields.optional("homeTeamLogoClass")(_.jsStr)
      val awayTeamLogoClass = fields.optional("awayTeamLogoClass")(_.jsStr)
      val competitionLogoClass = fields.optional("competitionLogoClass")(_.jsStr)
      val links = fields.mandatory("links")(Links.jsonToLinks(GameRowRel))

      // Split the validation into two as Scalaz' applicative builders aren't big enough.
      val left = (id |@| at |@| season |@| opponents |@| competition |@| location |@| result).tupled
      val right = (tickets |@| attended |@| homeTeamLogoClass |@| awayTeamLogoClass |@| competitionLogoClass |@|  links).tupled
      (left |@| right)((_, _)).map { case (l, r) =>
          GameRow(l._1, l._2, l._3, l._4, l._5, l._6, l._7, r._1, r._2, r._3, r._4, r._5, r._6)
      }
  }

  implicit val ordering: SOrdering[GameRow] = SOrdering.by(gr => gr.at)
}