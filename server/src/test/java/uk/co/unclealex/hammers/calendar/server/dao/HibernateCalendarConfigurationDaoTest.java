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

package uk.co.unclealex.hammers.calendar.server.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

/**
 * @author alex
 * 
 */
public class HibernateCalendarConfigurationDaoTest extends DaoTest {

	@Autowired
	CalendarConfigurationDao calendarConfigurationDao;

	public HibernateCalendarConfigurationDaoTest() {
		super(CalendarConfiguration.class);
	}

	public void doSetup() {
		CalendarConfiguration attendedCalendarConfiguration = new CalendarConfiguration(null, CalendarType.ATTENDED, "attended");
		CalendarConfiguration unattendedCalendarConfiguration = new CalendarConfiguration(null, CalendarType.UNATTENDED,
				"unattended");
		calendarConfigurationDao.saveOrUpdate(attendedCalendarConfiguration, unattendedCalendarConfiguration);
	}

	@Test
	public void testFindByGoogleCalendarId() {
		checkCalendar(calendarConfigurationDao.findByGoogleCalendarId("attended"));
	}

	@Test
	public void testFindByKey() {
		checkCalendar(calendarConfigurationDao.findByKey(CalendarType.ATTENDED));
	}

	protected void checkCalendar(CalendarConfiguration actualCalendarConfiguration) {
		Assert.assertNotNull("Could not find the attended calendar.", actualCalendarConfiguration);
		Assert.assertEquals("The attended calendar had the wrong calendar type.", CalendarType.ATTENDED,
				actualCalendarConfiguration.getCalendarType());
		Assert.assertEquals("The attended calendar had the wrong google id.", "attended",
				actualCalendarConfiguration.getGoogleCalendarId());
	}
}
