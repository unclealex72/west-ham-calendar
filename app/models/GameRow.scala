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

import dates.DateTimeJsonCodec
import model.Competition
import model.Location
import geo.GeoLocation

import argonaut._, Argonaut._, DecodeResult._
import org.joda.time.DateTime
import json.Json._
import DateTimeJsonCodec._
import model.Location.HOME

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

import GameTimeType._

/**
 * A game row allows a game to be shown as a row in a table.
 */
case class GameRow(
  id: Long,
  at: DateTime,
  gameTimeType: GameTimeType,
  season: Int,
  opponents: String,
  competition: Competition,
  location: Location,
  geoLocation: Option[GeoLocation],
  result: Option[String],
  matchReport: Option[String],
  tickets: Map[TicketType.Name, TicketingInformation],
  attended: Option[Boolean]) {

}

case class TicketingInformation(at: DateTime, formUrl: Option[String])

object GameRow {

  /**
   * Json Serialisation
   */
  implicit val TicketingInformationEncodeJson: EncodeJson[TicketingInformation] =
    casecodec2(TicketingInformation.apply, TicketingInformation.unapply)("at", "formUrl")

  implicit val GameRowEncodeJson: EncodeJson[GameRow] =
    jencode13L((gr: GameRow) =>
      (gr.id, gr.at, gr.gameTimeType.toString, gr.season, gr.opponents, gr.competition.name, gr.competition.isLeague,
       gr.location == Location.HOME, gr.geoLocation, gr.result, gr.matchReport, gr.tickets, gr.attended))(
        "id", "at", "gameTimeType", "season", "opponents", "competition", "league",
        "home", "geoLocation", "result", "matchReport", "tickets", "attended")
}