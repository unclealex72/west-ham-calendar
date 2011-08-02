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
package uk.co.unclealex.hammers.calendar.server.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.dao.OauthTokenDao;
import uk.co.unclealex.hammers.calendar.server.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.google.TokenResponse;
import uk.co.unclealex.hammers.calendar.server.google.oauth.CalendarService;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.server.model.OauthToken;
import uk.co.unclealex.hammers.calendar.server.model.OauthTokenType;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.ILink;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclNamespace;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.acl.AclScope.Type;
import com.google.gdata.data.calendar.CalendarAclRole;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.SelectedProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.BaseEventEntry.Transparency;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.ServiceException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Transactional
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

	/**
	 * 
	 */
	private static final String GOOGLE_OAUTH_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	private static final String GOOGLE_OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	private static final String OWNED_CALENDAR_FEED = "https://www.google.com/calendar/feeds/default/owncalendars/full";
	private static final String ALL_CALENDAR_FEED = "https://www.google.com/calendar/feeds/default/allcalendars/full";
	
	private static final String CONSUMER_SECRET = "MFy0s8Zh5lmjaz0IEiwDdoEj";
	private static final String CONSUMER_KEY = "566815420118.apps.googleusercontent.com";

	private static final String WEST_HAM = "West Ham";

	private static final Logger log = Logger.getLogger(GoogleCalendarService.class);
	
	private static final String ID_PROPERTY = "hammersId";
	private static final long EXPIRY_LEEWAY = 1000 * 60 * 10; // 10 Minutes
	
	private Map<CalendarType, GoogleCalendar> i_googleCalendarsByCalendarType;
	private GameDao i_gameDao;
	private CalendarConfigurationDao i_calendarConfigurationDao;
	private OauthTokenDao i_oauthTokenDao;
	private List<String> i_publishedCalendarIds;
	
	@Override
	public void updateCalendars() {
		try {
			doUpdateCalendars();
		}
		catch (Throwable t) {
			log.error("Could not open any google calendars.", t);
		}
	}

	protected void doUpdateCalendars() throws IOException, ServiceException, GeneralSecurityException, GoogleAuthenticationFailedException {
		CalendarService calendarService = createCalendarService();
		CalendarFeed resultFeed = getOwnedCalendarsFeed(calendarService);
		List<CalendarEntry> calendarEntries = resultFeed.getEntries();
		Iterable<Game> games = getGameDao().getAll();
		Map<CalendarType, GoogleCalendar> googleCalendarsByCalendarType = getGoogleCalendarsByCalendarType();
		for (CalendarConfiguration calendarConfiguration : getCalendarConfigurationDao().getAll()) {
			GoogleCalendar googleCalendar = googleCalendarsByCalendarType.get(calendarConfiguration.getCalendarType());
			CalendarEntry calendarEntry = findCalendar(calendarEntries, calendarConfiguration);
			try {
				if (calendarEntry == null) {
					calendarEntry = createNewCalendar(calendarService, googleCalendar, calendarConfiguration);
				}

				updateCalendar(calendarService, calendarEntry, googleCalendar, calendarConfiguration, games);
			}
			catch (ServiceException e) {
				log.error("Could not create calendar " + googleCalendar.getCalendarTitle(), e);
			}
			catch (IOException e) {
				log.error("Could not create calendar " + googleCalendar.getCalendarTitle(), e);
			}
		}
	}

	protected CalendarEntry findCalendar(List<CalendarEntry> calendarEntries, CalendarConfiguration calendarConfiguration) {
		final String calendarId = calendarConfiguration.getGoogleCalendarId();
		Predicate<CalendarEntry> predicate = new Predicate<CalendarEntry>() {
			@Override
			public boolean apply(CalendarEntry calendarEntry) {
				return calendarEntry.getId().equals(calendarId);
			}
		};
		CalendarEntry calendarEntry = Iterables.find(calendarEntries, predicate, null);
		return calendarEntry;
	}

	protected CalendarEntry createNewCalendar(CalendarService calendarService, GoogleCalendar googleCalendar, CalendarConfiguration calendarConfiguration) throws IOException, ServiceException {
		String calendarTitle = googleCalendar.getCalendarTitle();
		log.info("Creating calendar " + calendarTitle);
		CalendarEntry calendarEntry = new CalendarEntry();
		configureCalendar(googleCalendar, calendarConfiguration, calendarTitle, calendarEntry);

		// Insert the calendar
		calendarEntry = calendarService.insert(new URL(OWNED_CALENDAR_FEED), calendarEntry);
		
		// Deal with a google bug where sometimes the wrong title is given to created calendars
		String givenTitle;
		int retries = 0;
		while (!(givenTitle = calendarEntry.getTitle().getPlainText()).equals(calendarTitle)) {
		  if (++retries == 4) {
		    calendarEntry.delete();
		    throw new ServiceException("Retried renaming calendar 3 times. Giving up!");
		  }
		  log.warn(
		    String.format(
		      "Calendar %s was incorrectly given title %s. Trying to change it. " +
		      "(See http://code.google.com/a/google.com/p/apps-api-issues/issues/detail?id=1892)", calendarTitle, givenTitle));
		  calendarEntry.setTitle(new PlainTextConstruct(calendarTitle));
		  calendarEntry = calendarEntry.update();
		}
		calendarConfiguration.setGoogleCalendarId(calendarEntry.getId());
		shareOrUnshareCalendar(calendarService, calendarEntry, calendarConfiguration.isShared());
		getCalendarConfigurationDao().saveOrUpdate(calendarConfiguration);
		return calendarEntry;
	}

	protected void configureCalendar(GoogleCalendar googleCalendar, CalendarConfiguration calendarConfiguration,
			String calendarTitle, CalendarEntry calendarEntry) {
		if (calendarTitle != null) {
			calendarEntry.setTitle(new PlainTextConstruct(calendarTitle));
		}
		calendarEntry.setSummary(new PlainTextConstruct(googleCalendar.getDescription()));
		calendarEntry.setTimeZone(new TimeZoneProperty("Europe/London"));
		calendarEntry.setColor(new ColorProperty(calendarConfiguration.getColour().getRgb()));
		calendarEntry.addLocation(new Where("","","Upton Park"));
		boolean selected = calendarConfiguration.isSelected();
		calendarEntry.setSelected(selected?SelectedProperty.TRUE:SelectedProperty.FALSE);
		calendarEntry.setHidden(selected?HiddenProperty.FALSE:HiddenProperty.TRUE);
	}

	protected void shareOrUnshareCalendar(CalendarService calendarService, CalendarEntry calendarEntry, boolean share) throws IOException,
			ServiceException, MalformedURLException {
		Link aclLink = calendarEntry.getLink(AclNamespace.LINK_REL_ACCESS_CONTROL_LIST, Link.Type.ATOM);
		AclFeed aclFeed = calendarService.getFeed(new URL(aclLink.getHref()), AclFeed.class);
		boolean sharingStillRequired = share;
		for (AclEntry aclEntry : aclFeed.getEntries()) {
			Type scopeType = aclEntry.getScope().getType();
			AclRole role = aclEntry.getRole();
			if (AclScope.Type.DEFAULT.equals(scopeType) && CalendarAclRole.READ.equals(role)) {
				if (share) {
					sharingStillRequired = false;
				}
				else {
					log.info(String.format("Removing public access to calendar '%s'", calendarEntry.getTitle().getPlainText()));
					aclEntry.delete();
				}
			}
		}
		if (sharingStillRequired) {
			log.info(String.format("Adding public access to calendar '%s'", calendarEntry.getTitle().getPlainText()));
			AclEntry newEntry = new AclEntry();
			newEntry.setScope(new AclScope(AclScope.Type.DEFAULT, null));
			newEntry.setRole(CalendarAclRole.READ);
			aclFeed.insert(newEntry);
		}
	}

	public void updateCalendar(
			CalendarService calendarService, CalendarEntry calendarEntry, GoogleCalendar googleCalendar, CalendarConfiguration calendarConfiguration, Iterable<Game> games) {
		try {
			doUpdateCalendar(calendarService, calendarEntry, googleCalendar, calendarConfiguration, games);
		}
		catch (IOException e) {
			log.error("Could not open calendar " + googleCalendar.getCalendarTitle(), e);
		}
		catch (ServiceException e) {
			log.error("Could not open calendar " + googleCalendar.getCalendarTitle(), e);
		}
	}

	protected void doUpdateCalendar(
			CalendarService calendarService, CalendarEntry calendarEntry, GoogleCalendar googleCalendar, 
			CalendarConfiguration calendarConfiguration, Iterable<Game> games) throws IOException, ServiceException {
		Map<Integer, CalendarEventEntry> entriesById = mapCalendarEntriesByGameId(calendarService, calendarEntry);
		Collection<Integer> foundIds = new ArrayList<Integer>();
		for (Game game : games) {
			try {
				if (googleCalendar.contains(game)) {
					foundIds.add(game.getId());
					updateEvent(calendarService, calendarEntry, googleCalendar, calendarConfiguration, entriesById, game);
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

	protected Map<Integer, CalendarEventEntry> mapCalendarEntriesByGameId(
			CalendarService calendarService, CalendarEntry calendarEntry) throws IOException, ServiceException {
		URL feedUrl = asFeed(calendarEntry);
		CalendarEventFeed calendarEventFeed = calendarService.getFeed(feedUrl, CalendarEventFeed.class);
		Predicate<ExtendedProperty> predicate = new Predicate<ExtendedProperty>() {
			@Override
			public boolean apply(ExtendedProperty extendedProperty) {
				return ID_PROPERTY.equals(extendedProperty.getName());
			}
		};
		Map<Integer, CalendarEventEntry> entriesById = new HashMap<Integer, CalendarEventEntry>(); 
		for (CalendarEventEntry calendarEventEntry : calendarEventFeed.getEntries()) {
			ExtendedProperty extendedProperty = Iterables.find(calendarEventEntry.getExtendedProperty(), predicate, null);
			if (extendedProperty != null) {
				String id = extendedProperty.getValue();
				entriesById.put(Integer.valueOf(id), calendarEventEntry);
			}
		}
		return entriesById;
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

	public void updateEvent(CalendarService calendarService, CalendarEntry calendarEntry, GoogleCalendar calendar, CalendarConfiguration calendarConfiguration, Map<Integer, CalendarEventEntry> entriesById, Game game) throws IOException, ServiceException {
		String title = createTitle(game);
		URL feedUrl = asFeed(calendarEntry);
		int id = game.getId();
		boolean isNew = false;
		boolean isUpdated;
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
		Interval interval = createInterval(calendar, game);
		String description = createDescription(game);
		isUpdated = updateBusy(calendarEventEntry, calendarConfiguration);
		if (!title.equals(calendarEventEntry.getTitle().getPlainText())) {
			calendarEventEntry.setTitle(new PlainTextConstruct(title));
			isUpdated = true;
		}
		final When when = calendarEventEntry.getTimes().get(0);
		isUpdated = 
			updateTime(
					interval.getStart(), when, 
					new Function<When, DateTime>() { public DateTime apply(When when) { return when.getStartTime(); }}, 
					new TimeUpdater() { public void updateTime(DateTime dateTime) { when.setStartTime(dateTime); }})
			|| isUpdated;
		
		isUpdated = 
			updateTime(
					interval.getEnd(), when, 
					new Function<When, DateTime>() { public DateTime apply(When when) { return when.getEndTime(); }}, 
					new TimeUpdater() { public void updateTime(DateTime dateTime) { when.setEndTime(dateTime); }})
			|| isUpdated;
		
		isUpdated = updateReminder(calendarEventEntry, calendarConfiguration) || isUpdated;
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
		org.joda.time.DateTime startTime = new org.joda.time.DateTime(calendar.getGameDate(game));
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

	protected boolean updateTime(org.joda.time.DateTime newGameTime, When when, Function<When, DateTime> function,
			TimeUpdater timeUpdater) {
		DateTime newDateTime = DateTime.parseDateTime(newGameTime.toString(ISODateTimeFormat.dateTimeNoMillis()));
		DateTime oldDateTime = function.apply(when);
		if (newDateTime.equals(oldDateTime)) {
			return false;
		}
		else {
			timeUpdater.updateTime(newDateTime);
			return true;
		}
	}

	protected interface TimeUpdater {
		public void updateTime(DateTime newGameTime);
	}
	
	protected boolean updateReminder(CalendarEventEntry calendarEventEntry, CalendarConfiguration calendarConfiguration) {
		Integer reminderInMinutes = calendarConfiguration.getReminderInMinutes();
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

	protected boolean updateBusy(CalendarEventEntry calendarEventEntry, CalendarConfiguration calendarConfiguration) {
		Transparency transparency = calendarEventEntry.getTransparency();
		boolean busy = calendarConfiguration.isBusy();
		if (busy != (Transparency.OPAQUE.equals(transparency))) {
			calendarEventEntry.setTransparency(busy?Transparency.OPAQUE:Transparency.TRANSPARENT);
			return true;
		}
		return false;
	}
	
	@Override
	public void remove(final String googleCalendarId) throws IOException, ServiceException, GoogleAuthenticationFailedException {
		CalendarService calendarService = createCalendarService();
		List<CalendarEntry> calendarEntries = getOwnedCalendarsFeed(calendarService).getEntries();
		Predicate<CalendarEntry> hasIdPredicate = new Predicate<CalendarEntry>() {
			@Override
			public boolean apply(CalendarEntry calendarEntry) {
				return googleCalendarId.equals(calendarEntry.getId());
			}
		};
		CalendarEntry calendarEntry = Iterables.find(calendarEntries, hasIdPredicate, null);
		if (calendarEntry != null) {
			log.info(String.format("Removing calendar '%s'", calendarEntry.getTitle().getPlainText()));
			calendarEntry.delete();
		}
	}

	@Override
	public String createOrUpdate(CalendarConfiguration calendarConfiguration) throws IOException, ServiceException, GoogleAuthenticationFailedException {
		CalendarService calendarService = createCalendarService();
		Map<CalendarType, CalendarEntry> existingCalendarsByCalendarType = mapExistingCalendarsByCalendarType(calendarService);
		CalendarType calendarType = calendarConfiguration.getCalendarType();
		CalendarEntry calendarEntry = existingCalendarsByCalendarType.get(calendarType);
		GoogleCalendar googleCalendar = getGoogleCalendarsByCalendarType().get(calendarType);
		if (calendarEntry == null) {
			calendarEntry = createNewCalendar(calendarService, googleCalendar, calendarConfiguration);
		}
		else {
			reconfigureCalendar(calendarService, googleCalendar, calendarConfiguration, calendarEntry);
		}
		return calendarEntry.getId();
	}

	protected void reconfigureCalendar(CalendarService calendarService, GoogleCalendar googleCalendar, CalendarConfiguration calendarConfiguration, CalendarEntry calendarEntry) throws IOException, ServiceException {
		configureCalendar(googleCalendar, calendarConfiguration, null, calendarEntry);
		calendarEntry.update();
		shareOrUnshareCalendar(calendarService, calendarEntry, calendarConfiguration.isShared());
		URL feedUrl = asFeed(calendarEntry);
		CalendarEventFeed calendarEventFeed = calendarService.getFeed(feedUrl, CalendarEventFeed.class);
		
		for (CalendarEventEntry calendarEventEntry : calendarEventFeed.getEntries()) {
			boolean updated = updateBusy(calendarEventEntry, calendarConfiguration);
			updated = updateReminder(calendarEventEntry, calendarConfiguration) || updated;
			if (updated) {
				calendarEventEntry.update();
			}
		}
	}

	protected URL asFeed(CalendarEntry calendarEntry) throws MalformedURLException {
		return new URL(calendarEntry.getLink(ILink.Rel.ALTERNATE, "application/atom+xml").getHref());
	}

	@Override
	public Iterable<CalendarColour> getUsedCalendarColours() throws IOException, GoogleAuthenticationFailedException, ServiceException {
		CalendarFeed calendarFeed = createCalendarService().getFeed(new URL(ALL_CALENDAR_FEED), CalendarFeed.class);
		Function<CalendarColour, String> rgbFunction = new Function<CalendarColour, String>() {
			@Override
			public String apply(CalendarColour calendarColour) {
				return calendarColour.getRgb();
			}
		};
		final Map<String, CalendarColour> rgbMap = Maps.uniqueIndex(Arrays.asList(CalendarColour.values()), rgbFunction);
		Function<CalendarEntry, CalendarColour> function = new Function<CalendarEntry, CalendarColour>() {
			@Override
			public CalendarColour apply(CalendarEntry calendarEntry) {
				return rgbMap.get(calendarEntry.getColor().getValue());
			}
		};
		return Sets.newTreeSet(Iterables.transform(calendarFeed.getEntries(), function));
	}

	@Override
	public void attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, ServiceException {
		moveGame(gameId, CalendarType.UNATTENDED, CalendarType.ATTENDED);
	}

	@Override
	public void unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, ServiceException {
		moveGame(gameId, CalendarType.ATTENDED, CalendarType.UNATTENDED);
	}

	protected void moveGame(int gameId, CalendarType fromCalendarType, CalendarType toCalendarType) throws IOException, ServiceException, GoogleAuthenticationFailedException {
		CalendarService calendarService = createCalendarService();
		Map<CalendarType, CalendarEntry> existingCalendarsByCalendarType = mapExistingCalendarsByCalendarType(calendarService);
		CalendarEntry fromCalendarEntry = existingCalendarsByCalendarType.get(fromCalendarType);
		if (fromCalendarEntry != null) {
			Map<Integer, CalendarEventEntry> calendarEntriesByGameId = mapCalendarEntriesByGameId(calendarService, fromCalendarEntry);
			CalendarEventEntry calendarEventEntry = calendarEntriesByGameId.get(gameId);
			if (calendarEventEntry != null) {
				log.info(
						String.format(
								"Removing event '%s' at '%s' from calendar '%s'",
								calendarEventEntry.getTitle().getPlainText(),
								calendarEventEntry.getTimes().get(0).getStartTime().toStringRfc822(),
								fromCalendarEntry.getTitle().getPlainText()));
				calendarEventEntry.delete();				
			}
		}
		CalendarEntry toCalendarEntry = existingCalendarsByCalendarType.get(toCalendarType);
		if (toCalendarEntry != null) {
			Map<Integer, CalendarEventEntry> calendarEntriesByGameId = mapCalendarEntriesByGameId(calendarService, toCalendarEntry);
			Game game = getGameDao().findById(gameId);
			if (game != null) {
				updateEvent(
						calendarService,
						toCalendarEntry, 
						getGoogleCalendarsByCalendarType().get(toCalendarType), 
						getCalendarConfigurationDao().findByKey(toCalendarType), 
						calendarEntriesByGameId,
						game);
			}
		}
	}

	protected Map<CalendarType, CalendarEntry> mapExistingCalendarsByCalendarType(CalendarService calendarService) throws IOException, ServiceException {
		final List<CalendarEntry> calendarEntries = getAllOwnedCalendars(calendarService);
		Map<CalendarType, CalendarEntry> existingCalendarsByCalendarType = new HashMap<CalendarType, CalendarEntry>();
		CalendarConfigurationDao calendarConfigurationDao = getCalendarConfigurationDao();
		for (CalendarEntry calendarEntry : calendarEntries) {
			String googleCalendarId = calendarEntry.getId();
			CalendarConfiguration calendarConfiguration = calendarConfigurationDao.findByGoogleCalendarId(googleCalendarId);
			if (calendarConfiguration != null) {
				existingCalendarsByCalendarType.put(calendarConfiguration.getCalendarType(), calendarEntry);
			}
		}
		return existingCalendarsByCalendarType;
	}

	protected List<CalendarEntry> getAllOwnedCalendars(CalendarService calendarService) throws IOException,
			ServiceException, MalformedURLException {
		CalendarFeed calendarFeed = getOwnedCalendarsFeed(calendarService);
		final List<CalendarEntry> calendarEntries = calendarFeed.getEntries();
		return calendarEntries;
	}

	protected CalendarFeed getOwnedCalendarsFeed(CalendarService calendarService) throws IOException, ServiceException,
			MalformedURLException {
		return calendarService.getFeed(new URL(OWNED_CALENDAR_FEED), CalendarFeed.class);
	}
	
	@Override
	public void cleanCalendars(Map<CalendarType, CalendarConfiguration> calendarConfigurationsByCalendarType) throws GoogleAuthenticationFailedException, IOException, ServiceException {
		CalendarService calendarService = createCalendarService();
		List<CalendarEntry> allOwnedCalendars = getAllOwnedCalendars(calendarService);
		TreeMap<CalendarType, Collection<CalendarEntry>> calendarEntriesByCalendarType = new TreeMap<CalendarType, Collection<CalendarEntry>>();
		Set<CalendarType> foundCalendarTypes = new TreeSet<CalendarType>();
		Supplier<List<CalendarEntry>> supplier = new Supplier<List<CalendarEntry>>() {
			@Override
			public List<CalendarEntry> get() {
				return new ArrayList<CalendarEntry>();
			}
		};
		ListMultimap<CalendarType, CalendarEntry> calendarEntriesByCalendarTypeMultimap = 
				Multimaps.newListMultimap(calendarEntriesByCalendarType, supplier);
		Map<String, CalendarType> calendarTypesByTitle = 
				HashBiMap.create(
					Maps.transformValues(getGoogleCalendarsByCalendarType(),
					new Function<GoogleCalendar, String>() {
						public String apply(GoogleCalendar googleCalendar) { return googleCalendar.getCalendarTitle(); }
					})).inverse();
		for (CalendarEntry ownedCalendar : allOwnedCalendars) {
			CalendarType calendarType = calendarTypesByTitle.get(ownedCalendar.getTitle().getPlainText());
			if (calendarType != null) {
				calendarEntriesByCalendarTypeMultimap.put(calendarType, ownedCalendar);
			}
		}
		
		for (Entry<CalendarType, CalendarConfiguration> entry : calendarConfigurationsByCalendarType.entrySet()) {
			CalendarType calendarType = entry.getKey();
			CalendarConfiguration calendarConfiguration = entry.getValue();
			Collection<CalendarEntry> calendars = calendarEntriesByCalendarType.get(calendarType);
			if (calendars == null || calendars.isEmpty()) {
				// No calendar exists. Create it.
				GoogleCalendar googleCalendar = getGoogleCalendarsByCalendarType().get(calendarType);
				try {
					createNewCalendar(calendarService, googleCalendar, calendarConfiguration);
				}
				catch (IOException e) {
					log.warn(
							String.format(
									"Could not create the new calendar '%s'", googleCalendar.getCalendarTitle()), e);
				}
				catch (ServiceException e) {
					log.warn(
							String.format(
									"Could not create the new calendar '%s'", googleCalendar.getCalendarTitle()), e);
				}
			}
			else {
				// Make sure exactly one calendar exists.
				updateCalendarConfigurationAndRemoveSpurious(calendarService, calendarConfiguration, calendars);
				foundCalendarTypes.add(calendarType);
			}
		}
		for (Entry<CalendarType, Collection<CalendarEntry>> entry : calendarEntriesByCalendarType.entrySet()) {
		  CalendarType calendarType = entry.getKey();
		  if (!foundCalendarTypes.contains(calendarType)) {
		    createNewCalendarConfigurationFromExisting(calendarService, calendarType, entry.getValue());
		  }
		}
	}
	
	/**
   * @param calendarType
   * @param value
	 * @throws ServiceException 
	 * @throws IOException 
	 * @throws MalformedURLException 
   */
  protected void createNewCalendarConfigurationFromExisting(
      CalendarService calendarService, CalendarType calendarType, Collection<CalendarEntry> calendarEntries) throws IOException, ServiceException {
    CalendarEntry calendarEntry = calendarEntries.iterator().next();
    log.info("Creating a new configuration for calendar " + calendarType);
    CalendarConfiguration calendarConfiguration = new CalendarConfiguration();
    calendarConfiguration.setCalendarType(calendarType);
    final String rgb = calendarEntry.getColor().getValue();
    Predicate<CalendarColour> hasRgbPredicate = new Predicate<CalendarColour>() {
      public boolean apply(CalendarColour calendarColour) {
        return calendarColour.getRgb().equals(rgb);
      }
    };
    calendarConfiguration.setColour(
        Iterables.find(Arrays.asList(CalendarColour.values()), hasRgbPredicate, CalendarColour.PLUM));
    calendarConfiguration.setSelected(calendarEntry.getSelected().equals(SelectedProperty.TRUE));
    Link aclLink = calendarEntry.getLink(AclNamespace.LINK_REL_ACCESS_CONTROL_LIST, Link.Type.ATOM);
    AclFeed aclFeed = calendarService.getFeed(new URL(aclLink.getHref()), AclFeed.class);
    Predicate<AclEntry> isSharedPredicate = new Predicate<AclEntry>() {
      @Override
      public boolean apply(AclEntry aclEntry) {
        Type scopeType = aclEntry.getScope().getType();
        AclRole role = aclEntry.getRole();
        return AclScope.Type.DEFAULT.equals(scopeType) && CalendarAclRole.READ.equals(role);
      }
    };
    calendarConfiguration.setShared(Iterables.find(aclFeed.getEntries(), isSharedPredicate) != null);
    // There is no easy way to check either of these so just cop out!
    calendarConfiguration.setReminderInMinutes(null);
    calendarConfiguration.setBusy(false);
    updateCalendarConfigurationAndRemoveSpurious(calendarService, calendarConfiguration, calendarEntries);
  }

  /**
	 * @param calendarService
	 * @param calendarConfiguration
	 * @param calendars
	 */
	protected void updateCalendarConfigurationAndRemoveSpurious(CalendarService calendarService,
			final CalendarConfiguration calendarConfiguration, Collection<CalendarEntry> calendars) {
		Predicate<CalendarEntry> hasCorrectId = new Predicate<CalendarEntry>() {
			@Override
			public boolean apply(CalendarEntry calendarEntry) {
				return calendarEntry.getId().equals(calendarConfiguration.getGoogleCalendarId());
			}
		};
		if (Iterables.find(calendars, hasCorrectId, null) == null) {
			// No correct calendar - just use the first in the list.
			calendarConfiguration.setGoogleCalendarId(calendars.iterator().next().getId());
			getCalendarConfigurationDao().saveOrUpdate(calendarConfiguration);
		}
		for (CalendarEntry spuriousCalendarEntry : Iterables.filter(calendars, Predicates.not(hasCorrectId))) {
			deleteCalendarIgnoringExceptions(calendarService, spuriousCalendarEntry);
		}
	}

	/**
	 * @param calendarService
	 * @param calendarEntry
	 */
	protected void deleteCalendarIgnoringExceptions(CalendarService calendarService, CalendarEntry calendarEntry) {
		try {
			deleteCalendar(calendarService, calendarEntry);
		}
		catch (IOException e) {
			log.info(
					String.format(
							"Removing calendar %s (%s)", calendarEntry.getId(), calendarEntry.getTitle().getPlainText()), e);
		}
		catch (ServiceException e) {
			log.info(
					String.format(
							"Removing calendar %s (%s)", calendarEntry.getId(), calendarEntry.getTitle().getPlainText()), e);
		}
	}

	/**
	 * @param calendarService
	 * @param spuriousCalendarEntry
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	protected void deleteCalendar(CalendarService calendarService, CalendarEntry calendarEntry) throws IOException, ServiceException {
		final String id = calendarEntry.getId();
    String title = calendarEntry.getTitle().getPlainText();
    log.info(String.format("Removing calendar %s (%s)", id, title));
		Predicate<String> isPublishedCalendar = new Predicate<String>() {
		  @Override
		  public boolean apply(String publishedId) {
		    return id.contains(publishedId);
		  }
    };
    if (Iterables.find(getPublishedCalendarIds(), isPublishedCalendar, null) == null) {
      //calendarEntry.delete();
    }
    else {
      log.warn(String.format("Calendar %s (%s) is a published calendar. It will not be deleted.", id, title));
    }
	}

	protected CalendarService createCalendarService() throws GoogleAuthenticationFailedException, IOException {
		return new CalendarService("hammers.unclealex.co.uk", getOauthToken());
	}
	
	protected String getOauthToken() throws GoogleAuthenticationFailedException, IOException {
		OauthTokenDao oauthTokenDao = getOauthTokenDao();
		OauthToken accessToken = oauthTokenDao.findByKey(OauthTokenType.ACCESS);
		if (accessToken == null || accessToken.getExpiryDate().getTime() - EXPIRY_LEEWAY < System.currentTimeMillis()) {
			if (accessToken == null) {
				accessToken = new OauthToken();
				accessToken.setTokenType(OauthTokenType.ACCESS);
			}
			updateAccessToken(accessToken);
			oauthTokenDao.saveOrUpdate(accessToken);
		}
		return accessToken.getToken();
	}

	@Override
	public String createGoogleAuthenticationUrlIfRequired() {
		String url;
		if (getOauthTokenDao().findByKey(OauthTokenType.ACCESS) == null) {
			url = String.format("%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
					GOOGLE_OAUTH_URL, CONSUMER_KEY, "urn:ietf:wg:oauth:2.0:oob", ALL_CALENDAR_FEED);
		}
		else {
			url = null;
		}
		return url;
	}
	
	@Override
	public void installSuccessCode(String successCode) throws IOException, GoogleAuthenticationFailedException {
		final OauthTokenDao oauthTokenDao = getOauthTokenDao();
		Function<OauthTokenType, OauthToken> tokenFactory = new Function<OauthTokenType, OauthToken>() {
			@Override
			public OauthToken apply(OauthTokenType oauthTokenType) {
				OauthToken token = oauthTokenDao.findByKey(oauthTokenType);
				if (token == null) {
					token = new OauthToken();
					token.setTokenType(oauthTokenType);
				}
				return token;
			}
		}; 
		OauthToken accessToken = tokenFactory.apply(OauthTokenType.ACCESS);
		OauthToken refreshToken = tokenFactory.apply(OauthTokenType.REFRESH);
		TokenResponse tokenResponse = requestToken("code", successCode, "authorization_code", true);
		accessToken.setToken(tokenResponse.getAccessToken());
		accessToken.setExpiryDate(tokenResponse.getExpiryDate());
		refreshToken.setToken(tokenResponse.getRefreshToken());
		for (OauthToken token : new OauthToken[] { accessToken, refreshToken }) {
			oauthTokenDao.saveOrUpdate(token);
		}
	}

	/**
	 * @param accessToken
	 * @throws GoogleAuthenticationFailedException 
	 * @throws IOException 
	 */
	protected void updateAccessToken(OauthToken accessToken) throws GoogleAuthenticationFailedException, IOException {
		OauthToken refreshToken = getOauthTokenDao().findByKey(OauthTokenType.REFRESH);
		if (refreshToken == null) {
			throw new GoogleAuthenticationFailedException("No refresh token found.");
		}
		TokenResponse tokenResponse = requestToken("refresh_token", refreshToken.getToken(), "refresh_token", false);
		accessToken.setToken(tokenResponse.getAccessToken());
		accessToken.setExpiryDate(tokenResponse.getExpiryDate());
		getOauthTokenDao().saveOrUpdate(accessToken);
	}

	protected TokenResponse requestToken(String tokenType, String token, String grantType, boolean includeRedirect) throws IOException, GoogleAuthenticationFailedException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(GOOGLE_OAUTH_TOKEN_URL);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("client_id", getClientId()));
		formparams.add(new BasicNameValuePair("client_secret", CONSUMER_SECRET));
		formparams.add(new BasicNameValuePair(tokenType, token));
		if (includeRedirect) {
			formparams.add(new BasicNameValuePair("redirect_uri", "urn:ietf:wg:oauth:2.0:oob"));
		}
		formparams.add(new BasicNameValuePair("grant_type", grantType));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new GoogleAuthenticationFailedException("Requesting a token refresh resulted in a http status of " + statusCode);
		}
		Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
		try {
			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
			return gson.fromJson(reader, TokenResponse.class);
		}
		finally {
			Closeables.closeQuietly(reader);
		}
	}

	@Override
	public String getClientId() {
		return CONSUMER_KEY;
	}
	
	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

	public Map<CalendarType, GoogleCalendar> getGoogleCalendarsByCalendarType() {
		return i_googleCalendarsByCalendarType;
	}

	public void setGoogleCalendarsByCalendarType(Map<CalendarType, GoogleCalendar> googleCalendarsByCalendarType) {
		i_googleCalendarsByCalendarType = googleCalendarsByCalendarType;
	}

	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}

  public List<String> getPublishedCalendarIds() {
    return i_publishedCalendarIds;
  }

  public void setPublishedCalendarIds(List<String> publishedCalendarIds) {
    i_publishedCalendarIds = publishedCalendarIds;
  }
}
