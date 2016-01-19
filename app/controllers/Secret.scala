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

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
 * A trait that defines a secret action that is kept secret(ish) by its path containing a random string
 * @author alex
 *
 */
trait Secret extends Controller {

  /**
   * The secret part of the path.
   */
  val secret: String

  def Secret[A](secretPayload: String)(action: Action[A]): Action[A] =
    Action.async(action.parser) { request =>
      if (secret == secretPayload) {
        action(request)
      } else {
        Future(NotFound)
      }
    }
}