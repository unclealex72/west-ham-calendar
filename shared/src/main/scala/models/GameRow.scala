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
import enumeratum._
import io.circe.{Decoder, Encoder}
import json.JsonEnum

import scala.collection.immutable
import scala.math.Ordering

sealed trait GameTimeType extends EnumEntry

/**
 * @author alex
 *
 */
object GameTimeType extends JsonEnum[GameTimeType] {

  object THREE_PM_SATURDAY extends GameTimeType
  object WEEKEND extends GameTimeType
  object WEEKDAY extends GameTimeType

  override def values: immutable.IndexedSeq[GameTimeType] = findValues
}

/**
 * The types of tickets available from West Ham Utd.
 */

/**
 * A game row allows a game to be shown as a row in a table.
 */
case class GameRow[D](
  id: Long,
  at: D,
  season: Int,
  opponents: String,
  competition: Competition,
  location: Location,
  maybeResult: Option[GameResult],
  tickets: Map[TicketType, D],
  maybeAttended: Option[Boolean],
  homeTeamLogo: Option[String],
  awayTeamLogo: Option[String],
  competitionLogo: Option[String],
  links: Links[GameRowRel])

sealed trait GameRowRel extends Rel
object GameRowRel extends RelEnum[GameRowRel] {
  val values: immutable.IndexedSeq[GameRowRel] = findValues

  object ATTEND extends Rel_("attend") with GameRowRel
  object UNATTEND extends Rel_("unattend") with GameRowRel
  object LOCATION extends Rel_("location") with GameRowRel
  object MATCH_REPORT extends Rel_("match_report") with GameRowRel
  object HOME_LOGO extends Rel_("home_logo") with GameRowRel
  object AWAY_LOGO extends Rel_("away_logo") with GameRowRel
  object COMPETITION_LOGO extends Rel_("competition_logo") with GameRowRel

}

object GameRow extends {

  implicit def gameRowEncoder[D](implicit ev: Encoder[D]): Encoder[GameRow[D]] =
    Encoder.forProduct10(
      "id",
      "at",
      "season",
      "opponents",
      "competition",
      "location",
      "result",
      "tickets",
      "attended",
      "links") { gr =>
      (gr.id, gr.at, gr.season, gr.opponents, gr.competition, gr.location, gr.maybeResult, gr.tickets, gr.maybeAttended, gr.links)
    }

  implicit def gameRowDecoder[D](implicit ev: Decoder[D]): Decoder[GameRow[D]] =
    Decoder.forProduct10(
      "id",
      "at",
      "season",
      "opponents",
      "competition",
      "location",
      "result",
      "tickets",
      "attended",
      "links")(GameRow.apply[D])

  implicit def gameRowOrdering[D](implicit ev: Ordering[D]): Ordering[GameRow[D]] = Ordering.by(_.at)
}