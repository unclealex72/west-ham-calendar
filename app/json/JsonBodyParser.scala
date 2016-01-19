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

package json

import argonaut.DecodeJson
import play.api.libs.json.{Json => PlayJson}
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers.parse.json

import scala.concurrent.ExecutionContext
import scalaz.Validation

/**
 * Parse JSON bodies into objects using {@link Json} support.
 * @author alex
 *
 */
object JsonBodyParser {

  /**
   * Parse the body as Json if the Content-Type is text/json or application/json.
   * TODO: Error handling.
   */
  def apply[T](implicit e: DecodeJson[T], ec: ExecutionContext): BodyParser[Validation[String, T]] = json map {
    jsValue => Json.read[T](PlayJson.stringify(jsValue))
  }

}