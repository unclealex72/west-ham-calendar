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
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.calendar.MockGoogleCalendarDao.MockGame;
import uk.co.unclealex.hammers.calendar.server.calendar.google.AbstractGoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * The Class GoogleCalendarUpdatingServiceImplTest.
 * 
 * @author alex
 */
public class GoogleCalendarUpdatingServiceImplTest {

	/**
	 * Load game.
	 * 
	 * @param id
	 *          the id
	 * @return the game
	 * @throws JAXBException
	 *           the jAXB exception
	 */
	protected Game loadGame(String id) throws JAXBException {
		JAXBContext ctxt = JAXBContext.newInstance(Game.class);
		Unmarshaller unmarshaller = ctxt.createUnmarshaller();
		URL url = getClass().getClassLoader().getResource("calendar/game" + id + ".xml");
		return (Game) unmarshaller.unmarshal(url);
	}

	/**
	 * Test update calendars.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 * @throws JAXBException
	 *           the jAXB exception
	 */
	@Test
	public void testUpdateCalendars() throws IOException, GoogleAuthenticationFailedException, ClassNotFoundException,
			JAXBException {
		GoogleCalendarUpdatingServiceImpl googleCalendarUpdatingServiceImpl = new GoogleCalendarUpdatingServiceImpl();
		MockGoogleCalendarDao mockGoogleCalendarDao = new MockGoogleCalendarDao();
		googleCalendarUpdatingServiceImpl.setGoogleCalendarDaoFactory(mockGoogleCalendarDao);
		final GoogleCalendar googleCalendar = new AbstractGoogleCalendar("Calendar Title", "Calendar Description", 2, true) {
			@Override
			public DateTime getGameDate(Game game) {
				return game.getDateTimePlayed();
			}

			@Override
			public boolean contains(Game game) {
				return game.getId() != 4;
			}
		};
		String calendarId = "1";
		for (Game alreadyExistingGame : new Game[] { loadGame("1"), loadGame("2"), loadGame("4"), loadGame("5") }) {
			mockGoogleCalendarDao.createOrUpdateGame(calendarId, null, Integer.toString(alreadyExistingGame.getId()),
					alreadyExistingGame.getCompetition(), alreadyExistingGame.getLocation(), alreadyExistingGame.getOpponents(),
					googleCalendar.toCalendarDateInterval().apply(alreadyExistingGame), alreadyExistingGame.getResult(),
					alreadyExistingGame.getAttendence(), alreadyExistingGame.getMatchReport(),
					alreadyExistingGame.getTelevisionChannel(), googleCalendar.isBusy());
		}
		Game gameOne = loadGame("1");
		gameOne.setDateTimePlayed(gameOne.getDateTimePlayed().plusHours(1));
		List<Game> games = Lists.newArrayList(gameOne, loadGame("2"), loadGame("3"), loadGame("4"));
		Map<String, GoogleCalendar> googleCalendarsByCalendarId = Collections.singletonMap(calendarId, googleCalendar);
		SortedSet<UpdateChangeLog> actualUpdateChangeLogs = googleCalendarUpdatingServiceImpl.updateCalendars(
				googleCalendarsByCalendarId, games);
		// Check the correct changes were logged.
		SortedSet<UpdateChangeLog> expectedUpdateChangeLogs = Sets.newTreeSet(Arrays.asList((UpdateChangeLog) new AddedChangeLog(
				googleCalendar, loadGame("3")), (UpdateChangeLog) new RemovedChangeLog(googleCalendar, loadGame("5").getId().toString()),
				(UpdateChangeLog) new RemovedChangeLog(googleCalendar, loadGame("4").getId().toString()), (UpdateChangeLog) new UpdatedChangeLog(googleCalendar, gameOne)));
		Assert.assertArrayEquals("The wrong updates were found.",
				Iterables.toArray(expectedUpdateChangeLogs, UpdateChangeLog.class),
				Iterables.toArray(actualUpdateChangeLogs, UpdateChangeLog.class));
		// Check the correct games are left on the calendar.
		Integer[] expectedIds = new Integer[] { 1, 2, 3 };
		Function<String, Integer> actualIdFunction = new Function<String, Integer>() {
			@Override
			public Integer apply(String gameId) {
				return Integer.valueOf(gameId);
			}
		};
		Map<String, MockGame> actualMockGames = mockGoogleCalendarDao.gamesByCalendarId.get(calendarId);
		Integer[] actualIds = Iterables.toArray(
				Sets.newTreeSet(Iterables.transform(actualMockGames.keySet(), actualIdFunction)), Integer.class);
		Assert.assertArrayEquals("The wrong game ids were found.", expectedIds, actualIds);
		// Check the games are correct themselves.
		for (Game expectedGame : Iterables.filter(games, googleCalendar.toContainsGamePredicate())) {
			String gameId = Integer.toString(expectedGame.getId());
			MockGame actualGame = actualMockGames.get(gameId);
			Assert.assertNotNull("No game was stored for id " + gameId, actualGame);
			Assert.assertEquals("Game " + gameId + " had the wrong attendence.", expectedGame.getAttendence(),
					actualGame.attendence);
			Assert.assertEquals("Game " + gameId + " had the wrong competition.", expectedGame.getCompetition(),
					actualGame.competition);
			Assert.assertEquals("Game " + gameId + " had the wrong date.",
					googleCalendar.toCalendarDateInterval().apply(expectedGame), actualGame.gameInterval);
			Assert.assertEquals("Game " + gameId + " had the wrong location.", expectedGame.getLocation(),
					actualGame.location);
			Assert.assertEquals("Game " + gameId + " had the wrong academy members tickets available property.",
					expectedGame.getMatchReport(), actualGame.matchReport);
			Assert.assertEquals("Game " + gameId + " had the wrong opponents.", expectedGame.getOpponents(),
					actualGame.opponents);
			Assert.assertEquals("Game " + gameId + " had the wrong academy members tickets available property.",
					expectedGame.getResult(), actualGame.result);
			Assert.assertEquals("Game " + gameId + " had the transparency.", googleCalendar.isBusy(), actualGame.busy);
			Assert.assertEquals("Game " + gameId + " had the wrong television channel.", expectedGame.getTelevisionChannel(),
					actualGame.televisionChannel);
		}
	}
}
