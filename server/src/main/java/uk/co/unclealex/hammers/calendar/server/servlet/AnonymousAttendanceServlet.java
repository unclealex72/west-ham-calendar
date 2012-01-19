/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.servlet;

import java.io.IOException;

import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleException;
import uk.co.unclealex.hammers.calendar.shared.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceService;

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
public class AnonymousAttendanceServlet extends AbstractAttendanceServlet implements AnonymousAttendanceService {

	@Override
	public Integer initialise() {
		return createAttendanceService().initialise();
	}
	
	@Override
	public Integer[] getAllSeasons() {
		return createAttendanceService().getAllSeasons();
	}

	@Override
	public Game[] getAllGamesChronologicallyForSeason(int season) {
		return createAttendanceService().getAllGamesChronologicallyForSeason(season);
	}

	@Override
	public Game[] getAllGamesByOpponentsForSeason(int season) {
		return createAttendanceService().getAllGamesByOpponentsForSeason(season);
	}

	@Override
	public LeagueRow[] getLeagueForSeason(int season) {
		return createAttendanceService().getLeagueForSeason(season);
	}

	@Override
	public Integer getLatestSeason() {
		return createAttendanceService().getLatestSeason();
	}

	@Override
	public boolean authenticate(String username, String password) {
		return createAttendanceService().authenticate(username, password);
	}

	@Override
	public void ensureDefaultsExist() throws GoogleAuthenticationFailedException, GoogleException, IOException {
		createAttendanceService().ensureDefaultsExist();
	}

	@Override
	public String getUserPrincipal() {
		return createAttendanceService().getUserPrincipal();
	}

	@Override
	public void logout() {
		createAttendanceService().logout();
	}

}
