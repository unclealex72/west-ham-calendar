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
package dates

import javax.inject.Inject
import java.time.{LocalDate, ZonedDateTime}

import cats.data.NonEmptyList
/**
 * The default implementation of {@link DateService}.
 *
 * @author alex
 *
 */
class DateParserFactoryImpl @Inject()(implicit val zonedDateTimeFactory: ZonedDateTimeFactory) extends DateParserFactory {

  def makeParser(yearDeterminingDate: ZonedDateTime,
    yearDeterminingDateIsLaterThanTheDate: Boolean, possiblyYearlessDateFormats: Seq[String]) =
    new ChainingDateParser(possiblyYearlessDateFormats map (
      new PossiblyYearlessDateParser(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, _)))

  override def forSeason(year: Int, possiblyYearlessDateFormats: NonEmptyList[String]): DateParser = {
    val firstDayOfSeason = LocalDate.of(year, 7, 1).atStartOfDay(zonedDateTimeFactory.zoneId)
    makeParser(firstDayOfSeason, yearDeterminingDateIsLaterThanTheDate = false, possiblyYearlessDateFormats.toList)
  }

}
