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

import java.util.Date

import json.JsonConverters
import upickle.Js

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
  at: Date,
  season: Int,
  opponents: String,
  competition: Competition,
  location: Location,
  geoLocation: Option[GeoLocation],
  result: Option[GameResult],
  matchReport: Option[String],
  tickets: Map[TicketType, TicketingInformation],
  attended: Option[Boolean],
  links: Links[GameRowRel])

sealed trait GameRowRel extends Rel
object GameRowRel extends RelEnum[GameRowRel] {
  val values = findValues

  object ATTEND extends Rel_("attend") with GameRowRel
  object UNATTEND extends Rel_("unattend") with GameRowRel
  object LOCATION extends Rel_("location") with GameRowRel
}

case class TicketingInformation(at: Date, links: Links[TicketingInformationRel])

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
    value.jsObj { fields =>
      (fields.mandatory("at", "Cannot find an at field for ticketing information.")(_.jsDate) |@|
        fields.mandatory("links", "Cannot fina a links field for ticketing information")(Links.jsonToLinks(TicketingInformationRel))
        )(TicketingInformation.apply)
    }
  }

  private def jsonToTicketingInformationMap(value: Js.Value): ValidationNel[String, Map[TicketType, TicketingInformation]] = {
    value.jsObj { fields =>
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
      gr.geoLocation.map(geoLocation => "geoLocation" -> GeoLocation.enumToJson(geoLocation)) ++
      gr.result.map(result => "result" -> GameResult.serialise(result)) ++
      gr.matchReport.map(matchReport => "matchReport" -> Js.Str(matchReport)) ++
      gr.attended.map(attended => "attended" -> (if (attended) Js.True else Js.False))
    Js.Obj(fields :_*)
  }

  def deserialise(value: Js.Value): ValidationNel[String, GameRow] = value.jsObj {
    fields =>
      val id = fields.mandatory("id", "Cannot find an id field for a game.")(_.jsNum)
      val at = fields.mandatory("at", "Cannot find an at field for a game.")(_.jsDate)
      val season = fields.mandatory("season", "Cannot find a season field for a game.")(_.jsNum).map(_.toInt)
      val opponents = fields.mandatory("opponents", "Cannot find an opponents field for a game.")(_.jsStr)
      val competition = fields.mandatory("competition", "Cannot find a competition field for a game.")(Competition.jsonToEnum)
      val location = fields.mandatory("location", "Cannot find a location field for a game.")(Location.jsonToEnum)
      val geoLocation = fields.optional("geoLocation")(GeoLocation.jsonToEnum)
      val result = fields.optional("result")(GameResult.deserialise)
      val matchReport = fields.optional("matchReport")(_.jsStr)
      val tickets = fields.mandatory("tickets", "Cannot find a tickets field for a game")(jsonToTicketingInformationMap)
      val attended = fields.optional("attended")(_.jsBool)
      val links = fields.mandatory("links", "Cannot find a links field for a game.")(Links.jsonToLinks(GameRowRel))

      // Split the validation into two as Scalaz' applicative builders aren't big enough.
      val left = (id |@| at |@| season |@| opponents |@| competition |@| location).tupled
      val right = (geoLocation |@| result |@| matchReport |@| tickets |@| attended |@| links).tupled
      (left |@| right)((_, _)).map { case (l, r) =>
          GameRow(l._1, l._2, l._3, l._4, l._5, l._6, r._1, r._2, r._3, r._4, r._5, r._6)
      }
  }
}