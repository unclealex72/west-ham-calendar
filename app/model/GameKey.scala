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

package model

/**
 * A class that encapsulates the immutable information that uniquely identifies
 * a game.
 *
 * @author alex
 *
 */
case class GameKey(
  /**
   * The game's competition.
   */
  val competition: Competition,
  /**
   * The game's location.
   */
  val location: Location,
  /**
   * The game's opponents.
   */
  val opponents: String,
  /**
   * The game's season.
   */
  val season: Int)

object GameKey {

  implicit val gameKeyOrdering: Ordering[GameKey] = Ordering.by { gk =>
    (gk.season, Competition.indexOf(gk.competition), gk.opponents, Location.indexOf(gk.location))
  }
}