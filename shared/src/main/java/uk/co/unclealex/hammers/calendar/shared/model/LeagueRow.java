/**
 * Copyright 2012 Alex Jones
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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;


/**
 * A {@link LeagueRow} is a row in the league of results against opponents during a season.
 * @author alex
 *
 */
public class LeagueRow implements Comparable<LeagueRow>, Serializable {

	/**
	 * The points for a loss.
	 */
	private static final int LOSS = 0;

	/**
	 * The points for a draw.
	 */
	private static final int DRAW = 1;
	
	/**
	 * The points for a win.
	 */
	private static final int WIN = 3;
	
	/**
	 * The team who are the opponents for i_row.
	 */
	private String i_team;
	
	/**
	 * The number of games played for i_row.
	 */
	private int i_played;
	
	/**
	 * The number of games won for i_row.
	 */
	private int i_won;
	
	/**
	 * The number of games drawn for i_row.
	 */
	private int i_drawn;
	
	/**
	 * The number of games lost for i_row.
	 */
	private int i_lost;
	
	/**
	 * The number of goals scored for i_row.
	 */
	private int i_for;
	
	/**
	 * The number of goals conceded for i_row.
	 */
	private int i_against;
	
	/**
	 * The number of points for i_row.
	 */
	private int i_points;
	
	
	/**
	 * Instantiates a new league row.
	 */
	public LeagueRow() {
		super();
	}

	/**
	 * Instantiates a new league row.
	 * 
	 * @param team
	 *          the team
	 * @param goalsFor
	 *          the goals for
	 * @param goalsAgainst
	 *          the goals against
	 */
	public LeagueRow(String team, int goalsFor, int goalsAgainst) {
		setTeam(team);
		addGame(goalsFor, goalsAgainst);
	}
	
	/**
	 * Add a game to i_row.
	 * @param goalsFor The number of goals scored.
	 * @param goalsAgainst The number of goals conceded.
	 */
	public void addGame(int goalsFor, int goalsAgainst) {
		setFor(getFor() + goalsFor);
		setAgainst(getAgainst() + goalsAgainst);
		int points;
		if (goalsFor > goalsAgainst) {
			points = WIN;
			setWon(getWon() + 1);
		}
		else if (goalsFor == goalsAgainst) {
			points = DRAW;
			setDrawn(getDrawn() + 1);
		}
		else {
			points = LOSS;
			setLost(getLost() + 1);
		}
		setPoints(getPoints() +  points);
		setPlayed(getPlayed() + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(LeagueRow o) {
		int cmp = o.getPoints() - getPoints();
		if (cmp != 0) {
			return cmp;
		}
		cmp = o.getGoalDifference() - getGoalDifference();
		if (cmp != 0) {
			return cmp;
		}
		cmp = o.getFor() - getFor();
		if (cmp != 0) {
			return cmp;
		}
		cmp = getPlayed() - o.getPlayed();
		if (cmp != 0) {
			return cmp;
		}
		return o.getTeam().compareTo(getTeam());
	}
	
	/**
	 * Get the goal difference for i_row.
	 * @return The goal difference for i_row.
	 */
	public int getGoalDifference() {
		return getFor() - getAgainst();
	}
	
	/**
	 * Gets the team who are the opponents for i_row.
	 * 
	 * @return the team who are the opponents for i_row
	 */
	public String getTeam() {
		return i_team;
	}

	/**
	 * Sets the team who are the opponents for i_row.
	 * 
	 * @param team
	 *          the new team who are the opponents for i_row
	 */
	public void setTeam(String team) {
		i_team = team;
	}

	/**
	 * Gets the number of games played for i_row.
	 * 
	 * @return the number of games played for i_row
	 */
	public int getPlayed() {
		return i_played;
	}

	/**
	 * Sets the number of games played for i_row.
	 * 
	 * @param played
	 *          the new number of games played for i_row
	 */
	public void setPlayed(int played) {
		i_played = played;
	}

	/**
	 * Gets the number of games won for i_row.
	 * 
	 * @return the number of games won for i_row
	 */
	public int getWon() {
		return i_won;
	}

	/**
	 * Sets the number of games won for i_row.
	 * 
	 * @param won
	 *          the new number of games won for i_row
	 */
	public void setWon(int won) {
		i_won = won;
	}

	/**
	 * Gets the number of games drawn for i_row.
	 * 
	 * @return the number of games drawn for i_row
	 */
	public int getDrawn() {
		return i_drawn;
	}

	/**
	 * Sets the number of games drawn for i_row.
	 * 
	 * @param drawn
	 *          the new number of games drawn for i_row
	 */
	public void setDrawn(int drawn) {
		i_drawn = drawn;
	}

	/**
	 * Gets the number of games lost for i_row.
	 * 
	 * @return the number of games lost for i_row
	 */
	public int getLost() {
		return i_lost;
	}

	/**
	 * Sets the number of games lost for i_row.
	 * 
	 * @param lost
	 *          the new number of games lost for i_row
	 */
	public void setLost(int lost) {
		i_lost = lost;
	}

	/**
	 * Gets the number of goals scored for i_row.
	 * 
	 * @return the number of goals scored for i_row
	 */
	public int getFor() {
		return i_for;
	}

	/**
	 * Sets the number of goals scored for i_row.
	 * 
	 * @param for1
	 *          the new number of goals scored for i_row
	 */
	public void setFor(int for1) {
		i_for = for1;
	}

	/**
	 * Gets the number of goals conceded for i_row.
	 * 
	 * @return the number of goals conceded for i_row
	 */
	public int getAgainst() {
		return i_against;
	}

	/**
	 * Sets the number of goals conceded for i_row.
	 * 
	 * @param against
	 *          the new number of goals conceded for i_row
	 */
	public void setAgainst(int against) {
		i_against = against;
	}

	/**
	 * Gets the number of points for i_row.
	 * 
	 * @return the number of points for i_row
	 */
	public int getPoints() {
		return i_points;
	}

	/**
	 * Sets the number of points for i_row.
	 * 
	 * @param points
	 *          the new number of points for i_row
	 */
	public void setPoints(int points) {
		i_points = points;
	}
}
