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

import org.hibernate.Query;

import uk.co.unclealex.hammers.calendar.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.model.CalendarType;


/**
 * The Hibernate implementation of {@link CalendarConfigurationDao}.
 * @author alex
 *
 */
public class HibernateCalendarConfigurationDao extends
		BusinessKeyHibernateDaoSupport<CalendarType, CalendarConfiguration> implements CalendarConfigurationDao {

	/**
	 * Instantiates a new hibernate calendar configuration dao.
	 */
	public HibernateCalendarConfigurationDao() {
		super(CalendarConfiguration.class, "calendarType");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalendarConfiguration findByGoogleCalendarId(String googleCalendarId) {
		Query query = getSession().createQuery("from CalendarConfiguration where googleCalendarId = :googleCalendarId")
				.setString("googleCalendarId", googleCalendarId);
		return unique(query);
	}
}
