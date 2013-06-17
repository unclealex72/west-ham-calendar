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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.mvc.Action
import play.api.mvc.Results
import play.api.mvc.WithHeaders
import play.api.mvc.Headers
import com.google.common.net.MediaType
import play.api.http.HeaderNames.CONTENT_TYPE
import scala.collection.GenTraversableOnce
/**
 * A trait that allows controllers to automatically return JSON serialisable objects as strings.
 * @author alex
 *
 */
trait Json extends Results {

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  /**
   * Create an action that returns an object as a JSON serialised string.
   * @param obj The object to serialise.
   */
  def json(block: => Any) = Action { resp =>
    resp.method match {
      // only allow POST to avoid JSON array vunerabilities
      case "POST" => Ok(objectMapper.writeValueAsString(block)).withHeaders(CONTENT_TYPE -> "application/json")
      case _ => NotFound
    }
  }
}