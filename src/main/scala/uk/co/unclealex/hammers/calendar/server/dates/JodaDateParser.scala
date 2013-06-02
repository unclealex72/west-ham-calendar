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

import org.joda.time.format.DateTimeFormatter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.DayOfWeekIgnoringChronology
import org.joda.time.DateTimeZone
import org.joda.time.chrono.ISOChronology

/**
 * A date parser that uses Joda Time to parse dates.
 * @author alex
 *
 */
class JodaDateParser(dateFormat: String) extends DateParser {

  /**
   * The default (and only) time zone.
   */
  val EUROPE_LONDON = DateTimeZone.forID("Europe/London");

  /**
   * The default (and only) chronology with the default time zone.
   */
  val DEFAULT_CHRONOLOGY = ISOChronology.getInstance(EUROPE_LONDON);

  val dateTimeFormatter =
    DateTimeFormat.forPattern(dateFormat).withZone(EUROPE_LONDON).withChronology(
      new DayOfWeekIgnoringChronology(DEFAULT_CHRONOLOGY))

  override def parse(str: String): Option[DateTime] = try {
    Some(dateTimeFormatter.parseDateTime(str).withChronology(DEFAULT_CHRONOLOGY))
  } catch {
    case e: IllegalArgumentException => None
  }

  /**
   * Find a date within a string.
   * @return The found {@link DateTime} or None if the date could not be parsed.
   */
  override def find(str: String): Option[DateTime] = {
    def findInternal(str: String, maxLength: Int): Option[DateTime] = {
      str length match {
        case 0 => None
        case length => {
          val lengths = math.min(maxLength, length) to (0, -1) toStream
          val dateTimes = lengths map (len => parse(str substring (0, len)))
          dateTimes find (_.isDefined) getOrElse findInternal(str.substring(1), maxLength)
        }
      }
    }
    findInternal(str, dateTimeFormatter.getParser.estimateParsedLength)
  }

}