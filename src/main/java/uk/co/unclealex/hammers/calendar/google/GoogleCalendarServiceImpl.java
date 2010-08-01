/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.google;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;

import uk.co.unclealex.hammers.calendar.dao.GameDao;
import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclNamespace;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.calendar.CalendarAclRole;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.data.extensions.BaseEventEntry.Transparency;
import com.google.gdata.util.ServiceException;

public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	protected static final String WEST_HAM = "West Ham";

	private static final Logger log = Logger.getLogger(GoogleCalendarService.class);
	
	private static final String ID_PROPERTY = "hammersId";
	
	private List<GoogleCalendar> i_googleCalendars;
	private GoogleConfiguration i_googleConfiguration;
	private GameDao i_gameDao;
	
	@Override
	public void updateCalendars() {
		try {
			doUpdateCalendars();
		}
		catch (IOException e) {
			log.error("Could not open any google calendars.", e);
		}
		catch (ServiceException e) {
			log.error("Could not open any google calendars.", e);
		}
	}

	protected void doUpdateCalendars() throws IOException, ServiceException {
		URL calendarUrl = new URL("http://www.google.com/calendar/feeds/default/owncalendars/full");
		CalendarService calendarService = new CalendarService("uk.co.unclealex.hammers");
		GoogleConfiguration googleConfiguration = getGoogleConfiguration();
		calendarService.setUserCredentials(googleConfiguration.getUsername(), googleConfiguration.getPassword());
		CalendarFeed resultFeed = calendarService.getFeed(calendarUrl, CalendarFeed.class);
		List<CalendarEntry> calendarEntries = resultFeed.getEntries();
		List<Game> games = getGameDao().getAll();
		for (GoogleCalendar googleCalendar : getGoogleCalendars()) {
			final String calendarTitle = googleCalendar.getCalendarTitle();
			Predicate<CalendarEntry> predicate = new Predicate<CalendarEntry>() {
				@Override
				public boolean evaluate(CalendarEntry calendarEntry) {
					return calendarEntry.getTitle().getPlainText().equals(calendarTitle);
				}
			};
			CalendarEntry calendarEntry = CollectionUtils.find(calendarEntries, predicate);
			try {
				if (calendarEntry == null) {
					calendarEntry = createNewCalendar(calendarService, googleCalendar, calendarUrl);
				}
				Link link = calendarEntry.getLink(ILink.Rel.ALTERNATE, "application/atom+xml");
				updateCalendar(calendarService, googleCalendar, link.getHref(), games);
			}
			catch (ServiceException e) {
				log.error("Could not create calendar " + googleCalendar.getCalendarTitle(), e);
			}
			catch (IOException e) {
				log.error("Could not create calendar " + googleCalendar.getCalendarTitle(), e);
			}
		}
	}

	protected CalendarEntry createNewCalendar(CalendarService calendarService, GoogleCalendar googleCalendar, URL calendarUrl) throws IOException, ServiceException {
		String calendarTitle = googleCalendar.getCalendarTitle();
		log.info("Creating calendar " + calendarTitle);
		CalendarEntry calendarEntry = new CalendarEntry();
		calendarEntry.setTitle(new PlainTextConstruct(calendarTitle));
		calendarEntry.setSummary(new PlainTextConstruct(googleCalendar.getDescription()));
		calendarEntry.setTimeZone(new TimeZoneProperty("Europe/London"));
		calendarEntry.setHidden(HiddenProperty.FALSE);
		calendarEntry.setColor(new ColorProperty(googleCalendar.getDefaultColour()));
		calendarEntry.addLocation(new Where("","","Upton Park"));

		// Insert the calendar
		calendarEntry = calendarService.insert(calendarUrl, calendarEntry);
		if (googleCalendar.isShared()) {
		  Link aclLink = calendarEntry.getLink(AclNamespace.LINK_REL_ACCESS_CONTROL_LIST, Link.Type.ATOM);
		  AclEntry entry = new AclEntry();
		  entry.setScope(new AclScope(AclScope.Type.DEFAULT, null));
		  entry.setRole(CalendarAclRole.READ);
		  calendarService.insert(new URL(aclLink.getHref()), entry);
		}
		return calendarEntry;
	}

	public void updateCalendar(CalendarService calendarService, GoogleCalendar googleCalendar, String url, List<Game> games) {
		try {
			doUpdateCalendar(calendarService, googleCalendar, url, games);
		}
		catch (IOException e) {
			log.error("Could not open calendar " + googleCalendar.getCalendarTitle(), e);
		}
		catch (ServiceException e) {
			log.error("Could not open calendar " + googleCalendar.getCalendarTitle(), e);
		}
	}

	protected void doUpdateCalendar(CalendarService calendarService, GoogleCalendar googleCalendar, String url, List<Game> games) throws IOException, ServiceException {
		URL feedUrl = new URL(url);
		CalendarQuery query = new CalendarQuery(feedUrl);
		query.setMaxResults(Integer.MAX_VALUE);
		CalendarEventFeed calendarEventFeed = calendarService.query(query, CalendarEventFeed.class);
		Predicate<ExtendedProperty> predicate = new Predicate<ExtendedProperty>() {
			@Override
			public boolean evaluate(ExtendedProperty extendedProperty) {
				return ID_PROPERTY.equals(extendedProperty.getName());
			}
		};
		Map<Integer, CalendarEventEntry> entriesById = new HashMap<Integer, CalendarEventEntry>(); 
		for (CalendarEventEntry calendarEventEntry : calendarEventFeed.getEntries()) {
			ExtendedProperty extendedProperty = CollectionUtils.find(calendarEventEntry.getExtendedProperty(), predicate);
			if (extendedProperty != null) {
				String id = extendedProperty.getValue();
				entriesById.put(Integer.valueOf(id), calendarEventEntry);
			}
		}
		Collection<Integer> foundIds = new ArrayList<Integer>();
		for (Game game : games) {
			try {
				if (googleCalendar.evaluate(game)) {
					foundIds.add(game.getId());
					updateEvent(calendarService, googleCalendar, feedUrl, entriesById, game);
				}
			}
			catch (IOException e) {
				log.error(String.format("Could not update event %s for calendar %s", createTitle(game), googleCalendar.getCalendarTitle()), e);
			}
			catch (ServiceException e) {
				log.error(String.format("Could not update event %s for calendar %s", createTitle(game), googleCalendar.getCalendarTitle()), e);
			}
		}
		for (int id : foundIds) {
			entriesById.remove(id);
		}
		for (CalendarEventEntry calendarEventEntry : entriesById.values()) {
			doDelete(googleCalendar, calendarEventEntry);
		}
	}

	protected void doDelete(GoogleCalendar googleCalendar, CalendarEventEntry calendarEventEntry) {
		String entryTitle = calendarEventEntry.getTitle().getPlainText();
		String calendarTitle = googleCalendar.getCalendarTitle();
		try {
			log.info(
					String.format("Removing event %s in calendar %s.", entryTitle, calendarTitle));
			calendarEventEntry.delete();
		}
		catch (IOException e) {
			log.error(String.format("Could not delete event %s in calendar %s.", entryTitle, calendarTitle), e);
		}
		catch (ServiceException e) {
			log.error(String.format("Could not delete event %s in calendar %s.", entryTitle, calendarTitle), e);
		}
	}

	public void updateEvent(CalendarService calendarService, GoogleCalendar calendar, URL feedUrl, Map<Integer, CalendarEventEntry> entriesById, Game game) throws IOException, ServiceException {
		int id = game.getId();
		boolean isNew = false;
		boolean isUpdated = false;
		CalendarEventEntry calendarEventEntry = entriesById.get(id);
		if (calendarEventEntry == null) {
			calendarEventEntry = new CalendarEventEntry();
			calendarEventEntry.setTitle(new PlainTextConstruct(""));
			When now = new When();
			calendarEventEntry.addTime(now);
			calendarEventEntry.setContent(new PlainTextConstruct(""));
			ExtendedProperty extendedProperty = new ExtendedProperty();
			extendedProperty.setName(ID_PROPERTY);
			extendedProperty.setValue(Integer.toString(id));
			calendarEventEntry.addExtendedProperty(extendedProperty);
			isNew = true;
		}
		String title = createTitle(game);
		Interval interval = createInterval(calendar, game);
		String description = createDescription(game);
		Integer reminderInMinutes = calendar.getReminderInMinutes();
		boolean busy = calendar.isBusy();
		Transparency transparency = calendarEventEntry.getTransparency();
		if (busy != (Transparency.OPAQUE.equals(transparency))) {
			calendarEventEntry.setTransparency(busy?Transparency.OPAQUE:Transparency.TRANSPARENT);
			isUpdated = true;
		}
		if (!title.equals(calendarEventEntry.getTitle().getPlainText())) {
			calendarEventEntry.setTitle(new PlainTextConstruct(title));
			isUpdated = true;
		}
		final When when = calendarEventEntry.getTimes().get(0);
		isUpdated |= 
			updateTime(
					interval.getStart(), when, 
					new Transformer<When, DateTime>() { public DateTime transform(When when) { return when.getStartTime(); }}, 
					new Closure<DateTime>() { public void execute(DateTime dateTime) { when.setStartTime(dateTime); }});
		isUpdated |= 
			updateTime(
					interval.getEnd(), when, 
					new Transformer<When, DateTime>() { public DateTime transform(When when) { return when.getEndTime(); }}, 
					new Closure<DateTime>() { public void execute(DateTime dateTime) { when.setEndTime(dateTime); }});
		isUpdated |= updateReminder(calendarEventEntry, reminderInMinutes);
		if (!description.equals(calendarEventEntry.getTextContent().getContent().getPlainText())) {
			calendarEventEntry.setContent(new PlainTextConstruct(description));
			isUpdated = true;
		}
		if (isNew) {
			log.info(String.format("Creating game %s in calendar %s.", title, calendar.getCalendarTitle()));
			calendarService.insert(feedUrl, calendarEventEntry);
		}
		else if (isUpdated) {
			log.info(String.format("Updating game %s in calendar %s.", title, calendar.getCalendarTitle()));
			calendarEventEntry.update();
		}
		else {
			log.debug(String.format("Ignoring game %s in calendar %s.", title, calendar.getCalendarTitle()));
		}
	}

	protected Interval createInterval(GoogleCalendar calendar, Game game) {
		org.joda.time.DateTime startTime = new org.joda.time.DateTime(calendar.transform(game));
		Duration duration = new Duration(calendar.getDurationInHours() * 3600 * 1000);
		return new Interval(startTime, duration);
	}

	protected String createTitle(Game game) {
		String firstTeam;
		String secondTeam;
		Location location = game.getLocation();
		Competition competition = game.getCompetition();
		String opponents = game.getOpponents();
		
		if (location.equals(Location.HOME)) {
			firstTeam = WEST_HAM;
			secondTeam = opponents;
		}
		else {
			firstTeam = opponents;			
			secondTeam = WEST_HAM;
		}
		String title = String.format("%s vs. %s (%s)", firstTeam, secondTeam, competition.getName());
		String televisionChannel = game.getTelevisionChannel();
		if (televisionChannel != null) {
			title += String.format(" [%s]", televisionChannel);
		}
		return title;
	}		

	protected String createDescription(Game game) {
		List<String> descriptlets = new ArrayList<String>();
		String result = game.getResult();
		if (result != null) {
			descriptlets.add(result);
		}
		Integer attendence = game.getAttendence();
		if (attendence != null) {
			descriptlets.add(String.format("(Attendance: %s)", NumberFormat.getInstance().format(attendence.longValue())));
		}
		String matchReport = game.getMatchReport();
		if (matchReport != null) {
			descriptlets.add(matchReport);
		}
		return StringUtils.join(descriptlets, ' ');
	}

	protected boolean updateTime(org.joda.time.DateTime newGameTime, When when, Transformer<When, DateTime> transformer,
			Closure<DateTime> closure) {
		DateTime newDateTime = DateTime.parseDateTime(newGameTime.toString(ISODateTimeFormat.dateTimeNoMillis()));
		DateTime oldDateTime = transformer.transform(when);
		if (newDateTime.equals(oldDateTime)) {
			return false;
		}
		else {
			closure.execute(newDateTime);
			return true;
		}
	}

	protected boolean updateReminder(CalendarEventEntry calendarEventEntry, Integer reminderInMinutes) {
		List<Reminder> reminders = calendarEventEntry.getReminder();
		boolean updated;
		if (reminderInMinutes == null) {
			if (reminders.isEmpty()) {
				updated = false;
			}
			else {
				reminders.clear();
				updated = true;
			}
		}
		else {
			if (reminders.size() == 1 && reminderInMinutes.equals(reminders.get(0).getMinutes())) {
				updated = false;
			}
			else {
				reminders.clear();
				Reminder reminder = new Reminder();
				reminder.setMinutes(reminderInMinutes);
				reminders.add(reminder);
				updated = true;
			}
		}
		return updated;
	}

	public List<GoogleCalendar> getGoogleCalendars() {
		return i_googleCalendars;
	}

	public void setGoogleCalendars(List<GoogleCalendar> googleCalendars) {
		i_googleCalendars = googleCalendars;
	}

	public GoogleConfiguration getGoogleConfiguration() {
		return i_googleConfiguration;
	}

	public void setGoogleConfiguration(GoogleConfiguration googleConfiguration) {
		i_googleConfiguration = googleConfiguration;
	}

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

}
