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

package uk.co.unclealex.hammers.calendar.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.unclealex.hammers.calendar.dao.TicketingCalendarDao;
import uk.co.unclealex.hammers.calendar.model.CalendarType;
import uk.co.unclealex.hammers.calendar.model.TicketingCalendar;


/**
 * The Class HibernateTicketingCalendarDaoTest.
 * 
 * @author alex
 */
public class HibernateTicketingCalendarDaoTest extends DaoTest {

	/** The ticketing calendar dao. */
	@Autowired TicketingCalendarDao ticketingCalendarDao;

	
	/**
	 * Instantiates a new hibernate ticketing calendar dao test.
	 */
	public HibernateTicketingCalendarDaoTest() {
		super(TicketingCalendar.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetup() throws Exception {
		// Do nothing
	}
	
	/**
	 * Test find by key.
	 */
	@Test
	public void testFindByKey() {
		TicketingCalendar attendedTicketingCalendar = new TicketingCalendar(CalendarType.ATTENDED);
		TicketingCalendar unattendedTicketingCalendar = new TicketingCalendar(CalendarType.UNATTENDED);
		ticketingCalendarDao.saveOrUpdate(attendedTicketingCalendar, unattendedTicketingCalendar);
		TicketingCalendar actualTicketingCalendar = ticketingCalendarDao.findByKey(CalendarType.ATTENDED);
		Assert.assertNotNull("Could not find the attended ticketing calendar.", actualTicketingCalendar);
		Assert.assertEquals("The attended ticketing calendar was incorrect.", attendedTicketingCalendar, actualTicketingCalendar);
	}

}
