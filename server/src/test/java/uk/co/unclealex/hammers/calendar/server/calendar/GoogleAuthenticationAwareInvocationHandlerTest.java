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

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.calendar.GameUpdateInformation;
import uk.co.unclealex.hammers.calendar.server.calendar.GoogleAuthenticationAwareInvocationHandler;
import uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarDao;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.api.client.http.GoogleJsonResponseExceptionFactory;
import com.google.common.collect.BiMap;

/**
 * @author alex
 *
 */
public class GoogleAuthenticationAwareInvocationHandlerTest {

	interface MethodRunner {
		public void runMethod(GoogleCalendarDao googleCalendarDao) throws IOException, GoogleAuthenticationFailedException;
	}

	class TestGoogleCalendarDao implements GoogleCalendarDao {

		@Override
		public GameUpdateInformation createOrUpdateGame(String calendarId, String eventId, String gameId, Competition competition,
				Location location, String opponents, Interval gameInterval, String result,
				Integer attendence, String matchReport, String televisionChannel, boolean busy)
				throws IOException, GoogleAuthenticationFailedException {
			throw GoogleJsonResponseExceptionFactory.createGoogleJsonResponseException();
		}

		@Override
		public String findGame(String calendarId, String gameId, DateTime searchDate) throws IOException,
				GoogleAuthenticationFailedException {
			throw new IOException("Darn");
		}

		@Override
		public void moveGame(String sourceCalendarId, String targetCalendarId, String eventId, String gameId, boolean busy)
				throws IOException, GoogleAuthenticationFailedException {
			// Do nothing
		}
		
		@Override
		public void removeGame(String calendarId, String eventId, String gameId) throws IOException, GoogleAuthenticationFailedException {
			// Do nothing
		}
		
		@Override
		public String createOrUpdateCalendar(String calendarId, String calendarTitle, String calendarDescription)
				throws IOException, GoogleAuthenticationFailedException {
			return null;
		}

		@Override
		public BiMap<String, String> listGameIdsByEventId(String calendarId) throws IOException,
				GoogleAuthenticationFailedException {
			throw new RuntimeException("Darn");
		}
		
	}
	
	@Test
	public void testNoException() {
		MethodRunner methodRunner = new MethodRunner() {
			
			@Override
			public void runMethod(GoogleCalendarDao googleCalendarDao) throws IOException, GoogleAuthenticationFailedException {
				googleCalendarDao.createOrUpdateCalendar(null, null, null);
			}
		};
		runTest(methodRunner, null);
	}

	@Test
	public void testIoException() {
		MethodRunner methodRunner = new MethodRunner() {
			
			@Override
			public void runMethod(GoogleCalendarDao googleCalendarDao) throws IOException, GoogleAuthenticationFailedException {
				googleCalendarDao.findGame(null, null, null);
			}
		};
		runTest(methodRunner, IOException.class);
	}

	@Test
	public void testRuntimeException() {
		MethodRunner methodRunner = new MethodRunner() {
			
			@Override
			public void runMethod(GoogleCalendarDao googleCalendarDao) throws IOException, GoogleAuthenticationFailedException {
				googleCalendarDao.listGameIdsByEventId(null);
			}
		};
		runTest(methodRunner, RuntimeException.class);
	}

	@Test
	public void testGoogleAuthenticationFailedException() {
		MethodRunner methodRunner = new MethodRunner() {
			
			@Override
			public void runMethod(GoogleCalendarDao googleCalendarDao) throws IOException, GoogleAuthenticationFailedException {
				googleCalendarDao.createOrUpdateGame(null, null, null, null, null, null, null, null, null, null, null, false);
			}
		};
		runTest(methodRunner, GoogleAuthenticationFailedException.class);
	}

	/**
	 * @param methodRunner
	 * @param class1
	 */
	protected void runTest(MethodRunner methodRunner, Class<? extends Throwable> expectedExceptionClass) {
		try {
			InvocationHandler handler = new GoogleAuthenticationAwareInvocationHandler(new TestGoogleCalendarDao());
			GoogleCalendarDao googleCalendarDao = (GoogleCalendarDao) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { GoogleCalendarDao.class }, handler);
			methodRunner.runMethod(googleCalendarDao);
			if (expectedExceptionClass != null) {
				Assert.fail("Expected exception " + expectedExceptionClass + " to be thrown but none was.");
			}
		}
		catch (Throwable t) {
			if (expectedExceptionClass == null) {
				Assert.fail("Exception " + t.getClass() + " was thrown but no exception was expected");
			}
			else {
				Assert.assertEquals("The wrong exception was thrown.", expectedExceptionClass, t.getClass());
			}
		}
	}
}
