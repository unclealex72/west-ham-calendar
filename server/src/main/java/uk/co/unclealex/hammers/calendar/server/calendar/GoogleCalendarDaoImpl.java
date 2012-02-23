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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventExtendedProperties;
import com.google.api.services.calendar.model.Events;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author alex
 * 
 */
public class GoogleCalendarDaoImpl extends DurationFindingAwareGoogleCalendarDao {

	/**
	 * 
	 */
	private static final String TIMEZONE = "Europe/London";
	private static final Logger log = LoggerFactory.getLogger(GoogleCalendarDaoImpl.class);
	private static final String ID_PROPERTY = "hammersId";
	private static final String WEST_HAM = "West Ham";

	private final com.google.api.services.calendar.Calendar i_calendarService;
	private final Function<String, String> i_calendarFormatter;
	private final Function<String, String> i_gameFormatter;

	public GoogleCalendarDaoImpl(com.google.api.services.calendar.Calendar calendarService,
			Function<String, String> calendarFormatter, Function<String, String> gameFormatter) {
		super();
		i_calendarService = calendarService;
		i_calendarFormatter = calendarFormatter;
		i_gameFormatter = gameFormatter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameUpdateInformation createOrUpdateGame(String calendarId, String eventId, String gameId,
			Competition competition, Location location, String opponents, Interval gameInterval, String result,
			Integer attendence, String matchReport, String televisionChannel, boolean busy) throws IOException,
			GoogleAuthenticationFailedException {
		String title = createTitle(location, competition, opponents, televisionChannel);
		boolean isNew = eventId == null;
		boolean isUpdated;
		Event event;
		if (isNew) {
			event = new Event();
			event.setStart(new EventDateTime());
			event.setEnd(new EventDateTime());
			EventExtendedProperties eventExtendedProperties = new EventExtendedProperties();
			Map<String, String> shared = Maps.newHashMap();
			shared.put(ID_PROPERTY, gameId);
			eventExtendedProperties.setShared(shared);
			event.setExtendedProperties(eventExtendedProperties);
		}
		else {
			event = getCalendarService().events().get(calendarId, eventId).execute();
		}
		String description = createDescription(result, attendence, matchReport);
		isUpdated = updateBusy(event, busy);
		if (!title.equals(event.getSummary())) {
			event.setSummary(title);
			isUpdated = true;
		}
		isUpdated = updateTime(event.getStart(), gameInterval.getStart()) || isUpdated;
		isUpdated = updateTime(event.getEnd(), gameInterval.getEnd()) || isUpdated;
		if (!description.equals(Strings.nullToEmpty(event.getDescription()))) {
			event.setDescription(description);
			isUpdated = true;
		}
		if (isNew) {
			log.info(String.format("Creating game %s in calendar %s.", title, getCalendarFormatter().apply(calendarId)));
			return new GameWasCreatedInformation(getCalendarService().events().insert(calendarId, event).execute().getId());
		}
		else if (isUpdated) {
			log.info(String.format("Updating game %s in calendar %s.", title, getCalendarFormatter().apply(calendarId)));
			getCalendarService().events().update(calendarId, eventId, event).execute();
		}
		else {
			log.info(String.format("Ignoring game %s in calendar %s.", title, getCalendarFormatter().apply(calendarId)));
		}
		return new GameWasUpdatedInformation(isUpdated);
	}

	/**
	 * Create a title for a game.
	 * 
	 * @param location
	 *          The game's location.
	 * @param competition
	 *          The game's competition.
	 * @param opponents
	 *          The game's opponents.
	 * @param televisionChannel
	 *          The TV channel who showed the game.
	 * @return A really nice title.
	 */
	protected String createTitle(Location location, Competition competition, String opponents, String televisionChannel) {
		String firstTeam;
		String secondTeam;

		if (location.equals(Location.HOME)) {
			firstTeam = WEST_HAM;
			secondTeam = opponents;
		}
		else {
			firstTeam = opponents;
			secondTeam = WEST_HAM;
		}
		String title = String.format("%s vs. %s (%s)", firstTeam, secondTeam, competition.getName());
		if (televisionChannel != null) {
			title += String.format(" [%s]", televisionChannel);
		}
		return title;
	}

	/**
	 * Create a description for an event.
	 * 
	 * @param result
	 *          The game's result.
	 * @param attendence
	 *          The game's attendence.
	 * @param matchReport
	 *          A match report.
	 * @return A really nice description.
	 */
	protected String createDescription(String result, Integer attendence, String matchReport) {
		List<String> descriptlets = new ArrayList<String>();
		if (result != null) {
			descriptlets.add(result);
		}
		if (attendence != null) {
			descriptlets.add(String.format("(Attendance: %s)", NumberFormat.getInstance().format(attendence.longValue())));
		}
		if (matchReport != null) {
			descriptlets.add(matchReport);
		}
		return StringUtils.join(descriptlets, ' ');
	}

	/**
	 * Update an event's time.
	 * 
	 * @param eventDateTime
	 *          The time to change.
	 * @param newGameTime
	 *          The time to change it to.
	 * @return True if the time was changed, false otherwise.
	 */
	protected boolean updateTime(EventDateTime eventDateTime, DateTime newGameTime) {
		com.google.api.client.util.DateTime newGoogleGameTime = toGoogleDateTime(newGameTime);
		if (eventDateTime == null || eventDateTime.getDateTime() == null
				|| (eventDateTime.getDateTime().getValue() / 1000 != newGoogleGameTime.getValue() / 1000)) {
			eventDateTime.setDateTime(newGoogleGameTime);
			eventDateTime.setTimeZone(newGameTime.getZone().toTimeZone().getID());
			return true;
		}
		return false;
	}

	/**
	 * Update an event's transparency property.
	 * 
	 * @param event
	 *          The event to update.
	 * @param busy
	 *          True if the event is to be opaque, false otherwise.
	 * @return True if the event needed to change, false otherwise.
	 */
	protected boolean updateBusy(Event event, boolean busy) {
		final String transparency = busy ? "opaque" : "transparent";
		String currentTransparency = event.getTransparency();
		if (currentTransparency == null) {
			currentTransparency = "opaque";
		}

		if (!transparency.equals(currentTransparency)) {
			event.setTransparency(transparency);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveGame(String sourceCalendarId, String targetCalendarId, String eventId, String gameId, boolean busy)
			throws IOException, GoogleAuthenticationFailedException {
		Function<String, String> calendarFormatter = getCalendarFormatter();
		log.info("Moving game " + getGameFormatter().apply(gameId) + " from calendar " + calendarFormatter.apply(sourceCalendarId) + " to "
				+ calendarFormatter.apply(targetCalendarId));
		Event event = getCalendarService().events().move(sourceCalendarId, eventId, targetCalendarId).execute();
		if (updateBusy(event, busy)) {
			getCalendarService().events().update(targetCalendarId, eventId, event).execute();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeGame(String calendarId, String eventId, String gameId) throws IOException {
		log.info("Removing event " + getGameFormatter().apply(gameId) + " from calendar" + getCalendarFormatter().apply(calendarId));
		getCalendarService().events().delete(calendarId, eventId).execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public StringAndDurationFieldType findGameAndDurationFieldType(String calendarId, String gameId, DateTime searchDate)
			throws IOException, GoogleAuthenticationFailedException {
		for (DurationFieldType durationFieldType : durationFieldTypes()) {
			String foundGameEventId = findGame(calendarId, gameId, searchDate, durationFieldType);
			if (foundGameEventId != null) {
				log.info("Found game " + getGameFormatter().apply(gameId) + " in event " + foundGameEventId + ".");
				return new StringAndDurationFieldType(foundGameEventId, durationFieldType);
			}
		}
		log.info("Cannot find game " + getGameFormatter().apply(gameId) + " in calendar "
				+ getCalendarFormatter().apply(calendarId));
		return new StringAndDurationFieldType(null, null);
	}

	/**
	 * Search for a game with the given id.
	 * 
	 * @param calendarId
	 *          The id of the calendar to search.
	 * @param gameId
	 *          The id of the game to find.
	 * @param searchDate
	 *          The middle date on which the search is centered.
	 * @param durationFieldType
	 *          The duration field to add and subtract to find the interval in
	 *          which to search or null to search all events.
	 * @return The event id of the game or null if no game could be found.
	 * @throws IOException
	 */
	protected String findGame(String calendarId, String gameId, DateTime searchDate, DurationFieldType durationFieldType)
			throws IOException {
		String pageToken = null;
		String timeMin;
		String timeMax;
		if (durationFieldType == null) {
			timeMin = timeMax = null;
			log.info("Searching for game " + getGameFormatter().apply(gameId) + " in calendar "
					+ getCalendarFormatter().apply(calendarId) + " with no time limits.");
		}
		else {
			timeMin = createTime(searchDate, durationFieldType, -1);
			timeMax = createTime(searchDate, durationFieldType, 1);
			log.info("Searching for game " + getGameFormatter().apply(gameId) + " in calendar "
					+ getCalendarFormatter().apply(calendarId) + " between " + timeMin + " and " + timeMax + ".");
		}
		do {
			Events events = getCalendarService().events().list(calendarId).setPageToken(pageToken)
					.setMaxResults(Integer.MAX_VALUE).setTimeMin(timeMin).setTimeMax(timeMax).execute();
			for (Event event : notEmpty(events.getItems())) {
				String foundGameId = event.getExtendedProperties().getShared().get(ID_PROPERTY);
				if (gameId.equals(foundGameId)) {
					return event.getId();
				}
			}
			pageToken = events.getNextPageToken();

		} while (pageToken != null);
		return null;
	}

	/**
	 * Create a new google date time object
	 * 
	 * @param dateTime
	 *          The original joda date time
	 * @param fieldType
	 *          A field to add or remove.
	 * @param offset
	 *          The amount of the field to add or remove.
	 * @return The new date time in RFC3339 format.
	 */
	protected String createTime(DateTime dateTime, DurationFieldType fieldType, int offset) {
		DateTime newDateTime = dateTime.withFieldAdded(fieldType, offset);
		return toGoogleDateTime(newDateTime).toStringRfc3339();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createOrUpdateCalendar(String calendarId, String calendarTitle, String calendarDescription) throws IOException {
		Calendar calendar;
		if (calendarId == null) {
			log.info("Creating calendar " + calendarTitle);
			calendar = new Calendar();
		}
		else {
			log.info("Updating calendar " + calendarTitle);
			calendar = getCalendarService().calendars().get(calendarId).execute();
		}
		calendar.setSummary(calendarTitle);
		calendar.setDescription(calendarDescription);
		calendar.setTimeZone(TIMEZONE);
		calendar.setLocation("Upton Park");
		if (calendarId == null) {
			calendar = getCalendarService().calendars().insert(calendar).execute();
		}
		else {
			calendar = getCalendarService().calendars().update(calendarId, calendar).execute();
		}
		return calendar.getId();
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public BiMap<String, String> listGameIdsByEventId(String calendarId) throws IOException,
			GoogleAuthenticationFailedException {
		BiMap<String, String> gameIdsByEventId = HashBiMap.create();
		String pageToken = null;
		Predicate<Event> notCancelledEvent = new Predicate<Event>() {
			@Override
			public boolean apply(Event event) {
				return !"cancelled".equals(event.getStatus());
			}
		};
		do {
			com.google.api.services.calendar.Calendar.Events.List list = getCalendarService().events().list(calendarId)
					.setPageToken(pageToken).setShowDeleted(true);
			list.setFields("items(extendedProperties/shared,id,status),nextPageToken");
			Events events = list.execute();
			for (Event event : Iterables.filter(notEmpty(events.getItems()), notCancelledEvent)) {
				String gameId = event.getExtendedProperties().getShared().get(ID_PROPERTY);
				if (gameId != null) {
					gameIdsByEventId.put(event.getId(), gameId);
				}
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		return gameIdsByEventId;
	}

	protected <I> Iterable<I> notEmpty(Iterable<I> items) {
		return items == null ? new ArrayList<I>() : items;
	}

	protected com.google.api.client.util.DateTime toGoogleDateTime(DateTime dateTime) {
		return new com.google.api.client.util.DateTime(dateTime.toDate(), dateTime.getZone().toTimeZone());
	}

	public com.google.api.services.calendar.Calendar getCalendarService() {
		return i_calendarService;
	}

	public Function<String, String> getCalendarFormatter() {
		return i_calendarFormatter;
	}

	public Function<String, String> getGameFormatter() {
		return i_gameFormatter;
	}

}
