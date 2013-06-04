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

package uk.co.unclealex.hammers.calendar.model

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
  val season: Int) extends Ordered[GameKey] {

  def compare(other: GameKey) = {
    val orderings: Seq[GameKey => GameKey => Int] =
      Seq(
        gk1 => gk2 => gk1.season - gk2.season,
        gk1 => gk2 => gk1.competition.index compare gk2.competition.index,
        gk1 => gk2 => gk1.opponents compare gk2.opponents,
        gk1 => gk2 => gk1.location.index compare gk2.location.index)
    orderings.toStream.map(f => f(this)(other)) takeWhile (_ != 0) headOption match {
      case Some(cmp) => cmp
      case None => 0
    }
  }
}