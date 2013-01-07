/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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
import uk.co.unclealex.hammers.calendar.shared.model.GameView;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;


/**
 * The service for all GUI actions that need no authority to perform.
 * @author alex
 *
 */
public interface AnonymousAttendanceService {

	/**
	 * Get all known seasons.
	 * @return All known seasons.
	 */
	Integer[] getAllSeasons();
	
	/**
	 * Get a chronological list of all games for a season.
	 * @param season The season to interrogate.
	 * @return a chronological list of all {@link GameView}s for a season.
	 */
	GameView[] getAllGameViewsChronologicallyForSeason(int season);
	
	/**
	 * Get a list of all games for a season by opponent.
	 * @param season The season to interrogate.
	 * @return a list of all {@link GameView}s for a season by opponent.
	 */
	GameView[] getAllGameViewsByOpponentsForSeason(int season);
	
	/**
	 * Get the West Ham vs. opponents league for a season.
	 * @param season The league's season.
	 * @return A list of {@link LeagueRow}s that make up the league for the given season.
	 */
	LeagueRow[] getLeagueForSeason(int season);
	
	/**
	 * Authenticate a user.
	 * @param username The user's username.
	 * @param password The user's password.
	 * @return True if the user authenticated successfully, false otherwise.
	 */
	boolean authenticate(String username, String password);
	
	/**
	 * Make sure all required defaults exist.
	 * 
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	void ensureDefaultsExist() throws GoogleAuthenticationFailedException, IOException;
	
	/**
	 * Get the username of the current user.
	 * @return The username of the current user.
	 */
	String getUserPrincipal();
	
	/**
	 * Log out of the application.
	 */
	void logout();
}
