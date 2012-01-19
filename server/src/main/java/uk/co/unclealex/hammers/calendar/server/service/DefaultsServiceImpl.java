/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.common.collect.Maps;
import com.google.gdata.util.ServiceException;

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
@Transactional
public class DefaultsServiceImpl implements DefaultsService {

	private UserService i_userService;
	private CalendarConfigurationService i_calendarConfigurationService;
	private GoogleCalendarService i_googleCalendarService;
	private CalendarConfigurationDao i_calendarConfigurationDao;
	
	private String i_defaultUsername;
	private String i_defaultPassword;
	
	private List<CalendarConfiguration> i_defaultCalendarConfigurations;
	
	@Override
	public void ensureDefaultUsersExist() {
		getUserService().ensureDefaultUsersExists(getDefaultUsername(), getDefaultPassword());
	}

	@Override
	public void ensureDefaultCalendarsExistAndCalendarsAreSynchronised() throws IOException, GoogleAuthenticationFailedException, ServiceException {
		Map<CalendarType, CalendarConfiguration> calendarConfigurationsByCalendarType = 
				Maps.newHashMap(getCalendarConfigurationDao().getAllByKey());
		for (CalendarConfiguration defaultCalendarConfiguration : getDefaultCalendarConfigurations()) {
			CalendarType calendarType = defaultCalendarConfiguration.getCalendarType();
			if (calendarConfigurationsByCalendarType.get(calendarType) == null) {
				calendarConfigurationsByCalendarType.put(calendarType, defaultCalendarConfiguration);
			}
		}
		getGoogleCalendarService().cleanCalendars(calendarConfigurationsByCalendarType);
	}
	
	public List<CalendarConfiguration> getDefaultCalendarConfigurations() {
		return i_defaultCalendarConfigurations;
	}

	public void setDefaultCalendarConfigurations(List<CalendarConfiguration> defaultCalendarConfigurations) {
		i_defaultCalendarConfigurations = defaultCalendarConfigurations;
	}

	public UserService getUserService() {
		return i_userService;
	}

	public void setUserService(UserService userService) {
		i_userService = userService;
	}

	public CalendarConfigurationService getCalendarConfigurationService() {
		return i_calendarConfigurationService;
	}

	public void setCalendarConfigurationService(CalendarConfigurationService calendarConfigurationService) {
		i_calendarConfigurationService = calendarConfigurationService;
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

	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
	}

	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

}
