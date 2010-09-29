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
package uk.co.unclealex.hammers.calendar.dao;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;

@Transactional
public class HibernateGameDao extends HibernateDaoSupport implements GameDao {

	@Override
	public Game findByDatePlayed(Date datePlayed) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.datePlayed = :datePlayed").
			setTimestamp("datePlayed", datePlayed);
		return (Game) query.uniqueResult();
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
		return (Game) query.uniqueResult();
	}

	@Override
	public List<Game> getAllAfter(Date date) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.datePlayed >= :date").
			setTimestamp("date", date);
		return list(query);
	}

	@Override
	public List<Game> getAllTicketDatesAfter(Date date) {
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
	public SortedSet<Game> getAllForSeason(int season) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.season = :season").
			setInteger("season", season);
		List<Game> games = list(query);
		Comparator<Game> comparator = new Comparator<Game>() {
			@Override
			public int compare(Game o1, Game o2) {
				return o1.getDatePlayed().compareTo(o2.getDatePlayed());
			}
		};
		SortedSet<Game> sortedGames = new TreeSet<Game>(comparator);
		sortedGames.addAll(games);
		return sortedGames;
	}

	@Override
	public SortedSet<Integer> getAllSeasons() {
		Query query = getSession().createQuery("select distinct season from Game");
		return new TreeSet<Integer>(list(query, Integer.class));
	}

	@Override
	public List<Game> getAll() {
		return list(getSession().createQuery("from Game"));
	}
	
	@Override
	public List<Game> getAllByAttendence(boolean attended) {
		return list(getSession().createQuery("from Game where attended = :attended").setBoolean("attended", attended));
	}
	
	@Override
	public List<Game> getAllTicketDates() {
		return list(getSession().createQuery("from Game where ticketsAvailable is not null"));
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> list(Query query, Class<? extends T> clazz) {
		return query.list();
	}
	
	protected List<Game> list(Query query) {
		return list(query, Game.class);
	}
	
	@Override
	public Game findById(int id) {
		return (Game) getSession().get(Game.class, id);
	}

	@Override
	public void store(Game game) {
		getSession().saveOrUpdate(game);
	}

}
