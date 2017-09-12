/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */

package cal



import javax.inject.Inject

import dates.geo.GeoLocationFactory
import model.Game
import java.time.{Duration, ZonedDateTime}
import search.GameOrTicketSearchOption._
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption}
/**
 * The default implementation of the Calendar factory.
 *
 * @author alex
 *
 */
class CalendarFactoryImpl @Inject() (val geoLocationFactory: GeoLocationFactory) extends CalendarFactory {

  def create(
    games: List[Game],
    busyMask: Option[Boolean],
    attendedSearchOption: AttendedSearchOption,
    locationSearchOption: LocationSearchOption,
    gameOrTicketSearchOption: GameOrTicketSearchOption): Calendar = {
    val (dateFactory, duration) = gamePeriodFactory(gameOrTicketSearchOption)
    val gameToEvent = convert(dateFactory, busy(busyMask, attendedSearchOption), Duration.ofHours(duration))
    val events = for {
      game <- games
      event <- gameToEvent(game)
    } yield event
    val title = createTitle(attendedSearchOption, locationSearchOption, gameOrTicketSearchOption)
    Calendar("whufc" + title.replace(' ', '_'), title, events.sorted)
  }

  /**
   * Determine whether a calendar should be busy or not.
   */
  def busy(busyMask: Option[Boolean], attendedSearchOption: AttendedSearchOption): Boolean = {
    busyMask getOrElse (attendedSearchOption == AttendedSearchOption.ATTENDED)
  }

  def gamePeriodFactory(gameOrTicketSearchOption: GameOrTicketSearchOption): (Game => Option[ZonedDateTime], Int) = {
    gameOrTicketSearchOption match {
      case BONDHOLDERS => (g => g.bondholdersAvailable, 1)
      case PRIORITY_POINT => (g => g.priorityPointAvailable, 1)
      case SEASON => (g => g.seasonTicketsAvailable, 1)
      case ACADEMY => (g => g.academyMembersAvailable, 1)
      case GENERAL_SALE => (g => g.generalSaleAvailable, 1)
      case GAME => (g => g.at, 2)
    }
  }

  def createTitle(attendedSearchOption: AttendedSearchOption,
    locationSearchOption: LocationSearchOption,
    gameOrTicketSearchOption: GameOrTicketSearchOption): String = {
    val titleType = gameOrTicketSearchOption match {
      case BONDHOLDERS => "Bondholder ticket selling"
      case PRIORITY_POINT => "Priority point ticket selling"
      case SEASON => "Season ticket selling"
      case ACADEMY => "Academy member ticket selling"
      case GENERAL_SALE => "General sale ticket selling"
      case GAME => "Game"
    }
    val attendedType = attendedSearchOption match {
      case AttendedSearchOption.ANY => "all"
      case AttendedSearchOption.ATTENDED => "attended"
      case AttendedSearchOption.UNATTENDED => "unattended"
    }
    val locationType = locationSearchOption match {
      case LocationSearchOption.ANY => "all"
      case LocationSearchOption.HOME => "home"
      case LocationSearchOption.AWAY => "away"
    }
    s"$titleType times for ${List("all", attendedType, locationType).distinct.mkString(" ")} games"
  }

  /**
   * Convert a game to into an event if the date factory supplies a date.
   */
  def convert(dateFactory: Game => Option[ZonedDateTime], busy: Boolean, duration: Duration): Game => Option[Event] = { game =>
    dateFactory(game) map { date =>
      new Event(
        id = game.id.toString,
        gameId = game.id,
        competition = game.competition,
        location = game.location,
        geoLocation = geoLocationFactory.forGame(game),
        opponents = game.opponents,
        zonedDateTime = date,
        duration = duration,
        result = game.result.map(_.format),
        attendance = game.attendance,
        matchReport = game.matchReport,
        televisionChannel = game.televisionChannel,
        busy = busy,
        dateCreated = game.dateCreated,
        lastUpdated = game.lastUpdated)
    }
  }
}