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

package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.common.base.Function;


/**
 * Create a new {@link GoogleCalendarDao} using Spring.
 * 
 * @author alex
 * 
 */
public class SpringGoogleCalendarDaoFactory implements GoogleCalendarDaoFactory, ApplicationContextAware {

	/**
	 * The current Spring {@link AutowireCapableBeanFactory}. 
	 */
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	/**
	 * The {@link CalendarFactory} that will create the connection to the Google Calendar API.
	 */
	private CalendarFactory calendarFactory;

	/**
	 * A {@link Function} to print a calendar given its id.
	 */
	private Function<String, String> calendarFormatter;
	
	/**
	 * A {@link Function} to print a calendar given its id.
	 */
	private Function<String, String> gameFormatter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GoogleCalendarDao createGoogleCalendarDao() throws IOException, GoogleAuthenticationFailedException {
		GoogleCalendarDao googleCalendarDao = new GoogleCalendarDaoImpl(getCalendarFactory().createCalendar(),
				getCalendarFormatter(), getGameFormatter());
		getAutowireCapableBeanFactory().autowireBean(googleCalendarDao);
		InvocationHandler handler = new GoogleAuthenticationAwareInvocationHandler(googleCalendarDao);
		return (GoogleCalendarDao) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { GoogleCalendarDao.class }, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		setAutowireCapableBeanFactory(applicationContext.getAutowireCapableBeanFactory());
	}

	/**
	 * Gets the current Spring {@link AutowireCapableBeanFactory}.
	 * 
	 * @return the current Spring {@link AutowireCapableBeanFactory}
	 */
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
		return autowireCapableBeanFactory;
	}

	/**
	 * Sets the current Spring {@link AutowireCapableBeanFactory}.
	 * 
	 * @param autowireCapableBeanFactory
	 *          the new current Spring {@link AutowireCapableBeanFactory}
	 */
	public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

	/**
	 * Gets the {@link CalendarFactory} that will create the connection to the
	 * Google Calendar API.
	 * 
	 * @return the {@link CalendarFactory} that will create the connection to the
	 *         Google Calendar API
	 */
	public CalendarFactory getCalendarFactory() {
		return calendarFactory;
	}

	/**
	 * Sets the {@link CalendarFactory} that will create the connection to the
	 * Google Calendar API.
	 * 
	 * @param calendarFactory
	 *          the new {@link CalendarFactory} that will create the connection to
	 *          the Google Calendar API
	 */
	public void setCalendarFactory(CalendarFactory calendarFactory) {
		this.calendarFactory = calendarFactory;
	}

	/**
	 * Gets the a {@link Function} to print a calendar given its id.
	 * 
	 * @return the a {@link Function} to print a calendar given its id
	 */
	public Function<String, String> getCalendarFormatter() {
		return calendarFormatter;
	}

	/**
	 * Sets the a {@link Function} to print a calendar given its id.
	 * 
	 * @param calendarFormatter
	 *          the new a {@link Function} to print a calendar given its id
	 */
	public void setCalendarFormatter(Function<String, String> calendarFormatter) {
		this.calendarFormatter = calendarFormatter;
	}

	/**
	 * Gets the a {@link Function} to print a calendar given its id.
	 * 
	 * @return the a {@link Function} to print a calendar given its id
	 */
	public Function<String, String> getGameFormatter() {
		return gameFormatter;
	}

	/**
	 * Sets the a {@link Function} to print a calendar given its id.
	 * 
	 * @param gameFormatter
	 *          the new a {@link Function} to print a calendar given its id
	 */
	public void setGameFormatter(Function<String, String> gameFormatter) {
		this.gameFormatter = gameFormatter;
	}
}
