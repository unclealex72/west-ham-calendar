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

import uk.co.unclealex.hammers.calendar.model.Game
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * An interface for creating a calendar from a list of games.
 * @author alex
 *
 */
trait CalendarFactory {

  /**
   * Create a new calendar.
   * @param title The name of the calendar.
   * @param id The unique ID of the calendar.
   * @param busy True if calendar items should be marked as busy, false otherwise.
   * @param duration The length of events in the created calendar.
   * @param dateFactory a function to extract a date from a game. Games that do not have a date will not
   * be included in the generated calendar.
   * @param games The games that will appear in the calendar.
   */
  def create(title: String, id: String, busy: Boolean, duration: Duration, dateFactory: Game => Option[DateTime], games: Traversable[Game]): Calendar
}