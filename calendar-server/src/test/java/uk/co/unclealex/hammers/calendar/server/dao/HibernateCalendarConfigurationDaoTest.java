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

package uk.co.unclealex.hammers.calendar.server.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;


/**
 * The Class HibernateCalendarConfigurationDaoTest.
 * 
 * @author alex
 */
public class HibernateCalendarConfigurationDaoTest extends DaoTest {

	/** The calendar configuration dao. */
	@Autowired
	CalendarConfigurationDao calendarConfigurationDao;

	/**
	 * Instantiates a new hibernate calendar configuration dao test.
	 */
	public HibernateCalendarConfigurationDaoTest() {
		super(CalendarConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doSetup() {
		CalendarConfiguration attendedCalendarConfiguration = new CalendarConfiguration(null, CalendarType.ATTENDED, "attended");
		CalendarConfiguration unattendedCalendarConfiguration = new CalendarConfiguration(null, CalendarType.UNATTENDED,
				"unattended");
		calendarConfigurationDao.saveOrUpdate(attendedCalendarConfiguration, unattendedCalendarConfiguration);
	}

	/**
	 * Test find by google calendar id.
	 */
	@Test
	public void testFindByGoogleCalendarId() {
		checkCalendar(calendarConfigurationDao.findByGoogleCalendarId("attended"));
	}

	/**
	 * Test find by key.
	 */
	@Test
	public void testFindByKey() {
		checkCalendar(calendarConfigurationDao.findByKey(CalendarType.ATTENDED));
	}

	/**
	 * Check calendar.
	 * 
	 * @param actualCalendarConfiguration
	 *          the actual calendar configuration
	 */
	protected void checkCalendar(CalendarConfiguration actualCalendarConfiguration) {
		Assert.assertNotNull("Could not find the attended calendar.", actualCalendarConfiguration);
		Assert.assertEquals("The attended calendar had the wrong calendar type.", CalendarType.ATTENDED,
				actualCalendarConfiguration.getCalendarType());
		Assert.assertEquals("The attended calendar had the wrong google id.", "attended",
				actualCalendarConfiguration.getGoogleCalendarId());
	}
}
