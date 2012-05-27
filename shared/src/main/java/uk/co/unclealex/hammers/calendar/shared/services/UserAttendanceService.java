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
import uk.co.unclealex.hammers.calendar.shared.model.GameView;


/**
 * The service for all GUI actions that need a user authority to perform.
 * @author alex
 *
 */
public interface UserAttendanceService {
	
	/**
	 * Attend all home games for a season.
	 * 
	 * @param season
	 *          The season for the games.
	 * @return The new list of {@link GameView}s for the given season.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	GameView[] attendAllHomeGameViewsForSeason(int season) throws GoogleAuthenticationFailedException, IOException;
	
	/**
	 * Attend a game.
	 * 
	 * @param gameId
	 *          The id of the game.
	 * @return The updated {@link GameView} for the game.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	GameView attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException;

	/**
	 * Unattend a game.
	 * 
	 * @param gameId
	 *          The id of the game.
	 * @return The updated {@link GameView} for the game.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	GameView unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException;
	
	/**
	 * Ensure that a user is logged in.
	 */
	void forceLogin();
	
	/**
	 * Change a user's password.
	 * 
	 * @param newPassword
	 *          The user's new password.
	 */
	void changePassword(String newPassword);
}
