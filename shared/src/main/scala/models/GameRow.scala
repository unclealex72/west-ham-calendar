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

import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Date, Locale}

import json.JsonCodecs
import upickle.Js.Value
import upickle.{Js, default}

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
  result: Option[String],
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

object GameRow extends JsonCodecs {

  private def ticketingInformationToJson(ti: TicketingInformation): Js.Value = {
    val fields: Seq[(String, Js.Value)] = Seq("at" -> dateToJson(ti.at), "links" -> Links.linksToJson(ti.links))
    Js.Obj(fields :_*)
  }

  private def jsonToTicketingInformation(value: Js.Value): Either[String, TicketingInformation] = {
    value.jsObj { fields =>
      for {
        at <- fields.mandatory("at", "Cannot find an at field for ticketing information.")(_.jsDate).right
        links <- fields.mandatory("links", "Cannot fina a links field for ticketing information")(Links.jsonToLinks(TicketingInformationRel)).right
      } yield {
        TicketingInformation(at, links)
      }
    }
  }

  private def jsonToTicketingInformationMap(value: Js.Value): Either[String, Map[TicketType, TicketingInformation]] = {
    value.jsObj { fields =>
      val empty: Either[String, Map[TicketType, TicketingInformation]] = Right(Map.empty)
      fields.fields.foldLeft(empty) { (result, ttti) =>
        result match {
          case Right(existingTtTi) =>
            for {
              tt <- TicketType.jsonToEnum(Js.Str(ttti._1)).right
              ti <- jsonToTicketingInformation(ttti._2).right
            } yield {
              existingTtTi + (tt -> ti)
            }
          case Left(msg) => Left(msg)
        }
      }
    }
  }
  private def gameRowToJson(gr: GameRow): Js.Value = {
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
      gr.result.map(result => "result" -> Js.Str(result)) ++
      gr.matchReport.map(matchReport => "matchReport" -> Js.Str(matchReport)) ++
      gr.attended.map(attended => "attended" -> (if (attended) Js.True else Js.False))
    Js.Obj(fields :_*)
  }

  private def jsonToGameRow(value: Js.Value): Either[String, GameRow] = value.jsObj {
    fields =>
      for {
        id <- fields.mandatory("id", "Cannot find an id field for a game.")(_.jsNum).right
        at <- fields.mandatory("at", "Cannot find an at field for a game.")(_.jsDate).right
        season <- fields.mandatory("season", "Cannot find a season field for a game.")(_.jsNum).right
        opponents <- fields.mandatory("opponents", "Cannot find an opponents field for a game.")(_.jsStr).right
        location <- fields.mandatory("location", "Cannot find a location field for a game.")(Location.jsonToEnum).right
        competition <- fields.mandatory("competition", "Cannot find a competition field for a game.")(Competition.jsonToEnum).right
        geoLocation <- fields.optional("geoLocation")(GeoLocation.jsonToEnum).right
        result <- fields.optional("result")(_.jsStr).right
        matchReport <- fields.optional("matchReport")(_.jsStr).right
        attended <- fields.optional("attended")(_.jsBool).right
        tickets <- fields.mandatory("tickets", "Cannot find a tickets field for a game")(jsonToTicketingInformationMap).right
        links <- fields.mandatory("links", "Cannot find a links field for a game.")(Links.jsonToLinks(GameRowRel)).right
      } yield {
        GameRow(
          id,
          at,
          season.toInt,
          opponents,
          competition,
          location,
          geoLocation,
          result,
          matchReport,
          tickets,
          attended,
          links)
      }
  }

  // JSON Support
  implicit val gameRow2Writer: default.Writer[GameRow] = upickle.default.Writer[GameRow](gameRowToJson)

  implicit val gameRow2Reader: default.Reader[Either[String, GameRow]] =
    upickle.default.Reader[Either[String, GameRow]] {
      case value => jsonToGameRow(value)
    }

}