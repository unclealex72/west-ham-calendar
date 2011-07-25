/**
 * 
 */
package uk.co.unclealex.hammers.calendar.shared.remote;

import java.io.IOException;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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
@RemoteServiceRelativePath("admin")
public interface AdminAttendanceService extends RemoteService {

	public String createGoogleAuthenticationUrlIfRequired();
	
	public void authenticate(String successToken) throws GoogleAuthenticationFailedException, IOException;
	public void remove(CalendarType calendarType) throws GoogleAuthenticationFailedException, IOException, GoogleException;	
	public void createOrUpdate(CalendarConfiguration calendarConfiguration) throws GoogleAuthenticationFailedException, IOException, GoogleException;
	
	public CalendarColour[] getUsedCalendarColours() throws GoogleAuthenticationFailedException, IOException, GoogleException;
	public CalendarConfiguration[] getAllCalendarConfigurations();
	public CalendarConfiguration createNewCalendarConfiguration(CalendarType calendarType) throws GoogleAuthenticationFailedException, IOException, GoogleException;
	public CalendarConfiguration[] getCalendarConfigurations(boolean tickets);
	
	public void updateCalendars();
}
