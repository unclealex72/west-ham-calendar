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
package services

import java.time.ZonedDateTime
import javax.inject.Inject

import dates.JvmGameRow
import dates.ZonedDateTimeExtensions._
import dates.geo.GeoLocationFactory
import model.Game
import models.GameTimeType._
import models.TicketType._
import models._

/**
 * @author alex
 *
 */
class GameRowFactoryImpl @Inject() (val geoLocationFactory: GeoLocationFactory) extends GameRowFactory {

  def timeTypeOf(zonedDateTime: ZonedDateTime): GameTimeType = {
    if (zonedDateTime.isThreeOClockOnASaturday) {
      THREE_PM_SATURDAY
    } else if (zonedDateTime.isWeekday) {
      WEEKDAY
    } else {
      WEEKEND
    }
  }

  def toRow(
             includeAttended: Boolean,
             gameLinksFactory: Game => Links[GameRowRel]): Game => JvmGameRow = { game =>
    game.at match {
      case Some(gameAt) =>
        GameRow[ZonedDateTime](
          id = game.id,
          at = gameAt,
          season = game.season,
          opponents = game.opponents,
          competition = game.competition,
          location = game.location,
          maybeResult = game.result,
          tickets = ticketFactory(game),
          maybeAttended = if (includeAttended) Some(game.attended) else None,
          links = gameLinksFactory(game))
      case None => throw new IllegalStateException(s"Game $game did not have it's date played attribute set.")
    }
  }

  def ticketFactory(game: Game): Map[TicketType, ZonedDateTime] = {
    def tickets(ticketType: TicketType, maybeDate: Option[ZonedDateTime]): Map[TicketType, ZonedDateTime] = {
      Map.empty ++ maybeDate.toSeq.map(date => ticketType -> date)
    }
    tickets(BondholderTicketType, game.bondholdersAvailable) ++
      tickets(PriorityPointTicketType, game.priorityPointAvailable) ++
      tickets(SeasonTicketType, game.seasonTicketsAvailable) ++
      tickets(AcademyTicketType, game.academyMembersAvailable) ++
      tickets(GeneralSaleTicketType, game.generalSaleAvailable)
  }
}
