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

import models._
import model.Game
import org.joda.time.DateTime
import models.GameTimeType._
import dates.DateTimeImplicits._
import geo.GeoLocation
import geo.GeoLocation._
import model.Location._
import java.util.Date
import models.TicketType._
import models.GameTimeType
import models.GameRow
import scala.Some

/**
 * @author alex
 *
 */
class GameRowFactoryImpl extends GameRowFactory {

  def timeTypeOf(dateTime: DateTime): GameTimeType = {
    if (dateTime.isThreeOClockOnASaturday) {
      ThreePmSaturday
    } else if (dateTime.isWeekday) {
      Weekday
    } else {
      Weekend
    }
  }

  def toRow(includeAttended: Boolean, ticketFormUrlFactory: TicketType => Game => Option[String]): Game => GameRow = { game =>
    game.at match {
      case Some(gameAt) =>
        GameRow(
          id = game.id,
          at = gameAt,
          gameTimeType = timeTypeOf(gameAt),
          season = game.season,
          opponents = game.opponents,
          competition = game.competition,
          location = game.location,
          geoLocation = game.location match {
            case HOME => Some(WEST_HAM)
            case AWAY => GeoLocation(game.opponents)
          },
          result = game.result,
          matchReport = game.matchReport,
          tickets = ticketFactory(game, ticketFormUrlFactory),
          attended = if (includeAttended) game.attended orElse Some(false) else None)
      case None => throw new IllegalStateException(s"Game $game did not have it's date played attribute set.")
    }
  }

  def ticketFactory(game: Game, ticketFormUrlFactory: TicketType => Game=> Option[String]): Map[TicketType.Name, TicketingInformation] = {
    implicit val dt = (date : Date) => new DateTime(date)
    val ticketsAt: Map[TicketType, Option[DateTime]] = Map(
      BondholderTicketType -> game.bondholdersAvailable,
      PriorityPointTicketType -> game.priorityPointAvailable,
      SeasonTicketType -> game.seasonTicketsAvailable,
      AcademyTicketType -> game.academyMembersAvailable,
      GeneralSaleTicketType -> game.generalSaleAvailable)
    ticketsAt.foldLeft(Map.empty[TicketType.Name, TicketingInformation]) {
      case (tickets, (ticketType, Some(availableAt))) => {
        val ticketFormUrl = ticketFormUrlFactory(ticketType)(game)
        tickets + (ticketType.name -> TicketingInformation(availableAt, ticketFormUrl))
      }
      case (tickets, _) => tickets
    }
  }
}
