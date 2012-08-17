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
package uk.co.unclealex.hammers.calendar.server.calendar.formatter;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;

import com.google.common.base.Function;


/**
 * A class to format {@link GoogleCalendar}s given their id.
 * @author alex
 * 
 */
public class CalendarFormatter implements Function<String, String> {

	/**
	 * The {@link CalendarConfigurationDao} used to look up calendar ids.
	 */
	private CalendarConfigurationDao i_calendarConfigurationDao;
	
	/**
	 * The {@link GoogleCalendarFactory} used to get {@link GoogleCalendar}s.
	 */
	private GoogleCalendarFactory i_googleCalendarFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String apply(String googleCalendarId) {
		CalendarConfiguration calendarConfiguration = getCalendarConfigurationDao()
				.findByGoogleCalendarId(googleCalendarId);
		if (calendarConfiguration == null) {
			return String.format("{%s:unknown}", googleCalendarId);
		}
		GoogleCalendar googleCalendar = getGoogleCalendarFactory().getGoogleCalendar(
				calendarConfiguration.getCalendarType());
		return String.format("{%s}", googleCalendar.getCalendarTitle());
	}

	/**
	 * Gets the {@link CalendarConfigurationDao} used to look up calendar ids.
	 * 
	 * @return the {@link CalendarConfigurationDao} used to look up calendar ids
	 */
	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	/**
	 * Sets the {@link CalendarConfigurationDao} used to look up calendar ids.
	 * 
	 * @param calendarConfigurationDao
	 *          the new {@link CalendarConfigurationDao} used to look up calendar
	 *          ids
	 */
	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	/**
	 * Gets the {@link GoogleCalendarFactory} used to get {@link GoogleCalendar}s.
	 * 
	 * @return the {@link GoogleCalendarFactory} used to get
	 *         {@link GoogleCalendar}s
	 */
	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return i_googleCalendarFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarFactory} used to get {@link GoogleCalendar}s.
	 * 
	 * @param googleCalendarFactory
	 *          the new {@link GoogleCalendarFactory} used to get
	 *          {@link GoogleCalendar}s
	 */
	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		i_googleCalendarFactory = googleCalendarFactory;
	}
}
