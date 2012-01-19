/**
 * 
 */
package uk.co.unclealex.hammers.calendar.shared.model;

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
public enum CalendarType {
	ATTENDED(true, false, "Attended games"),
	UNATTENDED(true, false, "Unattended games"), 
	HOME(false, false, "Home games"),
	AWAY(false, false, "Away games"),
	ALL(false, false, "All games"),
	TELEVISED(false, false, "Televised games"),
	TICKETS_GENERAL_SALE(false, true, "General sale"),
	TICKETS_ACADEMY(false, true, "Academy members"),
	TICKETS_SEASON(false, true, "Season ticket holders"), 
	TICKETS_PRIORITY(false, true, "Priority point holders"), 
	TICKETS_BONDHOLDERS(false, true, "Bondholders");
	
	private final boolean i_mandatory;
	private final boolean i_ticketCalendar;
	private final String i_displayName;
	
	private CalendarType(boolean mandatory, boolean ticketCalendar, String displayName) {
		i_mandatory = mandatory;
		i_ticketCalendar = ticketCalendar;
		i_displayName = displayName;
	}
	
	public boolean isMandatory() {
		return i_mandatory;
	}
	
	public boolean isTicketCalendar() {
		return i_ticketCalendar;
	}

  public String getDisplayName() {
    return i_displayName;
  }
}
