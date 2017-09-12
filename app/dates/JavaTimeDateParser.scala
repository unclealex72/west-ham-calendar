/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
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

import java.time.ZonedDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.{ChronoField, TemporalAccessor, TemporalQuery}

import com.typesafe.scalalogging.StrictLogging

import scala.util.Try
/**
 * A date parser that uses Java Time to parse dates.
 * @author alex
 *
 */
class JavaTimeDateParser(dateFormat: String, maybeDefaultYear: Option[Int])(implicit val zonedDateTimeFactory: ZonedDateTimeFactory) extends DateParser with StrictLogging {

  private val dateTimeFormatter: DateTimeFormatter = {
    maybeDefaultYear.foldLeft(new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(dateFormat)) { (builder, year) =>
      builder.parseDefaulting(ChronoField.YEAR, year)
    }.toFormatter.withZone(zonedDateTimeFactory.zoneId)
  }

  private val zonedDateTimeQuery: TemporalQuery[ZonedDateTime] = (temporal: TemporalAccessor) => ZonedDateTime.from(temporal)
  override def parse(str: String): Option[ZonedDateTime] = {
    val tryTime = Try(dateTimeFormatter.parse(str, zonedDateTimeQuery))
    tryTime.toOption
  }

  /**
   * Find a date within a string.
   * @return The found {@link ZonedDateTime} or None if the date could not be parsed.
   */
  override def find(str: String): Option[ZonedDateTime] = {
    val dates = for {
      start <- (0 to str.length).toStream
      end <- Range.inclusive(str.length, start, -1).toStream
      substr = str.substring(start, end)
      date <- parse(substr).toStream
    } yield {
      date
    }
    val maybeDate = dates.headOption
    if (maybeDate.isEmpty) {
      logger.debug(s"Could not find a date time in string '$str' using date format '$dateFormat'")
    }
    maybeDate
  }

}

object JavaTimeDateParser {
  def apply(dateFormat: String, maybeYear: Option[Int])(implicit zonedDateTimeFactory: ZonedDateTimeFactory): JavaTimeDateParser = new JavaTimeDateParser(dateFormat, maybeYear)
  def apply(dateFormat: String)(implicit zonedDateTimeFactory: ZonedDateTimeFactory): JavaTimeDateParser = new JavaTimeDateParser(dateFormat, None)
  def apply(dateFormat: String, year: Int)(implicit zonedDateTimeFactory: ZonedDateTimeFactory): JavaTimeDateParser = new JavaTimeDateParser(dateFormat, Some(year))
}