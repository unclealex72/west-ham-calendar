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
import uk.co.unclealex.hammers.calendar.search.AttendedSearchOption
import uk.co.unclealex.hammers.calendar.search.LocationSearchOption
import uk.co.unclealex.hammers.calendar.search.GameOrTicketSearchOption

/**
 * An interface for creating a calendar from a list of games.
 * @author alex
 *
 */
trait CalendarFactory {

  /**
   * Create a new calendar.
   * @param attendedSearchOption the search option to use for searching whether a game was attended or not.
   * @param locationSearchOption the search option to use for searching whether a game was at home or not.
   * @param gameOrTicketSearchOption the search option to use for defining whether to return game or ticket information.
   */
  def create(
    attendedSearchOption: AttendedSearchOption, 
    locationSearchOption: LocationSearchOption, 
    gameOrTicketSearchOption: GameOrTicketSearchOption): Calendar
}