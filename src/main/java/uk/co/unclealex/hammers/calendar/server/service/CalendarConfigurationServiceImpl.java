/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.service;

import java.io.IOException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.common.collect.Lists;
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
public class CalendarConfigurationServiceImpl implements CalendarConfigurationService {

	private CalendarConfigurationDao i_calendarConfigurationDao;
	private GoogleCalendarService i_googleCalendarService;
	
	@Override
	public void remove(CalendarType calendarType) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			CalendarConfigurationDao calendarConfigurationDao = getCalendarConfigurationDao();
			CalendarConfiguration calendarConfiguration = calendarConfigurationDao.findByKey(calendarType);
			if (calendarConfiguration != null) {
				getGoogleCalendarService().remove(calendarConfiguration.getGoogleCalendarId());
				calendarConfigurationDao.remove(calendarType);
			}
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}

	@Override
	public void createOrUpdate(CalendarConfiguration calendarConfiguration, String googleCalendarId) throws GoogleAuthenticationFailedException {
		CalendarType calendarType = calendarConfiguration.getCalendarType();
		uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration cfg = 
				getCalendarConfigurationDao().findByKey(calendarType);
		if (cfg == null) {
			cfg = new uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration();
			cfg.setCalendarType(calendarType);
		}
		cfg.setGoogleCalendarId(googleCalendarId);
		cfg.setColour(calendarConfiguration.getColour());
		cfg.setReminderInMinutes(calendarConfiguration.getReminderInMinutes());
		cfg.setBusy(calendarConfiguration.isBusy());
		cfg.setShared(calendarConfiguration.isShared());
		getCalendarConfigurationDao().saveOrUpdate(cfg);
	}

	@Override
	public List<CalendarConfiguration> getAllCalendarConfigurations() {
		return Lists.newArrayList(getCalendarConfigurationDao().getAll());
	}
	
	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
	}
}
