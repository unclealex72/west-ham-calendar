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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.Calendar;


/**
 * A calendar factory that uses an Oauth 2 access and refresh token. Subclasses are responsible for storage
 * and retrieval of such tokens.
 * @author alex
 *
 */
public abstract class AbstractOauthCalendarFactory implements CalendarFactory {

	/**
	 * This application's secret key.
	 */
	private static final String CONSUMER_SECRET = "MFy0s8Zh5lmjaz0IEiwDdoEj";
	
	/**
	 * This application's public key.
	 */
	private static final String CONSUMER_KEY = "566815420118.apps.googleusercontent.com";
	
	/**
	 * The URL to supply to Google for redirecting.
	 */
	private static final String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
	
	/**
	 * The scope for full calendar access.
	 */
	private static final String SCOPE = "https://www.googleapis.com/auth/calendar";

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(AbstractOauthCalendarFactory.class);
	
	/**
	 * The {@link HttpTransport} used to connect to Google.
	 */
	private HttpTransport i_httpTransport;
	
	/**
	 * The {@link JsonFactory} used by the Google client.
	 */
	private JsonFactory i_jsonFactory;
	
	/**
	 * Create a new {@link Calendar} using Oauth.
	 * 
	 * @return A new Google {@link Calendar} object.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	@Override
	public Calendar createCalendar() throws IOException, GoogleAuthenticationFailedException {
		String accessToken = getAccessToken();
		String refreshToken = getRefreshToken();
		if (refreshToken == null) {
			throw new GoogleAuthenticationFailedException("There is no refresh token.");
		}
		GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
        accessToken, getHttpTransport(), getJsonFactory(), CONSUMER_KEY, CONSUMER_SECRET,
        refreshToken) {
			protected void onAccessToken(String accessToken) {
				log.info("Changing access token to " + accessToken);
				AbstractOauthCalendarFactory.this.setAccessToken(accessToken);
			};
		};
		return Calendar.builder(getHttpTransport(), getJsonFactory()).setApplicationName("West Ham Calendar")
				.setHttpRequestInitializer(accessProtectedResource).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthorisationUrl() {
    String authorisationUrl = new GoogleAuthorizationRequestUrl(CONSUMER_KEY, REDIRECT_URL, SCOPE)
        .build();
    return authorisationUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void installAuthorisationCode(String authorisationCode) throws IOException {
    AccessTokenResponse response = new GoogleAuthorizationCodeGrant(getHttpTransport(), getJsonFactory(),
        CONSUMER_KEY, CONSUMER_SECRET, authorisationCode, REDIRECT_URL).execute();
    installTokens(response.accessToken, response.refreshToken);
	}
	
	/**
	 * Install the access and refresh tokens.
	 * @param accessToken The access token to store.
	 * @param refreshToken The refresh token to store.
	 */
	protected abstract void installTokens(String accessToken, String refreshToken);
	
	/**
	 * Gets the refresh token.
	 * 
	 * @return The refresh token to use.
	 */
	protected abstract String getRefreshToken();

	/**
	 * Gets the access token.
	 * 
	 * @return The access token to use.
	 */
	protected abstract String getAccessToken();
	
	/**
	 * Set the access token when instructed by Google servers.
	 * @param accessToken The new access token.
	 */
	protected abstract void setAccessToken(String accessToken);

	/**
	 * Gets the {@link HttpTransport} used to connect to Google.
	 * 
	 * @return the {@link HttpTransport} used to connect to Google
	 */
	public HttpTransport getHttpTransport() {
		return i_httpTransport;
	}

	/**
	 * Sets the {@link HttpTransport} used to connect to Google.
	 * 
	 * @param httpTransport
	 *          the new {@link HttpTransport} used to connect to Google
	 */
	public void setHttpTransport(HttpTransport httpTransport) {
		i_httpTransport = httpTransport;
	}

	/**
	 * Gets the {@link JsonFactory} used by the Google client.
	 * 
	 * @return the {@link JsonFactory} used by the Google client
	 */
	public JsonFactory getJsonFactory() {
		return i_jsonFactory;
	}

	/**
	 * Sets the {@link JsonFactory} used by the Google client.
	 * 
	 * @param jsonFactory
	 *          the new {@link JsonFactory} used by the Google client
	 */
	public void setJsonFactory(JsonFactory jsonFactory) {
		i_jsonFactory = jsonFactory;
	}
}
