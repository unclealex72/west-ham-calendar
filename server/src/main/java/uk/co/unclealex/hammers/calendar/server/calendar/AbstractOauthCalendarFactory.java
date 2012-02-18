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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;

/**
 * A calendar factory that uses an Oauth 2 access and refresh token.
 * @author alex
 *
 */
public abstract class AbstractOauthCalendarFactory implements CalendarFactory {

	private static final String CONSUMER_SECRET = "MFy0s8Zh5lmjaz0IEiwDdoEj";
	private static final String CONSUMER_KEY = "566815420118.apps.googleusercontent.com";

	private static final Logger log = LoggerFactory.getLogger(AbstractOauthCalendarFactory.class);
	private HttpTransport i_httpTransport;
	private JsonFactory i_jsonFactory;
	
	/**
	 * Create a new {@link Calendar} using Oauth.
	 */
	@Override
	public Calendar createCalendar() throws IOException {
		GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
        getAccessToken(), getHttpTransport(), getJsonFactory(), CONSUMER_KEY, CONSUMER_SECRET,
        getRefreshToken()) {
			protected void onAccessToken(String accessToken) {
				log.info("Changing access token to " + accessToken);
				AbstractOauthCalendarFactory.this.setAccessToken(accessToken);
			};
		};
		return Calendar.builder(getHttpTransport(), getJsonFactory()).setApplicationName("West Ham Calendar")
				.setHttpRequestInitializer(accessProtectedResource).build();
	}

	/**
	 * @return The refresh token to use.
	 */
	protected abstract String getRefreshToken();

	/**
	 * @retrun The access token to use.
	 */
	protected abstract String getAccessToken();
	
	/**
	 * Set the access token when instructed by Google servers.
	 * @param accessToken The new access token.
	 */
	protected abstract void setAccessToken(String accessToken);

	public HttpTransport getHttpTransport() {
		return i_httpTransport;
	}

	public void setHttpTransport(HttpTransport httpTransport) {
		i_httpTransport = httpTransport;
	}

	public JsonFactory getJsonFactory() {
		return i_jsonFactory;
	}

	public void setJsonFactory(JsonFactory jsonFactory) {
		i_jsonFactory = jsonFactory;
	}
}
