/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.dao.CalendarConfigurationDao;
import uk.co.unclealex.hammers.calendar.server.dao.GameDao;
import uk.co.unclealex.hammers.calendar.server.dao.UserDao;
import uk.co.unclealex.hammers.calendar.server.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.model.Authority;
import uk.co.unclealex.hammers.calendar.server.scheduling.UpdateCalendarJob;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;
import uk.co.unclealex.hammers.calendar.shared.model.Role;
import uk.co.unclealex.hammers.calendar.shared.model.User;
import uk.co.unclealex.hammers.calendar.shared.remote.AttendanceService;
import uk.co.unclealex.hammers.calendar.shared.remote.SecurityInvalidator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gdata.util.ServiceException;

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
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

	private GameDao i_gameDao;
	private GameService i_gameService;
	private AuthenticationManager i_authenticationManager;
	private UserService i_userService;
	private DefaultsService i_defaultsService;
	private CalendarConfigurationService i_calendarConfigurationService;
	private CalendarConfigurationDao i_calendarConfigurationDao;
	private GoogleCalendarService i_googleCalendarService;
	private SecurityInvalidator i_securityInvalidator;
	private Map<CalendarType, GoogleCalendar> i_googleCalendarsByCalendarType;
	private UpdateCalendarJob i_updateCalendarJob;
	private UserDao i_userDao;
	
	public void initialise() {
		// Do nothing.
	}
	
	@Override
	public Integer[] getAllSeasons() {
		return toSortedArray(getGameDao().getAllSeasons(), Integer.class);
	}

	@Override
	public Game[] getAllGamesByOpponentsForSeason(int season) {
		Comparator<Game> comparator = new Comparator<Game>() {
			@Override
			public int compare(Game g1, Game g2) {
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
		return toSortedArray(Iterables.transform(getGameDao().getAllForSeason(season), createGameFunction()), Game.class, comparator);
	}

	@Override
	public Integer getLatestSeason() {
		Integer latestSeason = getGameDao().getLatestSeason();
		if (latestSeason == null) {
		  Calendar cal = new GregorianCalendar();
		  int currentYear = cal.get(Calendar.YEAR);
		  int currentMonth = cal.get(Calendar.MONTH);
		  latestSeason = currentYear;
		  if (currentMonth < Calendar.AUGUST) {
		    latestSeason = latestSeason - 1;
		  }
		}
		return latestSeason;
	}
	
	@Override
	public LeagueRow[] getLeagueForSeason(int season) {
		Predicate<Game> isLeagueGamePredicate = new Predicate<Game>() {
			@Override
			public boolean apply(Game game) {
				Competition competition = game.getCompetition();
				return competition == Competition.FLC || competition == Competition.PREM;
			}
		};
		Set<LeagueRow> league = new HashSet<LeagueRow>();
		for (final Game game : 
			Iterables.filter(Iterables.transform(getGameDao().getAllForSeason(season), createGameFunction()), isLeagueGamePredicate)) {
			final String opponents = game.getOpponents();
			Predicate<LeagueRow> predicate = new Predicate<LeagueRow>() {
				@Override
				public boolean apply(LeagueRow leagueRow) {
					return leagueRow.getTeam().equals(opponents);
				}
			};
			String result = game.getResult();
			if (result != null) {
				String[] scores = StringUtils.split(result.substring(2), '-');
				int goalsFor = Integer.valueOf(scores[0]);
				int goalsAgainst = Integer.valueOf(scores[1]);
				LeagueRow leagueRow = Iterables.find(league, predicate, null);
				if (leagueRow == null) {
					leagueRow = new LeagueRow(opponents, goalsFor, goalsAgainst);
					league.add(leagueRow);
				}
				else {
					leagueRow.addGame(goalsFor, goalsAgainst);
				}
			}
		}
		return toSortedArray(league, LeagueRow.class);
	}

	@Override
	public boolean authenticate(String username, String password) {
		try {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
			getAuthenticationManager().authenticate(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return true;
		}
		catch (AuthenticationException e) {
			logout();
			return false;
		}
	}

	@Override
	public String getUserPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication==null || isAuthenticatedAnonymously(authentication)?
				null:
				((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
	}

	protected boolean isAuthenticatedAnonymously(Authentication authentication) {
		Collection<GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.size() == 1 && "ROLE_ANONYMOUS".equals(authorities.iterator().next().getAuthority());
	}

	@Override
	public void ensureDefaultsExist() throws GoogleAuthenticationFailedException, GoogleException, IOException {
		getDefaultsService().ensureDefaultUsersExist();
		try {
			getDefaultsService().ensureDefaultCalendarsExistAndCalendarsAreSynchronised();
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}
	
	@Override
	public void logout() {
		getSecurityInvalidator().invalidate();
		SecurityContextHolder.clearContext();
	}

	public Game[] getAllGamesChronologicallyForSeason(int season) {
		return toSortedArray(Iterables.transform(getGameDao().getAllForSeason(season), createGameFunction()), Game.class);
	}


	protected <E extends Comparable<E>> E[] toSortedArray(Iterable<E> elements, Class<E> clazz) {
		Comparator<E> comparator = new Comparator<E>() {
			public int compare(E o1, E o2) {
				return o1.compareTo(o2);
			};
		};
		return toSortedArray(elements, clazz, comparator);
	}
	
	protected <E> E[] toSortedArray(Iterable<E> elements, Class<E> clazz, Comparator<E> comparator) {
		TreeSet<E> sortedElements = Sets.newTreeSet(comparator);
		Iterables.addAll(sortedElements, elements);
		return Iterables.toArray(sortedElements, clazz);
	}

	protected Function<uk.co.unclealex.hammers.calendar.server.model.Game, Game> createGameFunction() {
	  final Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date> ticketFunction =
	      createTicketFunction();
		final boolean isAuthenticated = isUserAuthenticated();
		return new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Game>() {
			@Override
			public Game apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
				boolean weekGame;
				boolean nonStandardWeekendGame;
				Date datePlayed = game.getDatePlayed();
				Calendar cal = new GregorianCalendar();
				cal.setTime(datePlayed);
				int day = cal.get(Calendar.DAY_OF_WEEK);
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				int minute = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);
				if (day == Calendar.SATURDAY && hour == 15 && minute == 0 && second == 0) {
					weekGame = false;
					nonStandardWeekendGame = false;
				}
				else if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
					weekGame = false;
					nonStandardWeekendGame = true;
				}
				else {
					weekGame = true;
					nonStandardWeekendGame = false;
				}
				return new Game(
						game.getId(), 
						game.getCompetition(), 
						game.getLocation(), game.getOpponents(), 
						game.getSeason(),
						datePlayed,
						game.getResult(),
						game.getAttendence(),
						game.getMatchReport(),
						game.getTelevisionChannel(),
						ticketFunction.apply(game),
						game.isAttended(),
						weekGame,
						nonStandardWeekendGame,
						isAuthenticated);
			}
		};
	}

  protected Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date> createTicketFunction() {
    CalendarType calendarType = getCalendarConfigurationService().getSelectedTicketingCalendar();
    Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date> ticketFunction = null;
    if (calendarType != null) {
      if (calendarType == CalendarType.TICKETS_GENERAL_SALE) {
        ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
          @Override
          public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
            return game.getGeneralSaleAvailable();
          }
        };
      }
      else if (calendarType == CalendarType.TICKETS_ACADEMY) {
        ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
          @Override
          public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
            return game.getAcademyMembersAvailable();
          }
        };
      }
      else if (calendarType == CalendarType.TICKETS_SEASON) {
        ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
          @Override
          public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
            return game.getSeasonTicketsAvailable();
          }
        };
      }
      else if (calendarType == CalendarType.TICKETS_PRIORITY) {
        ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
          @Override
          public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
            return game.getPriorityPointPostAvailable();
          }
        };
      }
      else if (calendarType == CalendarType.TICKETS_BONDHOLDERS) {
        ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
          @Override
          public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
            return game.getBondholdersAvailable();
          }
        };
      }
    }
    if (ticketFunction == null) {
      ticketFunction = new Function<uk.co.unclealex.hammers.calendar.server.model.Game, Date>() {
        @Override
        public Date apply(uk.co.unclealex.hammers.calendar.server.model.Game game) {
          return null;
        }
      };
    }
    return ticketFunction;
  }

  protected boolean isUserInRole(String role) {
		Function<GrantedAuthority, String> authorityFunction = new Function<GrantedAuthority, String>() {
			@Override
			public String apply(GrantedAuthority grantedAuthority) {
				return grantedAuthority.getAuthority();
			}
		};
		return Iterables.contains(
				Iterables.transform(SecurityContextHolder.getContext().getAuthentication().getAuthorities(), authorityFunction), role);
	}
	
	protected boolean isUserAuthenticated() {
		return isUserInRole("ROLE_USER");
	}

	@Override
	public Game[] attendAllHomeGamesForSeason(int season) {
		getGameDao().attendAllHomeGamesForSeason(season);
		return getAllGamesChronologicallyForSeason(season);
	}

	@Override
	public Game attendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			Game game = createGameFunction().apply(getGameService().attendGame(gameId));
			getGoogleCalendarService().attendGame(game.getId());
			return game;
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}

	@Override
	public Game unattendGame(int gameId) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			Game game = createGameFunction().apply(getGameService().unattendGame(gameId));
			getGoogleCalendarService().unattendGame(game.getId());
			return game;
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}

	@Override
	public void forceLogin() {
		// No need to do anything
	}

	@Override
	public void remove(CalendarType calendarType) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			getCalendarConfigurationService().remove(calendarType);
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}

	@Override
	public void createOrUpdate(CalendarConfiguration calendarConfiguration) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			CalendarType calendarType = calendarConfiguration.getCalendarType();
			uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration serverCalendarConfiguration = getCalendarConfigurationDao().findByKey(calendarType);
			if (serverCalendarConfiguration == null) {
				serverCalendarConfiguration = new uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration();
			}
			serverCalendarConfiguration.setBusy(calendarConfiguration.isBusy());
			serverCalendarConfiguration.setCalendarType(calendarType);
			serverCalendarConfiguration.setColour(calendarConfiguration.getColour());
			serverCalendarConfiguration.setReminderInMinutes(calendarConfiguration.getReminderInMinutes());
			serverCalendarConfiguration.setShared(calendarConfiguration.isShared());
			serverCalendarConfiguration.setSelected(calendarConfiguration.isSelected());
			String googleCalendarId = getGoogleCalendarService().createOrUpdate(serverCalendarConfiguration);
			getCalendarConfigurationService().createOrUpdate(serverCalendarConfiguration, googleCalendarId);
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
		
	}

	@Override
	public CalendarConfiguration[] getAllCalendarConfigurations() {
		return toSortedArray(Iterables.transform(getCalendarConfigurationDao().getAll(), createCalendarConfigurationFunction()), CalendarConfiguration.class);
	}

	public Function<uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration, CalendarConfiguration> createCalendarConfigurationFunction() {
		Function<uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration, CalendarConfiguration> calendarConfigurationFunction = 
				new Function<uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration, CalendarConfiguration>() {
			@Override
			public CalendarConfiguration apply(uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration calendarConfiguration) {
				CalendarType calendarType = calendarConfiguration.getCalendarType();
				GoogleCalendar googleCalendar = getGoogleCalendarsByCalendarType().get(calendarType);
				return new CalendarConfiguration(
						calendarType, 
						calendarConfiguration.getReminderInMinutes(), 
						calendarConfiguration.getColour(), 
						calendarConfiguration.isBusy(), 
						calendarConfiguration.isShared(),
						calendarConfiguration.isSelected(),
						googleCalendar.getCalendarTitle(),
						googleCalendar.getDescription(),
						calendarConfiguration.getId() != null);
			}
		};
		return calendarConfigurationFunction;
	}

	@Override
	public CalendarColour[] getUsedCalendarColours() throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			return Iterables.toArray(getGoogleCalendarService().getUsedCalendarColours(), CalendarColour.class);
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}
	
	@Override
	public String createGoogleAuthenticationUrlIfRequired() {
		return getGoogleCalendarService().createGoogleAuthenticationUrlIfRequired();
	}

	@Override
	public void authenticate(String successToken) throws GoogleAuthenticationFailedException, IOException {
		getGoogleCalendarService().installSuccessCode(successToken);
	}
	
	@Override
	public CalendarConfiguration createNewCalendarConfiguration(CalendarType calendarType) throws GoogleAuthenticationFailedException, IOException, GoogleException {
		try {
			List<CalendarColour> allCalendarColours = Arrays.asList(CalendarColour.values());
			List<CalendarColour> availableCalendarColours = new ArrayList<CalendarColour>(allCalendarColours);
			availableCalendarColours.removeAll(Lists.newArrayList(getGoogleCalendarService().getUsedCalendarColours()));
			if (availableCalendarColours.isEmpty()) {
				availableCalendarColours = allCalendarColours;
			}
			int randomIdx = new Random().nextInt(availableCalendarColours.size());
			uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration calendarConfiguration = 
					new uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration();
			calendarConfiguration.setCalendarType(calendarType);
			calendarConfiguration.setColour(availableCalendarColours.get(randomIdx));
			return createCalendarConfigurationFunction().apply(calendarConfiguration);
		}
		catch (ServiceException e) {
			throw new GoogleException(e);
		}
	}
	
	@Override
	public CalendarConfiguration[] getCalendarConfigurations(boolean tickets) {
		List<uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration> calendarConfigurations = 
				new ArrayList<uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration>();
		CalendarConfigurationDao calendarConfigurationDao = getCalendarConfigurationDao();
		for (CalendarType calendarType : CalendarType.values()) {
			if (calendarType.isTicketCalendar() == tickets) {
				uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration calendarConfiguration = calendarConfigurationDao.findByKey(calendarType);
				if (calendarConfiguration == null) {
					calendarConfiguration = new uk.co.unclealex.hammers.calendar.server.model.CalendarConfiguration();
					calendarConfiguration.setCalendarType(calendarType);
				}
				calendarConfigurations.add(calendarConfiguration);
			}
		}
		return Iterables.toArray(Iterables.transform(calendarConfigurations, createCalendarConfigurationFunction()), CalendarConfiguration.class);
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
	public void addUser(String username, String password, Role role)
	    throws UsernameAlreadyExistsException {
	  getUserService().addUser(username, password, role);
	}
	
	@Override
	public void alterUser(String username, String newPassword, Role newRole)
	    throws NoSuchUsernameException {
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
	    /* (non-Javadoc)
	     * @see com.google.common.base.Function#apply(java.lang.Object)
	     */
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
	  getCalendarConfigurationService().setSelectedTicketingCalendar(calendarType);
	}
	
	@Override
	public CalendarType getSelectedTicketingCalendar() {
	  return getCalendarConfigurationService().getSelectedTicketingCalendar();
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

	public AuthenticationManager getAuthenticationManager() {
		return i_authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		i_authenticationManager = authenticationManager;
	}

	public UserService getUserService() {
		return i_userService;
	}

	public void setUserService(UserService userService) {
		i_userService = userService;
	}

	public DefaultsService getDefaultsService() {
		return i_defaultsService;
	}

	public void setDefaultsService(DefaultsService defaultsService) {
		i_defaultsService = defaultsService;
	}

	public CalendarConfigurationService getCalendarConfigurationService() {
		return i_calendarConfigurationService;
	}

	public void setCalendarConfigurationService(CalendarConfigurationService calendarConfigurationService) {
		i_calendarConfigurationService = calendarConfigurationService;
	}

	public CalendarConfigurationDao getCalendarConfigurationDao() {
		return i_calendarConfigurationDao;
	}

	public void setCalendarConfigurationDao(CalendarConfigurationDao calendarConfigurationDao) {
		i_calendarConfigurationDao = calendarConfigurationDao;
	}

	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
	}

	public SecurityInvalidator getSecurityInvalidator() {
		return i_securityInvalidator;
	}

	public void setSecurityInvalidator(SecurityInvalidator securityInvalidator) {
		i_securityInvalidator = securityInvalidator;
	}

	public Map<CalendarType, GoogleCalendar> getGoogleCalendarsByCalendarType() {
		return i_googleCalendarsByCalendarType;
	}

	public void setGoogleCalendarsByCalendarType(Map<CalendarType, GoogleCalendar> googleCalendarsByCalendarType) {
		i_googleCalendarsByCalendarType = googleCalendarsByCalendarType;
	}

	public UpdateCalendarJob getUpdateCalendarJob() {
		return i_updateCalendarJob;
	}

	public void setUpdateCalendarJob(UpdateCalendarJob updateCalendarJob) {
		i_updateCalendarJob = updateCalendarJob;
	}

  public UserDao getUserDao() {
    return i_userDao;
  }

  public void setUserDao(UserDao userDao) {
    i_userDao = userDao;
  }
}
