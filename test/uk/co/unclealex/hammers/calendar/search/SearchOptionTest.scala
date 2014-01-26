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

import org.specs2.mutable.Specification

/**
 * @author alex
 *
 */
class SearchOptionTest extends Specification {

  "Attended search options" should {
    checkSearchOptions(
      AttendedSearchOption,
      "attended" -> AttendedSearchOption.ATTENDED,
      "unattended" -> AttendedSearchOption.UNATTENDED,
      "anyattendence" -> AttendedSearchOption.ANY)
  }

  "Location search options" should {
    checkSearchOptions(
      LocationSearchOption,
      "home" -> LocationSearchOption.HOME,
      "away" -> LocationSearchOption.AWAY,
      "anylocation" -> LocationSearchOption.ANY)
  }

  "Game or ticket search options" should {
    checkSearchOptions(
      GameOrTicketSearchOption,
      "games" -> GameOrTicketSearchOption.GAME,
      "bondholders" -> GameOrTicketSearchOption.BONDHOLDERS,
      "prioritypoint" -> GameOrTicketSearchOption.PRIORITY_POINT,
      "season" -> GameOrTicketSearchOption.SEASON,
      "academy" -> GameOrTicketSearchOption.ACADEMY,
      "academypostal" -> GameOrTicketSearchOption.ACADEMY_POSTAL,
      "general" -> GameOrTicketSearchOption.GENERAL_SALE,
      "generalpostal" -> GameOrTicketSearchOption.GENERAL_SALE_POSTAL)
  }

  def checkSearchOptions[E <: SearchOption](obj: SearchOptionLike[E], expectedResults: Pair[String, E]*) = {
    "have the correct number of options" in {
      obj.values must have size(expectedResults.size)
    }
    expectedResults foreach {
      case (key, value) =>
        s"return $value the token $key" in {
          obj(key) must be equalTo (Some(value))
        }
    }
  }
}