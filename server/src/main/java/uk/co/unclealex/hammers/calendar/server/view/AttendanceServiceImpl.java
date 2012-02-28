/**
 * Copyright 2011 Alex Jones
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
	private GameService i_gameService;

	/**
	 * The {@link SecurityInvalidator} for logging out.
	 */
	private SecurityInvalidator i_securityInvalidator;

	/**
	 * The {@link LeagueService} for creating leagues.
	 */
	private LeagueService i_leagueService;

	/**
	 * The {@link AuthenticationService} for user authentication.
	 */
	private AuthenticationService i_authenticationService;

	/**
	 * The {@link TicketingCalendarService} to get and set which calendar is to be
	 * shown for ticketing information.
	 */
	private TicketingCalendarService i_ticketingCalendarService;

	/**
	 * The {@link MainUpdateService} used for updating Google calendars.
	 */
	private MainUpdateService i_mainUpdateService;

	/**
	 * The {@link UserService} used for updating users.
	 */
	private UserService i_userService;

	/**
	 * The {@link UserService} used for getting user information.
	 */
	private UserDao i_userDao;

	/**
	 * The {@link DefaultsService} used to make sure the minimal defaults exist.
	 */
	private DefaultsService i_defaultsService;

	/**
	 * The {@link CalendarFactory} used to install Google Calendar authorisation.
	 */
	private CalendarFactory i_calendarFactory;

	/**
	 * The {@link UpdateCalendarJob} used to trigger a full manual calendar
	 * update.
	 */
	private UpdateCalendarJob i_updateCalendarJob;

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

	@Override
	public LeagueRow[] getLeagueForSeason(int season) {
		return Iterables.toArray(getLeagueService().getLeagueForSeason(season), LeagueRow.class);
	}

	@Override
	public boolean authenticate(String username, String password) {
		return getAuthenticationService().authenticate(username, password, getSecurityInvalidator());
	}

	@Override
	public String getUserPrincipal() {
		return getAuthenticationService().getUserPrincipal();
	}

	@Override
	public void ensureDefaultsExist() throws GoogleAuthenticationFailedException, IOException {
		getDefaultsService().createDefaultUser();
	}

	@Override
	public void createCalendars() throws IOException, GoogleAuthenticationFailedException {
		getDefaultsService().createCalendars();
	}

	@Override
	public void logout() {
		getAuthenticationService().logout(getSecurityInvalidator());
	}

	@Override
	public GameView[] attendAllHomeGameViewsForSeason(int season) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().attendAllHomeGamesForSeason(season);
		return getAllGameViewsChronologicallyForSeason(season);
	}

	@Override
	public GameView attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().attendGame(gameId);
		return getGameService().getGameViewById(gameId, getAuthenticationService().isUserAuthenticated());
	}

	@Override
	public GameView unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException {
		getMainUpdateService().unattendGame(gameId);
		return getGameService().getGameViewById(gameId, getAuthenticationService().isUserAuthenticated());
	}

	@Override
	public void forceLogin() {
		// No need to do anything
	}

	@Override
	public String createGoogleAuthorisationUrlIfRequired() {
		return getCalendarFactory().getAuthorisationUrl();
	}

	@Override
	public void authorise(String authorisationToken) throws GoogleAuthenticationFailedException, IOException {
		getCalendarFactory().installAuthorisationCode(authorisationToken);
	}

	@Override
	public void updateCalendars() {
		try {
			getUpdateCalendarJob().scheduleNow();
		}
		catch (SchedulerException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException {
		getUserService().addUser(username, password, role);
	}

	@Override
	public void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException {
		getUserService().alterUser(username, newPassword, newRole);
	}

	@Override
	public void changePassword(String newPassword) {
		try {
			getUserService().alterPassword(getUserPrincipal(), newPassword);
		}
		catch (NoSuchUsernameException e) {
			throw new IllegalStateException(e);
		}
	}

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

	@Override
	public void removeUser(String username) throws NoSuchUsernameException {
		getUserService().removeUser(username);
	}

	@Override
	public void setSelectedTicketingCalendar(CalendarType calendarType) {
		getTicketingCalendarService().setSelectedTicketingCalendar(calendarType);
	}

	@Override
	public CalendarType getSelectedTicketingCalendar() {
		return getTicketingCalendarService().getSelectedTicketingCalendar();
	}

	public GameService getGameService() {
		return i_gameService;
	}

	public void setGameService(GameService gameService) {
		i_gameService = gameService;
	}

	public SecurityInvalidator getSecurityInvalidator() {
		return i_securityInvalidator;
	}

	public void setSecurityInvalidator(SecurityInvalidator securityInvalidator) {
		i_securityInvalidator = securityInvalidator;
	}

	public LeagueService getLeagueService() {
		return i_leagueService;
	}

	public void setLeagueService(LeagueService leagueService) {
		i_leagueService = leagueService;
	}

	public AuthenticationService getAuthenticationService() {
		return i_authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		i_authenticationService = authenticationService;
	}

	public TicketingCalendarService getTicketingCalendarService() {
		return i_ticketingCalendarService;
	}

	public void setTicketingCalendarService(TicketingCalendarService ticketingCalendarService) {
		i_ticketingCalendarService = ticketingCalendarService;
	}

	public MainUpdateService getMainUpdateService() {
		return i_mainUpdateService;
	}

	public void setMainUpdateService(MainUpdateService mainUpdateService) {
		i_mainUpdateService = mainUpdateService;
	}

	public UserService getUserService() {
		return i_userService;
	}

	public void setUserService(UserService userService) {
		i_userService = userService;
	}

	public UserDao getUserDao() {
		return i_userDao;
	}

	public void setUserDao(UserDao userDao) {
		i_userDao = userDao;
	}

	public DefaultsService getDefaultsService() {
		return i_defaultsService;
	}

	public void setDefaultsService(DefaultsService defaultsService) {
		i_defaultsService = defaultsService;
	}

	public CalendarFactory getCalendarFactory() {
		return i_calendarFactory;
	}

	public void setCalendarFactory(CalendarFactory calendarFactory) {
		i_calendarFactory = calendarFactory;
	}

	public UpdateCalendarJob getUpdateCalendarJob() {
		return i_updateCalendarJob;
	}

	public void setUpdateCalendarJob(UpdateCalendarJob updateCalendarJob) {
		i_updateCalendarJob = updateCalendarJob;
	}
}
