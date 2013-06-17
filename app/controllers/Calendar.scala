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
package controllers

import java.io.StringWriter
import javax.inject.Inject
import play.api.mvc.Action
import uk.co.unclealex.hammers.calendar.cal.CalendarFactory
import uk.co.unclealex.hammers.calendar.cal.CalendarWriter
import uk.co.unclealex.hammers.calendar.search.AttendedSearchOption
import uk.co.unclealex.hammers.calendar.search.GameOrTicketSearchOption
import uk.co.unclealex.hammers.calendar.search.LocationSearchOption
import play.api.mvc.Controller

/**
 * The controller that handles generating calendars.
 * @author alex
 *
 */
class Calendar @Inject() (calendarFactory: CalendarFactory, calendarWriter: CalendarWriter) extends Controller {

  def searchSecure(attendedSearchOption: String, locationSearchOption: String, gameOrTicketSearchOption: String) =
    calendar(
      None,
      AttendedSearchOption(attendedSearchOption),
      LocationSearchOption(locationSearchOption),
      GameOrTicketSearchOption(gameOrTicketSearchOption))

  def search(mask: String, locationSearchOption: String, gameOrTicketSearchOption: String) =
    calendar(
      Some("busy" == mask),
      Some(AttendedSearchOption.ANY),
      LocationSearchOption(locationSearchOption),
      GameOrTicketSearchOption(gameOrTicketSearchOption))

  def calendar(
    busyMask: Option[Boolean],
    a: Option[AttendedSearchOption],
    l: Option[LocationSearchOption],
    g: Option[GameOrTicketSearchOption]) = Action {
    (a, l, g) match {
      case (Some(a), Some(l), Some(g)) => {
        val calendar = calendarFactory.create(busyMask, a, l, g)
        val buffer = new StringWriter
        calendarWriter.write(calendar, buffer)
        val output = buffer.toString
        Ok(output).as(calendarWriter.mimeType)
      }
      case _ => NotFound
    }
  }
}