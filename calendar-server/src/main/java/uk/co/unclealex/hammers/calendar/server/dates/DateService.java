/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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


import org.joda.time.DateTime;



/**
 * This service contains methods for parsing dates in various formats on the
 * West Ham website. All returned {@link DateTime} objects will have their timezone explicitly set
 * to Europe/London.
 * 
 * @author alex
 * 
 */
public interface DateService {

	/**
	 * Parse a date that may or may not have a year, taking the year from another date if need be.
	 * 
	 * @param date
	 *          The string that may contain the date inside it.
	 * @param yearDeterminingDate
	 *          The date to used to determine the year.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the date is earlier than the year determining date, false
	 *          otherwise.
	 * @param possiblyYearlessDateFormats
	 *          The expected formats to try, year aware first.
	 * @return The parsed date.
	 * @throws UnparseableDateException
	 *           Thrown if a date could not be found in any of the supplied formats.
	 */
	DateTime parsePossiblyYearlessDate(String date, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, PossiblyYearlessDateFormat... possiblyYearlessDateFormats)
			throws UnparseableDateException;

	/**
	 * Find a date that may or may not have a year, taking the year from another date if need be.
	 * 
	 * @param date
	 *          The date string to parse.
	 * @param yearDeterminingDate
	 *          The date to used to determine the year.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the date is earlier than the year determining date, false
	 *          otherwise.
	 * @param possiblyYearlessDateFormats
	 *          The expected formats to try, year aware first.
	 * @return The parsed date.
	 * @throws UnparseableDateException
	 *           Thrown if the date was not in any of the supplied formats.
	 */
	DateTime findPossiblyYearlessDate(String date, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, PossiblyYearlessDateFormat... possiblyYearlessDateFormats)
			throws UnparseableDateException;

	/**
	 * Parse a date.
	 * @param date The date to parse
	 * @param dateFormat The date format to use.
	 * @return The parsed date or null if no date could be parsed.
	 */
	DateTime parseDate(String date, String dateFormat);

	/**
	 * Find a date.
	 * @param date The string that may contain the date inside it.
	 * @param dateFormat The date format to use.
	 * @return The found date or null if no date could be found.
	 */
	DateTime findDate(String date, String dateFormat);
	
	/**
	 * Check whether a {@link DateTime} instant is occurs on a week-day.
	 * @param dateTime The {@link DateTime} to check.
	 * @return True if the instant is during the week, false otherwise.
	 */
	boolean isWeekday(DateTime dateTime);
	
	/**
	 * Check whether a {@link DateTime} instant is 3pm on a Saturday.
	 * @param dateTime The {@link DateTime} to check.
	 * @return True if the instant is 3pm on a Saturday, false otherwise.
	 */
	boolean isThreeOClockOnASaturday(DateTime dateTime);
}
