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
package uk.co.unclealex.hammers.calendar.google;

public abstract class AbstractGoogleCalendar implements GoogleCalendar {

	private String i_calendarTitle;
	private String i_description;
	private boolean i_shared;
	private boolean i_busy;
	private Integer i_reminderInMinutes;
	private int i_durationInHours;
	private String i_defaultColour;
	
	public String getCalendarTitle() {
		return i_calendarTitle;
	}

	public void setCalendarTitle(String calendarTitle) {
		i_calendarTitle = calendarTitle;
	}

	public String getDescription() {
		return i_description;
	}

	public void setDescription(String description) {
		i_description = description;
	}

	public boolean isShared() {
		return i_shared;
	}

	public void setShared(boolean shared) {
		i_shared = shared;
	}

	public Integer getReminderInMinutes() {
		return i_reminderInMinutes;
	}

	public void setReminderInMinutes(Integer reminderInMinutes) {
		i_reminderInMinutes = reminderInMinutes;
	}

	public int getDurationInHours() {
		return i_durationInHours;
	}

	public void setDurationInHours(int durationInHours) {
		i_durationInHours = durationInHours;
	}

	public boolean isBusy() {
		return i_busy;
	}

	public void setBusy(boolean busy) {
		i_busy = busy;
	}

	public String getDefaultColour() {
		return i_defaultColour;
	}

	public void setDefaultColour(String defaultColour) {
		i_defaultColour = defaultColour;
	}

}
