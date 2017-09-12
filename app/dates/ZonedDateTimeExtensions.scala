/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */

package dates

import java.time.{DayOfWeek, ZonedDateTime}
import java.time.DayOfWeek._

/**
 * Implicit predicates for ZonedDateTimes.
 * @author alex
 *
 */
object ZonedDateTimeExtensions {

  /**
   * Order by the number of milliseconds since the epoch.
   */
  implicit val zonedDateTimeOrdering: Ordering[ZonedDateTime] = Ordering.by((dt: ZonedDateTime) => dt.toInstant)

  implicit class Implicits(zonedDateTime: ZonedDateTime) {

    private val weekendDays: Seq[DayOfWeek] = Seq(SATURDAY, SUNDAY)
    /**
     * Check to see if a ZonedDateTime represents a week day.
     */
    def isWeekday: Boolean = {
      !weekendDays.contains(zonedDateTime.getDayOfWeek)
    }

    /**
     * Check to see if a ZonedDateTime represents 3pm on a Saturday.
     */
    def isThreeOClockOnASaturday: Boolean = {
      zonedDateTime.getDayOfWeek == SATURDAY &&
        zonedDateTime.getHour == 15 &&
        zonedDateTime.getMinute == 0
    }

  }

}