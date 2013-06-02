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

package uk.co.unclealex.hammers.calendar.server.dates

import org.joda.time.DateTime
import java.util.regex.Pattern

/**
 * A {@link PossiblyYearlessDateFormat} that contains the optional year defining
 * characters in square brackets. If a year pattern exists then it is tried first.
 *
 * @author alex
 *
 */
class PossiblyYearlessDateParser(
  /**
   * The date to used to determine the year.
   */
  yearDeterminingDate: DateTime,
  /**
   * True if the date is earlier than the year determining date, false otherwise.
   */
  yearDeterminingDateIsLaterThanTheDate: Boolean,
  /**
   * The string that will be parsed for a possibly yearless date
   */
  str: String) extends DelegatingDateParser {

  /**
   * The regular expression to find and remove the year part of format strings.
   */
  private val YEAR_STRIPPING_REGEX = """(.*?)\[(.+)\](.*)""".r

  val dateFormats: Array[NeedsYear] = {
    str match {
      case YEAR_STRIPPING_REGEX(preYear, year, postYear) =>
        Array(DoesNotNeedYear(preYear + year + postYear), DoesNeedYear(preYear + postYear))
      case str if str.contains("y") => Array(DoesNotNeedYear(str))
      case str => Array(DoesNeedYear(str))
    }
  }

  val yearSetter = YearSettingDateParserFactory(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate)

  val delegate: DateParser = {
    val dateParsers = dateFormats map {
      case DoesNeedYear(dateFormat) => yearSetter(new JodaDateParser(dateFormat))
      case DoesNotNeedYear(dateFormat) => new JodaDateParser(dateFormat)
    }
    new ChainingDateParser(dateParsers)
  }
}

/**
 * Classes used in case matches to decide whether a date format requires a year setting or not.
 */
sealed abstract class NeedsYear(dateFormat: String)

case class DoesNeedYear(dateFormat: String) extends NeedsYear(dateFormat)
case class DoesNotNeedYear(dateFormat: String) extends NeedsYear(dateFormat)

