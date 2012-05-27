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

package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * The default implementation of {@link GoogleCalendarService}.
 * 
 * @author alex
 * 
 */
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);

	/**
	 * The {@link CalendarConfigurationDao} to use for persting and retrieving
	 * Google Calendars.
	 */
	private CalendarConfigurationDao i_calendarConfigurationDao;

	/**
	 * The {@link GoogleCalendarUpdatingService} to use for updating calendars.
	 */
	private GoogleCalendarUpdatingService i_googleCalendarUpdatingService;

	/**
	 * The {@link GoogleCalendarDaoFactory} used for creating.
	 * {@link GoogleCalendarDao}s.
	 */
	private GoogleCalendarDaoFactory i_googleCalendarDaoFactory;

	/**
	 * The {@link GoogleCalendarFactory} used for getting instances of.
	 * {@link GoogleCalendar}s.
	 */
	private GoogleCalendarFactory i_googleCalendarFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attendGame(Game game) throws GoogleAuthenticationFailedException, IOException {
		moveGame(game, CalendarType.UNATTENDED, CalendarType.ATTENDED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unattendGame(Game game) throws GoogleAuthenticationFailedException, IOException {
		moveGame(game, CalendarType.ATTENDED, CalendarType.UNATTENDED);
	}

	/**
	 * Move a game from one calendar to another.
	 * 
	 * @param game
	 *          The game to move.
	 * @param source
	 *          The source calendar type.
	 * @param target
	 *          The target calendar type.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	protected void moveGame(Game game, CalendarType source, CalendarType target) throws IOException,
			GoogleAuthenticationFailedException {
		String sourceCalendarId = getGoogleCalendarIdForCalendarType(source);
		String targetCalendarId = getGoogleCalendarIdForCalendarType(target);
		GoogleCalendarFactory googleCalendarFactory = getGoogleCalendarFactory();
		GoogleCalendar sourceGoogleCalendar = googleCalendarFactory.getGoogleCalendar(source);
		GoogleCalendar targetGoogleCalendar = googleCalendarFactory.getGoogleCalendar(target);
		log.info(String.format("Moving game %s from calendar %s to calendar %s", game,
				sourceGoogleCalendar.getCalendarTitle(), targetGoogleCalendar.getCalendarTitle()));
		GoogleCalendarDao googleCalendarDao = getGoogleCalendarDaoFactory().createGoogleCalendarDao();
		String eventId = googleCalendarDao.findGame(sourceCalendarId, Integer.toString(game.getId()),
				game.getDateTimePlayed());
		googleCalendarDao.moveGame(sourceCalendarId, targetCalendarId, eventId, Integer.toString(game.getId()),
				targetGoogleCalendar.isBusy());
	}

	/**
	 * Find a calendar's id by it's calendar type.
	 * @param calendarType The {@link CalendarType} to search for.
	 * @return The id of the calendar.
	 */
	protected String getGoogleCalendarIdForCalendarType(CalendarType calendarType) {
		return getCalendarConfigurationDao().findByKey(calendarType).getGoogleCalendarId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<UpdateChangeLog> updateCalendars(Iterable<Game> games) throws IOException,
			GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> updates = Sets.newTreeSet();
		log.info("Updating all calendars.");
		Map<String, GoogleCalendar> googleCalendarsByCalendarId = Maps.newLinkedHashMap();
		for (Entry<CalendarType, GoogleCalendar> entry : getGoogleCalendarFactory().getGoogleCalendarsByCalendarType()
				.entrySet()) {
			CalendarType calendarType = entry.getKey();
			GoogleCalendar googleCalendar = entry.getValue();
			String googleCalendarId = getGoogleCalendarIdForCalendarType(calendarType);
			googleCalendarsByCalendarId.put(googleCalendarId, googleCalendar);
		}
		updates.addAll(getGoogleCalendarUpdatingService().updateCalendars(googleCalendarsByCalendarId, games));
		return updates;
	}

	/**
	 * Gets the {@link CalendarConfigurationDao} to use for persting and
	 * retrieving Google Calendars.
	 * 
	 * @return the {@link CalendarConfigurationDao} to use for persting and
	 *         retrieving Google Calendars
	 */
	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	/**
	 * Sets the {@link CalendarConfigurationDao} to use for persting and
	 * retrieving Google Calendars.
	 * 
	 * @param calendarConfigurationDao
	 *          the new {@link CalendarConfigurationDao} to use for persting and
	 *          retrieving Google Calendars
	 */
	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	/**
	 * Gets the {@link GoogleCalendarUpdatingService} to use for updating
	 * calendars.
	 * 
	 * @return the {@link GoogleCalendarUpdatingService} to use for updating
	 *         calendars
	 */
	public GoogleCalendarUpdatingService getGoogleCalendarUpdatingService() {
		return i_googleCalendarUpdatingService;
	}

	/**
	 * Sets the {@link GoogleCalendarUpdatingService} to use for updating
	 * calendars.
	 * 
	 * @param googleCalendarUpdatingService
	 *          the new {@link GoogleCalendarUpdatingService} to use for updating
	 *          calendars
	 */
	public void setGoogleCalendarUpdatingService(GoogleCalendarUpdatingService googleCalendarUpdatingService) {
		i_googleCalendarUpdatingService = googleCalendarUpdatingService;
	}

	/**
	 * Gets the {@link GoogleCalendarDaoFactory} used for creating.
	 * 
	 * @return the {@link GoogleCalendarDaoFactory} used for creating
	 */
	public GoogleCalendarDaoFactory getGoogleCalendarDaoFactory() {
		return i_googleCalendarDaoFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarDaoFactory} used for creating.
	 * 
	 * @param googleCalendarDaoFactory
	 *          the new {@link GoogleCalendarDaoFactory} used for creating
	 */
	public void setGoogleCalendarDaoFactory(GoogleCalendarDaoFactory googleCalendarDaoFactory) {
		i_googleCalendarDaoFactory = googleCalendarDaoFactory;
	}

	/**
	 * Gets the {@link GoogleCalendarFactory} used for getting instances of.
	 * 
	 * @return the {@link GoogleCalendarFactory} used for getting instances of
	 */
	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return i_googleCalendarFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarFactory} used for getting instances of.
	 * 
	 * @param googleCalendarFactory
	 *          the new {@link GoogleCalendarFactory} used for getting instances
	 *          of
	 */
	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		i_googleCalendarFactory = googleCalendarFactory;
	}
}
