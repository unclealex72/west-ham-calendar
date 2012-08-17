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

package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;


/**
 * A handler to allow proxies to intercept {@link IOException} and see if what actually was
 * thrown was a google authentication exception.
 * @author alex
 * 
 */
public class GoogleAuthenticationAwareInvocationHandler implements InvocationHandler {

	/**
	 * The {@link GoogleCalendarDao} that is being proxied.
	 */
	private final GoogleCalendarDao i_googleCalendarDao;

	/**
	 * Instantiates a new google authentication aware invocation handler.
	 * 
	 * @param googleCalendarDao
	 *          the google calendar dao
	 */
	public GoogleAuthenticationAwareInvocationHandler(GoogleCalendarDao googleCalendarDao) {
		super();
		i_googleCalendarDao = googleCalendarDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(getGoogleCalendarDao(), args);
		}
		catch (InvocationTargetException e) {
			final Throwable targetException = e.getTargetException();
			if (targetException instanceof HttpResponseException) {
				final HttpResponse response = ((HttpResponseException) targetException).getResponse();
				if (response.getStatusCode() == HttpStatusCodes.STATUS_CODE_FORBIDDEN) {
					throw new GoogleAuthenticationFailedException(response.getRequest().getMethod().name() + ": "
							+ response.getRequest().getUrl().build(), targetException);
				}
			}
			throw targetException;
		}
	}

	/**
	 * Gets the {@link GoogleCalendarDao} that is being proxied.
	 * 
	 * @return the {@link GoogleCalendarDao} that is being proxied
	 */
	public GoogleCalendarDao getGoogleCalendarDao() {
		return i_googleCalendarDao;
	}

}
