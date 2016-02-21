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

import java.sql.Timestamp
import java.util.Date

import org.joda.time.{DateTime, DateTimeConstants}

/**
 * Implicit predicates for DateTimes.
 * @author alex
 *
 */
object DateTimeImplicits {

  /**
   * Order by the number of milliseconds since the epoch.
   */
  implicit val ord = Ordering.by((dt: DateTime) => dt.getMillis)

  implicit val asSharedDate: DateTime => SharedDate = dt => {
    val offset = dt.getChronology.getZone.getOffset(dt) / 60000l
    SharedDate(
      dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, dt.getHourOfDay,
      dt.getMinuteOfHour, dt.getSecondOfMinute, (offset / 60).toInt, Math.abs(offset % 60).toInt)
  }

  implicit val asOptionalSharedDate: Option[DateTime] => Option[SharedDate] = odt => odt.map(asSharedDate)

  implicit val convertFromJdbc: Timestamp => DateTime = t => JodaDateTime(t)
  implicit val convertToJdbc: DateTime => Timestamp = t => new Timestamp(t.getMillis)

  implicit val oConvertFromJdbc: Option[Timestamp] => Option[DateTime] = _.map(convertFromJdbc)
  implicit val oConvertToJdbc: Option[DateTime] => Option[Timestamp] = _.map(convertToJdbc)

  implicit class Implicits(dateTime: DateTime) {

    /**
     * Check to see if a DateTime represents a week day.
     */
    def isWeekday: Boolean = {
      val dayOfWeek = dateTime.getDayOfWeek
      dayOfWeek != DateTimeConstants.SATURDAY && dayOfWeek != DateTimeConstants.SUNDAY
    }

    /**
     * Check to see if a DateTime represents 3pm on a Saturday.
     */
    def isThreeOClockOnASaturday: Boolean = {
      dateTime.getDayOfWeek == DateTimeConstants.SATURDAY &&
        dateTime.getHourOfDay == 15 &&
        dateTime.getMinuteOfHour == 0
    }

  }

}