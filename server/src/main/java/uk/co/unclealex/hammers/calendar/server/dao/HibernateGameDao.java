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
package uk.co.unclealex.hammers.calendar.server.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.collect.Sets;

@Transactional
public class HibernateGameDao extends GenericHibernateDaoSupport<Game> implements GameDao {

	/**
	 * @param clazz
	 */
	public HibernateGameDao() {
		super(Game.class);
	}

	@Override
	public Game findByDatePlayed(Date datePlayed) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.datePlayed = :datePlayed").
			setTimestamp("datePlayed", datePlayed);
		return unique(query);
	}
	
	@Override
	public Game findByDayPlayed(Date datePlayed) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(datePlayed);
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"year(g.datePlayed) = :year and " +
						"month(g.datePlayed) = :month and " +
						"day(g.datePlayed) = :day").
			setInteger("year", cal.get(Calendar.YEAR)).
			setInteger("month", cal.get(Calendar.MONTH) + 1).
			setInteger("day", cal.get(Calendar.DAY_OF_MONTH));
		return unique(query);
	}

	@Override
	public Iterable<Game> getAllAfter(Date date) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.datePlayed >= :date").
			setTimestamp("date", date);
		return list(query);
	}

	@Override
	public Iterable<Game> getAllTicketDatesAfter(Date date) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.ticketsAvailable >= :date").
			setTimestamp("date", date);
		return list(query);
	}
	
	@Override
	public Game findByBusinessKey(Competition competition, Location location,
			String opponents, int season) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.competition = :competition and " +
						"g.location = :location and " +
						"g.opponents = :opponents and " +
						"g.season = :season").
			setString("competition", competition.name()).
			setString("location", location.name()).
			setString("opponents", opponents).
			setInteger("season", season);
		return (Game) query.uniqueResult();
	}

	@Override
	public Iterable<Game> getAllForSeason(int season) {
		Query query = 
			getSession().createQuery(
					"from Game " +
					"where " +
						"season = :season").
			setInteger("season", season);
		return list(query);
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.hammers.calendar.server.dao.GameDao#attendAllHomeGamesForSeason(int)
	 */
	@Override
	public void attendAllHomeGamesForSeason(int season) {
		getSession().createQuery(
				"update Game set attended = :attended where season = :season and location = :location")
		        .setInteger("season", season)
		        .setBoolean("attended", true)
		        .setParameter("location", Location.HOME)
		        .executeUpdate();
	}
	
	@Override
	public SortedSet<Integer> getAllSeasons() {
		Query query = getSession().createQuery("select distinct season from Game");
		return Sets.newTreeSet(filter(query.list(), Integer.class));
	}

	@Override
	public Integer getLatestSeason() {
		Query query = getSession().createQuery("select distinct max(season) from Game");
		return (Integer) query.uniqueResult();
	}

	@Override
	public Iterable<Game> getAllByAttendence(boolean attended) {
		return list(getSession().createQuery("from Game where attended = :attended").setBoolean("attended", attended));
	}
	
	@Override
	public Iterable<Game> getAllTicketDates() {
		return list(getSession().createQuery("from Game where ticketsAvailable is not null"));
	}

}
