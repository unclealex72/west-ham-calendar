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

import com.typesafe.scalalogging.slf4j.StrictLogging
import play.api.http.HeaderNames
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import play.api.mvc.Results.NotModified

import scala.concurrent.Future

/**
 * A trait for controllers that allows for ETag headers to be queried and a 304 No Content to be returned if
 * the resource has not changed.
 */
trait Etag extends StrictLogging {

  def ETag[A](calculatedETag: String)(action: Action[A]): Action[A] =
    Action.async(action.parser) { implicit request =>
      val quotedETag = '"' + calculatedETag + '"'
      val modified = request.headers.get(HeaderNames.IF_NONE_MATCH) match {
        case None => {
          logger.info(s"No ${HeaderNames.IF_NONE_MATCH} header was sent for resource $request.uri")
          true
        }
        case Some(etag) => {
          logger.info(s"Header ${HeaderNames.IF_NONE_MATCH} for $request.uri has value $etag")
          etag != quotedETag
        }
      }
      val response =
      if (modified) {
        logger.info(s"Request $request.uri has been modified.")
        action(request)
      } else {
        logger.info(s"Request $request.uri has not been modified.")
        Future.successful(NotModified)
      }
      response.map(_.withHeaders(HeaderNames.ETAG -> quotedETag))
    }

}