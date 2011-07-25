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

import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
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
@Table(name="calendar")
public class CalendarConfiguration extends AbstractBusinessKeyBasedModel<CalendarType, CalendarConfiguration> {

	private Integer i_id;
	private CalendarType i_calendarType;
	private Integer i_reminderInMinutes;
	private String i_googleCalendarId;
	private CalendarColour i_colour;
	private boolean i_busy;
	private boolean i_shared;
	private boolean i_selected;
	
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
		return obj instanceof CalendarConfiguration && isEqual((CalendarConfiguration) obj);
	}
	
	@Enumerated(EnumType.STRING)
	@Column(unique=true, nullable=false)
	public CalendarType getCalendarType() {
		return i_calendarType;
	}
	
	public void setCalendarType(CalendarType calendarType) {
		i_calendarType = calendarType;
	}
	
	@Column(nullable=true)
	public Integer getReminderInMinutes() {
		return i_reminderInMinutes;
	}
	
	public void setReminderInMinutes(Integer reminderInMinutes) {
		i_reminderInMinutes = reminderInMinutes;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public CalendarColour getColour() {
		return i_colour;
	}
	
	public void setColour(CalendarColour colour) {
		i_colour = colour;
	}
	
	@Column(nullable=false)
	public boolean isBusy() {
		return i_busy;
	}
	
	public void setBusy(boolean busy) {
		i_busy = busy;
	}
	
	@Column(nullable=false)
	public boolean isShared() {
		return i_shared;
	}
	
	public void setShared(boolean shared) {
		i_shared = shared;
	}

	@Id @GeneratedValue
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}

	@Column(nullable=false, unique=true)
	public String getGoogleCalendarId() {
		return i_googleCalendarId;
	}

	public void setGoogleCalendarId(String googleCalendarId) {
		i_googleCalendarId = googleCalendarId;
	}

	public boolean isSelected() {
		return i_selected;
	}

	public void setSelected(boolean selected) {
		i_selected = selected;
	}
}
