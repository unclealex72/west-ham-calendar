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

package uk.co.unclealex.hammers.calendar.server.calendar;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;

/**
 * An abstract implementation of an {@link UpdateChangeLog} where the game that has changed is identified
 * by its id.
 * @author alex
 *
 */
public abstract class AbstractIdUpdateChangeLog extends AbstractUpdateChangeLog {

	/**
	 * The id of the game that was updated.
	 */
	private final String i_gameId;

	/**
	 * Create an change log based on a game's id.
	 * @param googleCalendar The calendar that was changed.
	 * @param gameId The id of the game that was changed.
	 */
	public AbstractIdUpdateChangeLog(GoogleCalendar googleCalendar, String gameId) {
		super(googleCalendar);
		i_gameId = gameId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String gameToString() {
		return getGameId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int gameToHashCode() {
		return getGameId().hashCode();
	}
	
	public String getGameId() {
		return i_gameId;
	}
	
	
}
