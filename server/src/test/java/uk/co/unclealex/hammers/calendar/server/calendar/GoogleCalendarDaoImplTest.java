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
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author alex
 * 
 */
public class GoogleCalendarDaoImplTest extends AbstractGoogleCalendarDaoTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractGoogleCalendarDaoTest.class);

	private static DurationFindingAwareGoogleCalendarDao s_googleCalendarDaoImpl;
	private static com.google.api.services.calendar.Calendar s_calendarService;
	private static Calendar s_primaryCalendar;
	private static Calendar s_secondaryCalendar;

	/**
	 * Create a calendar dao to test.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() {
		try {
			com.google.api.services.calendar.Calendar calendarService = new ConstantOauthCalendarFactory().createCalendar();
			Function<String, String> formatter = new Function<String, String>() {
				@Override
				public String apply(String input) {
					return "{{" + input + "}}";
				}
			};
			DurationFindingAwareGoogleCalendarDao googleCalendarDaoImpl = new GoogleCalendarDaoImpl(calendarService,
					formatter, formatter);
			setCalendarService(calendarService);
			setGoogleCalendarDaoImpl(googleCalendarDaoImpl);
			setPrimaryCalendar(findOrCreateUnitTestCalendar("Unit Test Calendar"));
			setSecondaryCalendar(findOrCreateUnitTestCalendar("Unit Test Calendar (Secondary)"));
		}
		catch (Throwable t) {
			log.error("Could not set up this test class.", t);
			throw new RuntimeException("Could not set up a test class.", t);
		}
	}

	protected static Calendar findOrCreateUnitTestCalendar(String title) throws IOException {
		Calendar calendar = null;
		String pageToken = null;
		do {
			CalendarList calendarList = getCalendarService().calendarList().list().setPageToken(pageToken).execute();
			if (calendarList.getItems() != null) {
				for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
					if (title.equals(calendarListEntry.getSummary())) {
						calendar = getCalendarService().calendars().get(calendarListEntry.getId()).execute();
					}
				}
			}
		} while (pageToken != null && calendar == null);
		if (calendar == null) {
			calendar = new Calendar();
			calendar.setSummary(title);
			calendar.setDescription(title);
			calendar.setTimeZone("Europe/London");
			calendar.setLocation("Upton Park");
			calendar = getCalendarService().calendars().insert(calendar).execute();
			log.info("Created simple calendar " + calendar.getId());
		}
		return calendar;
	}

	@After
	public void tearDown() throws IOException {
		String pageToken = null;
		List<String> eventIds = Lists.newArrayList();
		String calendarId = getPrimaryCalendar().getId();
		do {
			Events events = getCalendarService().events().list(calendarId).setPageToken(pageToken).execute();
			if (events.getItems() != null) {
				for (Event event : events.getItems()) {
					eventIds.add(event.getId());
				}
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		for (String eventId : eventIds) {
			getCalendarService().events().delete(calendarId, eventId).execute();
		}
	}

	@AfterClass
	public static void tearDownClass() {
		try {
			// Juuuuust in case...
			List<String> publishedCalendarIds = Lists.newArrayList("pf4f1vat7io0iicsefduo02e7k%40group.calendar.google.com",
					"gn6t63cv72p8od3dhdrvqkvpug%40group.calendar.google.com",
					"709a8qimgsjhj7aq87b4ofd3s0%40group.calendar.google.com",
					"k7b6psuhbqueaaqtcfglvjk3gc%40group.calendar.google.com",
					"tt7o01aoihprtiau5283cu8r60%40group.calendar.google.com",
					"6mkgld1nke52okp5nlm8rgsm28%40group.calendar.google.com",
					"cnitefp2m9epk0prt2fgr9tvro%40group.calendar.google.com",
					"kb2nn05vhvmmt9rcdd8arkj0jg%40group.calendar.google.com",
					"sl4gtp1o2a8nf8jlcbo4n237k4%40group.calendar.google.com");
			List<String> existingCalendarIds = Lists.newArrayList();
			Calendar primaryUnitTestCalendar = getPrimaryCalendar();
			String primaryUnitTestCalendarId = primaryUnitTestCalendar == null ? null : primaryUnitTestCalendar.getId();
			Calendar secondaryUnitTestCalendar = getPrimaryCalendar();
			String secondaryUnitTestCalendarId = secondaryUnitTestCalendar == null ? null : secondaryUnitTestCalendar.getId();
			String pageToken = null;
			do {
				CalendarList calendarList = getCalendarService().calendarList().list().setPageToken(pageToken).execute();
				for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
					String id = calendarListEntry.getId();
					if (!publishedCalendarIds.contains(id) && !id.equals(primaryUnitTestCalendarId)
							&& !id.equals(secondaryUnitTestCalendarId)) {
						if ("owner".equals(calendarListEntry.getAccessRole())) {
							existingCalendarIds.add(id);
						}
					}
				}
				pageToken = calendarList.getNextPageToken();
			} while (pageToken != null);
			String primaryCalendarId = getCalendarService().calendars().get("primary").execute().getId();
			existingCalendarIds.remove(primaryCalendarId);
			for (String calendarId : existingCalendarIds) {
				log.info("Removing calendar " + calendarId);
				getCalendarService().calendars().delete(calendarId).execute();
			}
		}
		catch (Throwable t) {
			log.error("Could not set up this test.", t);
			throw new RuntimeException("Could not set up a test.", t);
		}
		finally {
			setCalendarService(null);
			setGoogleCalendarDaoImpl(null);
		}
	}

	protected void checkGame(String calendarId, String eventId, String gameId, Competition competition,
			Location location, String opponents, DateTime dateStarted, DateTime dateFinished, String result,
			Integer attendence, String matchReport, String televisionChannel, boolean busy) throws IOException {
		Assert.assertNotNull("The event id of a game was null", eventId);
		Event event = getCalendarService().events().get(calendarId, eventId).execute();
		Assert.assertNotNull("Could not find event with id " + eventId, event);
		Map<String, String> extendedProperties = event.getExtendedProperties().getShared();
		Assert.assertNotNull("The extended properties for game " + gameId + " were null");
		Assert.assertEquals("The wrong game id was stored in the extended properties.", gameId,
				extendedProperties.get("hammersId"));
		String title = event.getSummary();
		Assert.assertTrue("The entry title does not mention West Ham", title.contains("West Ham"));
		Assert.assertTrue("The entry title does not mention the competition", title.contains(competition.getName()));
		if (location == Location.HOME) {
			Assert.assertTrue("The entry title does not start with West Ham for a home game", title.startsWith("West Ham"));
		}
		else {
			Assert.assertFalse("The entry title starts with West Ham for an away game", title.startsWith("West Ham"));
		}
		Assert.assertTrue("The entry title does not mention the opponents", title.contains(opponents));
		Assert.assertTrue("The entry title does not mention the TV broadcaster", title.contains(televisionChannel));
		String description = event.getDescription();
		Assert.assertTrue("The entry description does not mention the result", description.contains(result));
		Assert.assertTrue("The entry description does not mention the attendence",
				description.contains(NumberFormat.getInstance().format(attendence.longValue())));
		Assert.assertTrue("The entry description does not mention the match report", description.contains(matchReport));
		checkDate("The game's start date was wrong", dateStarted, event.getStart());
		checkDate("The game's end date was wrong", dateFinished, event.getEnd());
		String transparency = event.getTransparency();
		if (transparency == null) {
			transparency = "opaque";
		}
		Assert.assertEquals("The game's transparency was wrong", busy ? "opaque" : "transparent", transparency);
	}

	protected void checkGameRemoved(String calendarId, final String eventId) throws IOException {
		try {
			Event e = getCalendarService().events().get(calendarId, eventId).execute();
			Assert.assertEquals("The deleted event has the wrong status.", "cancelled", e.getStatus());
			Predicate<Event> notSameEvent = new Predicate<Event>() {
				@Override
				public boolean apply(Event event) {
					return eventId != event.getId();
				}
			};
			String pageToken = null;
			do {
				Events events = getCalendarService().events().list(calendarId).setPageToken(pageToken).execute();
				List<Event> items = events.getItems();
				if (items != null) {
					Assert.assertTrue("The calendar contained the deleted event.", Iterables.all(items, notSameEvent));
				}
				pageToken = events.getNextPageToken();
			} while (pageToken != null);
		}
		catch (GoogleJsonResponseException e) {
			Assert.assertEquals("The wrong exception code was returned for a deleted event.", 404, e.getDetails().code);
		}
	}

	protected void checkCalendar(String calendarId, String title, String description) throws IOException {
		Calendar calendar = getCalendarService().calendars().get(calendarId).execute();
		Assert.assertEquals("The calendar has the wrong title", title, calendar.getSummary());
		Assert.assertEquals("The calendar has the wrong description", description, calendar.getDescription());
	}

	@Override
	protected DurationFindingAwareGoogleCalendarDao getGoogleCalendarDao() {
		return getGoogleCalendarDaoImpl();
	}

	@Override
	protected String getPrimaryCalendarId() {
		return getPrimaryCalendar().getId();
	}

	@Override
	protected String getSecondaryCalendarId() {
		return getSecondaryCalendar().getId();
	}

	public static DurationFindingAwareGoogleCalendarDao getGoogleCalendarDaoImpl() {
		return s_googleCalendarDaoImpl;
	}

	public static void setGoogleCalendarDaoImpl(DurationFindingAwareGoogleCalendarDao googleCalendarDaoImpl) {
		s_googleCalendarDaoImpl = googleCalendarDaoImpl;
	}

	public static com.google.api.services.calendar.Calendar getCalendarService() {
		return s_calendarService;
	}

	public static void setCalendarService(com.google.api.services.calendar.Calendar calendarService) {
		s_calendarService = calendarService;
	}

	public static Calendar getPrimaryCalendar() {
		return s_primaryCalendar;
	}

	public static void setPrimaryCalendar(Calendar calendar) {
		s_primaryCalendar = calendar;
	}

	public static Calendar getSecondaryCalendar() {
		return s_secondaryCalendar;
	}

	public static void setSecondaryCalendar(Calendar secondaryCalendar) {
		s_secondaryCalendar = secondaryCalendar;
	}

}
