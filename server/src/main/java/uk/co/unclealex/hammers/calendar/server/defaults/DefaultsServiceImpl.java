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

	private static final Logger log = LoggerFactory.getLogger(DefaultsServiceImpl.class);

	/**
	 * The {@link CalendarConfigurationDao} used for finding and creaing calendar
	 * configurations.
	 */
	private CalendarConfigurationDao i_calendarConfigurationDao;

	/**
	 * The {@link GoogleCalendarDaoFactory} used for creating
	 * {@link GoogleCalendarDao}s.
	 */
	private GoogleCalendarDaoFactory i_googleCalendarDaoFactory;

	/**
	 * The {@link GoogleCalendarFactory} used for creating {@link GoogleCalendar}
	 * s.
	 */
	private GoogleCalendarFactory i_googleCalendarFactory;

	/**
	 * The {@link UserService} used to look for and create users.
	 */
	private UserService i_userService;

	/**
	 * The username for the default user.
	 */
	private String i_defaultUsername;

	/**
	 * The password for the default user.
	 */
	private String i_defaultPassword;

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

	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	public GoogleCalendarDaoFactory getGoogleCalendarDaoFactory() {
		return i_googleCalendarDaoFactory;
	}

	public void setGoogleCalendarDaoFactory(GoogleCalendarDaoFactory googleCalendarDaoFactory) {
		i_googleCalendarDaoFactory = googleCalendarDaoFactory;
	}

	public String getDefaultUsername() {
		return i_defaultUsername;
	}

	public void setDefaultUsername(String defaultUsername) {
		i_defaultUsername = defaultUsername;
	}

	public String getDefaultPassword() {
		return i_defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		i_defaultPassword = defaultPassword;
	}

	public UserService getUserService() {
		return i_userService;
	}

	public void setUserService(UserService userService) {
		i_userService = userService;
	}

	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return i_googleCalendarFactory;
	}

	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		i_googleCalendarFactory = googleCalendarFactory;
	}

}
