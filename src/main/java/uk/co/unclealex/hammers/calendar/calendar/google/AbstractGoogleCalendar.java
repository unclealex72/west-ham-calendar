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
package uk.co.unclealex.hammers.calendar.calendar.google;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import uk.co.unclealex.hammers.calendar.model.Game;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * The base class for all {@link GoogleCalendar}s.
 * @author alex
 *
 */
public abstract class AbstractGoogleCalendar implements GoogleCalendar {

	/**
	 * The title for calendar.
	 */
	private String calendarTitle;
	
	/**
	 * The description of calendar.
	 */
	private String description;
	
	/**
	 * The length of time (in hours) that a game takes in calendar.
	 */
	private int durationInHours;
	
	/**
	 * True if time in calendar should be marked as busy, false otherwise.
	 */
	private boolean busy;

	/**
	 * Default constructor.
	 */
	protected AbstractGoogleCalendar() {
		super();
	}

	/**
	 * Constructor for testing.
	 * 
	 * @param calendarTitle
	 *          The title of calendar.
	 * @param description
	 *          The description of calendar.
	 * @param durationInHours
	 *          The duration, in hours, of how long an event takes in this
	 *          calendar.
	 * @param busy
	 *          True if the time in calendar should be marked as busy, false
	 *          otherwise.
	 */
	public AbstractGoogleCalendar(String calendarTitle, String description, int durationInHours, boolean busy) {
		super();
		this.calendarTitle = calendarTitle;
		this.description = description;
		this.durationInHours = durationInHours;
		this.busy = busy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function<Game, Interval> toCalendarDateInterval() {
		return new Function<Game, Interval>() {
			@Override
			public Interval apply(Game game) {
				DateTime gameDate = getGameDate(game);
        return gameDate == null ? null : new Interval(gameDate, Duration.standardHours(getDurationInHours()));
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Predicate<Game> toContainsGamePredicate() {
		return new Predicate<Game>() {
			@Override
			public boolean apply(Game game) {
				return contains(game);
			}
		};
	}

	/**
	 * Get the instant that a game starts should it appear on calendar.
	 * @param game The {@link Game} in question.
	 * @return The instant that a game starts should it appear on calendar.
	 */
	public abstract DateTime getGameDate(Game game);
	
	/**
	 * Check to see if a game should appear on calendar.
	 * @param game The {@link Game} to check.
	 * @return True if the given game should appear on calendar, false otherwise.
	 */
	protected abstract boolean contains(Game game);

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of calendar.
	 * 
	 * @param description
	 *          the new description of calendar
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the length of time (in hours) that a game takes in calendar.
	 * 
	 * @return the length of time (in hours) that a game takes in calendar
	 */
	public int getDurationInHours() {
		return durationInHours;
	}

	/**
	 * Sets the length of time (in hours) that a game takes in calendar.
	 * 
	 * @param durationInHours
	 *          the new length of time (in hours) that a game takes in this
	 *          calendar
	 */
	public void setDurationInHours(int durationInHours) {
		this.durationInHours = durationInHours;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCalendarTitle() {
		return calendarTitle;
	}

	/**
	 * Sets the title for calendar.
	 * 
	 * @param calendarTitle
	 *          the new title for calendar
	 */
	public void setCalendarTitle(String calendarTitle) {
		this.calendarTitle = calendarTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * Sets the true if time in calendar should be marked as busy, false
	 * otherwise.
	 * 
	 * @param busy
	 *          the new true if time in calendar should be marked as busy,
	 *          false otherwise
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

}
