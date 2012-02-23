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

package uk.co.unclealex.hammers.calendar.server.update;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import uk.co.unclealex.hammers.calendar.server.calendar.MockGoogleCalendarDao;
import uk.co.unclealex.hammers.calendar.server.calendar.UpdateChangeLog;
import uk.co.unclealex.hammers.calendar.server.calendar.UpdateChangeLog.Action;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
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
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/application-contexts/update/context.xml", "/application-contexts/dao/context.xml",
		"/application-contexts/dao/test-db.xml", "/application-contexts/calendar/context.xml",
		"/application-contexts/calendar/test-dao.xml" })
@SuppressWarnings({"unchecked", "deprecation"})
public class MainUpdateServiceImplTest {

	@Autowired
	MainUpdateServiceImpl mainUpdateService;
	@Autowired
	SimpleJdbcTemplate simpleJdbcTemplate;
	@Autowired
	CalendarConfigurationDao calendarConfigurationDao;
	@Autowired
	GoogleCalendarFactory googleCalendarFactory;
	@Autowired
	GameDao gameDao;
	@Autowired
	MockGoogleCalendarDao mockGoogleCalendarDao;

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

	@Test
	public void testOneUpdate() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.HOME, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		expect(createUpdateChangeLog(CalendarType.ALL, Competition.FACP, Location.HOME, "Them", 2011, Action.ADDED),
				createUpdateChangeLog(CalendarType.HOME, Competition.FACP, Location.HOME, "Them", 2011, Action.ADDED),
				createUpdateChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.HOME, "Them", 2011, Action.ADDED));
	}

	@Test
	public void testSameUpdatesAreIdempotent() throws IOException, GoogleAuthenticationFailedException {
		GameLocator nonTicketGameLocator = createGameLocator(Competition.FACP, Location.HOME, "Them", 2011);
		GameLocator ticketsGameLocator = createGameLocator(5, 9, 2011, 15, 0);
		GameUpdateCommand[] fullGameUpdateCommands = new GameUpdateCommand[] {
				GameUpdateCommand.datePlayed(nonTicketGameLocator,
						dateOf(5, 9, 2011, 15, 0)),
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

	@Test
	public void testChangeOfDate() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 17, 30)));
		expect(createUpdateChangeLog(CalendarType.ALL, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED),
				createUpdateChangeLog(CalendarType.AWAY, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED),
				createUpdateChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED));
	}

	@Test
	public void testTicketsAdded() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.seasonTickets(createGameLocator(5, 9, 2011, 15, 0), dateOf(5, 9, 2011, 17, 30)),
				GameUpdateCommand.generalSaleTickets(createGameLocator(5, 9, 2011, 15, 0), dateOf(5, 9, 2011, 18, 30)));
		expect(
				createUpdateChangeLog(CalendarType.TICKETS_SEASON, Competition.FACP, Location.AWAY, "Them", 2011, Action.ADDED),
				createUpdateChangeLog(CalendarType.TICKETS_GENERAL_SALE, Competition.FACP, Location.AWAY, "Them", 2011,
						Action.ADDED));
	}

	@Test
	public void testGamePlayed() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		makeUpdates(GameUpdateCommand.result(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011), "1-0"));
		expect(createUpdateChangeLog(CalendarType.ALL, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED),
				createUpdateChangeLog(CalendarType.AWAY, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED),
				createUpdateChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011, Action.UPDATED));
	}

	@Test
	public void testGameAttended() throws IOException, GoogleAuthenticationFailedException {
		makeUpdates(GameUpdateCommand.datePlayed(createGameLocator(Competition.FACP, Location.AWAY, "Them", 2011),
				dateOf(5, 9, 2011, 15, 0)));
		mainUpdateService.updateAllCalendars();
		Game game = gameDao.findByBusinessKey(Competition.FACP, Location.AWAY, "Them", 2011);
		game.setAttended(true);
		gameDao.saveOrUpdate(game);
		makeUpdates();
		expect(
				createUpdateChangeLog(CalendarType.ATTENDED, Competition.FACP, Location.AWAY, "Them", 2011, Action.ADDED),
				createUpdateChangeLog(CalendarType.UNATTENDED, Competition.FACP, Location.AWAY, "Them", 2011, Action.REMOVED));
	}

	protected void expect(Supplier<UpdateChangeLog>... expectedUpdateChangeLogSuppliers) throws IOException,
			GoogleAuthenticationFailedException {
		SortedSet<UpdateChangeLog> actualUpdateChangeLogs = mainUpdateService.updateAllCalendars();
		Function<Supplier<UpdateChangeLog>, UpdateChangeLog> supplierFunction = Suppliers.supplierFunction();
		UpdateChangeLog[] expectedUpdateChangeLogs = Iterables.toArray(
				Iterables.transform(Arrays.asList(expectedUpdateChangeLogSuppliers), supplierFunction), UpdateChangeLog.class);
		Arrays.sort(expectedUpdateChangeLogs);
		final Map<GoogleCalendar, CalendarType> calendarTypesByGoogleCalendar = HashBiMap.create(
				googleCalendarFactory.getGoogleCalendarsByCalendarType()).inverse();
		Function<UpdateChangeLog, String> formatter = new Function<UpdateChangeLog, String>() {
			@Override
			public String apply(UpdateChangeLog updateChangeLog) {
				Game game = updateChangeLog.getGame();
				return String.format(
						"createUpdateChangeLog(CalendarType.%s, Competition.%s, Location.%s, \"%s\", %d, Action.%s)",
						calendarTypesByGoogleCalendar.get(updateChangeLog.getGoogleCalendar()), game.getCompetition(),
						game.getLocation(), game.getOpponents(), game.getSeason(), updateChangeLog.getAction());
			}
		};
		System.out
				.println("expect(" + Joiner.on(",\n").join(Iterables.transform(actualUpdateChangeLogs, formatter)) + ");");
		Assert.assertArrayEquals("The wrong changes were performed.", expectedUpdateChangeLogs,
				Iterables.toArray(actualUpdateChangeLogs, UpdateChangeLog.class));
	}

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

	protected GameLocator createGameLocator(int day, int month, int year, int hour, int minute) {
		return GameLocator.datePlayedLocator(dateOf(day, month, year, hour, minute));
	}

	protected DateTime dateOf(int day, int month, int year, int hour, int minute) {
		return new DateTime(year, month, day, hour, minute, 0, 0, DateTimeZone.forID("Europe/London"));
	}

	protected Supplier<UpdateChangeLog> createUpdateChangeLog(final CalendarType calendarType,
			final Competition competition, final Location location, final String opponents, final int season,
			final Action action) {
		return new Supplier<UpdateChangeLog>() {
			@Override
			public UpdateChangeLog get() {
				return new UpdateChangeLog(action, gameDao.findByBusinessKey(competition, location, opponents, season),
						googleCalendarFactory.getGoogleCalendarsByCalendarType().get(calendarType));
			}
		};
	}

	protected GameLocator createGameLocator(Competition competition, Location location, String opponents, int season) {
		return GameLocator.gameKeyLocator(new GameKey(competition, location, opponents, season));
	}

	class MainPageServiceImpl implements MainPageService {
		@Override
		public URI getFixturesUri() {
			return URI.create("http://localhost:8081");
		}

		@Override
		public URI getTicketsUri() {
			return URI.create("http://localhost:8082");
		}
	}
}
