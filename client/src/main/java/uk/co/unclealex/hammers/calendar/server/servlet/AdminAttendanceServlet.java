/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.servlet;

import java.io.IOException;

import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceService;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.Role;
import uk.co.unclealex.hammers.calendar.shared.model.User;

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
public class AdminAttendanceServlet extends AbstractAttendanceServlet implements AdminAttendanceService {

	@Override
	public void authorise(String authorisationToken) throws GoogleAuthenticationFailedException, IOException {
		createAttendanceService().authorise(authorisationToken);
	}

	@Override
	public String createGoogleAuthorisationUrl() {
		return createAttendanceService().createGoogleAuthorisationUrl();
	}
	
	@Override
	public void updateCalendars() {
		createAttendanceService().updateCalendars();		
	}
	
	@Override
	public void createCalendars() throws IOException, GoogleAuthenticationFailedException {
		createAttendanceService().createCalendars();
	}
	
	@Override
	public void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException {
	  createAttendanceService().addUser(username, password, role);
	}
	
	@Override
	public void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException {
	  createAttendanceService().alterUser(username, newPassword, newRole);
	}
	
	@Override
	public void removeUser(String username) throws NoSuchUsernameException {
	  createAttendanceService().removeUser(username);
	}
	
	@Override
	public User[] getAllUsers() {
	  return createAttendanceService().getAllUsers();
	}
	
	@Override
	public CalendarType getSelectedTicketingCalendar() {
	  return createAttendanceService().getSelectedTicketingCalendar();
	}
	
	@Override
	public void setSelectedTicketingCalendar(CalendarType calendarType) {
	  createAttendanceService().setSelectedTicketingCalendar(calendarType);
	}
}
