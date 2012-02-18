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

package uk.co.unclealex.hammers.calendar.server.dao;

import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.collect.Maps;

/**
 * @author alex
 * 
 */
public class HibernateGameDaoTest extends DaoTest {

	
	public HibernateGameDaoTest() {
		super(Game.class);
	}

	@Autowired private GameDao gameDao;
	Map<Integer, Game> gamesById = Maps.newHashMap();
	
	public void doSetup() throws JAXBException {
		JAXBContext ctxt = JAXBContext.newInstance(Game.class);
		Unmarshaller unmarshaller = ctxt.createUnmarshaller();
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate, "game");
		for (int id : new int[] { 1, 2, 3 }) {
			URL gameUrl = getClass().getClassLoader().getResource("dao/game" + id + ".xml");
			Game game = (Game) unmarshaller.unmarshal(gameUrl);
			gameDao.saveOrUpdate(game);
			gamesById.put(id, game);
		}
	}
	
	/**
	 * public Game findByDatePlayed(DateTime datePlayed);
	 * public Game findByDayPlayed(DateTime datePlayed);
	 * public Iterable<Game> getAllForSeason(int season); 
	 * public SortedSet<Integer> getAllSeasons();
	 * public void attendAllHomeGamesForSeason(int season);
	 * public Integer getLatestSeason();
	 */
	@Test
	public void testFindByBusinessKey() {
		checkGame1Found(gameDao.findByBusinessKey(Competition.FACP, Location.HOME, "Them", 2011));
	}

	@Test
	public void testFindByDatePlayed() {
		checkGame1Found(gameDao.findByDatePlayed(new DateTime(2011, 11, 5, 3, 0, DateTimeZone.forID("Europe/London"))));
	}

	protected void checkGame1Found(Game actualGame) {
		Game expectedGame = gamesById.get(1);
		Assert.assertNotNull("No game could be found.", actualGame);
		Assert.assertEquals("The found game was wrong.", expectedGame, actualGame);
	}
}
