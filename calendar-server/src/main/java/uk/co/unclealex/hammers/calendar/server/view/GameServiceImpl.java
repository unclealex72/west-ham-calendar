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

package uk.co.unclealex.hammers.calendar.server.view;

import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.dates.DateService;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.server.tickets.TicketingCalendarService;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.GameView;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * The default implementation of {@link GameService}.
 * @author alex
 * 
 */
public class GameServiceImpl implements GameService {

	/**
	 * The {@link GameDao} used to persist {@link Game}s.
	 */
	private GameDao gameDao;
	
	/**
	 * The {@link TicketingCalendarService} used to interface with the selected ticketing calendar.
	 */
	private TicketingCalendarService ticketingCalendarService;
	
	/**
	 * The {@link DateService} use to manipulate dates and times.
	 */
	private DateService dateService;
	
	/**
	 * The {@link GoogleCalendarFactory} used to get information about {@link GoogleCalendar}s.
	 */
	private GoogleCalendarFactory googleCalendarFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<GameView> getGameViewsForSeasonByOpponents(boolean enabled, int season) {
		Comparator<GameView> comparator = new Comparator<GameView>() {
			@Override
			public int compare(GameView g1, GameView g2) {
				int cmp = g1.getOpponents().compareTo(g2.getOpponents());
				if (cmp == 0) {
					cmp = g1.getLocation().compareTo(g2.getLocation());
					if (cmp == 0) {
						cmp = g1.getDatePlayed().compareTo(g2.getDatePlayed());
					}
				}
				return cmp;
			}
		};
		return getGameViewsForSeason(enabled, season, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<GameView> getGameViewsForSeasonByDatePlayed(boolean enabled, int season) {
		Comparator<GameView> comparator = new Comparator<GameView>() {
			@Override
			public int compare(GameView g1, GameView g2) {
				return g1.getDatePlayed().compareTo(g2.getDatePlayed());
			}
		};
		return getGameViewsForSeason(enabled, season, comparator);
	}

	/**
	 * Get all the {@link GameView}s for a given season.
	 * @param enabled True if these {@link GameView}s can be altered, false otherwise.
	 * @param season The season for the {@link GameView}s.
	 * @param comparator A {@link Comparator} to order the {@link GameView}s.
	 * @return A sorted set of all the {@link GameView}s for a season.
	 */
	protected SortedSet<GameView> getGameViewsForSeason(boolean enabled, int season, Comparator<GameView> comparator) {
		SortedSet<GameView> gameViewsForSeason = Sets.newTreeSet(comparator);
		Iterables.addAll(gameViewsForSeason,
				Iterables.transform(getGameDao().getAllForSeason(season), createGameViewFunction(enabled)));
		return gameViewsForSeason;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView getGameViewById(int gameId, boolean enabled) {
		Game game = getGameDao().findById(gameId);
		return createGameViewFunction(enabled).apply(game);
	}

	/**
	 * Create a {@link Function} that transforms a {@link Game} into a {@link GameView}.
	 * @param enabled True if the {@link GameView} can be edited, false otherwise.
	 * @return A {@link Function} that transforms a {@link Game} into a {@link GameView}.
	 */
	protected Function<Game, GameView> createGameViewFunction(final boolean enabled) {
		CalendarType selectedTicketingCalendarType = getTicketingCalendarService().getSelectedTicketingCalendar();
		final GoogleCalendar googleCalendar = selectedTicketingCalendarType == null ? null : getGoogleCalendarFactory()
				.getGoogleCalendar(selectedTicketingCalendarType);
		return new Function<uk.co.unclealex.hammers.calendar.server.model.Game, GameView>() {
			@Override
			public GameView apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
				Date ticketDate;
			  if (googleCalendar == null) {
			    ticketDate = null;
			  }
			  else {
			    Interval interval = googleCalendar.toCalendarDateInterval().apply(game);
			    ticketDate = interval == null ? null : interval.getStart().toDate();
			  }
				DateTime datePlayed = game.getDateTimePlayed();
				boolean weekGame = getDateService().isWeekday(datePlayed);
				boolean nonStandardWeekendGame = !weekGame && !getDateService().isThreeOClockOnASaturday(datePlayed);
				return new GameView(game.getId(), game.getCompetition(), game.getLocation(), game.getOpponents(),
						game.getSeason(), datePlayed.toDate(), game.getResult(), game.getAttendence(), game.getMatchReport(),
						game.getTelevisionChannel(), ticketDate, game.isAttended(), weekGame, nonStandardWeekendGame, enabled);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<Integer> getAllSeasons() {
		return getGameDao().getAllSeasons();
	}

	/**
	 * Gets the {@link GameDao} used to persist {@link Game}s.
	 * 
	 * @return the {@link GameDao} used to persist {@link Game}s
	 */
	public GameDao getGameDao() {
		return gameDao;
	}

	/**
	 * Sets the {@link GameDao} used to persist {@link Game}s.
	 * 
	 * @param gameDao
	 *          the new {@link GameDao} used to persist {@link Game}s
	 */
	public void setGameDao(GameDao gameDao) {
		this.gameDao = gameDao;
	}

	/**
	 * Gets the {@link TicketingCalendarService} used to interface with the
	 * selected ticketing calendar.
	 * 
	 * @return the {@link TicketingCalendarService} used to interface with the
	 *         selected ticketing calendar
	 */
	public TicketingCalendarService getTicketingCalendarService() {
		return ticketingCalendarService;
	}

	/**
	 * Sets the {@link TicketingCalendarService} used to interface with the
	 * selected ticketing calendar.
	 * 
	 * @param ticketingCalendarService
	 *          the new {@link TicketingCalendarService} used to interface with
	 *          the selected ticketing calendar
	 */
	public void setTicketingCalendarService(TicketingCalendarService ticketingCalendarService) {
		this.ticketingCalendarService = ticketingCalendarService;
	}

	/**
	 * Gets the {@link DateService} use to manipulate dates and times.
	 * 
	 * @return the {@link DateService} use to manipulate dates and times
	 */
	public DateService getDateService() {
		return dateService;
	}

	/**
	 * Sets the {@link DateService} use to manipulate dates and times.
	 * 
	 * @param dateService
	 *          the new {@link DateService} use to manipulate dates and times
	 */
	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	/**
	 * Gets the {@link GoogleCalendarFactory} used to get information about
	 * {@link GoogleCalendar}s.
	 * 
	 * @return the {@link GoogleCalendarFactory} used to get information about
	 *         {@link GoogleCalendar}s
	 */
	public GoogleCalendarFactory getGoogleCalendarFactory() {
		return googleCalendarFactory;
	}

	/**
	 * Sets the {@link GoogleCalendarFactory} used to get information about
	 * {@link GoogleCalendar}s.
	 * 
	 * @param googleCalendarFactory
	 *          the new {@link GoogleCalendarFactory} used to get information
	 *          about {@link GoogleCalendar}s
	 */
	public void setGoogleCalendarFactory(GoogleCalendarFactory googleCalendarFactory) {
		this.googleCalendarFactory = googleCalendarFactory;
	}

}
