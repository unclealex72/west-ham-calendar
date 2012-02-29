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
package uk.co.unclealex.hammers.calendar.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;


/**
 * A ticketing calendar is used to select the calendar to use to show ticketing
 * information.
 * 
 * @author alex
 * 
 */
@Entity
@Table(name = "ticketing")
public class TicketingCalendar extends AbstractBusinessKeyBasedModel<CalendarType, TicketingCalendar> {

	/**
	 * The primary key of the ticketing calendar.
	 */
	private Integer i_id;

	/**
	 * The {@link CalendarType} of the ticketing calendar.
	 */
	private CalendarType i_calendarType;

	/**
	 * Instantiates a new ticketing calendar.
	 */
	protected TicketingCalendar() {
		// Default constructor for ORMs.
	}

	/**
	 * Instantiates a new ticketing calendar.
	 * 
	 * @param calendarType
	 *          the calendar type
	 */
	public TicketingCalendar(CalendarType calendarType) {
		super();
		i_calendarType = calendarType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public CalendarType getBusinessKey() {
		return getCalendarType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBusinessKey(CalendarType businessKey) {
		setCalendarType(businessKey);
	}

	/**
	 * Gets the {@link CalendarType} of the ticketing calendar.
	 * 
	 * @return the {@link CalendarType} of the ticketing calendar
	 */
	@Enumerated(EnumType.STRING)
	@Column(unique = true, nullable = false)
	public CalendarType getCalendarType() {
		return i_calendarType;
	}

	/**
	 * Sets the {@link CalendarType} of the ticketing calendar.
	 * 
	 * @param calendarType
	 *          the new {@link CalendarType} of the ticketing calendar
	 */
	public void setCalendarType(CalendarType calendarType) {
		i_calendarType = calendarType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return i_id;
	}

	/**
	 * Sets the primary key of the ticketing calendar.
	 * 
	 * @param id
	 *          the new primary key of the ticketing calendar
	 */
	public void setId(Integer id) {
		i_id = id;
	}
}
