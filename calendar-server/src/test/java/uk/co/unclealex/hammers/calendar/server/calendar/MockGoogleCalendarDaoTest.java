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

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Before;

import uk.co.unclealex.hammers.calendar.server.calendar.MockGoogleCalendarDao.MockGame;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;


/**
 * The Class MockGoogleCalendarDaoTest.
 * 
 * @author alex
 */
public class MockGoogleCalendarDaoTest extends AbstractGoogleCalendarDaoTest {

	/** The mock google calendar dao. */
	MockGoogleCalendarDao mockGoogleCalendarDao = new MockGoogleCalendarDao();

	/**
	 * Clear dao.
	 */
	@Before
	public void clearDao() {
		mockGoogleCalendarDao.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkGame(String calendarId, String eventId, String gameId, Competition competition,
			Location location, String opponents, DateTime dateStarted, DateTime dateFinished, String result,
			Integer attendence, String matchReport, String televisionChannel, boolean busy) throws IOException {
		MockGame mockGame = mockGoogleCalendarDao.getGamesForCalendar(calendarId).get(gameId);
		Assert.assertEquals("The wrong mock game was returned.", new MockGame(competition, location, opponents,
				new Interval(dateStarted, dateFinished), result, attendence, matchReport, televisionChannel, busy), mockGame);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkGameRemoved(String calendarId, String eventId) throws IOException {
		Assert.assertFalse("The event " + eventId + " was still found in " + calendarId, mockGoogleCalendarDao
				.getGamesForCalendar(calendarId).containsKey(eventId));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkCalendar(String calendarId, String title, String description) throws IOException {
		// This always works.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPrimaryCalendarId() {
		return "PRIMARY";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSecondaryCalendarId() {
		return "SECONDARY";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DurationFindingAwareGoogleCalendarDao getGoogleCalendarDao() {
		return mockGoogleCalendarDao;
	}

}
