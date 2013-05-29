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

package uk.co.unclealex.hammers.calendar.server.dates

import org.joda.time.DateTime

/**
 * An object that can be used to decorate date parsers so that the year can be explicitly set.
 * @author alex
 *
 */
object YearSettingDateParserFactory {

  def setYear(dateTime: DateTime, yearDeterminingDate: DateTime, yearDeterminingDateIsLaterThanTheDate: Boolean) =
    yearSetter(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate)(dateTime)

  def yearSetter(yearDeterminingDate: DateTime, yearDeterminingDateIsLaterThanTheDate: Boolean): DateTime => DateTime = {
    dtWithoutYear =>
      {
        val dateTimeWithYear = dtWithoutYear.withYear(yearDeterminingDate.getYear)
        if (yearDeterminingDateIsLaterThanTheDate && yearDeterminingDate.isBefore(dateTimeWithYear)) {
          dateTimeWithYear.minusYears(1)
        } else if (!yearDeterminingDateIsLaterThanTheDate && yearDeterminingDate.isAfter(dateTimeWithYear)) {
          dateTimeWithYear.plusYears(1)
        } else {
          dateTimeWithYear
        }
      }
  }

  /**
   * Decorate a date parser so that it returns a date with a given year.
   *
   * @param yearDeterminingDate
   *          The {@link DateTime} that should be used to determine the year to
   *          add.
   * @param yearDeterminingDateIsLaterThanTheDate
   *          True if the year determining date is later than the date the year
   *          needs to be added to, false otherwise.
   * @return A {@link DateParser} decorator.
   */
  def apply(yearDeterminingDate: DateTime, yearDeterminingDateIsLaterThanTheDate: Boolean): DateParser => DateParser = {
    dateParser =>
      val ys = yearSetter(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate)
      new DateParser() {
        override def parse(str: String) = dateParser.parse(str) map ys
        override def find(str: String) = dateParser.find(str) map ys
      }
  }
}