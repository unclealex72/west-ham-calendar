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
import java.net.URL;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * The Class GoogleCalendarServiceImplTest.
 * 
 * @author alex
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-contexts/calendar/test-dao.xml",
		"/application-contexts/calendar/context.xml" })
public class GoogleCalendarServiceImplTest {

	/** The I d_ generator. */
	static Function<CalendarType, String> ID_GENERATOR = new Function<CalendarType, String>() {
		@Override
		public String apply(CalendarType calendarType) {
			return calendarType.getDisplayName();
		}
	};

	/** The UNATTENDE d_ calenda r_ id. */
	static String UNATTENDED_CALENDAR_ID = "unattended";

	/** The google calendar dao factory. */
	@Inject
	GoogleCalendarDaoFactory googleCalendarDaoFactory;
	
	/** The google calendar service. */
	@Inject
	GoogleCalendarServiceImpl googleCalendarService;
	
	/** The google calendar factory. */
	@Inject
	GoogleCalendarFactory googleCalendarFactory;
	
	/** The mock google calendar dao. */
	MockGoogleCalendarDao mockGoogleCalendarDao;
	
	/** The game dao. */
	GameDao gameDao;
	
	/** The game1. */
	Game game1;
	
	/** The game2. */
	Game game2;
	
	/** The game3. */
	Game game3;
	
	/** The game2_altered. */
	Game game2_altered;
	
	/** The game4. */
	Game game4;
	
	/**
	 * Sets the up.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *           the jAXB exception
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Before
	public void setUp() throws IOException, JAXBException, GoogleAuthenticationFailedException {
		mockGoogleCalendarDao = (MockGoogleCalendarDao) googleCalendarDaoFactory.createGoogleCalendarDao();
		mockGoogleCalendarDao.clear();
		JAXBContext ctxt = JAXBContext.newInstance(Game.class);
		final Unmarshaller unmarshaller = ctxt.createUnmarshaller();
		final ClassLoader classLoader = getClass().getClassLoader();
		class Loader {
			public Game load(String name) throws JAXBException {
				URL u = classLoader.getResource("calendar/game" + name + ".xml");
				return (Game) unmarshaller.unmarshal(u);
			}
		}
		Loader loader = new Loader();
		game1 = loader.load("1");
		game2 = loader.load("2");
		game2_altered = loader.load("2-altered");
		game3 = loader.load("3");
		game4 = loader.load("4");
		ReadOnlyDaoFactory readOnlyDaoFactory = new ReadOnlyDaoFactory();
		Function<CalendarType, CalendarConfiguration> calendarConfigurationFactory = new Function<CalendarType, CalendarConfiguration>() {
			int id = 0;

			@Override
			public CalendarConfiguration apply(CalendarType calendarType) {
				return new CalendarConfiguration(id++, calendarType, ID_GENERATOR.apply(calendarType));
			}
		};
		gameDao = readOnlyDaoFactory.createCrudDao(GameDao.class, game1, game2, game3);
		CalendarConfigurationDao calendarConfigurationDao = readOnlyDaoFactory.createBusinessCrudDao(
				CalendarConfigurationDao.class, Iterables.transform(googleCalendarFactory.getGoogleCalendarsByCalendarType()
						.keySet(), calendarConfigurationFactory));
		googleCalendarService.setCalendarConfigurationDao(calendarConfigurationDao);
		class GameAdder {
			public void addGame(String calendarId, Game game) {
				mockGoogleCalendarDao.createOrUpdateGame(calendarId, null, game.getId().toString(), game.getCompetition(),
						game.getLocation(), game.getOpponents(), new Interval(game.getDateTimePlayed(), Duration.standardHours(2)),
						game.getResult(), game.getAttendence(), game.getMatchReport(), game.getTelevisionChannel(), true);
			}
		}
		GameAdder gameAdder = new GameAdder();
		gameAdder.addGame(ID_GENERATOR.apply(CalendarType.ATTENDED), game1);
		gameAdder.addGame(ID_GENERATOR.apply(CalendarType.UNATTENDED), game2_altered);
		gameAdder.addGame(ID_GENERATOR.apply(CalendarType.HOME), game4);
	}

	/**
	 * Test method for.
	 * 
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 *           {@link uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarServiceImpl#attendGame(int)}
	 *           .
	 */
	@Test
	public void testAttendGame() throws GoogleAuthenticationFailedException, IOException {
		googleCalendarService.attendGame(gameDao.findById(2));
		checkGame(2, CalendarType.ATTENDED, CalendarType.UNATTENDED);
	}

	/**
	 * Test method for.
	 * 
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 *           {@link uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarServiceImpl#unattendGame(int)}
	 *           .
	 */
	@Test
	public void testUnattendGame() throws GoogleAuthenticationFailedException, IOException {
		googleCalendarService.unattendGame(gameDao.findById(1));
		checkGame(1, CalendarType.UNATTENDED, CalendarType.ATTENDED);
	}

	/**
	 * Check game.
	 * 
	 * @param gameId
	 *          the game id
	 * @param containingCalendarType
	 *          the containing calendar type
	 * @param previousCalendarType
	 *          the previous calendar type
	 */
	protected void checkGame(final int gameId, CalendarType containingCalendarType, CalendarType previousCalendarType) {
		String containingCalendarId = ID_GENERATOR.apply(containingCalendarType);
		String previousCalendarId = ID_GENERATOR.apply(previousCalendarType);
		Predicate<String> calendarContainsGamePredicate = new Predicate<String>() {
			@Override
			public boolean apply(String calendarId) {
				return mockGoogleCalendarDao.listGameIdsByEventId(calendarId).values().contains(Integer.toString(gameId));
			}
		};
		Assert.assertTrue("Game " + gameId + " was not found in calendar " + containingCalendarId,
				calendarContainsGamePredicate.apply(containingCalendarId));
		Assert.assertFalse("Game " + gameId + " was found in calendar " + previousCalendarId,
				calendarContainsGamePredicate.apply(previousCalendarId));
	}

	/**
	 * Test method for.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 *           {@link uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarServiceImpl#updateCalendars()}
	 *           .
	 */
	@Test
	public void testUpdateCalendars() throws IOException, GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> actualUpdateChangeLogs = googleCalendarService.updateCalendars(gameDao.getAll());
		// Check games. For all but the attended and unattended calendars, all games
		// will just be added.
		SortedSet<UpdateChangeLog> expectedUpdateChangeLogs = Sets.newTreeSet();
		for (Entry<CalendarType, GoogleCalendar> entry : googleCalendarFactory.getGoogleCalendarsByCalendarType()
				.entrySet()) {
			CalendarType calendarType = entry.getKey();
			GoogleCalendar googleCalendar = entry.getValue();
			if (calendarType == CalendarType.ATTENDED) {
				populateExpectedAttendedLog(googleCalendar, expectedUpdateChangeLogs);
			}
			else if (calendarType == CalendarType.UNATTENDED) {
				populateExpectedUnattendedLog(googleCalendar, expectedUpdateChangeLogs);
			}
			else {
				if (calendarType == CalendarType.HOME) {
					// Add that game 4 will be removed as it vanished from the database.
					expectedUpdateChangeLogs.add(new RemovedChangeLog(googleCalendar, game4.getId().toString()));					
				}
				populateExpectedAdditionLog(googleCalendar, expectedUpdateChangeLogs);
			}
		}
		Assert.assertArrayEquals("The wrong changes were recorded.",
				Iterables.toArray(expectedUpdateChangeLogs, UpdateChangeLog.class),
				Iterables.toArray(actualUpdateChangeLogs, UpdateChangeLog.class));
	}

	/**
	 * Populate expected attended log.
	 * 
	 * @param googleCalendar
	 *          the google calendar
	 * @param expectedUpdateChangeLogs
	 *          the expected update change logs
	 */
	protected void populateExpectedAttendedLog(GoogleCalendar googleCalendar,
			SortedSet<UpdateChangeLog> expectedUpdateChangeLogs) {
		expectedUpdateChangeLogs.add(new RemovedChangeLog(googleCalendar, game1.getId().toString()));
		expectedUpdateChangeLogs.add(new AddedChangeLog(googleCalendar, game3));
	}

	/**
	 * Populate expected unattended log.
	 * 
	 * @param googleCalendar
	 *          the google calendar
	 * @param expectedUpdateChangeLogs
	 *          the expected update change logs
	 */
	protected void populateExpectedUnattendedLog(GoogleCalendar googleCalendar,
			SortedSet<UpdateChangeLog> expectedUpdateChangeLogs) {
		expectedUpdateChangeLogs.add(new AddedChangeLog(googleCalendar, game1));
		expectedUpdateChangeLogs.add(new UpdatedChangeLog(googleCalendar, game2));
	}

	/**
	 * Populate expected addition log.
	 * 
	 * @param googleCalendar
	 *          the google calendar
	 * @param expectedUpdateChangeLogs
	 *          the expected update change logs
	 */
	protected void populateExpectedAdditionLog(final GoogleCalendar googleCalendar,
			SortedSet<UpdateChangeLog> expectedUpdateChangeLogs) {
		Function<Game, UpdateChangeLog> function = new Function<Game, UpdateChangeLog>() {
			@Override
			public UpdateChangeLog apply(Game game) {
				return new AddedChangeLog(googleCalendar, game);
			}
		};
		Iterables.addAll(expectedUpdateChangeLogs, Iterables.transform(
				Iterables.filter(Lists.newArrayList(game1, game2, game3), googleCalendar.toContainsGamePredicate()),
				function));
	}
}