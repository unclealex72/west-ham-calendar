/**
 * Copyright 2010-2012 Alex Jones
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
package dao

import model.FatalError
import java.time.ZonedDateTime

import monads.FO.FutureOption

import scala.concurrent.Future

/**
  * The data access object for {@link Game}s.
 *
 * @author alex
 */
trait FatalErrorDao {

  /**
   * Update a game
   */
  def store(fatalError: FatalError): Future[FatalError]

  /**
   * Find a game by its ID.
   */
  def findById(id: Long): FutureOption[FatalError]

  /**
    * Find all the errors since a given date.
    * @param since
    * @return
    */
  def since(since: ZonedDateTime): Future[List[FatalError]]

  /**
   * Get all known games.
   */
  def getAll: Future[List[FatalError]]
}

