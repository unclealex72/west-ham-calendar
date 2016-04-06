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
import java.net.URI
import javax.inject.Inject

import cal.{CalendarFactory, CalendarWriter, LinkFactory}
import dao.GameDao
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption}
import security.Definitions._
import update.LastUpdated

import scala.concurrent.ExecutionContext
/**
 * The controller that handles generating calendars.
  *
  * @author alex
 *
 */
class Calendar @Inject() (val secret: SecretToken,
                          val lastUpdated: LastUpdated,
                          val calendarFactory: CalendarFactory,
                          val gameDao: GameDao,
                          val calendarWriter: CalendarWriter,
                          val messagesApi: MessagesApi,
                          val env:DefaultEnvironment,
                          implicit val ec: ExecutionContext) extends Secret with Etag {

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

  def calendar(busyMask: Option[Boolean],
          oa: Option[AttendedSearchOption],
          ol: Option[LocationSearchOption],
          og: Option[GameOrTicketSearchOption]) = {
    val oCalendarAction = for {
      a <- oa
      l <- ol
      g <- og
    } yield ETag(calculateETag(busyMask, oa, ol, og)) {
      Action.async { implicit request =>
        gameDao.search(a, l, g).map { games =>
          val calendar = calendarFactory.create(games, busyMask, a, l, g)
          val buffer = new StringWriter
          val linkFactory = new LinkFactory {
            override def locationLink(gameId: Long): URI = {
              new URI(routes.Location.location(gameId).absoluteURL())
            }
          }
          calendarWriter.write(calendar, buffer, linkFactory)
          val output = buffer.toString
          Ok(output).as(calendarWriter.mimeType)
        }
      }
    }
    oCalendarAction.getOrElse {
      Action(implicit request => NotFound)
    }
  }

  def calculateETag(parts: Option[Any]*): String = {
    val allParts = lastUpdated.when :: parts.toList
    val toStringFactory = { any: Option[Any] => 
      any match {
        case Some(a) => a.toString
        case None => ""
      }
    }
    allParts.map(toStringFactory).mkString(",")
  }

}