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

package uk.co.unclealex.hammers.calendar.server.dates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * The Class DateServiceImplTest.
 * 
 * @author alex
 */
public class DateServiceImplTest {

  /**
   * Test next year.
   */
  @Test
  public void testNextYear() {
    testYear(dayOf(6, 1), dateOf(25, 12, 2012), false, dateOf(6, 1, 2013));
  }

  /**
   * Test year at expecting later.
   */
  @Test
  public void testThisYearAtExpectingLater() {
    testYear(dayOf(6, 1), dateOf(5, 1, 2013), false, dateOf(6, 1, 2013));
  }

  /**
   * Test year at expecting earlier.
   */
  @Test
  public void testThisYearAtExpectingEarlier() {
    testYear(dayOf(6, 1), dateOf(5, 9, 2012), true, dateOf(6, 1, 2012));
  }

  /**
   * Test last year.
   */
  @Test
  public void testLastYear() {
    testYear(dayOf(25, 12), dateOf(6, 1, 2012), true, dateOf(25, 12, 2011));
  }

  /**
   * Test year aware parse.
   */
  @Test
  public void testYearAwareParse() {
    testParseAndFind(minuteOf(5, 9, 1972, 9, 12), "05/09/1972 9:12", dateOf(10, 10, 2012), true, "dd/MM[/yyyy] HH:mm");
  }

  /**
   * Test possibly yearless parse.
   */
  @Test
  public void testPossiblyYearlessParse() {
    testParseAndFind(minuteOf(5, 9, 2012, 9, 12), "05/09 9:12", dateOf(10, 10, 2012), true, "dd/MM[/yyyy] HH:mm");
  }

  /**
   * Test definitely yearless parse.
   */
  @Test
  public void testDefinitelyYearlessParse() {
    testParseAndFind(minuteOf(5, 9, 2012, 9, 12), "05/09 9:12", dateOf(10, 10, 2012), true, "dd/MM HH:mm");
  }

  /**
   * Test second pass parse.
   */
  @Test
  public void testSecondPassParse() {
    testParseAndFind(
        minuteOf(5, 9, 2012, 9, 12),
        "05/09 9:12",
        dateOf(10, 10, 2012),
        true,
        "HH:mm dd/MM[/yyyy]",
        "dd/MM[/yyyy] HH:mm");
  }

  /**
   * Test yearless with day of week parse.
   */
  @Test
  public void testYearlessWithDayOfWeekParse() {
    testParseAndFind(minuteOf(26, 1, 2012, 9, 0), "9am Thu 26 Jan", dateOf(18, 2, 2012), true, "ha EEE dd MMM");
  }

  /**
   * Test failure to parse.
   */
  @Test
  public void testFailureToParse() {
    testParseAndFind(null, "05:09 9:12", dateOf(10, 10, 2012), true, "dd/MM[/yyyy] HH:mm");
  }

  /**
   * Test parse and find.
   * 
   * @param expectedDateTime
   *          the expected date time
   * @param date
   *          the date
   * @param yearDeterminingDate
   *          the year determining date
   * @param yearDeterminingDateIsLaterThanTheDate
   *          the year determining date is later than the date
   * @param possiblyYearlessDateFormats
   *          the possibly yearless date formats
   */
  public void testParseAndFind(
      final DateTime expectedDateTime,
      final String date,
      final DateTime yearDeterminingDate,
      final boolean yearDeterminingDateIsLaterThanTheDate,
      final String... possiblyYearlessDateFormats) {
    try {
      final DateTime actualDateTime =
          new DateServiceImpl().parsePossiblyYearlessDate(
              date,
              yearDeterminingDate,
              yearDeterminingDateIsLaterThanTheDate,
              possiblyYearlessDateFormats);
      assertEquals("The wrong date was parsed from '" + date + "'.", expectedDateTime, actualDateTime);
    }
    catch (final UnparseableDateException e) {
      assertNull("A date could not be parsed from '" + date + "'.", expectedDateTime);
    }
    for (int paddingLength = 1; paddingLength < 10; paddingLength++) {
      final String padding = Strings.repeat("x", paddingLength);
      for (final String dateContainingString : new String[] {
          date,
          padding + date,
          date + padding,
          padding + date + padding }) {
        try {
          final DateTime actualDateTime =
              new DateServiceImpl().findPossiblyYearlessDate(
                  dateContainingString,
                  yearDeterminingDate,
                  yearDeterminingDateIsLaterThanTheDate,
                  possiblyYearlessDateFormats);
          assertEquals(
              "The wrong date was found within '" + dateContainingString + "'.",
              expectedDateTime,
              actualDateTime);
        }
        catch (final UnparseableDateException e) {
          assertNull("A date could not be found within '" + dateContainingString + "'.", expectedDateTime);
        }
      }
    }
  }

  /**
   * Test week.
   */
  @Test
  public void testWeek() {
    final DateTime tuesday = dateOf(5, 9, 1972);
    final DateTime wednesday = dateOf(6, 9, 1972);
    final DateTime thursday = dateOf(7, 9, 1972);
    final DateTime friday = dateOf(8, 9, 1972);
    final DateTime saturday = dateOf(9, 9, 1972);
    final DateTime sunday = dateOf(10, 9, 1972);
    final DateTime monday = dateOf(11, 9, 1972);

    for (final DateTime dateTime : new DateTime[] { tuesday, wednesday, thursday, friday, monday }) {
      assertTrue(dateTime + " was not identified as a weekday.", DateTimeImplicits.isWeekday(dateTime));
    }

    for (final DateTime dateTime : new DateTime[] { saturday, sunday }) {
      assertFalse(dateTime + " was wrongly identified as a weekday.", DateTimeImplicits.isWeekday(dateTime));
    }
  }

  /**
   * Test3pm saturday.
   */
  @Test
  public void test3pmSaturday() {
    final Map<DateTime, Boolean> expectedResultsByDateTime = Maps.newLinkedHashMap();
    expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 15, 0), true);
    expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 12, 0), false);
    expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 15, 30), false);
    expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 17, 30), false);
    expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 15, 0), false);
    expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 12, 0), false);
    expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 15, 30), false);
    expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 17, 30), false);
    for (final Entry<DateTime, Boolean> entry : expectedResultsByDateTime.entrySet()) {
      final DateTime dateTime = entry.getKey();
      final boolean expectedResult = entry.getValue();
      final boolean actualResult = DateTimeImplicits.isThreeOClockOnASaturday(dateTime);
      assertEquals(
          "The wrong result was found checking whether " + dateTime + " is 3pm on a Saturday.",
          expectedResult,
          actualResult);
    }
  }

  /**
   * Create a new date time.
   * 
   * @param day
   *          The day.
   * @param month
   *          The month.
   * @param year
   *          The year,
   * @param hour
   *          The hour.
   * @param minute
   *          The minute.
   * @return A {@link DateTime} for the supplied information.
   */
  protected DateTime minuteOf(final int day, final int month, final int year, final int hour, final int minute) {
    return new DateTime(year, month, day, hour, minute, 0, 0, DateTimeZone.forID("Europe/London"));
  }

  /**
   * Create a new date time.
   * 
   * @param day
   *          The day.
   * @param month
   *          The month
   * @param year
   *          The year
   * @return A {@link DateTime} for the supplied information.
   */
  protected DateTime dateOf(final int day, final int month, final int year) {
    return minuteOf(day, month, year, 0, 0);
  }

  /**
   * Create a new date time with an unspecified year.
   * 
   * @param day
   *          The day.
   * @param month
   *          The month
   * @return A {@link DateTime} for the supplied information.
   */
  protected DateTime dayOf(final int day, final int month) {
    return new DateTime(1970, month, day, 0, 0, 0, 0, DateTimeZone.forID("Europe/London"));
  }

  /**
   * Test year.
   * 
   * @param dateTime
   *          the date time
   * @param yearDeterminingDate
   *          the year determining date
   * @param yearDeterminingDateIsLaterThanTheDate
   *          the year determining date is later than the date
   * @param expectedDateTime
   *          the expected date time
   */
  protected void testYear(
      final DateTime dateTime,
      final DateTime yearDeterminingDate,
      final boolean yearDeterminingDateIsLaterThanTheDate,
      final DateTime expectedDateTime) {
    final DateTime actualDateTime =
        YearSettingDateParserFactory.setYear(dateTime, yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate);
    assertEquals("The wrong date time was produced.", expectedDateTime, actualDateTime);
  }
}
