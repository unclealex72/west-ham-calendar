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
import javax.inject.Named
import uk.co.unclealex.hammers.calendar.update.LastUpdated

/**
 * The controller that handles generating calendars.
 * @author alex
 *
 */
class Calendar @Inject() (
  /**
   * The secret used to protect the update path.
   */
  @Named("secret") val secret: String,
  /**
   * The last update service to get when the calendars were last updated.
   */
  lastUpdated: LastUpdated,
  /**
   * The calendar factory used to generate calendars.
   */
  calendarFactory: CalendarFactory,
  /**
   * The calendar write used to write calendars.
   */
  calendarWriter: CalendarWriter) extends Controller with Secret with Etag {

  def searchSecure(secretPayload: String, attendedSearchOption: String, locationSearchOption: String, gameOrTicketSearchOption: String) =
    Secret(secretPayload) {
      calendar(
        None,
        AttendedSearchOption(attendedSearchOption),
        LocationSearchOption(locationSearchOption),
        GameOrTicketSearchOption(gameOrTicketSearchOption))
    }

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
    g: Option[GameOrTicketSearchOption]) = {
    (a, l, g) match {
      case (Some(a), Some(l), Some(g)) => ETag(calculateETag(busyMask, Some(a), Some(l), Some(g))) {
        Action { implicit request =>
          val calendar = calendarFactory.create(busyMask, a, l, g)
          val buffer = new StringWriter
          calendarWriter.write(calendar, buffer)
          val output = buffer.toString
          Ok(output).as(calendarWriter.mimeType)
        }
      }
      case _ => Action { implicit request => NotFound }
    }
  }

  def calculateETag(parts: Option[Any]*): String = {
    val allParts = (lastUpdated when) :: parts.toList
    val toStringFactory = { any: Option[Any] => 
      any match {
        case Some(any) => any.toString
        case None => ""
      }
    }
    allParts.map(toStringFactory).mkString(",")
  }

}