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
package uk.co.unclealex.hammers.calendar.cal

import org.joda.time.DateTime
import org.joda.time.Duration
import org.scalamock.specs2.MockFactory
import org.specs2.mutable.Specification

import uk.co.unclealex.hammers.calendar.August
import uk.co.unclealex.hammers.calendar.Instant
import uk.co.unclealex.hammers.calendar.July
import uk.co.unclealex.hammers.calendar.September
import uk.co.unclealex.hammers.calendar.dao.GameDao
import uk.co.unclealex.hammers.calendar.dao.Transactional
import uk.co.unclealex.hammers.calendar.geo.GeoLocation
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Location
import uk.co.unclealex.hammers.calendar.search.{ AttendedSearchOption => A }
import uk.co.unclealex.hammers.calendar.search.{ GameOrTicketSearchOption => G }
import uk.co.unclealex.hammers.calendar.search.{ LocationSearchOption => L }

/**
 * @author alex
 *
 */
class CalendarFactoryImplTest extends Specification with MockFactory {

  "The busy mask" should {
    val variations = List(
      (None, A.ATTENDED, true),
      (None, A.UNATTENDED, false),
      (None, A.ANY, false),
      (Some(true), A.ATTENDED, true),
      (Some(true), A.UNATTENDED, true),
      (Some(true), A.ANY, true),
      (Some(false), A.ATTENDED, false),
      (Some(false), A.UNATTENDED, false),
      (Some(false), A.ANY, false))
    variations foreach {
      case (busyMask, attendedSearchOption, expectedResult) =>
        s"make calendars with ${attendedSearchOption} ${if (expectedResult) "" else "not "}busy when set to ${busyMask}" in {
          val calendarFactory = new CalendarFactoryImpl(mock[Transactional])
          val actualResult = calendarFactory.busy(busyMask, attendedSearchOption)
          actualResult must be equalTo (expectedResult)
        }
    }
  }
  "Generating a calendar" should {
    val game = Game(GameKey(Competition.FACP, Location.HOME, "Them", 2013))
    game.bondholdersAvailable = Some(August(1, 2013) at (9, 0))
    game.priorityPointAvailable = Some(August(2, 2013) at (9, 0))
    game.seasonTicketsAvailable = Some(August(3, 2013) at (9, 0))
    game.academyMembersAvailable = Some(August(4, 2013) at (9, 0))
    game.generalSaleAvailable = Some(August(5, 2013) at (9, 0))
    game.at = Some(September(5, 2013) at (15, 0))
    game.attendence = Some(100)
    game.matchReport = Some("report")
    game.result = Some("4-0")
    game.televisionChannel = Some("TV")
    game.dateCreated = July(1, 2013) at (5, 0)
    game.lastUpdated = July(7, 2013) at (9, 0)
    val expectations = for (a <- A.values; l <- L.values; g <- G.values) yield {
      val expectedGameTypeDescriptor = (a, l) match {
        case (A.ATTENDED, L.HOME) => "all attended home"
        case (A.ATTENDED, L.AWAY) => "all attended away"
        case (A.ATTENDED, L.ANY) => "all attended"
        case (A.UNATTENDED, L.HOME) => "all unattended home"
        case (A.UNATTENDED, L.AWAY) => "all unattended away"
        case (A.UNATTENDED, L.ANY) => "all unattended"
        case (A.ANY, L.HOME) => "all home"
        case (A.ANY, L.AWAY) => "all away"
        case (A.ANY, L.ANY) => "all"
      }
      val (expectedEventTypeDescriptor, expectedPeriod) = g match {
        case (G.BONDHOLDERS) => ("Bondholder ticket selling", August(1, 2013) at (9, 0) lasting 1)
        case (G.PRIORITY_POINT) => ("Priority point ticket selling", August(2, 2013) at (9, 0) lasting 1)
        case (G.SEASON) => ("Season ticket selling", August(3, 2013) at (9, 0) lasting 1)
        case (G.ACADEMY) => ("Academy member ticket selling", August(4, 2013) at (9, 0) lasting 1)
        case (G.GENERAL_SALE) => ("General sale ticket selling", August(5, 2013) at (9, 0) lasting 1)
        case (G.GAME) => ("Game", September(5, 2013) at (15, 0) lasting 2)
      }
      val expectedTitle = s"$expectedEventTypeDescriptor times for $expectedGameTypeDescriptor games"
      val busy = a == A.ATTENDED
      val expectedEvent = Event(
        id = "0",
        competition = Competition.FACP,
        location = Location.HOME,
        geoLocation = Some(GeoLocation.WEST_HAM),
        opponents = "Them",
        dateTime = expectedPeriod._1,
        duration = expectedPeriod._2,
        result = Some("4-0"),
        attendence = Some(100),
        matchReport = Some("report"),
        televisionChannel = Some("TV"),
        busy = busy,
        dateCreated = July(1, 2013) at (5, 0),
        lastUpdated = July(7, 2013) at (9, 0))
      (a, l, g, expectedTitle, expectedEvent)
    }
    expectations foreach {
      case (a, l, g, expectedTitle, expectedEvent) =>
        s"generate the correct title and event for search keys $a, $l and $g" in {
          val gameDao = mock[GameDao]
          val tx = new Transactional() {
            def tx[E](block: GameDao => E) = block(gameDao)
          }
          (gameDao.search _) expects (a, l, g) returning (List(game))
          val actualCalendar = new CalendarFactoryImpl(tx).create(None, a, l, g)
          actualCalendar.title must be equalTo (expectedTitle)
          actualCalendar.events must have size (1)
          actualCalendar.events foreach (_ must be equalTo (expectedEvent))
        }
    }
  }

  implicit class InstantImplicits(i: Instant) {
    def lasting(hours: Int): Pair[DateTime, Duration] = (i, Duration.standardHours(hours))
  }
}