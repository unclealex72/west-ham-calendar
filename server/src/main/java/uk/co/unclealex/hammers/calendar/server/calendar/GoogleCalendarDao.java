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
package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.collect.BiMap;

/**
 * A data access view of the google calendar API with knowledge of how both
 * games and calendars are stored and mapped to google properties.
 * 
 * @author alex
 * 
 */
public interface GoogleCalendarDao {

	/**
	 * Create or update a game.
	 * 
	 * @param calendarId
	 *          The id of the calendar for the game.
	 * @param eventId
	 *          The id of the game event or null if this game is to be created.
	 * @param gameId
	 *          The id of the game.
	 * @param competition
	 *          The competition the game was played in.
	 * @param location
	 *          The game's location.
	 * @param opponents
	 *          The opponents for this game.
	 * @param gameInterval
	 *          The start and end time of this game with respect to the calendar
	 *          type.
	 * @param result
	 *          The game's result.
	 * @param attendence
	 *          The game's attendance.
	 * @param matchReport
	 *          The URL for the match report.
	 * @param televisionChannel
	 *          The TV channel showing the match.
	 * @param busy
	 *          True if the time should be marked as busy, false otherwise.
	 * @return A {@link GameUpdateInformation} bean that describes whether the
	 *         game was updated, created or neither.
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	GameUpdateInformation createOrUpdateGame(String calendarId, String eventId, String gameId, Competition competition,
			Location location, String opponents, Interval gameInterval, String result, Integer attendence,
			String matchReport, String televisionChannel, boolean busy) throws IOException,
			GoogleAuthenticationFailedException;

	/**
	 * Remove a game from the calendar.
	 * 
	 * @param calendarId
	 *          The id of the calendar to search.
	 * @param eventId
	 *          The id of the event to delete.
	 * @param gameId
	 *          The id of the game to delete (for logging purposes).
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	void removeGame(String calendarId, String eventId, String gameId) throws IOException,
			GoogleAuthenticationFailedException;

	/**
	 * Move a game from one calendar to the other.
	 * 
	 * @param sourceCalendarId
	 *          The id of the source calendar.
	 * @param targetCalendarId
	 *          The id of the target calendar.
	 * @param eventId
	 *          The id of the event to move.
	 * @param gameId
	 *          The id of the game to move (for logging purposes).
	 * @param busy
	 *          True if the game should be busy after moving, false otherwise.
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	void moveGame(String sourceCalendarId, String targetCalendarId, String eventId, String gameId, boolean busy)
			throws IOException, GoogleAuthenticationFailedException;

	/**
	 * Find a game in a calendar.
	 * 
	 * @param calendarId
	 *          The id of the calendar to search.
	 * @param gameId
	 *          The id of the game to search for.
	 * @param searchDate
	 *          The date when the game may have been played.
	 * @return Then event id of the game if found, null otherwise.
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	String findGame(String calendarId, String gameId, DateTime searchDate) throws IOException,
			GoogleAuthenticationFailedException;

	/**
	 * Create or update a calendar.
	 * 
	 * @param calendarId
	 *          The id of the calendar to update or null to create a calendar.
	 * @param calendarTitle
	 *          The title of the calendar to update or create.
	 * @param calendarDescription
	 *          The description of the calender.
	 * @return The id of the calendar.
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	String createOrUpdateCalendar(String calendarId, String calendarTitle, String calendarDescription)
			throws IOException, GoogleAuthenticationFailedException;

	/**
	 * List all the games in a calendar.
	 * 
	 * @param calendarId
	 *          The id of the calendar to search.
	 * @return A bimap of all game ids keyed by their event id.
	 * @throws IOException
	 *           Thrown if there is a problem contacting Google.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if the application cannot authenticate itself.
	 */
	BiMap<String, String> listGameIdsByEventId(String calendarId) throws IOException, GoogleAuthenticationFailedException;

}
