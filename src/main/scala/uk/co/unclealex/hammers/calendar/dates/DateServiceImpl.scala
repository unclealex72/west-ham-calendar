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
package uk.co.unclealex.hammers.calendar.dates;

import java.util.List;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.DayOfWeekIgnoringChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import scala.collection.JavaConversions._
/**
 * The default implementation of {@link DateService}.
 *
 * @author alex
 *
 */
class DateServiceImpl extends DateService {

  override def parsePossiblyYearlessDate(date: String, yearDeterminingDate: DateTime,
    yearDeterminingDateIsLaterThanTheDate: Boolean, possiblyYearlessDateFormats: String*): Option[DateTime] = {
    val parser = makeParser(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats)
    parser.parse(date)
  }

  override def findPossiblyYearlessDate(date: String, yearDeterminingDate: DateTime,
    yearDeterminingDateIsLaterThanTheDate: Boolean, possiblyYearlessDateFormats: String*): Option[DateTime] = {
    val parser = makeParser(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats)
    parser.find(date)
  }

  def makeParser(yearDeterminingDate: DateTime,
    yearDeterminingDateIsLaterThanTheDate: Boolean, possiblyYearlessDateFormats: Seq[String]) =
    new ChainingDateParser(possiblyYearlessDateFormats map (
      new PossiblyYearlessDateParser(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, _)))

  override def parseDate(date: String, dateFormats: String*): Option[DateTime] =
    parseOrFindDate(dateFormats, dateParser => dateParser.parse(date))

  override def findDate(date: String, dateFormats: String*): Option[DateTime] =
    parseOrFindDate(dateFormats, dateParser => dateParser.find(date))

  def parseOrFindDate(dateFormats: Seq[String], parseOrFind: DateParser => Option[DateTime]): Option[DateTime] =
    dateFormats.toStream.flatMap(df => parseOrFind(new JodaDateParser(df))).headOption
}
