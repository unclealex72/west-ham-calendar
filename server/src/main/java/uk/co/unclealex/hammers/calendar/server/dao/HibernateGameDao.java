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

import java.util.SortedSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
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
	public Game findByDatePlayed(DateTime datePlayed) {
		Query query = 
			getSession().createQuery(
					"from Game g " +
					"where " +
						"g.datePlayed = :datePlayed").
			setParameter("datePlayed", datePlayed);
		return unique(query);
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

	@Override
	public void attendAllHomeGamesForSeason(int season) {
		Session session = getSession();
		session.createQuery(
				"update Game set attended = :attended where season = :season and location = :location")
		        .setInteger("season", season)
		        .setBoolean("attended", true)
		        .setParameter("location", Location.HOME)
		        .executeUpdate();
		session.flush();
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

}
