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
package uk.co.unclealex.hammers.calendar.servlet;

public class LeagueRow implements Comparable<LeagueRow> {

	private String i_team;
	private int i_played;
	private int i_won;
	private int i_drawn;
	private int i_lost;
	private int i_for;
	private int i_against;
	private int i_points;
	
	public LeagueRow(String team, int goalsFor, int goalsAgainst) {
		setTeam(team);
		addGame(goalsFor, goalsAgainst);
	}
	
	public void addGame(int goalsFor, int goalsAgainst) {
		setFor(getFor() + goalsFor);
		setAgainst(getAgainst() + goalsAgainst);
		int points;
		if (goalsFor > goalsAgainst) {
			points = 3;
			setWon(getWon() + 1);
		}
		else if (goalsFor == goalsAgainst) {
			points = 1;
			setDrawn(getDrawn() + 1);
		}
		else {
			points = 0;
			setLost(getLost() + 1);
		}
		setPoints(getPoints() +  points);
		setPlayed(getPlayed() + 1);
	}

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
	
	public int getGoalDifference() {
		return getFor() - getAgainst();
	}
	
	public String getTeam() {
		return i_team;
	}

	public void setTeam(String team) {
		i_team = team;
	}

	public int getPlayed() {
		return i_played;
	}

	public void setPlayed(int played) {
		i_played = played;
	}

	public int getWon() {
		return i_won;
	}

	public void setWon(int won) {
		i_won = won;
	}

	public int getDrawn() {
		return i_drawn;
	}

	public void setDrawn(int drawn) {
		i_drawn = drawn;
	}

	public int getLost() {
		return i_lost;
	}

	public void setLost(int lost) {
		i_lost = lost;
	}

	public int getFor() {
		return i_for;
	}

	public void setFor(int for1) {
		i_for = for1;
	}

	public int getAgainst() {
		return i_against;
	}

	public void setAgainst(int against) {
		i_against = against;
	}

	public int getPoints() {
		return i_points;
	}

	public void setPoints(int points) {
		i_points = points;
	}
}
