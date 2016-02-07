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

import org.joda.time.DateTime
import model.Competition
import model.Location
import org.joda.time.Duration
import geo.GeoLocation
/**
 * A representation of a game (or event) in a calendar. This is not tied to any implementation.
 * @author alex
 *
 */
case class Event(
  /**
   * The unique (relative to this application) ID of the event.
   */
  val id: String,
  /**
   * The ID of the game that created this event.
    */
  val gameId: Long,
  /**
   * The game's {@link Competition}.
   */
  val competition: Competition,
  /**
   * The game's {@link Location}.
   */
  val location: Location,
  /**
   * The game's geographic location.
   */
  val geoLocation: Option[GeoLocation],
  /**
   * The game's opponents.
   */
  val opponents: String,
  /**
   * The {@link DateTime} of the event.
   */
  val dateTime: DateTime,
  /**
   * The {@link Duration} of the event.
   */
  val duration: Duration,
  /**
   * The game's result.
   */
  val result: Option[String],
  /**
   * The game's attendence.
   */
  val attendence: Option[Int],
  /**
   * The game's match report.
   */
  val matchReport: Option[String],
  /**
   * The TV channel that showed the match.
   */
  val televisionChannel: Option[String],
  /**
   * True if this event should be marked as busy, false otherwise.
   */
  val busy: Boolean,
  /**
   * The date this event was created.
   */
  val dateCreated: DateTime,
  /**
   * The date this event was last updated.
   */
  val lastUpdated: DateTime) extends Ordered[Event] {

  /**
   * Compare events chronologically
   */
  def compare(that: Event) = Ordering.by((e: Event) => e.dateTime.getMillis).compare(this, that)
}