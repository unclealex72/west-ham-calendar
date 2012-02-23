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

	private AutowireCapableBeanFactory i_autowireCapableBeanFactory;
	private CalendarFactory i_calendarFactory;
	private Function<String, String> i_calendarFormatter;
	private Function<String, String> i_gameFormatter;

	/**
	 * {@inheritDoc}
	 * @throws GoogleAuthenticationFailedException 
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

	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
		return i_autowireCapableBeanFactory;
	}

	public void setAutowireCapableBeanFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		i_autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

	public CalendarFactory getCalendarFactory() {
		return i_calendarFactory;
	}

	public void setCalendarFactory(CalendarFactory calendarFactory) {
		i_calendarFactory = calendarFactory;
	}

	public Function<String, String> getCalendarFormatter() {
		return i_calendarFormatter;
	}

	public void setCalendarFormatter(Function<String, String> calendarFormatter) {
		i_calendarFormatter = calendarFormatter;
	}

	public Function<String, String> getGameFormatter() {
		return i_gameFormatter;
	}

	public void setGameFormatter(Function<String, String> gameFormatter) {
		i_gameFormatter = gameFormatter;
	}
}
