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
package uk.co.unclealex.hammers.calendar.server.calendar.google;

import org.joda.time.Interval;

import uk.co.unclealex.hammers.calendar.server.model.Game;

import com.google.common.base.Function;
import com.google.common.base.Predicate;


/**
 * A {@link GoogleCalendar} represents a calendar within Google. It both
 * describes how the calendar is shown in calendar applications as well as
 * whether a game should be shown as an event in i_calendar and, if so, how.
 * 
 * @author alex
 * 
 */
public interface GoogleCalendar {

	/**
	 * Get the calendar's title.
	 * 
	 * @return The calendar's title.
	 */
	String getCalendarTitle();

	/**
	 * Get the calendar's description.
	 * 
	 * @return The calendar's description.
	 */
	String getDescription();

	/**
	 * Create a {@link Function} that describes the start and end time of a given.
	 * 
	 * @return A {@link Function} that transforms a {@link Game} into an
	 *         {@link Game}. {@link Interval} describing its beginning and end
	 *         time.
	 */
	Function<Game, Interval> toCalendarDateInterval();

	/**
	 * Create a {@link Predicate} that can be used to see if a {@link Game} should
	 * be shown on i_calendar.
	 * 
	 * @return A {@link Predicate} that can be used to see if a {@link Game}
	 *         should be shown on i_calendar.
	 */
	Predicate<Game> toContainsGamePredicate();

	/**
	 * A flag to indicate whether any games on i_calendar should be marked as
	 * busy.
	 * 
	 * @return True if games in i_calendar are marked as busy, false otherwise.
	 */
	boolean isBusy();
}
