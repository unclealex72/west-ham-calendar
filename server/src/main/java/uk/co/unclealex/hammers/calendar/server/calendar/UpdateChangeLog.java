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

package uk.co.unclealex.hammers.calendar.server.calendar;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.model.Game;

import com.google.common.base.Objects;

/**
 * A class to record a change made to a google calendar.
 * 
 * @author alex
 * 
 */
public class UpdateChangeLog implements Comparable<UpdateChangeLog> {

	/**
	 * A enumeration to model whether a game was added to a calendar, removed from
	 * a calendar or updated.
	 * 
	 * @author alex
	 * 
	 */
	public static enum Action {
		ADDED, REMOVED, UPDATED
	}

	private final Action i_action;
	private final Game i_game;
	private final GoogleCalendar i_googleCalendar;

	public UpdateChangeLog(Action action, Game game, GoogleCalendar googleCalendar) {
		super();
		i_action = action;
		i_game = game;
		i_googleCalendar = googleCalendar;
	}

	@Override
	public int compareTo(UpdateChangeLog o) {
		int cmp = getGoogleCalendar().getCalendarTitle().compareTo(o.getGoogleCalendar().getCalendarTitle());
		if (cmp == 0) {
			cmp = getGame().getGameKey().compareTo(o.getGame().getGameKey());
		}
		if (cmp == 0) {
			cmp = getAction().compareTo(o.getAction());
		}
		return cmp;
	}

	@Override
	public String toString() {
		return String.format("{Calendar: %s, Game: %s, Action: %s}", getGoogleCalendar().getCalendarTitle(), getGame(),
				getAction());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof UpdateChangeLog && compareTo((UpdateChangeLog) obj) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getGoogleCalendar(), getGame(), getAction());
	}

	/**
	 * @return The action that was performed: added, removed or update.
	 */
	public Action getAction() {
		return i_action;
	}

	/**
	 * @return The game that was involved in this change.
	 */
	public Game getGame() {
		return i_game;
	}

	/**
	 * @return The calendar that was involved in this change.
	 */
	public GoogleCalendar getGoogleCalendar() {
		return i_googleCalendar;
	}
}
