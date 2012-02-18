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
package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.IOException;
import java.util.SortedSet;

import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

/**
 * The main interfaces for other classes to talk to google calendars.
 * 
 * @author alex
 * 
 */
public interface GoogleCalendarService {

	/**
	 * Move a game to the attendend calendar.
	 * 
	 * @param game
	 *          The game to move.
	 * @throws GoogleAuthenticationFailedException
	 * @throws IOException
	 */
	public void attendGame(Game game) throws GoogleAuthenticationFailedException, IOException;

	/**
	 * Move a game to the unattendend calendar.
	 * 
	 * @param game
	 *          The game to move.
	 * @throws GoogleAuthenticationFailedException
	 * @throws IOException
	 */
	public void unattendGame(Game game) throws GoogleAuthenticationFailedException, IOException;

	/**
	 * Update all google calendars so that they are in sync with games in the
	 * database.
	 * 
	 * @param games
	 *          All (possibly) updated games.
	 * @return A list of all updates.
	 * @throws GoogleAuthenticationFailedException
	 * @throws IOException
	 */
	public SortedSet<UpdateChangeLog> updateCalendars(Iterable<Game> games) throws IOException,
			GoogleAuthenticationFailedException;

}
