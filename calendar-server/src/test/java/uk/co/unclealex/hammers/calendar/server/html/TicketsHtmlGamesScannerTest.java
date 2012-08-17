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

package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.dates.DateServiceImpl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * The Class TicketsHtmlGamesScannerTest.
 * 
 * @author alex
 */
public class TicketsHtmlGamesScannerTest {

	/**
	 * Test blackpool away.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void testBlackpoolAway() throws IOException, URISyntaxException {
		Function<GameLocator, List<GameUpdateCommand>> expectedGameUpdateCommandsFunction = new Function<GameLocator, List<GameUpdateCommand>>() {
			@Override
			public List<GameUpdateCommand> apply(GameLocator gameLocator) {
				return Lists.newArrayList(GameUpdateCommand.priorityPointTickets(gameLocator, dateOf(25, 1, 2012, 10, 0)),
						GameUpdateCommand.bondHolderTickets(gameLocator, dateOf(26, 1, 2012, 9, 0)),
						GameUpdateCommand.seasonTickets(gameLocator, dateOf(28, 1, 2012, 9, 0)),
						GameUpdateCommand.academyTickets(gameLocator, dateOf(30, 1, 2012, 9, 0)),
						GameUpdateCommand.generalSaleTickets(gameLocator, dateOf(1, 2, 2012, 9, 0)));
			}
		};
		test("tickets-blackpool-away.html", dateOf(18, 2, 2012, 15, 0), expectedGameUpdateCommandsFunction);
	}

	/**
	 * Test southampton home.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void testSouthamptonHome() throws IOException, URISyntaxException {
		Function<GameLocator, List<GameUpdateCommand>> expectedGameUpdateCommandsFunction = new Function<GameLocator, List<GameUpdateCommand>>() {
			@Override
			public List<GameUpdateCommand> apply(GameLocator gameLocator) {
				return Lists.newArrayList(
						GameUpdateCommand.seasonTickets(gameLocator, dateOf(16, 1, 2012, 9, 0)),
						GameUpdateCommand.academyTickets(gameLocator, dateOf(10, 1, 2012, 9, 0)),
						GameUpdateCommand.generalSaleTickets(gameLocator, dateOf(17, 1, 2012, 9, 0)));
			}
		};
		test("tickets-southampton-home.html", dateOf(14, 2, 2012, 19, 45), expectedGameUpdateCommandsFunction);
	}

	/**
	 * Test palace home.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void testPalaceHome() throws IOException, URISyntaxException {
		Function<GameLocator, List<GameUpdateCommand>> expectedGameUpdateCommandsFunction = new Function<GameLocator, List<GameUpdateCommand>>() {
			@Override
			public List<GameUpdateCommand> apply(GameLocator gameLocator) {
				return Lists.newArrayList();
			}
		};
		test("tickets-palace-home.html", dateOf(25, 2, 2012, 12, 45), expectedGameUpdateCommandsFunction);
	}

	/**
	 * Test peterborough away.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void testPeterboroughAway() throws IOException, URISyntaxException {
		Function<GameLocator, List<GameUpdateCommand>> expectedGameUpdateCommandsFunction = new Function<GameLocator, List<GameUpdateCommand>>() {
			@Override
			public List<GameUpdateCommand> apply(GameLocator gameLocator) {
				return Lists.newArrayList();
			}
		};
		test("tickets-peterborough-away.html", dateOf(25, 2, 2012, 12, 45), expectedGameUpdateCommandsFunction);
	}

	/**
	 * Test.
	 * 
	 * @param resourceName
	 *          the resource name
	 * @param dateTime
	 *          the date time
	 * @param expectedGameUpdateCommandsFunction
	 *          the expected game update commands function
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	protected void test(String resourceName, DateTime dateTime,
			Function<GameLocator, List<GameUpdateCommand>> expectedGameUpdateCommandsFunction) throws IOException, URISyntaxException {
		TicketsHtmlSingleGameScanner ticketsHtmlSingleGameScanner = new TicketsHtmlSingleGameScanner();
		ticketsHtmlSingleGameScanner.setDateService(new DateServiceImpl());
		ticketsHtmlSingleGameScanner.setHtmlPageLoader(new HtmlPageLoaderImpl());
		URL url = getClass().getClassLoader().getResource("html/" + resourceName);
		SortedSet<GameUpdateCommand> actualGameUpdateCommands = ticketsHtmlSingleGameScanner.scan(url.toURI());
		SortedSet<GameUpdateCommand> expectedGameUpdateCommands = Sets.newTreeSet(expectedGameUpdateCommandsFunction
				.apply(GameLocator.datePlayedLocator(dateTime)));
		Assert.assertArrayEquals("The wrong updates were returned for " + resourceName,
				Iterables.toArray(expectedGameUpdateCommands, GameUpdateCommand.class),
				Iterables.toArray(actualGameUpdateCommands, GameUpdateCommand.class));
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
		return new DateTime(year, month, day, hour, minute).withZone(DateTimeZone.forID("Europe/London"));
	}
}
