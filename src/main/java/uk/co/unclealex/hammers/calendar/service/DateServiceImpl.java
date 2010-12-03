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
package uk.co.unclealex.hammers.calendar.service;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.unclealex.hammers.calendar.exception.UnparseableDateException;

public class DateServiceImpl implements DateService {

	private Map<String, DateFormat> i_dateFormatCache = new HashMap<String, DateFormat>();
	
	protected DateFormat getDateFormat(String dateFormat) {
		Map<String, DateFormat> dateFormatCache = getDateFormatCache();
		DateFormat fmt = dateFormatCache.get(dateFormat);
		if (fmt == null) {
			fmt = new SimpleDateFormat(dateFormat);
			dateFormatCache.put(dateFormat, fmt);
		}
		return fmt;
	}
	
	public Date parseDate(String dateFormat, String date, URL referringUrl) throws UnparseableDateException {
		try {
			date = date.replaceAll("noon", "pm");
			return getDateFormat(dateFormat).parse(date);
		}
		catch (ParseException e) {
			throw new UnparseableDateException("Cannot parse date " + date + " using format string " + dateFormat + " on page " + referringUrl, e);
		}
	}
	
	public Date parseYearlessDate(String dateFormat, String date, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl) throws UnparseableDateException {
		Date yearlessDate = parseDate(dateFormat, date, referringUrl);
		return addYearToDate(yearlessDate, yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate);
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
		return getDateFormat(dateFormat).format(date);
	}

	@Override
	public Date parsePossiblyYearlessDate(
			String dateFormat, String yearlessDateFormat, String date, Date yearDeterminingDate, 
			boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl) throws UnparseableDateException {
		try {
			return getDateFormat(dateFormat).parse(date);
		}
		catch (ParseException e) {
			return parseYearlessDate(yearlessDateFormat, date, yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, referringUrl);
		}
	}
	
	public Map<String, DateFormat> getDateFormatCache() {
		return i_dateFormatCache;
	}
}
