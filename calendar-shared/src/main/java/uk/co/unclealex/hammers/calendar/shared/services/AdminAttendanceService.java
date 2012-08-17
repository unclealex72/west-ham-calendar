/**
 * Copyright 2012 Alex Jones
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
package uk.co.unclealex.hammers.calendar.shared.services;

import java.io.IOException;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.Role;
import uk.co.unclealex.hammers.calendar.shared.model.User;


/**
 * The service for all GUI actions that need an admin authority to perform.
 * @author alex
 *
 */
public interface AdminAttendanceService {

	/**
	 * Create the Google authorisation URL.
	 * @return The Google authorisation URL required to get an authorisation code.
	 */
	String createGoogleAuthorisationUrl();
	
	/**
	 * Create the required set of Google calendars.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 */
	void createCalendars() throws IOException, GoogleAuthenticationFailedException;
	
	/**
	 * Process the Google authorisation token.
	 * 
	 * @param authorisationToken
	 *          The Google authorisation token.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	void authorise(String authorisationToken) throws GoogleAuthenticationFailedException, IOException;
	
	/**
	 * Synchronise all the Google calendars with the persisted state.
	 */
	void updateCalendars();
	
	/**
	 * Get all users.
	 * @return All users.
	 */
	User[] getAllUsers();
	
	/**
	 * Add a new user.
	 * @param username The user's username.
	 * @param password The user's password.
	 * @param role The user's most senior role.
	 * @throws UsernameAlreadyExistsException Thrown if a user for the given username exists.
	 */
	void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException;
	
	/**
	 * Remove a user.
	 * @param username The username of the user to remove.
	 * @throws NoSuchUsernameException Thrown if no such user exits.
	 */
	void removeUser(String username) throws NoSuchUsernameException;
	
	/**
	 * Alter a user.
	 * @param username The username of the user to alter.
	 * @param newPassword The user's new password. 
	 * @param newRole The user's new most senior role.
	 * @throws NoSuchUsernameException Thrown if no such user exits.
	 */
	void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException;
	
	/**
	 * Select the calendar to use for ticketing.
	 * @param calendarType The {@link CalendarType} to use for ticketing.
	 */
	void setSelectedTicketingCalendar(CalendarType calendarType);
	
	/**
	 * Get the {@link CalendarType} of the selected ticketing calendar.
	 * @return The {@link CalendarType} of the selected ticketing calendar.
	 */
	CalendarType getSelectedTicketingCalendar();
}
