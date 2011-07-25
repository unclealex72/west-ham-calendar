/**
 * 
 */
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;


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
public class CalendarConfiguration implements Comparable<CalendarConfiguration>, Serializable {

	private CalendarType i_calendarType;
	private Integer i_reminderInMinutes;
	private CalendarColour i_colour;
	private boolean i_busy;
	private boolean i_shared;
	private boolean i_selected;
	private String i_calendarTitle;
	private String i_description;
	private boolean i_persisted;
	
	protected CalendarConfiguration() {
		super();
	}

	public CalendarConfiguration(CalendarType calendarType, Integer reminderInMinutes, CalendarColour colour,
			boolean busy, boolean shared, boolean selected, String calendarTitle, String description, boolean persisted) {
		super();
		i_calendarType = calendarType;
		i_reminderInMinutes = reminderInMinutes;
		i_colour = colour;
		i_busy = busy;
		i_shared = shared;
		i_selected = selected;
		i_calendarTitle = calendarTitle;
		i_description = description;
		i_persisted = persisted;
	}

	@Override
	public int compareTo(CalendarConfiguration o) {
		return getCalendarType().compareTo(o.getCalendarType());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CalendarConfiguration && compareTo((CalendarConfiguration) obj) == 0;
	}

	public CalendarType getCalendarType() {
		return i_calendarType;
	}

	public void setCalendarType(CalendarType calendarType) {
		i_calendarType = calendarType;
	}

	public Integer getReminderInMinutes() {
		return i_reminderInMinutes;
	}

	public void setReminderInMinutes(Integer reminderInMinutes) {
		i_reminderInMinutes = reminderInMinutes;
	}

	public CalendarColour getColour() {
		return i_colour;
	}

	public void setColour(CalendarColour colour) {
		i_colour = colour;
	}

	public boolean isBusy() {
		return i_busy;
	}

	public void setBusy(boolean busy) {
		i_busy = busy;
	}

	public boolean isShared() {
		return i_shared;
	}

	public void setShared(boolean shared) {
		i_shared = shared;
	}

	public boolean isSelected() {
		return i_selected;
	}

	public void setSelected(boolean selected) {
		i_selected = selected;
	}

	protected void setCalendarTitle(String calendarTitle) {
		i_calendarTitle = calendarTitle;
	}
	
	public String getCalendarTitle() {
		return i_calendarTitle;
	}

	protected void setDescription(String description) {
		i_description = description;
	}
	
	public String getDescription() {
		return i_description;
	}

	public boolean isPersisted() {
		return i_persisted;
	}

	protected void setPersisted(boolean persisted) {
		i_persisted = persisted;
	}
	
}
