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
package uk.co.unclealex.hammers.calendar.search

import scala.collection.immutable.SortedMap

/**
 * An object that contains information on how games can be searched for calendars
 * @author alex
 *
 */
trait SearchOptionLike[E <: SearchOption] {

  trait Value { self: E =>

    /**
     *  The index used for ordering.
     */
    _map += (this.token.toLowerCase -> this)

  }

  /**
   * A list of all the registered instances of this type.
   */
  private var _map = SortedMap.empty[String, E]
  def map: SortedMap[String, E] = _map
  def values = _map.values.toSeq

  def apply(token: String) = map.get(token.toLowerCase)
}

sealed trait SearchOption {
  /**
   * The token used to indentify the search option.
   */
  val token: String
}

/**
 * Location search options
 */
sealed abstract class LocationSearchOption(val token: String) extends LocationSearchOption.Value with SearchOption
object LocationSearchOption extends SearchOptionLike[LocationSearchOption]{
  case object HOME extends LocationSearchOption("home"); HOME
  case object AWAY extends LocationSearchOption("away"); AWAY
  case object ANY extends LocationSearchOption("anylocation"); ANY
}

/**
 * Attended search options
 */
sealed abstract class AttendedSearchOption(val token: String) extends AttendedSearchOption.Value with SearchOption
object AttendedSearchOption extends SearchOptionLike[AttendedSearchOption]{
  case object ATTENDED extends AttendedSearchOption("attended"); ATTENDED
  case object UNATTENDED extends AttendedSearchOption("unattended"); UNATTENDED
  case object ANY extends AttendedSearchOption("anyattendence"); ANY
}

/**
 * Game or ticket search options
 */
sealed abstract class GameOrTicketSearchOption(val token: String) extends GameOrTicketSearchOption.Value with SearchOption
object GameOrTicketSearchOption extends SearchOptionLike[GameOrTicketSearchOption]{
  case object GAME extends GameOrTicketSearchOption("games"); GAME
  case object BONDHOLDERS extends GameOrTicketSearchOption("bondholders"); BONDHOLDERS
  case object PRIORITY_POINT extends GameOrTicketSearchOption("prioritypoint"); PRIORITY_POINT
  case object SEASON extends GameOrTicketSearchOption("season"); SEASON
  case object ACADEMY extends GameOrTicketSearchOption("academy"); ACADEMY
  case object ACADEMY_POSTAL extends GameOrTicketSearchOption("academypostal"); ACADEMY_POSTAL
  case object GENERAL_SALE extends GameOrTicketSearchOption("general"); GENERAL_SALE
  case object GENERAL_SALE_POSTAL extends GameOrTicketSearchOption("generalpostal"); GENERAL_SALE_POSTAL
}
