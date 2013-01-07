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

package uk.co.unclealex.hammers.calendar.server.tickets;

import uk.co.unclealex.hammers.calendar.server.dao.TicketingCalendarDao;
import uk.co.unclealex.hammers.calendar.server.model.TicketingCalendar;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.common.collect.Iterables;


/**
 * The Class TicketingCalendarServiceImpl.
 * 
 * @author alex
 */
public class TicketingCalendarServiceImpl implements TicketingCalendarService {

	/**
	 * The {@link TicketingCalendarDao} use to persist the ticketing calendar.
	 */
	private TicketingCalendarDao ticketingCalendarDao;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedTicketingCalendar(CalendarType calendarType) throws IllegalArgumentException {
		if (calendarType == null) {
			deselectTicketingCalendar();
		}
		else {
			if (!calendarType.isTicketCalendar()) {
				throw new IllegalArgumentException(calendarType + " is not a ticketing calendar.");
			}
			TicketingCalendarDao ticketingCalendarDao = getTicketingCalendarDao();
			Iterable<TicketingCalendar> ticketingCalendars = ticketingCalendarDao.getAll();
			TicketingCalendar ticketingCalendar;
			if (Iterables.isEmpty(ticketingCalendars)) {
				ticketingCalendar = new TicketingCalendar(calendarType);
			}
			else {
				ticketingCalendar = ticketingCalendars.iterator().next();
				ticketingCalendar.setCalendarType(calendarType);
			}
			ticketingCalendarDao.saveOrUpdate(ticketingCalendar);
		}
	}

	/**
	 * Deselect ticketing calendar.
	 */
	protected void deselectTicketingCalendar() {
		TicketingCalendarDao ticketingCalendarDao = getTicketingCalendarDao();
		for (TicketingCalendar ticketingCalendar : ticketingCalendarDao.getAll()) {
			ticketingCalendarDao.remove(ticketingCalendar.getId());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalendarType getSelectedTicketingCalendar() {
		Iterable<TicketingCalendar> ticketingCalendars = getTicketingCalendarDao().getAll();
		if (Iterables.isEmpty(ticketingCalendars)) {
			return null;
		}
		else {
			return ticketingCalendars.iterator().next().getCalendarType();
		}
	}

	/**
	 * Gets the {@link TicketingCalendarDao} use to persist the ticketing
	 * calendar.
	 * 
	 * @return the {@link TicketingCalendarDao} use to persist the ticketing
	 *         calendar
	 */
	public TicketingCalendarDao getTicketingCalendarDao() {
		return ticketingCalendarDao;
	}

	/**
	 * Sets the {@link TicketingCalendarDao} use to persist the ticketing
	 * calendar.
	 * 
	 * @param ticketingCalendarDao
	 *          the new {@link TicketingCalendarDao} use to persist the ticketing
	 *          calendar
	 */
	public void setTicketingCalendarDao(TicketingCalendarDao ticketingCalendarDao) {
		this.ticketingCalendarDao = ticketingCalendarDao;
	}

}
