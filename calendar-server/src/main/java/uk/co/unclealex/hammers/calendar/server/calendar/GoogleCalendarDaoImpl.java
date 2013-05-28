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
 * The Class GoogleCalendarDaoImpl.
 * 
 * @author alex
 */
public class GoogleCalendarDaoImpl extends DurationFindingAwareGoogleCalendarDao {

  /** The Constant MILLIS_IN_A_SECOND. */
  private static final int MILLIS_IN_A_SECOND = 1000;

  /** The logger for this class. */
  private static final Logger log = LoggerFactory.getLogger(GoogleCalendarDaoImpl.class);

  /**
   * The timezone for any created calendars.
   */
  private static final String TIMEZONE = "Europe/London";

  /**
   * The name of an {@link Event}'s shared property that will store the game id.
   */
  private static final String ID_PROPERTY = "hammersId";

  /** COYI!. */
  private static final String WEST_HAM = "West Ham";

  /**
   * The {@link com.google.api.services.calendar.Calendar} object used to
   * communicate with Google.
   */
  private final com.google.api.services.calendar.Calendar calendarService;

  /**
   * The {@link Function} used to format a calendar for printing given its id.
   */
  private final Function<String, String> calendarFormatter;

  /**
   * The {@link Function} used to format a game for printing given its id.
   */
  private final Function<String, String> gameFormatter;

  /**
   * Instantiates a new google calendar dao impl.
   * 
   * @param calendarService
   *          the calendar service
   * @param calendarFormatter
   *          the calendar formatter
   * @param gameFormatter
   *          the game formatter
   */
  public GoogleCalendarDaoImpl(
      final com.google.api.services.calendar.Calendar calendarService,
      final Function<String, String> calendarFormatter,
      final Function<String, String> gameFormatter) {
    super();
    this.calendarService = calendarService;
    this.calendarFormatter = calendarFormatter;
    this.gameFormatter = gameFormatter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GameUpdateInformation createOrUpdateGame(
      final String calendarId,
      final String eventId,
      final String gameId,
      final Competition competition,
      final Location location,
      final String opponents,
      final Interval gameInterval,
      final String result,
      final Integer attendence,
      final String matchReport,
      final String televisionChannel,
      final boolean busy) throws IOException, GoogleAuthenticationFailedException {
    final String title = createTitle(location, competition, opponents, televisionChannel);
    final boolean isNew = eventId == null;
    boolean isUpdated;
    Event event;
    if (isNew) {
      event = new Event();
      event.setStart(new EventDateTime());
      event.setEnd(new EventDateTime());
      final EventExtendedProperties eventExtendedProperties = new EventExtendedProperties();
      final Map<String, String> shared = Maps.newHashMap();
      shared.put(ID_PROPERTY, gameId);
      eventExtendedProperties.setShared(shared);
      event.setExtendedProperties(eventExtendedProperties);
    }
    else {
      event = getCalendarService().events().get(calendarId, eventId).execute();
    }
    final String description = createDescription(result, attendence, matchReport);
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
  protected String createTitle(
      final Location location,
      final Competition competition,
      final String opponents,
      final String televisionChannel) {
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
  protected String createDescription(final String result, final Integer attendence, final String matchReport) {
    final List<String> descriptlets = new ArrayList<String>();
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
  protected boolean updateTime(final EventDateTime eventDateTime, final DateTime newGameTime) {
    final com.google.api.client.util.DateTime newGoogleGameTime = toGoogleDateTime(newGameTime);
    if (eventDateTime == null
        || eventDateTime.getDateTime() == null
        || (eventDateTime.getDateTime().getValue() / MILLIS_IN_A_SECOND != newGoogleGameTime.getValue()
            / MILLIS_IN_A_SECOND)) {
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
  protected boolean updateBusy(final Event event, final boolean busy) {
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
  public void moveGame(
      final String sourceCalendarId,
      final String targetCalendarId,
      final String eventId,
      final String gameId,
      final boolean busy) throws IOException, GoogleAuthenticationFailedException {
    final Function<String, String> calendarFormatter = getCalendarFormatter();
    log.info("Moving game "
        + getGameFormatter().apply(gameId)
        + " from calendar "
        + calendarFormatter.apply(sourceCalendarId)
        + " to "
        + calendarFormatter.apply(targetCalendarId));
    final Event event = getCalendarService().events().move(sourceCalendarId, eventId, targetCalendarId).execute();
    if (updateBusy(event, busy)) {
      getCalendarService().events().update(targetCalendarId, eventId, event).execute();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeGame(final String calendarId, final String eventId, final String gameId) throws IOException {
    log.info("Removing event "
        + getGameFormatter().apply(gameId)
        + " from calendar"
        + getCalendarFormatter().apply(calendarId));
    getCalendarService().events().delete(calendarId, eventId).execute();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GameIdAndDurationFieldType findGameAndDurationFieldType(
      final String calendarId,
      final String gameId,
      final DateTime searchDate) throws IOException, GoogleAuthenticationFailedException {
    for (final DurationFieldType durationFieldType : durationFieldTypes()) {
      final String foundGameEventId = findGame(calendarId, gameId, searchDate, durationFieldType);
      if (foundGameEventId != null) {
        log.info("Found game " + getGameFormatter().apply(gameId) + " in event " + foundGameEventId + ".");
        return new GameIdAndDurationFieldType(foundGameEventId, durationFieldType);
      }
    }
    log.info("Cannot find game "
        + getGameFormatter().apply(gameId)
        + " in calendar "
        + getCalendarFormatter().apply(calendarId));
    return new GameIdAndDurationFieldType(null, null);
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
   *           Signals that an I/O exception has occurred.
   */
  protected String findGame(
      final String calendarId,
      final String gameId,
      final DateTime searchDate,
      final DurationFieldType durationFieldType) throws IOException {
    String pageToken = null;
    String timeMin;
    String timeMax;
    if (durationFieldType == null) {
      timeMin = null;
      timeMax = null;
      log.info("Searching for game "
          + getGameFormatter().apply(gameId)
          + " in calendar "
          + getCalendarFormatter().apply(calendarId)
          + " with no time limits.");
    }
    else {
      timeMin = createTime(searchDate, durationFieldType, -1);
      timeMax = createTime(searchDate, durationFieldType, 1);
      log.info("Searching for game "
          + getGameFormatter().apply(gameId)
          + " in calendar "
          + getCalendarFormatter().apply(calendarId)
          + " between "
          + timeMin
          + " and "
          + timeMax
          + ".");
    }
    do {
      final Events events =
          getCalendarService()
              .events()
              .list(calendarId)
              .setPageToken(pageToken)
              .setMaxResults(Integer.MAX_VALUE)
              .setTimeMin(timeMin)
              .setTimeMax(timeMax)
              .execute();
      for (final Event event : notEmpty(events.getItems())) {
        final String foundGameId = event.getExtendedProperties().getShared().get(ID_PROPERTY);
        if (gameId.equals(foundGameId)) {
          return event.getId();
        }
      }
      pageToken = events.getNextPageToken();

    } while (pageToken != null);
    return null;
  }

  /**
   * Create a new google date time object.
   * 
   * @param dateTime
   *          The original joda date time.
   * @param fieldType
   *          A field to add or remove.
   * @param offset
   *          The amount of the field to add or remove.
   * @return The new date time in RFC3339 format.
   */
  protected String createTime(final DateTime dateTime, final DurationFieldType fieldType, final int offset) {
    final DateTime newDateTime = dateTime.withFieldAdded(fieldType, offset);
    return toGoogleDateTime(newDateTime).toStringRfc3339();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createOrUpdateCalendar(
      final String calendarId,
      final String calendarTitle,
      final String calendarDescription) throws IOException {
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
   * {@inheritDoc}
   */
  @Override
  public BiMap<String, String> listGameIdsByEventId(final String calendarId)
      throws IOException,
      GoogleAuthenticationFailedException {
    final BiMap<String, String> gameIdsByEventId = HashBiMap.create();
    String pageToken = null;
    final Predicate<Event> notCancelledEvent = new Predicate<Event>() {
      @Override
      public boolean apply(final Event event) {
        return !"cancelled".equals(event.getStatus());
      }
    };
    do {
      final com.google.api.services.calendar.Calendar.Events.List list =
          getCalendarService().events().list(calendarId).setPageToken(pageToken).setShowDeleted(true);
      list.setFields("items(extendedProperties/shared,id,status),nextPageToken");
      final Events events = list.execute();
      for (final Event event : Iterables.filter(notEmpty(events.getItems()), notCancelledEvent)) {
        final String gameId = event.getExtendedProperties().getShared().get(ID_PROPERTY);
        if (gameId != null) {
          gameIdsByEventId.put(event.getId(), gameId);
        }
      }
      pageToken = events.getNextPageToken();
    } while (pageToken != null);
    return gameIdsByEventId;
  }

  /**
   * Convert a null {@link Iterable} into an empty {@link Iterable}.
   * 
   * @param <I>
   *          The generic parameter of the {@link Iterable}.
   * @param items
   *          The original {@link Iterable}.
   * @return The original {@link Iterable} if it was not null or an empty
   *         {@link Iterable} if it was.
   */
  protected <I> Iterable<I> notEmpty(final Iterable<I> items) {
    return items == null ? new ArrayList<I>() : items;
  }

  /**
   * Convert a Joda {@link DateTime} to a Google
   * {@link com.google.api.client.util.DateTime}.
   * 
   * @param dateTime
   *          The Joda {@link DateTime} to convert.
   * @return The Google representation of the same instant in the same time
   *         zone.
   */
  protected com.google.api.client.util.DateTime toGoogleDateTime(final DateTime dateTime) {
    return new com.google.api.client.util.DateTime(dateTime.toDate(), dateTime.getZone().toTimeZone());
  }

/**
	 * Gets the {@link com.
	 *
	 * @return the {@link com
	 */
  public com.google.api.services.calendar.Calendar getCalendarService() {
    return calendarService;
  }

  /**
   * Gets the {@link Function} used to format a calendar for printing given its
   * id.
   * 
   * @return the {@link Function} used to format a calendar for printing given
   *         its id
   */
  public Function<String, String> getCalendarFormatter() {
    return calendarFormatter;
  }

  /**
   * Gets the {@link Function} used to format a game for printing given its id.
   * 
   * @return the {@link Function} used to format a game for printing given its
   *         id
   */
  public Function<String, String> getGameFormatter() {
    return gameFormatter;
  }

}
