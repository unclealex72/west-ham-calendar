/**
 * Copyright 2010-2012 Alex Jones
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
 * The Class MainUpdateServiceImpl.
 * 
 * @author alex
 */
public class MainUpdateServiceImpl implements MainUpdateService {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(MainUpdateServiceImpl.class);

	/**
	 * The {@link GameDao} for getting persisted {@link Game} information.
	 */
	private GameDao i_gameDao;
	
	/**
	 * The {@link MainPageService} for finding the links off the main page.
	 */
	private MainPageService i_mainPageService;
	
	/**
	 * The {@link HtmlGamesScanner} for getting ticketing information.
	 */
	private HtmlGamesScanner i_ticketsHtmlGamesScanner;
	
	/**
	 * The {@link HtmlGamesScanner} for getting fixture information.
	 */
	private HtmlGamesScanner i_fixturesHtmlGamesScanner;
	
	/**
	 * The {@link GoogleCalendarService} used for updating the Google calendars.
	 */
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

	/**
	 * Process updates.
	 * 
	 * @param updatesType
	 *          the updates type
	 * @param uri
	 *          the uri
	 * @param scanner
	 *          the scanner
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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
			// Make sure that games that are not listed but ticket availability has been mentioned aren't added.
			if (game != null) {
  			updatesByGame.put(game, gameUpdateCommand);
			}
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
	
	/**
	 * Attend a game.
	 * 
	 * @param game
	 *          The game to attend.
	 * @throws GoogleAuthenticationFailedException
	 *           Thrown if authentication with the Google servers fails.
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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

	/**
	 * A class that uses the {@link GameLocator} to finds games and also caches results.
	 * @author alex
	 *
	 */
	class DaoGameLocator {
		
		/**
		 * The cache of {@link Games} found by their {@link GameKey}.
		 */
		Map<GameKey, Game> gamesByGameKey;
		
		
		/**
		 * The cache of {@link Games} found by their date played.
		 */
		Map<DateTime, Game> gamesByDatePlayed;

		/**
		 * All {@link Game}s.
		 */
		Iterable<Game> games;
		
		/**
		 * A list of games that could not be found but need to be created.
		 */
		List<Game> newGames = Lists.newArrayList();
		
		/**
		 * Instantiates a new dao game locator.
		 */
		public DaoGameLocator() {
			games = getGameDao().getAll();
		}

		/**
		 * Find a game using a {@link GameLocator}.
		 * @param gameLocator The {@link GameLocator} used to find the game.
		 * @return A game that matches the {@link GameLocator} or a new game if none exist.
		 */
		public Game locate(GameLocator gameLocator) {
			DaoGameLocatorVisitor visitor = new DaoGameLocatorVisitor();
			gameLocator.accept(visitor);
			return visitor.game;
		}

		/**
		 * Persist all new games.
		 */
		public void synchronise() {
			getGameDao().saveOrUpdate(newGames);
		}
		
		/**
		 * A {@link GameLocatorVisitor} used to find games from i_cache.
		 * @author alex
		 *
		 */
		class DaoGameLocatorVisitor extends GameLocatorVisitor {
			
			/**
			 * The found or new game.
			 */
			Game game;

			/**
			 * {@inheritDoc}
			 */
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

			/**
			 * {@inheritDoc}
			 */
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

	/**
	 * Gets the {@link GameDao} for getting persisted {@link Game} information.
	 * 
	 * @return the {@link GameDao} for getting persisted {@link Game} information
	 */
	public GameDao getGameDao() {
		return i_gameDao;
	}

	/**
	 * Sets the {@link GameDao} for getting persisted {@link Game} information.
	 * 
	 * @param gameDao
	 *          the new {@link GameDao} for getting persisted {@link Game}
	 *          information
	 */
	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

	/**
	 * Gets the {@link MainPageService} for finding the links off the main page.
	 * 
	 * @return the {@link MainPageService} for finding the links off the main page
	 */
	public MainPageService getMainPageService() {
		return i_mainPageService;
	}

	/**
	 * Sets the {@link MainPageService} for finding the links off the main page.
	 * 
	 * @param mainPageService
	 *          the new {@link MainPageService} for finding the links off the main
	 *          page
	 */
	public void setMainPageService(MainPageService mainPageService) {
		i_mainPageService = mainPageService;
	}

	/**
	 * Gets the {@link HtmlGamesScanner} for getting ticketing information.
	 * 
	 * @return the {@link HtmlGamesScanner} for getting ticketing information
	 */
	public HtmlGamesScanner getTicketsHtmlGamesScanner() {
		return i_ticketsHtmlGamesScanner;
	}

	/**
	 * Sets the {@link HtmlGamesScanner} for getting ticketing information.
	 * 
	 * @param ticketsHtmlGameScanner
	 *          the new {@link HtmlGamesScanner} for getting ticketing information
	 */
	public void setTicketsHtmlGamesScanner(HtmlGamesScanner ticketsHtmlGameScanner) {
		i_ticketsHtmlGamesScanner = ticketsHtmlGameScanner;
	}

	/**
	 * Gets the {@link HtmlGamesScanner} for getting fixture information.
	 * 
	 * @return the {@link HtmlGamesScanner} for getting fixture information
	 */
	public HtmlGamesScanner getFixturesHtmlGamesScanner() {
		return i_fixturesHtmlGamesScanner;
	}

	/**
	 * Sets the {@link HtmlGamesScanner} for getting fixture information.
	 * 
	 * @param fixturesHtmlGameScanner
	 *          the new {@link HtmlGamesScanner} for getting fixture information
	 */
	public void setFixturesHtmlGamesScanner(HtmlGamesScanner fixturesHtmlGameScanner) {
		i_fixturesHtmlGamesScanner = fixturesHtmlGameScanner;
	}

	/**
	 * Gets the {@link GoogleCalendarService} used for updating the Google
	 * calendars.
	 * 
	 * @return the {@link GoogleCalendarService} used for updating the Google
	 *         calendars
	 */
	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	/**
	 * Sets the {@link GoogleCalendarService} used for updating the Google
	 * calendars.
	 * 
	 * @param googleCalendarService
	 *          the new {@link GoogleCalendarService} used for updating the Google
	 *          calendars
	 */
	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
	}

}
