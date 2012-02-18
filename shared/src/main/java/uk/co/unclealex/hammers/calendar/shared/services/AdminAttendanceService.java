/**
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
public interface AdminAttendanceService {

	public String createGoogleAuthenticationUrlIfRequired();
	
	public void authenticate(String successToken) throws GoogleAuthenticationFailedException, IOException;
	
	public void updateCalendars();
	
	public User[] getAllUsers();
	public void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException;	  
	public void removeUser(String username) throws NoSuchUsernameException;
	public void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException;
	public void setSelectedTicketingCalendar(CalendarType calendarType);
	public CalendarType getSelectedTicketingCalendar();
}
