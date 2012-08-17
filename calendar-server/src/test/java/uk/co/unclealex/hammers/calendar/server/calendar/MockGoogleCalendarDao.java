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
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;

import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;


/**
 * A mock object to simulate, in memory, google calendar storage. For simplicity
 * it is assumed that the returned event id is the same as the supplied game id.
 * 
 * @author alex
 * 
 */
public class MockGoogleCalendarDao extends DurationFindingAwareGoogleCalendarDao implements GoogleCalendarDao,
		GoogleCalendarDaoFactory {

	/** The games by calendar id. */
	final Map<String, Map<String, MockGame>> gamesByCalendarId = Maps.newHashMap();

	/**
	 * The Class MockGame.
	 */
	static class MockGame {
		
		/** The competition. */
		Competition competition;
		
		/** The location. */
		Location location;
		
		/** The opponents. */
		String opponents;
		
		/** The game interval. */
		Interval gameInterval;
		
		/** The result. */
		String result;
		
		/** The attendence. */
		Integer attendence;
		
		/** The match report. */
		String matchReport;
		
		/** The television channel. */
		String televisionChannel;
		
		/** The busy. */
		boolean busy;

		/**
		 * Instantiates a new mock game.
		 * 
		 * @param competition
		 *          the competition
		 * @param location
		 *          the location
		 * @param opponents
		 *          the opponents
		 * @param gameInterval
		 *          the game interval
		 * @param result
		 *          the result
		 * @param attendence
		 *          the attendence
		 * @param matchReport
		 *          the match report
		 * @param televisionChannel
		 *          the television channel
		 * @param busy
		 *          the busy
		 */
		public MockGame(Competition competition, Location location, String opponents, Interval gameInterval, String result,
				Integer attendence, String matchReport, String televisionChannel, boolean busy) {
			super();
			this.competition = competition;
			this.location = location;
			this.opponents = opponents;
			this.gameInterval = gameInterval;
			this.result = Strings.emptyToNull(result);
			this.attendence = attendence;
			this.matchReport = Strings.emptyToNull(matchReport);
			this.televisionChannel = Strings.emptyToNull(televisionChannel);
			this.busy = busy;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MockGame)) {
				return false;
			}
			MockGame other = (MockGame) obj;
			return Objects.equal(competition, other.competition) && Objects.equal(location, other.location)
					&& Objects.equal(opponents, other.opponents) && Objects.equal(gameInterval, other.gameInterval)
					&& Objects.equal(result, other.result) && Objects.equal(attendence, other.attendence)
					&& Objects.equal(matchReport, other.matchReport) && Objects.equal(televisionChannel, other.televisionChannel)
					&& Objects.equal(busy, other.busy);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GoogleCalendarDao createGoogleCalendarDao() throws IOException {
		return this;
	}

	/**
	 * Get the games in a calendar.
	 * 
	 * @param calendarId
	 *          The id of the calendar.
	 * @return A map of games for a calendar. This is never null.
	 */
	public Map<String, MockGame> getGamesForCalendar(String calendarId) {
		Map<String, MockGame> mockGamesById = gamesByCalendarId.get(calendarId);
		if (mockGamesById == null) {
			mockGamesById = Maps.newHashMap();
			gamesByCalendarId.put(calendarId, mockGamesById);
		}
		return mockGamesById;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameUpdateInformation createOrUpdateGame(String calendarId, String eventId, String gameId,
			Competition competition, Location location, String opponents, Interval gameInterval, String result,
			Integer attendence, String matchReport, String televisionChannel, boolean busy) {
		Map<String, MockGame> games = getGamesForCalendar(calendarId);
		MockGame newMockGame = new MockGame(competition, location, opponents, gameInterval, result, attendence,
				matchReport, televisionChannel, busy);
		MockGame existingMockGame = games.get(gameId);
		games.put(gameId, newMockGame);
		if (existingMockGame == null) {
			return new GameWasCreatedInformation(gameId);
		}
		else {
			return new GameWasUpdatedInformation(!newMockGame.equals(existingMockGame));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeGame(String calendarId, String eventId, String gameId) {
		getGamesForCalendar(calendarId).remove(eventId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveGame(String sourceCalendarId, String targetCalendarId, String eventId, String gameId, boolean busy) {
		Map<String, MockGame> sourceCalendarGames = getGamesForCalendar(sourceCalendarId);
		MockGame mockGame = sourceCalendarGames.get(eventId);
		sourceCalendarGames.remove(eventId);
		mockGame.busy = busy;
		getGamesForCalendar(targetCalendarId).put(eventId, mockGame);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameIdAndDurationFieldType findGameAndDurationFieldType(String calendarId, String gameId, DateTime searchDate) {
		MockGame mockGame = getGamesForCalendar(calendarId).get(gameId);
		String eventId = null;
		DurationFieldType durationFieldType = null;
		if (mockGame != null) {
			eventId = gameId;
			DateTime gameTime = mockGame.gameInterval.getStart();
			final long timeDifference = Math.abs(gameTime.getMillis() - searchDate.getMillis());
			Predicate<DurationFieldType> predicate = new Predicate<DurationFieldType>() {
				@Override
				public boolean apply(DurationFieldType durationFieldType) {
					return durationFieldType == null || durationFieldType.getField(null).getUnitMillis() >= timeDifference;
				}
			};
			durationFieldType = Iterables.find(Arrays.asList(durationFieldTypes()), predicate);
		}
		return new GameIdAndDurationFieldType(eventId, durationFieldType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createOrUpdateCalendar(String calendarId, String calendarTitle, String calendarDescription) {
		if (calendarId == null) {
			calendarId = UUID.randomUUID().toString();
		}
		getGamesForCalendar(calendarId);
		return calendarId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BiMap<String, String> listGameIdsByEventId(String calendarId) {
		BiMap<String, String> gameIdsByEventId = HashBiMap.create();
		for (String gameId : getGamesForCalendar(calendarId).keySet()) {
			gameIdsByEventId.put(gameId, gameId);
		}
		return gameIdsByEventId;
	}

	/**
	 * Reset the DAO.
	 */
	public void clear() {
		gamesByCalendarId.clear();
	}
}
