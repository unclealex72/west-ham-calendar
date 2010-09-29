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
package uk.co.unclealex.hammers.calendar.service;

import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.dao.GameDao;
import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;

@Transactional
public class GameServiceImpl implements GameService {

	private GameDao i_gameDao;
	
	@Override
	public Game findOrCreateGame(Competition competition, Location location,
			String opponents, int season, Date datePlayed) {
		Game game = getGameDao().findByBusinessKey(competition, location, opponents, season);
		if (game == null) {
			game = new Game();
			game.setCompetition(competition);
			game.setLocation(location);
			game.setOpponents(opponents);
			game.setSeason(season);
		}
		game.setDatePlayed(datePlayed);
		return game;
	}

	@Override
	public SortedSet<Game> attendGames(int season, Collection<Integer> gameIds) {
		GameDao gameDao = getGameDao();
		SortedSet<Game> games = gameDao.getAllForSeason(season);
		for (Game game : games) {
			game.setAttended(gameIds.contains(game.getId()));
			gameDao.store(game);
		}
		return games;
	}

	@Override
	public void storeGame(Game game) {
		getGameDao().store(game);
	}

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

}
