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

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

/**
 * A stub class for calendars that can also return the required duration needed
 * to find a calendar.
 * 
 * @author alex
 * 
 */
public abstract class DurationFindingAwareGoogleCalendarDao implements GoogleCalendarDao {

	/**
	 * This method is for testing so that the duration type required to find a
	 * game can be tested.
	 * 
	 * @param calendarId
	 *          The id of the calendar containing the game.
	 * @param gameId
	 *          The id of the game to search for.
	 * @param searchDate
	 *          The search date to try first.
	 * @return The found game id (if any) and the used duration (if any).
	 * @throws IOException
	 * @throws GoogleAuthenticationFailedException
	 */
	public abstract StringAndDurationFieldType findGameAndDurationFieldType(String calendarId, String gameId,
			DateTime searchDate) throws IOException, GoogleAuthenticationFailedException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String findGame(String calendarId, String gameId, DateTime searchDate) throws IOException,
			GoogleAuthenticationFailedException {
		StringAndDurationFieldType stringAndDurationFieldType = findGameAndDurationFieldType(calendarId, gameId, searchDate);
		return stringAndDurationFieldType == null ? null : stringAndDurationFieldType.string;
	}

	/**
	 * @return The array of {@link DurationFieldType}s to use when searching for a game.
	 */
	protected DurationFieldType[] durationFieldTypes() {
		return new DurationFieldType[] { DurationFieldType.hours(),
				DurationFieldType.days(), DurationFieldType.weeks(), DurationFieldType.months(), DurationFieldType.years(),
				null };
	}

	/**
	 * A small class for returning a game id and a duration field type.
	 * 
	 * @author alex
	 * 
	 */
	class StringAndDurationFieldType {

		public StringAndDurationFieldType(String string, DurationFieldType durationFieldType) {
			super();
			this.string = string;
			this.durationFieldType = durationFieldType;
		}

		String string;
		DurationFieldType durationFieldType;
	}

}
