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

package uk.co.unclealex.hammers.calendar.server.update;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.calendar.GoogleCalendarService;
import uk.co.unclealex.hammers.calendar.server.calendar.UpdateChangeLog;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.DatePlayedLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.GameKeyLocator;
import uk.co.unclealex.hammers.calendar.server.html.GameLocator.GameLocatorVisitor;
import uk.co.unclealex.hammers.calendar.server.html.GameUpdateCommand;
import uk.co.unclealex.hammers.calendar.server.html.HtmlGamesScanner;
import uk.co.unclealex.hammers.calendar.server.html.MainPageService;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;

/**
 * @author alex
 * 
 */
public class MainUpdateServiceImpl implements MainUpdateService {

	private static final Logger log = LoggerFactory.getLogger(MainUpdateServiceImpl.class);

	private GameDao i_gameDao;
	private MainPageService i_mainPageService;
	private HtmlGamesScanner i_ticketsHtmlGamesScanner;
	private HtmlGamesScanner i_fixturesHtmlGamesScanner;
	private GoogleCalendarService i_googleCalendarService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<UpdateChangeLog> updateAllCalendars() throws IOException, GoogleAuthenticationFailedException {
		MainPageService mainPageService = getMainPageService();
		processUpdates("fixture", mainPageService.getFixturesUri(), getFixturesHtmlGamesScanner());
		processUpdates("ticket", mainPageService.getTicketsUri(), getTicketsHtmlGamesScanner());
		return getGoogleCalendarService().updateCalendars(getGameDao().getAll());
	}

	protected void processUpdates(String updatesType, URI uri, HtmlGamesScanner scanner) throws IOException {
		log.info("Scanning for " + updatesType + " changes.");
		SortedSet<GameUpdateCommand> allGameUpdateCommands = scanner.scan(uri);
		Supplier<SortedSet<GameUpdateCommand>> factory = new Supplier<SortedSet<GameUpdateCommand>>() {
			@Override
			public SortedSet<GameUpdateCommand> get() {
				return Sets.newTreeSet();
			}
		};
		SortedSetMultimap<Game, GameUpdateCommand> updatesByGame = Multimaps.newSortedSetMultimap(
				new TreeMap<Game, Collection<GameUpdateCommand>>(), factory);
		DaoGameLocator gameLocator = new DaoGameLocator();
		for (GameUpdateCommand gameUpdateCommand : allGameUpdateCommands) {
			Game game = gameLocator.locate(gameUpdateCommand.getGameLocator());
			updatesByGame.put(game, gameUpdateCommand);
		}
		gameLocator.synchronise();
		List<Game> updatedGames = Lists.newArrayList();
		for (Entry<Game, Collection<GameUpdateCommand>> entry : updatesByGame.asMap().entrySet()) {
			Game game = entry.getKey();
			Collection<GameUpdateCommand> gameUpdateCommands = entry.getValue();
			boolean updated = false;
			for (GameUpdateCommand gameUpdateCommand : gameUpdateCommands) {
				updated |= gameUpdateCommand.update(game);
			}
			if (updated) {
				log.info("Updating game " + game);
				updatedGames.add(game);
			}
			else {
				log.debug("Ingoring game " + game);
			}
		}
		getGameDao().saveOrUpdate(updatedGames);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		Game game = getGameDao().findById(gameId);
		attendGame(game);
	}
	
	protected void attendGame(Game game) throws GoogleAuthenticationFailedException, IOException {
		getGoogleCalendarService().attendGame(game);
		game.setAttended(true);
		getGameDao().saveOrUpdate(game);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		Game game = getGameDao().findById(gameId);
		getGoogleCalendarService().unattendGame(game);
		game.setAttended(false);
		getGameDao().saveOrUpdate(game);
	}

	class DaoGameLocator {
		Map<GameKey, Game> gamesByGameKey;
		Map<DateTime, Game> gamesByDatePlayed;
		Iterable<Game> games;
		List<Game> newGames = Lists.newArrayList();
		
		public DaoGameLocator() {
			games = getGameDao().getAll();
		}

		public Game locate(GameLocator gameLocator) {
			DaoGameLocatorVisitor visitor = new DaoGameLocatorVisitor();
			gameLocator.accept(visitor);
			return visitor.game;
		}

		public void synchronise() {
			getGameDao().saveOrUpdate(newGames);
		}
		
		class DaoGameLocatorVisitor extends GameLocatorVisitor {
			Game game;

			@Override
			public void visit(DatePlayedLocator datePlayedLocator) {
				if (gamesByDatePlayed == null) {
					Function<Game, DateTime> datePlayedFunction = new Function<Game, DateTime>() {
						@Override
						public DateTime apply(Game game) {
							return game.getDateTimePlayed();
						}
					};
					Predicate<Game> datePlayedKnownPredicate = Predicates.compose(Predicates.notNull(), datePlayedFunction);
					gamesByDatePlayed = Maps.uniqueIndex(Iterables.filter(games, datePlayedKnownPredicate),
							datePlayedFunction);
				}
				game = gamesByDatePlayed.get(datePlayedLocator.getLocator());
			}

			@Override
			public void visit(GameKeyLocator gameKeyLocator) {
				if (gamesByGameKey == null) {
					Function<Game, GameKey> gameKeyFunction = new Function<Game, GameKey>() {
						@Override
						public GameKey apply(Game game) {
							return game.getGameKey();
						}
					};
					gamesByGameKey = Maps.newHashMap(Maps.uniqueIndex(games, gameKeyFunction));
				}
				GameKey gameKey = gameKeyLocator.getLocator();
				game = gamesByGameKey.get(gameKey);
				if (game == null) {
					game = new Game(null, gameKey.getCompetition(), gameKey.getLocation(), gameKey.getOpponents(),
							gameKey.getSeason(), null, null, null, null, null, null, null, null, null, null, false);
					log.info("Creating game " + game);
					newGames.add(game);
					gamesByGameKey.put(gameKey, game);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attendAllHomeGamesForSeason(int season) throws GoogleAuthenticationFailedException, IOException {
		for (Game game : getGameDao().getAllForSeasonAndLocation(season, Location.HOME)) {
			attendGame(game);
		}
	}

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

	public MainPageService getMainPageService() {
		return i_mainPageService;
	}

	public void setMainPageService(MainPageService mainPageService) {
		i_mainPageService = mainPageService;
	}

	public HtmlGamesScanner getTicketsHtmlGamesScanner() {
		return i_ticketsHtmlGamesScanner;
	}

	public void setTicketsHtmlGamesScanner(HtmlGamesScanner ticketsHtmlGameScanner) {
		i_ticketsHtmlGamesScanner = ticketsHtmlGameScanner;
	}

	public HtmlGamesScanner getFixturesHtmlGamesScanner() {
		return i_fixturesHtmlGamesScanner;
	}

	public void setFixturesHtmlGamesScanner(HtmlGamesScanner fixturesHtmlGameScanner) {
		i_fixturesHtmlGamesScanner = fixturesHtmlGameScanner;
	}

	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
	}

}
