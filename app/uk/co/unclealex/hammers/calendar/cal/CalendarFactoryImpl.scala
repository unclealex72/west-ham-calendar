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

package uk.co.unclealex.hammers.calendar.cal

import scala.collection.Traversable
import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.dates.DateTimeImplicits._
import org.joda.time.DateTime
import org.joda.time.Duration
import scala.collection.SortedSet
import uk.co.unclealex.hammers.calendar.geo.GeoLocation
/**
 * @author alex
 *
 */
class CalendarFactoryImpl extends CalendarFactory {

  def create(
    title: String,
    id: String,
    busy: Boolean,
    duration: Duration,
    dateFactory: Game => Option[DateTime],
    games: Traversable[Game]): Calendar = {
    val events = games.foldLeft(SortedSet.empty[Event]){ (events, game) =>
      convert(dateFactory, busy, duration, game) match {
        case Some(event) => events + event
        case None => events
      }
    }
    Calendar(id, title, events)
  }

  /**
   * Convert a game to into an event if the date factory supplies a date.
   */
  def convert(dateFactory: Game => Option[DateTime], busy: Boolean, duration: Duration, game: Game): Option[Event] = {
    dateFactory(game) map { date =>
      new Event(
        id = game.id.toString,
        competition = game.competition,
        location = game.location,
        geoLocation = GeoLocation(game),
        opponents = game.opponents,
        dateTime = date,
        duration = duration,
        result = game.result,
        attendence = game.attendence,
        matchReport = game.matchReport,
        televisionChannel = game.televisionChannel,
        busy = busy)
    }
  }
}