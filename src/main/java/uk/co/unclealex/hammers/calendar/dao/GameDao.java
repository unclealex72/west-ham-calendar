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
package uk.co.unclealex.hammers.calendar.dao;

import java.util.SortedSet;

import org.joda.time.DateTime;

import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;


/**
 * The data access object for {@link Game}s.
 * 
 * @author alex
 */
public interface GameDao extends CrudDao<Game> {

	/**
	 * Find a game by the {@link DateTime} it was played.
	 * @param datePlayed The {@link DateTime} to search for.
	 * @return The {@link Game} played at the given {@link DateTime} or null if one could not be found.
	 */
	Game findByDatePlayed(DateTime datePlayed);
	
	/**
	 * Get all the {@link Game}s for a given season.
	 * @param season The season to search for.
	 * @return All the {@link Game}s during the given season.
	 */
	Iterable<Game> getAllForSeason(int season);
	
	/**
	 * Find all the seasons known so far.
	 * @return All the known seasons.
	 */
	SortedSet<Integer> getAllSeasons();
	
	/**
	 * Find a game by its {@link Competition}, {@link Location}, opponents and season. Together, these are guaranteed to
	 * uniquely define a {@link Game}.
	 * @param competition The {@link Competition} to search for.
	 * @param location The {@link Location} to search for.
	 * @param opponents The opponents to search for.
	 * @param season The season to search for.
	 * @return The uniquely defined {@link Game} if it exists or null otherwise.
	 */
	Game findByBusinessKey(Competition competition, Location location, String opponents, int season);
	
	/**
	 * Get the latest known season.
	 * @return The latest known season.
	 */
	Integer getLatestSeason();
	
	/**
	 * Get all {@link Game}s for a given season and {@link Location}.
	 * @param season The season to search for.
	 * @param location The {@link Location} to search for.
	 * @return All {@link Game}s with the given season and {@link Location}.
	 */
	Iterable<Game> getAllForSeasonAndLocation(int season, Location location);

}
