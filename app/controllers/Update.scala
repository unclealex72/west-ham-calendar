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

import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject
import javax.inject.Named
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.Results._
import play.mvc.Controller
import securesocial.core.Authorization
import uk.co.unclealex.hammers.calendar.update.MainUpdateService
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.iteratee.Concurrent
import play.api.mvc.ChunkedResult
import play.api.mvc.ResponseHeader
/**
 * @author alex
 *
 */
class Update @Inject() (
  /**
   * The secret used to protect the update path.
   */
  @Named("secret") val secret: String,
  /**
   * The authorization object used to check a user is authorised.
   */
  authorization: Authorization,
  /**
   * The main update service used to scrape the West Ham site and update game information.
   */
  mainUpdateService: MainUpdateService) extends Controller with Secure with Secret {

  implicit val implicitAuthorization = authorization

  /**
   * Update all games in the database from the web.
   */
  def update(secretPayload: String) = SecretResult(secretPayload) {
    val (enumerator, channel) = Concurrent.broadcast[String]
    scala.concurrent.Future { mainUpdateService.processDatabaseUpdates() }
    Ok.stream(enumerator)
  }

  /**
   * Attend a game.
   */
  def attend(gameId: Long) = SecuredAction(true, authorization) { empty { mainUpdateService attendGame gameId } }

  /**
   * Unattend a game.
   */
  def unattend(gameId: Long) = SecuredAction(true, authorization) { empty { mainUpdateService unattendGame gameId } }

  /**
   * Execute code and return a no-content response.
   */
  def empty(block: => Unit) = (request: Request[Any]) => {
    block
    NoContent
  }
}