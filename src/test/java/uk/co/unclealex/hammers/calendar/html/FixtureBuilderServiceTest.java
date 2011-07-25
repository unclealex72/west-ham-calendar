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
import java.text.SimpleDateFormat;
import java.util.List;

import uk.co.unclealex.hammers.calendar.SpringTest;
import uk.co.unclealex.hammers.calendar.server.html.OnlineFixtureBuilderService;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

public class FixtureBuilderServiceTest extends SpringTest {

	public void testBuild() throws IOException {
		OnlineFixtureBuilderService fixtureBuilderService = autowire(new OnlineFixtureBuilderService());
		List<Game> games = fixtureBuilderService.build(2007, getTestResourceUrl(), null);
		assertEquals("The wrong number of games were found.", 2, games.size());
		checkGame(
				games, 1, Competition.PREM, Location.HOME, "Manchester City", 
				"11/08/2007 15:00", "L 0-2", 34921, "file:/page/MatchReport/0,,12562~37428,00.html", 2007);
		checkGame(
				games, 2, Competition.PREM, Location.HOME, "Aston Villa", 
				"11/05/2008 15:00", null, null, null, 2007);
	}

	protected void checkGame(
		List<Game> games, int idx, Competition competition, Location location, String opponents,
		String datePlayed, String result, Integer attendence, String matchReport, int season) {
		
		Game game = games.get(idx - 1);
		assertEqualsOrBothNull("Game " + idx + " has the wrong competition", competition, game.getCompetition());
		assertEqualsOrBothNull("Game " + idx + " has the wrong destination", location, game.getLocation());
		assertEqualsOrBothNull("Game " + idx + " has the wrong opponents", opponents, game.getOpponents());
		assertEqualsOrBothNull(
			"Game " + idx + " has the wrong date played", 
			datePlayed, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(game.getDatePlayed()));
		assertEqualsOrBothNull("Game " + idx + " has the wrong result", result, game.getResult());
		assertEqualsOrBothNull("Game " + idx + " has the wrong attendence", attendence, game.getAttendence());
		assertEqualsOrBothNull(
				"Game " + idx + " has the wrong match report", matchReport, toString(game.getMatchReport()));
		assertEqualsOrBothNull("Game " + idx + " has the wrong season", season, game.getSeason());
	}
	
	protected String toString(Object obj) {
		return obj == null?null:obj.toString();
	}

	protected void assertEqualsOrBothNull(String message, Object expected, Object actual) {
		if (expected == null) {
			assertNull(message, actual);
		}
		else {
			assertNotNull(message, actual);
			assertEquals(message, expected, actual);
		}
	}
}
