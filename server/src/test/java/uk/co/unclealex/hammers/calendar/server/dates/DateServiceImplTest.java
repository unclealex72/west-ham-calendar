/**
 * Copyright 2011 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.dates;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author alex
 * 
 */
public class DateServiceImplTest {

	@Test
	public void testNextYear() {
		testYear(dayOf(6, 1), dateOf(25, 12, 2012), false, dateOf(6, 1, 2013));
	}

	@Test
	public void testThisYearAtExpectingLater() {
		testYear(dayOf(6, 1), dateOf(5, 1, 2013), false, dateOf(6, 1, 2013));
	}

	@Test
	public void testThisYearAtExpectingEarlier() {
		testYear(dayOf(6, 1), dateOf(5, 9, 2012), true, dateOf(6, 1, 2012));
	}

	@Test
	public void testLastYear() {
		testYear(dayOf(25, 12), dateOf(6, 1, 2012), true, dateOf(25, 12, 2011));
	}

	@Test
	public void testYearAwareParse() {
		PossiblyYearlessDateFormat possiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				"dd/MM[/yyyy] HH:mm");
		testParseAndFind(minuteOf(5, 9, 1972, 9, 12), "05/09/1972 9:12", dateOf(10, 10, 2012), true,
				possiblyYearlessDateFormat);
	}

	@Test
	public void testPossiblyYearlessParse() {
		PossiblyYearlessDateFormat possiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				"dd/MM[/yyyy] HH:mm");
		testParseAndFind(minuteOf(5, 9, 2012, 9, 12), "05/09 9:12", dateOf(10, 10, 2012), true, possiblyYearlessDateFormat);
	}

	@Test
	public void testDefinitelyYearlessParse() {
		PossiblyYearlessDateFormat possiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat("dd/MM HH:mm");
		testParseAndFind(minuteOf(5, 9, 2012, 9, 12), "05/09 9:12", dateOf(10, 10, 2012), true, possiblyYearlessDateFormat);
	}

	@Test
	public void testSecondPassParse() {
		PossiblyYearlessDateFormat firstPossiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				"HH:mm dd/MM[/yyyy]");
		PossiblyYearlessDateFormat secondPossiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				"dd/MM[/yyyy] HH:mm");
		testParseAndFind(minuteOf(5, 9, 2012, 9, 12), "05/09 9:12", dateOf(10, 10, 2012), true,
				firstPossiblyYearlessDateFormat, secondPossiblyYearlessDateFormat);
	}

	@Test
	public void testYearlessWithDayOfWeekParse() {
		PossiblyYearlessDateFormat possiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat("ha EEE dd MMM");
		testParseAndFind(minuteOf(26, 1, 2012, 9, 0), "9am Thu 26 Jan", dateOf(18, 2, 2012), true,
				possiblyYearlessDateFormat);
	}

	@Test
	public void testFailureToParse() {
		PossiblyYearlessDateFormat possiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				"dd/MM[/yyyy] HH:mm");
		testParseAndFind(null, "05:09 9:12", dateOf(10, 10, 2012), true, possiblyYearlessDateFormat);
	}

	public void testParseAndFind(DateTime expectedDateTime, String date, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, PossiblyYearlessDateFormat... possiblyYearlessDateFormats) {
		try {
			DateTime actualDateTime = new DateServiceImpl().parsePossiblyYearlessDate(date, yearDeterminingDate,
					yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats);
			Assert.assertEquals("The wrong date was parsed.", expectedDateTime, actualDateTime);
		}
		catch (UnparseableDateException e) {
			Assert.assertNull("A date could not be parsed", expectedDateTime);
		}
		for (int paddingLength = 1; paddingLength < 10; paddingLength++) {
			String padding = Strings.repeat("x", paddingLength);
			for (String dateContainingString : new String[] { date, padding + date, date + padding, padding + date + padding }) {
				try {
					DateTime actualDateTime = new DateServiceImpl().findPossiblyYearlessDate(dateContainingString,
							yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats);
					Assert.assertEquals("The wrong date was found.", expectedDateTime, actualDateTime);
				}
				catch (UnparseableDateException e) {
					Assert.assertNull("A date could not be found", expectedDateTime);
				}
			}
		}
	}

	@Test
	public void testWeek() {
		DateTime tuesday = dateOf(5, 9, 1972);
		DateTime wednesday = dateOf(6, 9, 1972);
		DateTime thursday = dateOf(7, 9, 1972);
		DateTime friday = dateOf(8, 9, 1972);
		DateTime saturday = dateOf(9, 9, 1972);
		DateTime sunday = dateOf(10, 9, 1972);
		DateTime monday = dateOf(11, 9, 1972);

		DateServiceImpl dateServiceImpl = new DateServiceImpl();

		for (DateTime dateTime : new DateTime[] { tuesday, wednesday, thursday, friday, monday }) {
			Assert.assertTrue(dateTime + " was not identified as a weekday.", dateServiceImpl.isWeekday(dateTime));
		}

		for (DateTime dateTime : new DateTime[] { saturday, sunday }) {
			Assert.assertFalse(dateTime + " was wrongly identified as a weekday.", dateServiceImpl.isWeekday(dateTime));
		}
	}

	@Test
	public void test3pmSaturday() {
		Map<DateTime, Boolean> expectedResultsByDateTime = Maps.newLinkedHashMap();
		expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 15, 0), true);
		expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 12, 0), false);
		expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 15, 30), false);
		expectedResultsByDateTime.put(minuteOf(9, 9, 1972, 17, 30), false);
		expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 15, 0), false);
		expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 12, 0), false);
		expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 15, 30), false);
		expectedResultsByDateTime.put(minuteOf(10, 9, 1972, 17, 30), false);
		DateServiceImpl dateServiceImpl = new DateServiceImpl();
		for (Entry<DateTime, Boolean> entry : expectedResultsByDateTime.entrySet()) {
			DateTime dateTime = entry.getKey();
			boolean expectedResult = entry.getValue();
			boolean actualResult = dateServiceImpl.isThreeOClockOnASaturday(dateTime);
			Assert.assertEquals("The wrong result was found checking whether " + dateTime + " is 3pm on a Saturday.",
					expectedResult, actualResult);
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
	protected DateTime minuteOf(int day, int month, int year, int hour, int minute) {
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
	protected DateTime dateOf(int day, int month, int year) {
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
	protected DateTime dayOf(int day, int month) {
		return new DateTime(1970, month, day, 0, 0, 0, 0, DateTimeZone.forID("Europe/London"));
	}

	protected void testYear(DateTime dateTime, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, DateTime expectedDateTime) {
		DateServiceImpl dateServiceImpl = new DateServiceImpl();
		DateTime actualDateTime = dateServiceImpl.withYear(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate,
				dateTime);
		Assert.assertEquals("The wrong date time was produced.", expectedDateTime, actualDateTime);
	}
}
