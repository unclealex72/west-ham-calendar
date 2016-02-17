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

import play.api.http.HeaderNames._
import play.api.mvc.{Result, Results}
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author alex
 *
 */
trait JsonResults extends Results {

  self: {val ec: ExecutionContext} =>

  private implicit lazy val _JsonResults_ec = ec

  def json[A: Writer](a: A): Result = {
    Ok(write(a, 2)).withHeaders(
      CONTENT_TYPE -> "application/json",
      CACHE_CONTROL -> "max-age=0, no-store, no-cache, must-revalidate",
      PRAGMA -> "no-cache",
      EXPIRES -> "0")
  }

  def jsonFOO[A, B: Writer](fa: Future[Option[A]])(f: A => Option[B]): Future[Result] = fa.map { a =>
    a.flatMap(f) match {
      case Some(b) => json(b)
      case _ => NotFound
    }
  }

  def jsonFO[A, B: Writer](fa: Future[Option[A]])(f: A => B): Future[Result] = jsonFOO(fa)(a => Some(f(a)))

  def jsonF[A, B: Writer](fa: Future[A])(f: A => B): Future[Result] = jsonFO(fa.map(Some(_)))(f)
}

