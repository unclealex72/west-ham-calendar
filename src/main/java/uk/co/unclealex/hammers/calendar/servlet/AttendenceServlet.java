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
package uk.co.unclealex.hammers.calendar.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import uk.co.unclealex.hammers.calendar.dao.GameDao;
import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.service.GameService;

public class AttendenceServlet extends AutowiringFrameworkServlet {

	private GameDao i_gameDao;
	private GameService i_gameService;
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GameDao gameDao = getGameDao();
		SortedSet<Integer> seasons = gameDao.getAllSeasons();
		
		// Find which season we're looking for.
		request.setAttribute("seasons", seasons);
		int season = seasons.last();
		String requestedSeason = request.getParameter("season");
		if (requestedSeason != null) {
			season = Integer.valueOf(requestedSeason);
		}
		request.setAttribute("season", season);
		
		// Get all the games for the season
		String[] attendedGameIds = request.getParameterValues("attended");
		SortedSet<Game> games;
		if (attendedGameIds != null) {
			Collection<Integer> gameIds = 
				CollectionUtils.collect(
					Arrays.asList(attendedGameIds), 
					new Transformer<String, Integer>() {
						@Override
						public Integer transform(String string) {
							return Integer.valueOf(string);
						}
					}
				);
			games = getGameService().attendGames(season, gameIds);
		}
		else {
			games = gameDao.getAllForSeason(season);
		}
		SortedMap<Date, List<Game>> gamesByMonth = new TreeMap<Date, List<Game>>();
		for (Game game : games) {
			Date month = DateUtils.truncate(game.getDatePlayed(), Calendar.MONTH);
			List<Game> gamesForMonth = gamesByMonth.get(month);
			if (gamesForMonth == null) {
				gamesForMonth = new LinkedList<Game>();
				gamesByMonth.put(month, gamesForMonth);
			}
			gamesForMonth.add(game);
		}
		request.setAttribute("league", createLeague(games));
		request.setAttribute("gamesByMonth", gamesByMonth);
		request.setAttribute("months", gamesByMonth.keySet());
		request.getRequestDispatcher("/WEB-INF/pages/attendence.jsp").include(request, response);
	}

	protected SortedSet<LeagueRow> createLeague(SortedSet<Game> games) {
		Predicate<Game> isLeagueGamePredicate = new Predicate<Game>() {
			@Override
			public boolean evaluate(Game game) {
				Competition competition = game.getCompetition();
				return competition == Competition.FLC || competition == Competition.PREM;
			}
		};
		Set<LeagueRow> league = new HashSet<LeagueRow>();
		for (final Game game : CollectionUtils.select(games, isLeagueGamePredicate)) {
			final String opponents = game.getOpponents();
			Predicate<LeagueRow> predicate = new Predicate<LeagueRow>() {
				@Override
				public boolean evaluate(LeagueRow leagueRow) {
					return leagueRow.getTeam().equals(opponents);
				}
			};
			String result = game.getResult();
			if (result != null) {
				String[] scores = StringUtils.split(result.substring(2), '-');
				int goalsFor = Integer.valueOf(scores[0]);
				int goalsAgainst = Integer.valueOf(scores[1]);
				LeagueRow leagueRow = CollectionUtils.find(league, predicate);
				if (leagueRow == null) {
					leagueRow = new LeagueRow(opponents, goalsFor, goalsAgainst);
					league.add(leagueRow);
				}
				else {
					leagueRow.addGame(goalsFor, goalsAgainst);
				}
			}
		}
		return new TreeSet<LeagueRow>(league);
	}

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

	public GameService getGameService() {
		return i_gameService;
	}

	public void setGameService(GameService gameService) {
		i_gameService = gameService;
	}
}
