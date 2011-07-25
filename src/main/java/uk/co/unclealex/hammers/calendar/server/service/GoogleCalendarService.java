/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.service;

import java.io.IOException;
import java.util.Map;

import uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;

import com.google.gdata.util.ServiceException;


public interface GoogleCalendarService {

	public void installSuccessCode(String successCode) throws IOException, GoogleAuthenticationFailedException;
	
	public String createGoogleAuthenticationUrlIfRequired();
	
	public void attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, ServiceException;
	
	public void unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, ServiceException;
	
	public void updateCalendars() throws GoogleAuthenticationFailedException, IOException, ServiceException;

	public void remove(String googleCalendarId) throws GoogleAuthenticationFailedException, IOException, ServiceException;

	public Iterable<CalendarColour> getUsedCalendarColours() throws GoogleAuthenticationFailedException, IOException, ServiceException;

	public String getClientId();

	public String createOrUpdate(CalendarConfiguration calendarConfiguration) throws IOException, ServiceException, GoogleAuthenticationFailedException;

	public void cleanCalendars(Map<CalendarType, CalendarConfiguration> calendarConfigurationsByCalendarType) throws IOException, ServiceException, GoogleAuthenticationFailedException;
}
