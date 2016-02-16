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

import java.util.Date

import dates.DateTimeImplicits._
import dates.geo.GeoLocationFactory
import model.Game
import models.GameTimeType._
import models.TicketType._
import models._
import org.joda.time.DateTime

/**
 * @author alex
 *
 */
class GameRowFactoryImpl(val geoLocationFactory: GeoLocationFactory) extends GameRowFactory {

  def timeTypeOf(dateTime: DateTime): GameTimeType = {
    if (dateTime.isThreeOClockOnASaturday) {
      ThreePmSaturday
    } else if (dateTime.isWeekday) {
      Weekday
    } else {
      Weekend
    }
  }

  def toRow(includeAttended: Boolean, gameLinksFactory: Game => Links[GameRowRel], ticketLinksFactory: Game => TicketType => Links[TicketingInformationRel]): Game => GameRow = { game =>
    game.at match {
      case Some(gameAt) =>
        GameRow(
          id = game.id,
          at = gameAt,
          season = game.season,
          opponents = game.opponents,
          competition = game.competition,
          location = game.location,
          geoLocation = geoLocationFactory.forGame(game),
          result = game.result,
          matchReport = game.matchReport,
          tickets = ticketFactory(game, ticketLinksFactory),
          attended = if (includeAttended) game.attended orElse Some(false) else None,
          links = gameLinksFactory(game))
      case None => throw new IllegalStateException(s"Game $game did not have it's date played attribute set.")
    }
  }

  def ticketFactory(game: Game, ticketLinksFactory: Game => TicketType => Links[TicketingInformationRel]): Map[TicketType, TicketingInformation] = {
    implicit val dt = (date : Date) => new DateTime(date)
    val ticketsAt: Map[TicketType, Option[DateTime]] = Map(
      BondholderTicketType -> game.bondholdersAvailable,
      PriorityPointTicketType -> game.priorityPointAvailable,
      SeasonTicketType -> game.seasonTicketsAvailable,
      AcademyTicketType -> game.academyMembersAvailable,
      GeneralSaleTicketType -> game.generalSaleAvailable)
    ticketsAt.foldLeft(Map.empty[TicketType, TicketingInformation]) {
      case (tickets, (ticketType, Some(availableAt))) =>
        val ticketFormUrl = ticketLinksFactory(game)(ticketType)
        tickets + (ticketType -> TicketingInformation(availableAt, ticketFormUrl))
      case (tickets, _) => tickets
    }
  }
}
