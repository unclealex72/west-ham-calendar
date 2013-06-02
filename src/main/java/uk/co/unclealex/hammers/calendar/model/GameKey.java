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

package uk.co.unclealex.hammers.calendar.model;

import com.google.common.base.Objects;



/**
 * A class that encapsulates the immutable information that uniquely identifies
 * a game.
 * 
 * @author alex
 * 
 */
public class GameKey implements Comparable<GameKey> {

	/**
	 * The game's competition.
	 */
	private final Competition competition;
	
	/**
	 * The game's location.
	 */
	private final Location location;
	
	/**
	 * The game's opponents.
	 */
	private final String opponents;
	
	/**
	 * The game's season.
	 */
	private final int season;

	/**
	 * Create a new game key.
	 * 
	 * @param competition
	 *          The game's competition.
	 * @param location
	 *          The game's location.
	 * @param opponents
	 *          The game's opponents.
	 * @param season
	 *          The game's season.
	 */
	public GameKey(Competition competition, Location location, String opponents, int season) {
		super();
		this.competition = competition;
		this.location = location;
		this.opponents = opponents;
		this.season = season;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(GameKey o) {
		int cmp = getSeason() - o.getSeason();
		if (cmp == 0) {
			cmp = getCompetition().compareTo(o.getCompetition());
		}
		if (cmp == 0) {
			cmp = getOpponents().compareTo(o.getOpponents());
		}
		if (cmp == 0) {
			cmp = getLocation().compareTo(o.getLocation());
		}
		return cmp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%d %s %s %s", getSeason(), getCompetition(), getOpponents(), getLocation());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof GameKey && compareTo((GameKey) obj) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(getSeason(), getCompetition(), getOpponents(), getLocation());
	}
	
	/**
	 * Gets the game's competition.
	 * 
	 * @return The game's competition.
	 */
	public Competition getCompetition() {
		return competition;
	}

	/**
	 * Gets the game's location.
	 * 
	 * @return The game's location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the game's opponents.
	 * 
	 * @return The game's opponents.
	 */
	public String getOpponents() {
		return opponents;
	}

	/**
	 * Gets the game's season.
	 * 
	 * @return The game's season.
	 */
	public int getSeason() {
		return season;
	}

}
