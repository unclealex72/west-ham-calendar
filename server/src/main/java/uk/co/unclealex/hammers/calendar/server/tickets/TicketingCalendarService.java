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

package uk.co.unclealex.hammers.calendar.server.tickets;

import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

/**
 * The service to select and find the calendar to use for ticketing information.
 * @author alex
 *
 */
public interface TicketingCalendarService {

	/**
	 * Set the selected ticketing calendar type.
	 * @param calendarType The {@link CalendarType} that is to be the ticketing calendar or null to clear.
	 * @throws IllegalArgumentException Thrown if the calendar type is not a ticketing calendar type.
	 */
	void setSelectedTicketingCalendar(CalendarType calendarType) throws IllegalArgumentException;
	
	/**
	 * @return The {@link CalendarType} that has been selected as the primary ticketing calendar or null if one
	 * has not been selected.
	 */
	CalendarType getSelectedTicketingCalendar();
}
