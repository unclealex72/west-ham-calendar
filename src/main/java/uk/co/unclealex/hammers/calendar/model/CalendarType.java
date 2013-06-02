/**
 * Copyright 2012 Alex Jones
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
package uk.co.unclealex.hammers.calendar.model;


/**
 * The different types of calendars that are available.
 * @author alex
 *
 */
public enum CalendarType {
	/**
	 * The calendar type for attended games.
	 */
	ATTENDED(false, "Attended games"),
	
	/**
	 * The calendar type for unattended games.
	 */
	UNATTENDED(false, "Unattended games"),
	
	/**
	 * The calendar type fo home games.
	 */
	HOME(false, "Home games"),
	
	/**
	 * The calendar type for away games.
	 */
	AWAY(false, "Away games"),
	
	/**
	 * The calendar type for all games.
	 */
	ALL(false, "All games"),
	
	/**
	 * The calendar type for all games.
	 */
	TELEVISED(false, "Televised games"),
	
	/**
	 * The calendar type for general sale ticket sales.
	 */
	TICKETS_GENERAL_SALE(true, "General sale"),
	
	/**
	 * The calendar type for Academy member ticket sales.
	 */
	TICKETS_ACADEMY(true, "Academy members"),
	
	/**
	 * The calendar type for season ticket holder ticket sales.
	 */
	TICKETS_SEASON(true, "Season ticket holders"),
	
	/**
	 * The calendar type for priority point holder ticket sales.
	 */
	TICKETS_PRIORITY(true, "Priority point holders"),
	
	/**
	 * The calendar type for Bondholder ticket sales.
	 */
	TICKETS_BONDHOLDERS(true, "Bondholders");

	/**
	 * True if calendar is for ticket sales, false otherwise.
	 */
	private final boolean ticketCalendar;
	
	/**
	 * The display name for calendar type.
	 */
	private final String displayName;
	
	/**
	 * Instantiates a new calendar type.
	 * 
	 * @param ticketCalendar
	 *          the ticket calendar
	 * @param displayName
	 *          the display name
	 */
	private CalendarType(boolean ticketCalendar, String displayName) {
		this.ticketCalendar = ticketCalendar;
		this.displayName = displayName;
	}
	
	/**
	 * Checks if is true if calendar is for ticket sales, false otherwise.
	 * 
	 * @return the true if calendar is for ticket sales, false otherwise
	 */
	public boolean isTicketCalendar() {
		return ticketCalendar;
	}

  /**
	 * Gets the display name for calendar type.
	 * 
	 * @return the display name for calendar type
	 */
  public String getDisplayName() {
    return displayName;
  }
}
