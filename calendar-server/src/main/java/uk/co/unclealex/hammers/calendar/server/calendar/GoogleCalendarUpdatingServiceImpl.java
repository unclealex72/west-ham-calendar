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
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * The default implementation of {@link GoogleCalendarUpdatingService}.
 * 
 * @author alex
 * 
 */
public class GoogleCalendarUpdatingServiceImpl implements GoogleCalendarUpdatingService {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(GoogleCalendarUpdatingServiceImpl.class);

	/**
	 * The {@link GoogleCalendarDaoFactory} used to create.
	 * {@link GoogleCalendarDao}s.
	 */
	private GoogleCalendarDaoFactory googleCalendarDaoFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<UpdateChangeLog> updateCalendars(Map<String, GoogleCalendar> googleCalendarsByCalendarId,
			Iterable<Game> games) throws IOException, GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> updates = Sets.newTreeSet();
		GoogleCalendarDao googleCalendarDao = getGoogleCalendarDaoFactory().createGoogleCalendarDao();
		for (Entry<String, GoogleCalendar> entry : googleCalendarsByCalendarId.entrySet()) {
			String calendarId = entry.getKey();
			GoogleCalendar googleCalendar = entry.getValue();
			updateCalendar(updates, googleCalendarDao, calendarId, googleCalendar, games, googleCalendar.isBusy());
		}
		return updates;
	}

	/**
	 * Update a {@link GoogleCalendar}.
	 * 
	 * @param updates
	 *          A set of {@link UpdateChangeLog}s in which changes can be stored.
	 * @param googleCalendarDao
	 *          The {@link GoogleCalendarDao} used to update events.
	 * @param calendarId
	 *          The id of the calendar to update.
	 * @param googleCalendar
	 *          The {@link GoogleCalendar} to update.
	 * @param games
	 *          The {@link Game}s to be used to update the calendar.
	 * @param busy
	 *          True if calendar is marked as busy, false otherwise.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	protected void updateCalendar(SortedSet<UpdateChangeLog> updates, GoogleCalendarDao googleCalendarDao,
			String calendarId, final GoogleCalendar googleCalendar, Iterable<Game> games, boolean busy) throws IOException,
			GoogleAuthenticationFailedException {
		String calendarTitle = googleCalendar.getCalendarTitle();
		log.info("Updating games in calendar " + calendarTitle);
		Map<String, String> eventIdsByGameId = Maps
				.newHashMap(googleCalendarDao.listGameIdsByEventId(calendarId).inverse());
		Iterable<Game> gamesForThisCalendar = Iterables.filter(games, googleCalendar.toContainsGamePredicate());
		for (Game game : gamesForThisCalendar) {
			String gameId = Integer.toString(game.getId());
			String eventId = eventIdsByGameId.get(gameId);
			Interval gameInterval = googleCalendar.toCalendarDateInterval().apply(game);
			log.info("Checking game " + game);
			GameUpdateInformation gameUpdateInformation = googleCalendarDao.createOrUpdateGame(calendarId, eventId, gameId,
					game.getCompetition(), game.getLocation(), game.getOpponents(), gameInterval, game.getResult(),
					game.getAttendence(), game.getMatchReport(), game.getTelevisionChannel(), busy);
			eventIdsByGameId.remove(gameId);
			updateChangeLog(updates, game, googleCalendar, gameUpdateInformation);
		}
		Function<Game, String> gameFunction = new Function<Game, String>() {
			@Override
			public String apply(Game game) {
				return Integer.toString(game.getId());
			}
		};
		Map<String, Game> gamesById = Maps.uniqueIndex(games, gameFunction);
		for (Map.Entry<String, String> entry : eventIdsByGameId.entrySet()) {
			String gameId = entry.getKey();
			String eventId = entry.getValue();
			Game game = gamesById.get(gameId);
			if (game == null) {
				log.info("Removing unknown game " + gameId);
			}
			else {
				log.info("Removing game " + game);
			}
			googleCalendarDao.removeGame(calendarId, eventId, gameId);
			updates.add(new RemovedChangeLog(googleCalendar, gameId));
		}
	}

	/**
	 * Add an {@link UpdateChangeLog} to the list of changes.
	 * 
	 * @param updates
	 *          The list of changes to add to.
	 * @param game
	 *          The {@link Game} that was updated.
	 * @param googleCalendar
	 *          The calendar that was updated.
	 * @param gameUpdateInformation
	 *          The {@link GameUpdateInformation} describing how the game was
	 *          changed.
	 */
	protected void updateChangeLog(final SortedSet<UpdateChangeLog> updates, final Game game,
			final GoogleCalendar googleCalendar, GameUpdateInformation gameUpdateInformation) {
		GameUpdateInformationVisitor visitor = new GameUpdateInformationVisitor.Default() {

			@Override
			public void visit(GameWasCreatedInformation gameWasCreatedInformation) {
				updates.add(new AddedChangeLog(googleCalendar, game));
			}

			@Override
			public void visit(GameWasUpdatedInformation gameWasUpdatedInformation) {
				if (gameWasUpdatedInformation.isUpdated()) {
					updates.add(new UpdatedChangeLog(googleCalendar, game));
				}
			}
		};
		gameUpdateInformation.accept(visitor);
	}

	/**
	 * Gets the {@link GoogleCalendarDaoFactory} used to create.
	 * 
	 * @return the {@link GoogleCalendarDaoFactory} used to create
	 */
	public GoogleCalendarDaoFactory getGoogleCalendarDaoFactory() {
		return googleCalendarDaoFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarDaoFactory} used to create.
	 * 
	 * @param googleCalendarDaoFactory
	 *          the new {@link GoogleCalendarDaoFactory} used to create
	 */
	public void setGoogleCalendarDaoFactory(GoogleCalendarDaoFactory googleCalendarDaoFactory) {
		this.googleCalendarDaoFactory = googleCalendarDaoFactory;
	}

}
