/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.servlet;

import java.io.IOException;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.Game;
import uk.co.unclealex.hammers.calendar.shared.services.UserAttendanceService;

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
public class UserAttendanceServlet extends AbstractAttendanceServlet implements UserAttendanceService {

	@Override
	public Game[] attendAllHomeGamesForSeason(int season) {
		return createAttendanceService().attendAllHomeGamesForSeason(season);
	}

	@Override
	public Game attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		return createAttendanceService().attendGame(gameId);
	}

	@Override
	public Game unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		return createAttendanceService().unattendGame(gameId);
	}

	@Override
	public void forceLogin() {
		createAttendanceService().forceLogin();
	}

	@Override
	public void changePassword(String newPassword) {
	  createAttendanceService().changePassword(newPassword);
	  
	}
}
