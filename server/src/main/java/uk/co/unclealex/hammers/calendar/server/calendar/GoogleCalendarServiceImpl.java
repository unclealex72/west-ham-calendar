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
 * The default implementation of {@link GoogleCalendarService}
 * 
 * @author alex
 * 
 */
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	private static final Logger log = LoggerFactory.getLogger(GoogleCalendarServiceImpl.class);

	private CalendarConfigurationDao i_calendarConfigurationDao;
	private GoogleCalendarUpdatingService i_googleCalendarUpdatingService;
	private GoogleCalendarDaoFactory i_googleCalendarDaoFactory;
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
	 * @throws GoogleAuthenticationFailedException 
	 * @throws IOException 
	 */
	protected void moveGame(Game game, CalendarType source, CalendarType target) throws IOException, GoogleAuthenticationFailedException {
		String sourceCalendarId = getGoogleCalendarIdForCalendarType(source);
		String targetCalendarId = getGoogleCalendarIdForCalendarType(target);
		GoogleCalendarFactory googleCalendarFactory = getGoogleCalendarFactory();
		GoogleCalendar sourceGoogleCalendar = googleCalendarFactory.getGoogleCalendar(source);
		GoogleCalendar targetGoogleCalendar = googleCalendarFactory.getGoogleCalendar(target);
		log.info(String.format("Moving game %s from calendar %s to calendar %s", game,
				sourceGoogleCalendar.getCalendarTitle(), targetGoogleCalendar.getCalendarTitle()));
		GoogleCalendarDao googleCalendarDao = getGoogleCalendarDaoFactory().createGoogleCalendarDao();
		String eventId = googleCalendarDao.findGame(sourceCalendarId, Integer.toString(game.getId()), game.getDateTimePlayed());
		googleCalendarDao.moveGame(sourceCalendarId, targetCalendarId, eventId, Integer.toString(game.getId()), targetGoogleCalendar.isBusy());
	}

	protected String getGoogleCalendarIdForCalendarType(CalendarType calendarType) {
		return getCalendarConfigurationDao().findByKey(calendarType).getGoogleCalendarId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<UpdateChangeLog> updateCalendars(Iterable<Game> games) throws IOException, GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> updates = Sets.newTreeSet();
		log.info("Updating all calendars.");
		Map<String, GoogleCalendar> googleCalendarsByCalendarId = Maps.newLinkedHashMap();
		for (Entry<CalendarType, GoogleCalendar> entry : getGoogleCalendarFactory().getGoogleCalendarsByCalendarType().entrySet()) {
			CalendarType calendarType = entry.getKey();
			GoogleCalendar googleCalendar = entry.getValue();
			String googleCalendarId = getGoogleCalendarIdForCalendarType(calendarType);
			googleCalendarsByCalendarId.put(googleCalendarId, googleCalendar);
		}
		updates.addAll(getGoogleCalendarUpdatingService().updateCalendars(googleCalendarsByCalendarId, games));
		return updates;
	}

	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	public GoogleCalendarUpdatingService getGoogleCalendarUpdatingService() {
		return i_googleCalendarUpdatingService;
	}

	public void setGoogleCalendarUpdatingService(GoogleCalendarUpdatingService googleCalendarUpdatingService) {
		i_googleCalendarUpdatingService = googleCalendarUpdatingService;
	}

	public GoogleCalendarDaoFactory getGoogleCalendarDaoFactory() {
		return i_googleCalendarDaoFactory;
	}

	public void setGoogleCalendarDaoFactory(GoogleCalendarDaoFactory googleCalendarDaoFactory) {
		i_googleCalendarDaoFactory = googleCalendarDaoFactory;
	}

	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return i_googleCalendarFactory;
	}

	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		i_googleCalendarFactory = googleCalendarFactory;
	}
}
