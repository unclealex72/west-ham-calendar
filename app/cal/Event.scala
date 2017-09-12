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

import models.{GeoLocation, Location, Competition}
import java.time.{ZonedDateTime, Duration}

/**
 * A representation of a game (or event) in a calendar. This is not tied to any implementation.
  *
  * @author alex
 *
 */
case class Event(
                  /**
                   * The unique (relative to this application) ID of the event.
                   */
                  id: String,
                  /**
                   * The ID of the game that created this event.
                    */
                  gameId: Long,
                  /**
                   * The game's {@link Competition}.
                   */
                  competition: Competition,
                  /**
                   * The game's {@link Location}.
                   */
                  location: Location,
                  /**
                   * The game's geographic location.
                   */
                  geoLocation: Option[GeoLocation],
                  /**
                   * The game's opponents.
                   */
                  opponents: String,
                  /**
                   * The {@link ZonedDateTime} of the event.
                   */
                  zonedDateTime: ZonedDateTime,
                  /**
                   * The {@link Duration} of the event.
                   */
                  duration: Duration,
                  /**
                   * The game's result.
                   */
                  result: Option[String],
                  /**
                   * The game's attendance.
                   */
                  attendance: Option[Int],
                  /**
                   * The game's match report.
                   */
                  matchReport: Option[String],
                  /**
                   * The TV channel that showed the match.
                   */
                  televisionChannel: Option[String],
                  /**
                   * True if this event should be marked as busy, false otherwise.
                   */
                  busy: Boolean,
                  /**
                   * The date this event was created.
                   */
                  dateCreated: ZonedDateTime,
                  /**
                   * The date this event was last updated.
                   */
                  lastUpdated: ZonedDateTime) {

}

object Event {

  implicit val eventOrdering: Ordering[Event] = Ordering.by(e => e.zonedDateTime.toInstant)
}