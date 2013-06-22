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

import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.Location
import uk.co.unclealex.hammers.calendar.geo.GeoLocation

import java.util.Date

/**
 * @author alex
 *
 */
object GameTimeType extends Enumeration {
  type GameTimeType = Value
  val ThreePmSaturday, Weekend, Weekday = Value
}
object TicketType extends Enumeration {
  type TicketType = Value
  val Bondholder, PriorityPoint, SeasonTicket, Academy, GeneralSale = Value
}

import GameTimeType._
import TicketType._

/**
 * A game row allows a game to be shown as a row in a table.
 */
case class GameRow(
  id: Long,
  at: Date,
  gameTimeType: GameTimeType,
  season: Int,
  opponents: String,
  competition: Competition,
  location: Location,
  geoLocation: Option[GeoLocation],
  result: Option[String],
  matchReport: Option[String],
  ticketsAt: Map[TicketType, Option[Date]],
  attended: Option[Boolean]) {

}

