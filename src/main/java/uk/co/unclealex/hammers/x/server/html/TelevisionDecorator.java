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
package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.dao.GameDao;
import uk.co.unclealex.hammers.calendar.model.Game;

@Transactional
public abstract class TelevisionDecorator implements Decorator {

	private GameDao i_gameDao;
	private static final Logger log = Logger.getLogger(TelevisionDecorator.class);
	
	@Override
	public void decorate() throws IOException {
		URL fixtureUrl = findFixturesUrl();
		Map<Date, String> channelsByGameTime = findChannelsByGameTime(fixtureUrl);
		GameDao gameDao = getGameDao();
		for (Entry<Date, String> entry : channelsByGameTime.entrySet()) {
			Date gameTime = entry.getKey();
			String channel = entry.getValue();
			Game game = gameDao.findByDatePlayed(gameTime);
			if (game == null) {
				log.warn(String.format("Could not find a game for date %s and channel %s", gameTime, channel));
			}
			else {
				game.setTelevisionChannel(channel);
				gameDao.store(game);
			}
		}
	}

	/**
	 * @return
	 */
	protected abstract URL findFixturesUrl() throws IOException;

	protected abstract Map<Date, String> findChannelsByGameTime(URL fixtureUrl) throws IOException;

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}
}
