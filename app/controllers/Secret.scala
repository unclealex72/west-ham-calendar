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

import play.api.mvc.Action
import play.api.mvc.Results.NotFound
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result

/**
 * A trait that defines a secret action that is kept secret(ish) by its path containing a random string
 * @author alex
 *
 */
trait Secret {

  /**
   * The secret part of the path.
   */
  val secret: String

  def SecretResult(secretPayload: String)(r: Result) = SecretAction(secretPayload) { implicit request =>
    r
  }

  def SecretAction(secretPayload: String)(f: Request[AnyContent] => Result) = Action { implicit request =>
    if (secret == secretPayload) {
      f(request)
    } else {
      NotFound
    }
  }

}