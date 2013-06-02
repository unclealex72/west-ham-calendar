/**
 * Copyright 2010-2012 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.calendar.formatter;

import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.model.Game;

import com.google.common.base.Function;
import com.google.common.base.Strings;


/**
 * A class that prints a game if it is known or its id otherwise.
 * @author alex
 *
 */
public class GameFormatter implements Function<String, String> {

	/**
	 * The {@link GameDao} to use to look up persisted games.
	 */
	private GameDao gameDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String apply(String gameId) {
		gameId = Strings.nullToEmpty(gameId).trim();
		if (!gameId.matches("[0-9]+")) {
			return unknown(gameId);
		}
		int id = Integer.valueOf(gameId);
		Game game = getGameDao().findById(id);
		return game == null ? unknown(gameId) : game.toString();
	}
	
	/**
	 * Format an unknown game.
	 * @param gameId The id of the unknown game.
	 * @return A string containing the id, noting also the game is "unknown".
	 */
	protected String unknown(String gameId) {
		return String.format("[%s:unknown]", gameId);
	}
	
	/**
	 * Gets the {@link GameDao} to use to look up persisted games.
	 * 
	 * @return the {@link GameDao} to use to look up persisted games
	 */
	public GameDao getGameDao() {
		return gameDao;
	}

	/**
	 * Sets the {@link GameDao} to use to look up persisted games.
	 * 
	 * @param gameDao
	 *          the new {@link GameDao} to use to look up persisted games
	 */
	public void setGameDao(GameDao gameDao) {
		this.gameDao = gameDao;
	}
	
	
}
