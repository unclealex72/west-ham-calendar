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

import java.io.IOException;
import java.util.SortedSet;

import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.auth.AuthenticationService;
import uk.co.unclealex.hammers.calendar.server.auth.UserService;
import uk.co.unclealex.hammers.calendar.server.calendar.CalendarFactory;
import uk.co.unclealex.hammers.calendar.server.dao.UserDao;
import uk.co.unclealex.hammers.calendar.server.defaults.DefaultsService;
import uk.co.unclealex.hammers.calendar.server.model.Authority;
import uk.co.unclealex.hammers.calendar.server.tickets.TicketingCalendarService;
import uk.co.unclealex.hammers.calendar.server.update.MainUpdateService;
import uk.co.unclealex.hammers.calendar.server.update.UpdateCalendarJob;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.GameView;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;
import uk.co.unclealex.hammers.calendar.shared.model.Role;
import uk.co.unclealex.hammers.calendar.shared.model.User;
import uk.co.unclealex.hammers.calendar.shared.services.AttendanceService;
import uk.co.unclealex.hammers.calendar.shared.services.SecurityInvalidator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * The default implementation of {@link AttendanceService}.
 * 
 * @author alex
 * 
 */
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

	/**
	 * The {@link GameService} used to create sets of {@link GameView} instances
	 * to be viewed by the GUI.
	 */
	private GameService gameService;

	/**
	 * The {@link SecurityInvalidator} for logging out.
	 */
	private SecurityInvalidator securityInvalidator;

	/**
	 * The {@link LeagueService} for creating leagues.
	 */
	private LeagueService leagueService;

	/**
	 * The {@link AuthenticationService} for user authentication.
	 */
	private AuthenticationService authenticationService;

	/**
	 * The {@link TicketingCalendarService} to get and set which calendar is to be
	 * shown for ticketing information.
	 */
	private TicketingCalendarService ticketingCalendarService;

	/**
	 * The {@link MainUpdateService} used for updating Google calendars.
	 */
	private MainUpdateService mainUpdateService;

	/**
	 * The {@link UserService} used for updating users.
	 */
	private UserService userService;

	/**
	 * The {@link UserService} used for getting user information.
	 */
	private UserDao userDao;

	/**
	 * The {@link DefaultsService} used to make sure the minimal defaults exist.
	 */
	private DefaultsService defaultsService;

	/**
	 * The {@link CalendarFactory} used to install Google Calendar authorisation.
	 */
	private CalendarFactory calendarFactory;

	/**
	 * The {@link UpdateCalendarJob} used to trigger a full manual calendar
	 * update.
	 */
	private UpdateCalendarJob updateCalendarJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer[] getAllSeasons() {
		return Iterables.toArray(getGameService().getAllSeasons(), Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView[] getAllGameViewsChronologicallyForSeason(final int season) {
		Function<Boolean, SortedSet<GameView>> gameViewsFunction = new Function<Boolean, SortedSet<GameView>>() {
			@Override
			public SortedSet<GameView> apply(Boolean enabled) {
				return getGameService().getGameViewsForSeasonByDatePlayed(enabled, season);
			}
		};
		return getGameViewsForSeason(gameViewsFunction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView[] getAllGameViewsByOpponentsForSeason(final int season) {
		Function<Boolean, SortedSet<GameView>> gameViewsFunction = new Function<Boolean, SortedSet<GameView>>() {
			@Override
			public SortedSet<GameView> apply(Boolean enabled) {
				return getGameService().getGameViewsForSeasonByOpponents(enabled, season);
			}
		};
		return getGameViewsForSeason(gameViewsFunction);
	}

	/**
	 * Get all {@link GameView}s for a given season.
	 * 
	 * @param gameViewsFunction
	 *          A {@link Function} that returns a sorted set of {@link GameView}s
	 *          that are enabled or disabled depending on the argument supplied to
	 *          the function.
	 * @return An array of {@link GameView}s.
	 */
	protected GameView[] getGameViewsForSeason(Function<Boolean, SortedSet<GameView>> gameViewsFunction) {
		return Iterables.toArray(gameViewsFunction.apply(getAuthenticationService().isUserAuthenticated()), GameView.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LeagueRow[] getLeagueForSeason(int season) {
		return Iterables.toArray(getLeagueService().getLeagueForSeason(season), LeagueRow.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authenticate(String username, String password) {
		return getAuthenticationService().authenticate(username, password, getSecurityInvalidator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserPrincipal() {
		return getAuthenticationService().getUserPrincipal();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ensureDefaultsExist() throws GoogleAuthenticationFailedException, IOException {
		getDefaultsService().createDefaultUser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createCalendars() throws IOException, GoogleAuthenticationFailedException {
		getDefaultsService().createCalendars();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout() {
		getAuthenticationService().logout(getSecurityInvalidator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView[] attendAllHomeGameViewsForSeason(int season) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().attendAllHomeGamesForSeason(season);
		return getAllGameViewsChronologicallyForSeason(season);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().attendGame(gameId);
		return getGameService().getGameViewById(gameId, getAuthenticationService().isUserAuthenticated());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameView unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().unattendGame(gameId);
		return getGameService().getGameViewById(gameId, getAuthenticationService().isUserAuthenticated());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forceLogin() {
		// No need to do anything
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createGoogleAuthorisationUrl() {
		return getCalendarFactory().getAuthorisationUrl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void authorise(String authorisationToken) throws GoogleAuthenticationFailedException, IOException {
		getCalendarFactory().installAuthorisationCode(authorisationToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateCalendars() {
		try {
			getUpdateCalendarJob().scheduleNow();
		}
		catch (SchedulerException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException {
		getUserService().addUser(username, password, role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException {
		getUserService().alterUser(username, newPassword, newRole);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changePassword(String newPassword) {
		try {
			getUserService().alterPassword(getUserPrincipal(), newPassword);
		}
		catch (NoSuchUsernameException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User[] getAllUsers() {
		final Function<Authority, Role> authorityFunction = new Function<Authority, Role>() {
			@Override
			public Role apply(Authority authority) {
				return authority.getRole();
			}
		};
		final String userPrincipal = getUserPrincipal();
		Function<uk.co.unclealex.hammers.calendar.server.model.User, User> userFunction = 
				new Function<uk.co.unclealex.hammers.calendar.server.model.User, User>() {
			@Override
			public User apply(uk.co.unclealex.hammers.calendar.server.model.User user) {
				SortedSet<Role> roles = Sets.newTreeSet(Iterables.transform(user.getAuthorities(), authorityFunction));
				String username = user.getUsername();
				return new User(username, username.equals(userPrincipal), roles.last());
			}
		};
		return Iterables.toArray(Iterables.transform(Sets.newTreeSet(getUserDao().getAll()), userFunction), User.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeUser(String username) throws NoSuchUsernameException {
		getUserService().removeUser(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedTicketingCalendar(CalendarType calendarType) {
		getTicketingCalendarService().setSelectedTicketingCalendar(calendarType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalendarType getSelectedTicketingCalendar() {
		return getTicketingCalendarService().getSelectedTicketingCalendar();
	}

	/**
	 * Gets the {@link GameService} used to create sets of {@link GameView}
	 * instances to be viewed by the GUI.
	 * 
	 * @return the {@link GameService} used to create sets of {@link GameView}
	 *         instances to be viewed by the GUI
	 */
	public GameService getGameService() {
		return gameService;
	}

	/**
	 * Sets the {@link GameService} used to create sets of {@link GameView}
	 * instances to be viewed by the GUI.
	 * 
	 * @param gameService
	 *          the new {@link GameService} used to create sets of
	 *          {@link GameView} instances to be viewed by the GUI
	 */
	public void setGameService(GameService gameService) {
		this.gameService = gameService;
	}

	/**
	 * Gets the {@link SecurityInvalidator} for logging out.
	 * 
	 * @return the {@link SecurityInvalidator} for logging out
	 */
	public SecurityInvalidator getSecurityInvalidator() {
		return securityInvalidator;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSecurityInvalidator(SecurityInvalidator securityInvalidator) {
		this.securityInvalidator = securityInvalidator;
	}

	/**
	 * Gets the {@link LeagueService} for creating leagues.
	 * 
	 * @return the {@link LeagueService} for creating leagues
	 */
	public LeagueService getLeagueService() {
		return leagueService;
	}

	/**
	 * Sets the {@link LeagueService} for creating leagues.
	 * 
	 * @param leagueService
	 *          the new {@link LeagueService} for creating leagues
	 */
	public void setLeagueService(LeagueService leagueService) {
		this.leagueService = leagueService;
	}

	/**
	 * Gets the {@link AuthenticationService} for user authentication.
	 * 
	 * @return the {@link AuthenticationService} for user authentication
	 */
	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	/**
	 * Sets the {@link AuthenticationService} for user authentication.
	 * 
	 * @param authenticationService
	 *          the new {@link AuthenticationService} for user authentication
	 */
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	/**
	 * Gets the {@link TicketingCalendarService} to get and set which calendar is
	 * to be shown for ticketing information.
	 * 
	 * @return the {@link TicketingCalendarService} to get and set which calendar
	 *         is to be shown for ticketing information
	 */
	public TicketingCalendarService getTicketingCalendarService() {
		return ticketingCalendarService;
	}

	/**
	 * Sets the {@link TicketingCalendarService} to get and set which calendar is
	 * to be shown for ticketing information.
	 * 
	 * @param ticketingCalendarService
	 *          the new {@link TicketingCalendarService} to get and set which
	 *          calendar is to be shown for ticketing information
	 */
	public void setTicketingCalendarService(TicketingCalendarService ticketingCalendarService) {
		this.ticketingCalendarService = ticketingCalendarService;
	}

	/**
	 * Gets the {@link MainUpdateService} used for updating Google calendars.
	 * 
	 * @return the {@link MainUpdateService} used for updating Google calendars
	 */
	public MainUpdateService getMainUpdateService() {
		return mainUpdateService;
	}

	/**
	 * Sets the {@link MainUpdateService} used for updating Google calendars.
	 * 
	 * @param mainUpdateService
	 *          the new {@link MainUpdateService} used for updating Google
	 *          calendars
	 */
	public void setMainUpdateService(MainUpdateService mainUpdateService) {
		this.mainUpdateService = mainUpdateService;
	}

	/**
	 * Gets the {@link UserService} used for updating users.
	 * 
	 * @return the {@link UserService} used for updating users
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Sets the {@link UserService} used for updating users.
	 * 
	 * @param userService
	 *          the new {@link UserService} used for updating users
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Gets the {@link UserService} used for getting user information.
	 * 
	 * @return the {@link UserService} used for getting user information
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * Sets the {@link UserService} used for getting user information.
	 * 
	 * @param userDao
	 *          the new {@link UserService} used for getting user information
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Gets the {@link DefaultsService} used to make sure the minimal defaults
	 * exist.
	 * 
	 * @return the {@link DefaultsService} used to make sure the minimal defaults
	 *         exist
	 */
	public DefaultsService getDefaultsService() {
		return defaultsService;
	}

	/**
	 * Sets the {@link DefaultsService} used to make sure the minimal defaults
	 * exist.
	 * 
	 * @param defaultsService
	 *          the new {@link DefaultsService} used to make sure the minimal
	 *          defaults exist
	 */
	public void setDefaultsService(DefaultsService defaultsService) {
		this.defaultsService = defaultsService;
	}

	/**
	 * Gets the {@link CalendarFactory} used to install Google Calendar
	 * authorisation.
	 * 
	 * @return the {@link CalendarFactory} used to install Google Calendar
	 *         authorisation
	 */
	public CalendarFactory getCalendarFactory() {
		return calendarFactory;
	}

	/**
	 * Sets the {@link CalendarFactory} used to install Google Calendar
	 * authorisation.
	 * 
	 * @param calendarFactory
	 *          the new {@link CalendarFactory} used to install Google Calendar
	 *          authorisation
	 */
	public void setCalendarFactory(CalendarFactory calendarFactory) {
		this.calendarFactory = calendarFactory;
	}

	/**
	 * Gets the {@link UpdateCalendarJob} used to trigger a full manual calendar
	 * update.
	 * 
	 * @return the {@link UpdateCalendarJob} used to trigger a full manual
	 *         calendar update
	 */
	public UpdateCalendarJob getUpdateCalendarJob() {
		return updateCalendarJob;
	}

	/**
	 * Sets the {@link UpdateCalendarJob} used to trigger a full manual calendar
	 * update.
	 * 
	 * @param updateCalendarJob
	 *          the new {@link UpdateCalendarJob} used to trigger a full manual
	 *          calendar update
	 */
	public void setUpdateCalendarJob(UpdateCalendarJob updateCalendarJob) {
		this.updateCalendarJob = updateCalendarJob;
	}
}
