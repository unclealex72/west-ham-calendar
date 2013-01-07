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

package uk.co.unclealex.hammers.calendar.server.defaults;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.auth.UserService;
import uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarDao;
import uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarDaoFactory;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;


/**
 * The default implementation of {@link DefaultsService}.
 * @author alex
 * 
 */
public class DefaultsServiceImpl implements DefaultsService {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(DefaultsServiceImpl.class);

	/**
	 * The {@link CalendarConfigurationDao} used for finding and creaing calendar
	 * configurations.
	 */
	private CalendarConfigurationDao calendarConfigurationDao;

	/**
	 * The {@link GoogleCalendarDaoFactory} used for creating.
	 * {@link GoogleCalendarDao}s.
	 */
	private GoogleCalendarDaoFactory googleCalendarDaoFactory;

	/**
	 * The {@link GoogleCalendarFactory} used for creating {@link GoogleCalendar}
	 * s.
	 */
	private GoogleCalendarFactory googleCalendarFactory;

	/**
	 * The {@link UserService} used to look for and create users.
	 */
	private UserService userService;

	/**
	 * The username for the default user.
	 */
	private String defaultUsername;

	/**
	 * The password for the default user.
	 */
	private String defaultPassword;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createCalendars() throws IOException, GoogleAuthenticationFailedException {
		GoogleCalendarDao googleCalendarDao = getGoogleCalendarDaoFactory().createGoogleCalendarDao();
		CalendarConfigurationDao calendarConfigurationDao = getCalendarConfigurationDao();
		Map<CalendarType, CalendarConfiguration> calendarConfigurationsByCalendarType = calendarConfigurationDao
				.getAllByKey();
		for (Entry<CalendarType, GoogleCalendar> entry : getGoogleCalendarFactory().getGoogleCalendarsByCalendarType()
				.entrySet()) {
			CalendarType calendarType = entry.getKey();
			GoogleCalendar googleCalendar = entry.getValue();
			String calendarTitle = googleCalendar.getCalendarTitle();
			log.info("Creating calendar " + calendarTitle);
			String googleCalendarId = googleCalendarDao.createOrUpdateCalendar(null, calendarTitle,
					googleCalendar.getDescription());
			CalendarConfiguration calendarConfiguration = calendarConfigurationsByCalendarType.get(calendarType);
			if (calendarConfiguration == null) {
				calendarConfiguration = new CalendarConfiguration(null, calendarType, googleCalendarId);
			}
			else {
				calendarConfiguration.setGoogleCalendarId(googleCalendarId);
			}
			calendarConfigurationDao.saveOrUpdate(calendarConfiguration);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createDefaultUser() {
		getUserService().ensureDefaultUsersExists(getDefaultUsername(), getDefaultPassword());
	}

	/**
	 * Gets the {@link CalendarConfigurationDao} used for finding and creaing
	 * calendar configurations.
	 * 
	 * @return the {@link CalendarConfigurationDao} used for finding and creaing
	 *         calendar configurations
	 */
	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return calendarConfigurationDao;
	}

	/**
	 * Sets the {@link CalendarConfigurationDao} used for finding and creaing
	 * calendar configurations.
	 * 
	 * @param calendarConfigurationDao
	 *          the new {@link CalendarConfigurationDao} used for finding and
	 *          creaing calendar configurations
	 */
	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		this.calendarConfigurationDao = calendarConfigurationDao;
	}

	/**
	 * Gets the {@link GoogleCalendarDaoFactory} used for creating.
	 * 
	 * @return the {@link GoogleCalendarDaoFactory} used for creating
	 */
	public GoogleCalendarDaoFactory getGoogleCalendarDaoFactory() {
		return googleCalendarDaoFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarDaoFactory} used for creating.
	 * 
	 * @param googleCalendarDaoFactory
	 *          the new {@link GoogleCalendarDaoFactory} used for creating
	 */
	public void setGoogleCalendarDaoFactory(GoogleCalendarDaoFactory googleCalendarDaoFactory) {
		this.googleCalendarDaoFactory = googleCalendarDaoFactory;
	}

	/**
	 * Gets the username for the default user.
	 * 
	 * @return the username for the default user
	 */
	public String getDefaultUsername() {
		return defaultUsername;
	}

	/**
	 * Sets the username for the default user.
	 * 
	 * @param defaultUsername
	 *          the new username for the default user
	 */
	public void setDefaultUsername(String defaultUsername) {
		this.defaultUsername = defaultUsername;
	}

	/**
	 * Gets the password for the default user.
	 * 
	 * @return the password for the default user
	 */
	public String getDefaultPassword() {
		return defaultPassword;
	}

	/**
	 * Sets the password for the default user.
	 * 
	 * @param defaultPassword
	 *          the new password for the default user
	 */
	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	/**
	 * Gets the {@link UserService} used to look for and create users.
	 * 
	 * @return the {@link UserService} used to look for and create users
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Sets the {@link UserService} used to look for and create users.
	 * 
	 * @param userService
	 *          the new {@link UserService} used to look for and create users
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Gets the {@link GoogleCalendarFactory} used for creating
	 * {@link GoogleCalendar} s.
	 * 
	 * @return the {@link GoogleCalendarFactory} used for creating
	 *         {@link GoogleCalendar} s
	 */
	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return googleCalendarFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarFactory} used for creating
	 * {@link GoogleCalendar} s.
	 * 
	 * @param googleCalendarFactory
	 *          the new {@link GoogleCalendarFactory} used for creating
	 *          {@link GoogleCalendar} s
	 */
	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		this.googleCalendarFactory = googleCalendarFactory;
	}

}
