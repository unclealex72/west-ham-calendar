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

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.api.services.calendar.Calendar;


/**
 * 
 * Create a new Google {@link Calendar} object.
 * @author alex
 *
 */
public interface CalendarFactory {

	/**
	 * Create a new Google {@link Calendar} object.
	 * 
	 * @return A new, authenticated Google {@link Calendar} object.
	 * @throws IOException
	 *           Thrown if there is a network problem.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	Calendar createCalendar() throws IOException, GoogleAuthenticationFailedException;
	
	/**
	 * Get the authorisation URL supplied by Google.
	 * @return The Google Calendar API authorisation URL.
	 */
	String getAuthorisationUrl();
	
	/**
	 * Install an authorisation code so that access and refresh tokens can be requested from Google.
	 * @param authorisationCode The authorisation code supplied by Google.
	 * @throws IOException Thrown if there are any issues getting the access and refresh tokens.
	 */
	void installAuthorisationCode(String authorisationCode) throws IOException;
}
