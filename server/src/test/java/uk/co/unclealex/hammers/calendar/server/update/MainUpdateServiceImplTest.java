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

package uk.co.unclealex.hammers.calendar.server.update;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import uk.co.unclealex.hammers.calendar.server.calendar.AddedChangeLog;
import uk.co.unclealex.hammers.calendar.server.calendar.MockGoogleCalendarDao;
import uk.co.unclealex.hammers.calendar.server.calendar.RemovedChangeLog;
import uk.co.unclealex.hammers.calendar.server.calendar.UpdateChangeLog;
import uk.co.unclealex.hammers.calendar.server.calendar.UpdatedChangeLog;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.DatePlayedLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.GameKeyLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.GameLocatorVisitor;
import uk.co.unclealex.hammers.calendar.server.html.GameUpdateCommand;
import uk.co.unclealex.hammers.calendar.server.html.HtmlGamesScanner;
import uk.co.unclealex.hammers.calendar.server.html.MainPageService;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * The Class MainUpdateServiceImplTest.
 * 
 * @author alex
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/application-contexts/update/context.xml", "/application-contexts/dao/context.xml",
		"/application-contexts/dao/test-db.xml", "/application-contexts/calendar/context.xml",
		"/application-contexts/calendar/test-dao.xml" })
@SuppressWarnings({ "unchecked", "deprecation" })
public class MainUpdateServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	/** The main update service. */
	@Autowired
	MainUpdateServiceImpl mainUpdateService;
	
	/** The simple jdbc template. */
	@Autowired
	SimpleJdbcTemplate simpleJdbcTemplate;
	
	/** The calendar configuration dao. */
	@Autowired
	CalendarConfigurationDao calendarConfigurationDao;
	
	/** The google calendar factory. */
	@Autowired
	GoogleCalendarFactory googleCalendarFactory;
	
	/** The game dao. */
	@Autowired
	GameDao gameDao;
	
	/** The mock google calendar dao. */
	@Autowired
	MockGoogleCalendarDao mockGoogleCalendarDao;

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		mockGoogleCalendarDao.clear();
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate, "game", "calendar");
		for (CalendarType calendarType : googleCalendarFactory.getGoogleCalendarsByCalendarType().keySet()) {
			CalendarConfiguration calendarConfiguration = new CalendarConfiguration(null, calendarType, calendarType.name()
					.toLowerCase());
			calendarConfigurationDao.saveOrUpdate(calendarConfiguration);
		}
	}

	/**
	 * Test one update.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testOneUpdate() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.HOME, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		expect(createUpdatedChangeLog(CalendarType.ALL, Competition.FACP, Location.HOME, "Them", 2011),
				createAddedChangeLog(CalendarType.HOME, Competition.FACP, Location.HOME, "Them", 2011),
				createUpdatedChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.HOME, "Them", 2011));
	}

	/**
	 * Test same updates are idempotent.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testSameUpdatesAreIdempotent() throws IOException, GoogleAuthenticationFailedException {
		GameLocator nonTicketGameLocator = createGameLocator(Competition.FACP, Location.HOME, "Them", 2011);
		GameLocator ticketsGameLocator = createGameLocator(5, 9, 2011, 15, 0);
		GameUpdateCommand[] fullGameUpdateCommands = new GameUpdateCommand[] {
				GameUpdateCommand.datePlayed(nonTicketGameLocator, dateOf(5, 9, 2011, 15, 0)),
				GameUpdateCommand.academyTickets(ticketsGameLocator, dateOf(4, 9, 2011, 15, 0)),
				GameUpdateCommand.attended(nonTicketGameLocator, false),
				GameUpdateCommand.attendence(nonTicketGameLocator, 100),
				GameUpdateCommand.bondHolderTickets(ticketsGameLocator, dateOf(4, 10, 2011, 0, 0)),
				GameUpdateCommand.generalSaleTickets(ticketsGameLocator, dateOf(4, 10, 2011, 0, 0)),
				GameUpdateCommand.matchReport(nonTicketGameLocator, "Brillo"),
				GameUpdateCommand.priorityPointTickets(ticketsGameLocator, dateOf(4, 9, 2011, 0, 0)),
				GameUpdateCommand.result(nonTicketGameLocator, "1-0"),
				GameUpdateCommand.seasonTickets(ticketsGameLocator, dateOf(4, 9, 2011, 0, 0)),
				GameUpdateCommand.televisionChannel(nonTicketGameLocator, "BBC")

		};
		makeUpdates(fullGameUpdateCommands);
		mainUpdateService.updateAllCalendars();
		makeUpdates(fullGameUpdateCommands);
		expect();
	}

	/**
	 * Test change of date.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testChangeOfDate() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 17, 30)));
		expect(createUpdatedChangeLog(CalendarType.ALL, Competition.FACP, Location.AWAY, "Them", 2011),
				createUpdatedChangeLog(CalendarType.AWAY, Competition.FACP, Location.AWAY, "Them", 2011),
				createUpdatedChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011));
	}

	/**
	 * Test tickets added.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testTicketsAdded() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.seasonTickets(createGameLocator(5, 9, 2011, 15, 0), dateOf(5, 9, 2011, 17, 30)),
				GameUpdateCommand.generalSaleTickets(createGameLocator(5, 9, 2011, 15, 0), dateOf(5, 9, 2011, 18, 30)));
		expect(createAddedChangeLog(CalendarType.TICKETS_SEASON, Competition.FACP, Location.AWAY, "Them", 2011),
				createAddedChangeLog(CalendarType.TICKETS_GENERAL_SALE, Competition.FACP, Location.AWAY, "Them", 2011));
	}

	/**
	 * Test game played.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testGamePlayed() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.result(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011), "1-0"));
		expect(createUpdatedChangeLog(CalendarType.ALL, Competition.FACP, Location.AWAY, "Them", 2011),
				createUpdatedChangeLog(CalendarType.AWAY, Competition.FACP, Location.AWAY, "Them", 2011),
				createUpdatedChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011));
	}

	/**
	 * Test game attended.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Test
	public void testGameAttended() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		Game game = gameDao.findByBusinessKey(Competition.FACP, Location.AWAY, "Them", 2011);
		game.setAttended(true);
		gameDao.saveOrUpdate(game);
		makeUpdates();
		expect(createAddedChangeLog(CalendarType.ATTENDED, Competition.FACP, Location.AWAY, "Them", 2011),
				createRemovedChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011));
	}

	/**
	 * Expect.
	 * 
	 * @param expectedUpdateChangeLogSuppliers
	 *          the expected update change log suppliers
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	protected void expect(Supplier<UpdateChangeLog>... expectedUpdateChangeLogSuppliers) throws IOException,
			GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> actualUpdateChangeLogs = mainUpdateService.updateAllCalendars();
		Function<Supplier<UpdateChangeLog>, UpdateChangeLog> supplierFunction = Suppliers.supplierFunction();
		UpdateChangeLog[] expectedUpdateChangeLogs = Iterables.toArray(
				Iterables.transform(Arrays.asList(expectedUpdateChangeLogSuppliers), supplierFunction), UpdateChangeLog.class);
		Arrays.sort(expectedUpdateChangeLogs);
		Function<UpdateChangeLog, String> formatter = new Function<UpdateChangeLog, String>() {
			@Override
			public String apply(UpdateChangeLog updateChangeLog) {
				return String.format("createUpdateChangeLog(%s)", updateChangeLog);
			}
		};
		System.out
				.println("expect(" + Joiner.on(",\n").join(Iterables.transform(actualUpdateChangeLogs, formatter)) + ");");
		Assert.assertArrayEquals("The wrong changes were performed.", expectedUpdateChangeLogs,
				Iterables.toArray(actualUpdateChangeLogs, UpdateChangeLog.class));
	}

	/**
	 * Make updates.
	 * 
	 * @param gameUpdateCommands
	 *          the game update commands
	 */
	protected void makeUpdates(GameUpdateCommand... gameUpdateCommands) {
		final SortedSet<GameUpdateCommand> fixtureUpdateCommands = Sets.newTreeSet();
		final SortedSet<GameUpdateCommand> ticketsUpdateCommands = Sets.newTreeSet();
		for (final GameUpdateCommand gameUpdateCommand : gameUpdateCommands) {
			GameLocatorVisitor visitor = new GameLocatorVisitor() {
				@Override
				public void visit(DatePlayedLocator datePlayedLocator) {
					ticketsUpdateCommands.add(gameUpdateCommand);
				}

				@Override
				public void visit(GameKeyLocator gameKeyLocator) {
					fixtureUpdateCommands.add(gameUpdateCommand);
				}
			};
			gameUpdateCommand.getGameLocator().accept(visitor);
		}
		HtmlGamesScanner ticketsScanner = new HtmlGamesScanner() {
			@Override
			public SortedSet<GameUpdateCommand> scan(URI uri) throws IOException {
				return ticketsUpdateCommands;
			}
		};
		HtmlGamesScanner fixtureScanner = new HtmlGamesScanner() {
			@Override
			public SortedSet<GameUpdateCommand> scan(URI uri) throws IOException {
				return fixtureUpdateCommands;
			}
		};
		mainUpdateService.setFixturesHtmlGamesScanner(fixtureScanner);
		mainUpdateService.setTicketsHtmlGamesScanner(ticketsScanner);
		mainUpdateService.setMainPageService(new MainPageServiceImpl());
	}

	/**
	 * Creates the game locator.
	 * 
	 * @param day
	 *          the day
	 * @param month
	 *          the month
	 * @param year
	 *          the year
	 * @param hour
	 *          the hour
	 * @param minute
	 *          the minute
	 * @return the game locator
	 */
	protected GameLocator createGameLocator(int day, int month, int year, int hour, int minute) {
		return GameLocator.datePlayedLocator(dateOf(day, month, year, hour, minute));
	}

	/**
	 * Date of.
	 * 
	 * @param day
	 *          the day
	 * @param month
	 *          the month
	 * @param year
	 *          the year
	 * @param hour
	 *          the hour
	 * @param minute
	 *          the minute
	 * @return the date time
	 */
	protected DateTime dateOf(int day, int month, int year, int hour, int minute) {
		return new DateTime(year, month, day, hour, minute, 0, 0, DateTimeZone.forID("Europe/London"));
	}

	/**
	 * Creates the added change log.
	 * 
	 * @param calendarType
	 *          the calendar type
	 * @param competition
	 *          the competition
	 * @param location
	 *          the location
	 * @param opponents
	 *          the opponents
	 * @param season
	 *          the season
	 * @return the supplier
	 */
	protected Supplier<UpdateChangeLog> createAddedChangeLog(final CalendarType calendarType,
			final Competition competition, final Location location, final String opponents, final int season) {
		return new Supplier<UpdateChangeLog>() {
			@Override
			public UpdateChangeLog get() {
				return new AddedChangeLog(googleCalendarFactory.getGoogleCalendarsByCalendarType().get(calendarType),
						gameDao.findByBusinessKey(competition, location, opponents, season));
			}
		};
	}

	/**
	 * Creates the updated change log.
	 * 
	 * @param calendarType
	 *          the calendar type
	 * @param competition
	 *          the competition
	 * @param location
	 *          the location
	 * @param opponents
	 *          the opponents
	 * @param season
	 *          the season
	 * @return the supplier
	 */
	protected Supplier<UpdateChangeLog> createUpdatedChangeLog(final CalendarType calendarType,
			final Competition competition, final Location location, final String opponents, final int season) {
		return new Supplier<UpdateChangeLog>() {
			@Override
			public UpdateChangeLog get() {
				return new UpdatedChangeLog(googleCalendarFactory.getGoogleCalendarsByCalendarType().get(calendarType),
						gameDao.findByBusinessKey(competition, location, opponents, season));
			}
		};
	}

	/**
	 * Creates the removed change log.
	 * 
	 * @param calendarType
	 *          the calendar type
	 * @param competition
	 *          the competition
	 * @param location
	 *          the location
	 * @param opponents
	 *          the opponents
	 * @param season
	 *          the season
	 * @return the supplier
	 */
	protected Supplier<UpdateChangeLog> createRemovedChangeLog(final CalendarType calendarType,
			final Competition competition, final Location location, final String opponents, final int season) {
		return new Supplier<UpdateChangeLog>() {
			@Override
			public UpdateChangeLog get() {
				return new RemovedChangeLog(googleCalendarFactory.getGoogleCalendarsByCalendarType().get(calendarType), gameDao
						.findByBusinessKey(competition, location, opponents, season).getId().toString());
			}
		};
	}

	/**
	 * Creates the game locator.
	 * 
	 * @param competition
	 *          the competition
	 * @param location
	 *          the location
	 * @param opponents
	 *          the opponents
	 * @param season
	 *          the season
	 * @return the game locator
	 */
	protected GameLocator createGameLocator(Competition competition, Location location, String opponents, int season) {
		return GameLocator.gameKeyLocator(new GameKey(competition, location, opponents, season));
	}

	/**
	 * The Class MainPageServiceImpl.
	 */
	class MainPageServiceImpl implements MainPageService {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public URI getFixturesUri() {
			return URI.create("http://localhost:8081");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public URI getTicketsUri() {
			return URI.create("http://localhost:8082");
		}
	}
}
