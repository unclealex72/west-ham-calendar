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

package uk.co.unclealex.hammers.calendar.server.calendar.google;

import java.util.Map;

import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;


/**
 * A factory for getting all google calendars.
 * @author alex
 *
 */
public interface GoogleCalendarFactory {

	/**
	 * Get all {@link GoogleCalendar}s by the {@link CalendarType} they represent.
	 * @return A map of all {@link GoogleCalendar}s by the {@link CalendarType} they represent.
	 */
	Map<CalendarType, GoogleCalendar> getGoogleCalendarsByCalendarType();
	
	/**
	 * Get a {@link GoogleCalendar} that represents the given calendar type.
	 * @param calendarType The {@link CalendarType} that the returned {@link GoogleCalendar} represents.
	 * @return A {@link GoogleCalendar} that represents the given calendar type.
	 */
	GoogleCalendar getGoogleCalendar(CalendarType calendarType);
}
