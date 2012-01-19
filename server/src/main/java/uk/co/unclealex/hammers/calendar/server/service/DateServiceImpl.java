/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.service;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import uk.co.unclealex.hammers.calendar.server.exception.UnparseableDateException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class DateServiceImpl implements DateService {

	protected abstract class DateFormatter {
		private final String i_dateFormat;
		
		public DateFormatter(String dateFormat) {
			super();
			i_dateFormat = dateFormat;
		}

		public abstract Date parseDate(String date) throws ParseException;

		@Override
		public String toString() {
			return getDateFormat();
		}
		
		public String getDateFormat() {
			return i_dateFormat;
		}
	}
	
	protected class StandardDateFormatter extends DateFormatter {

		public StandardDateFormatter(String dateFormat) {
			super(dateFormat);
		}

		@Override
		public Date parseDate(String date) throws ParseException {
			return new SimpleDateFormat(getDateFormat()).parse(date);
		}
		
	}
	
	protected class YearlessDateFormatter extends StandardDateFormatter {
		private final Date i_yearDeterminingDate;
		private final boolean i_yearDeterminingDateIsLaterThanTheDate;
		
		public YearlessDateFormatter(String dateFormat, Date yearDeterminingDate,
				boolean yearDeterminingDateIsLaterThanTheDate) {
			super(dateFormat);
			i_yearDeterminingDate = yearDeterminingDate;
			i_yearDeterminingDateIsLaterThanTheDate = yearDeterminingDateIsLaterThanTheDate;
		}

		@Override
		public Date parseDate(String date) throws ParseException {
			Date yearlessDate = super.parseDate(date);
			return addYearToDate(yearlessDate, getYearDeterminingDate(), isYearDeterminingDateIsLaterThanTheDate());
		}
		
		public Date getYearDeterminingDate() {
			return i_yearDeterminingDate;
		}

		public boolean isYearDeterminingDateIsLaterThanTheDate() {
			return i_yearDeterminingDateIsLaterThanTheDate;
		}
		
		
	}
	protected Date parseDate(String date, URL referringUrl, Iterable<DateFormatter> dateFormatters) throws UnparseableDateException {
		Date parsedDate = null;
		for (Iterator<DateFormatter> iter = dateFormatters.iterator(); parsedDate == null && iter.hasNext(); ) {
			try {
				parsedDate = iter.next().parseDate(date);
			}
			catch (ParseException e) {
				// Try the next one.
			}
		}
		if (parsedDate == null) {
			String message = 
					String.format(
							"Cannot parse date '%s' on page '%s' using the following format strings: ",
							date, referringUrl, Joiner.on(", ").join(dateFormatters));
			throw new UnparseableDateException(message);
		}
		return parsedDate;
		
	}
	
	protected Function<String, DateFormatter> createStandardDateFormatterFunction() {
		return new Function<String, DateFormatter>() {
			@Override
			public DateFormatter apply(String dateFormat) {
				return new StandardDateFormatter(dateFormat);
			}
		};
	}
	
	protected Function<String, DateFormatter> createYearlessDateFormatterFunction(final Date yearDeterminingDate, final boolean yearDeterminingDateIsLaterThanTheDate) {
		return new Function<String, DateFormatter>() {
			@Override
			public DateFormatter apply(String dateFormat) {
				return new YearlessDateFormatter(dateFormat, yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate);
			}
		};
	}

	public Date parseYearlessDate(String date, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, String... dateFormats) throws UnparseableDateException {
		return parseDate(
				date, 
				referringUrl, 
				Iterables.transform(
						Arrays.asList(dateFormats), 
						createYearlessDateFormatterFunction(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate)));
	}
	
	public Date addYearToDate(Date yearlessDate, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate) {
		int determinant = yearDeterminingDateIsLaterThanTheDate?-1:1;
		Calendar cal = new GregorianCalendar();
		cal.setTime(yearlessDate);
		Calendar yearDeterminingCalendar = new GregorianCalendar();
		yearDeterminingCalendar.setTime(yearDeterminingDate);
		int year = yearDeterminingCalendar.get(Calendar.YEAR);
		if ((cal.get(Calendar.MONTH) - yearDeterminingCalendar.get(Calendar.MONTH)) * determinant < 0) {
			year += determinant;
		}
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
	public String printDate(String dateFormat, Date date) {
		return new SimpleDateFormat(dateFormat).format(date);
	}
	
	@Override
	public Date parsePossiblyYearlessDate(
			String date, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, 
			String[] dateFormats, String[] yearlessDateFormats) throws UnparseableDateException {
		Iterable<DateFormatter> dateFormatters = Iterables.concat(
			Iterables.transform(Arrays.asList(dateFormats), createStandardDateFormatterFunction()),
			Iterables.transform(
					Arrays.asList(yearlessDateFormats), 
					createYearlessDateFormatterFunction(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate)));
		return parseDate(date, referringUrl, dateFormatters);
	}

	@Override
	public Date parseDate(String date, URL referringUrl, String... dateFormats) throws UnparseableDateException {
		return parseDate(date, referringUrl, Iterables.transform(Arrays.asList(dateFormats), createStandardDateFormatterFunction()));
	}

	@Override
	public Date parsePossiblyYearlessDate(String date, Date yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, String dateFormat, String yearlessDateFormat)
			throws UnparseableDateException {
		return parsePossiblyYearlessDate(
				date, yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, referringUrl, 
				new String[] { dateFormat }, new String[] { yearlessDateFormat });
	}	
}
