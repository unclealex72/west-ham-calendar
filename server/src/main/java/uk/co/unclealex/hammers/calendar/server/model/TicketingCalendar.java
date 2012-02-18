/**
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
@Entity
@Table(name="ticketing")
public class TicketingCalendar extends AbstractBusinessKeyBasedModel<CalendarType, TicketingCalendar> {

	private Integer i_id;
	private CalendarType i_calendarType;
	
	protected TicketingCalendar() {
		// Default constructor for ORMs.
	}
	
	public TicketingCalendar(CalendarType calendarType) {
		super();
		i_calendarType = calendarType;
	}

	@Override @Transient
	public CalendarType getBusinessKey() {
		return getCalendarType();
	}
	
	@Override
	public void setBusinessKey(CalendarType businessKey) {
		setCalendarType(businessKey);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TicketingCalendar && isEqual((TicketingCalendar) obj);
	}
	
	@Enumerated(EnumType.STRING)
	@Column(unique=true, nullable=false)
	public CalendarType getCalendarType() {
		return i_calendarType;
	}
	
	public void setCalendarType(CalendarType calendarType) {
		i_calendarType = calendarType;
	}
	
	@Id @GeneratedValue
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}
}
