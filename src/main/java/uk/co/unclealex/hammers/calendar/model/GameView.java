/**
 * Copyright 2012 Alex Jones
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

import java.io.Serializable;
import java.util.Date;


/**
 * The view of how a {@link Game} is shown within the web application.
 * @author alex
 *
 */
public class GameView implements Serializable, Comparable<GameView> {

	/**
	 * The game's id.
	 */
	private Integer id;
	
	/**
	 * The game's {@link Competition}.
	 */
	private Competition competition;
	
	/**
	 * The game's {@link Location}.
	 */
	private Location location;
	
	/**
	 * The game's opponents.
	 */
	private String opponents;
	
	/**
	 * The game's season.
	 */
	private int season;

	/**
	 * The date and time the game was played.
	 */
	private Date datePlayed;

	/**
	 * The game's result or null if it has yet to be played.
	 */
	private String result;
	
	/**
	 * The game's attendance or null if it has yet to be played.
	 */
	private Integer attendence;

	/**
	 * The game's match report or null if it has yet to be played.
	 */
	private String matchReport;

	/**
	 * The game's television channel or null if it has not been televised.
	 */
	private String televisionChannel;
	

	/**
	 * The date tickets are available or null if is not known.
	 */
	private Date ticketsAvailable;
	
	/**
	 * True if game has been attended, false otherwise.
	 */
	private boolean attended;
	
	/**
	 * True if game is being played during the week, false otherwise.
	 */
	private boolean weekGame;
	

	/**
	 * True if game is being played at the weekend but not at 3pm, false otherwise.
	 */
	private boolean nonStandardWeekendGame;
	
	/**
	 * True if game is editable, false otherwise.
	 */
	private boolean enabled;
	
	/**
	 * Instantiates a new game view.
	 */
	public GameView() {
		super();
	}

	/**
	 * Instantiates a new game view.
	 * 
	 * @param id
	 *          the id
	 * @param competition
	 *          the competition
	 * @param location
	 *          the location
	 * @param opponents
	 *          the opponents
	 * @param season
	 *          the season
	 * @param datePlayed
	 *          the date played
	 * @param result
	 *          the result
	 * @param attendence
	 *          the attendence
	 * @param matchReport
	 *          the match report
	 * @param televisionChannel
	 *          the television channel
	 * @param ticketsAvailable
	 *          the tickets available
	 * @param attended
	 *          the attended
	 * @param weekGame
	 *          the week game
	 * @param nonStandardWeekendGame
	 *          the non standard weekend game
	 * @param enabled
	 *          the enabled
	 */
	public GameView(Integer id, Competition competition, Location location, String opponents, int season, Date datePlayed,
			String result, Integer attendence, String matchReport, String televisionChannel, Date ticketsAvailable, 
			boolean attended, boolean weekGame, boolean nonStandardWeekendGame, boolean enabled) {
		super();
		this.id = id;
		this.competition = competition;
		this.location = location;
		this.opponents = opponents;
		this.season = season;
		this.datePlayed = datePlayed;
		this.result = result;
		this.attendence = attendence;
		this.matchReport = matchReport;
		this.televisionChannel = televisionChannel;
		this.ticketsAvailable = ticketsAvailable;
		this.attended = attended;
		this.weekGame = weekGame;
		this.nonStandardWeekendGame = nonStandardWeekendGame;
		this.enabled = enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(GameView o) {
		return getDatePlayed().compareTo(o.getDatePlayed());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GameView) && compareTo((GameView) obj) == 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getDatePlayed().hashCode();
	}

	/**
	 * Gets the game's id.
	 * 
	 * @return the game's id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the game's id.
	 * 
	 * @param id
	 *          the new game's id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the game's {@link Competition}.
	 * 
	 * @return the game's {@link Competition}
	 */
	public Competition getCompetition() {
		return competition;
	}

	/**
	 * Sets the game's {@link Competition}.
	 * 
	 * @param competition
	 *          the new game's {@link Competition}
	 */
	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	/**
	 * Gets the game's {@link Location}.
	 * 
	 * @return the game's {@link Location}
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the game's {@link Location}.
	 * 
	 * @param location
	 *          the new game's {@link Location}
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Gets the game's opponents.
	 * 
	 * @return the game's opponents
	 */
	public String getOpponents() {
		return opponents;
	}

	/**
	 * Sets the game's opponents.
	 * 
	 * @param opponents
	 *          the new game's opponents
	 */
	public void setOpponents(String opponents) {
		this.opponents = opponents;
	}

	/**
	 * Gets the game's season.
	 * 
	 * @return the game's season
	 */
	public int getSeason() {
		return season;
	}

	/**
	 * Sets the game's season.
	 * 
	 * @param season
	 *          the new game's season
	 */
	public void setSeason(int season) {
		this.season = season;
	}

	/**
	 * Gets the date and time the game was played.
	 * 
	 * @return the date and time the game was played
	 */
	public Date getDatePlayed() {
		return datePlayed;
	}

	/**
	 * Sets the date and time the game was played.
	 * 
	 * @param datePlayed
	 *          the new date and time the game was played
	 */
	public void setDatePlayed(Date datePlayed) {
		this.datePlayed = datePlayed;
	}

	/**
	 * Gets the game's result or null if it has yet to be played.
	 * 
	 * @return the game's result or null if it has yet to be played
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Sets the game's result or null if it has yet to be played.
	 * 
	 * @param result
	 *          the new game's result or null if it has yet to be played
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Gets the game's attendance or null if it has yet to be played.
	 * 
	 * @return the game's attendance or null if it has yet to be played
	 */
	public Integer getAttendence() {
		return attendence;
	}

	/**
	 * Sets the game's attendance or null if it has yet to be played.
	 * 
	 * @param attendence
	 *          the new game's attendance or null if it has yet to be played
	 */
	public void setAttendence(Integer attendence) {
		this.attendence = attendence;
	}

	/**
	 * Gets the game's match report or null if it has yet to be played.
	 * 
	 * @return the game's match report or null if it has yet to be played
	 */
	public String getMatchReport() {
		return matchReport;
	}

	/**
	 * Sets the game's match report or null if it has yet to be played.
	 * 
	 * @param matchReport
	 *          the new game's match report or null if it has yet to be played
	 */
	public void setMatchReport(String matchReport) {
		this.matchReport = matchReport;
	}

	/**
	 * Gets the game's television channel or null if it has not been televised.
	 * 
	 * @return the game's television channel or null if it has not been televised
	 */
	public String getTelevisionChannel() {
		return televisionChannel;
	}

	/**
	 * Sets the game's television channel or null if it has not been televised.
	 * 
	 * @param televisionChannel
	 *          the new game's television channel or null if it has not been
	 *          televised
	 */
	public void setTelevisionChannel(String televisionChannel) {
		this.televisionChannel = televisionChannel;
	}

	/**
	 * Checks if is true if game has been attended, false otherwise.
	 * 
	 * @return the true if game has been attended, false otherwise
	 */
	public boolean isAttended() {
		return attended;
	}

	/**
	 * Sets the true if game has been attended, false otherwise.
	 * 
	 * @param attended
	 *          the new true if game has been attended, false otherwise
	 */
	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	/**
	 * Checks if is true if game is being played during the week, false
	 * otherwise.
	 * 
	 * @return the true if game is being played during the week, false
	 *         otherwise
	 */
	public boolean isWeekGame() {
		return weekGame;
	}

	/**
	 * Sets the true if game is being played during the week, false
	 * otherwise.
	 * 
	 * @param weekGame
	 *          the new true if game is being played during the week, false
	 *          otherwise
	 */
	public void setWeekGame(boolean weekGame) {
		this.weekGame = weekGame;
	}

	/**
	 * Checks if is true if game is being played at the weekend but not at
	 * 3pm, false otherwise.
	 * 
	 * @return the true if game is being played at the weekend but not at
	 *         3pm, false otherwise
	 */
	public boolean isNonStandardWeekendGame() {
		return nonStandardWeekendGame;
	}

	/**
	 * Sets the true if game is being played at the weekend but not at 3pm,
	 * false otherwise.
	 * 
	 * @param nonStandardWeekendGame
	 *          the new true if game is being played at the weekend but not
	 *          at 3pm, false otherwise
	 */
	public void setNonStandardWeekendGame(boolean nonStandardWeekendGame) {
		this.nonStandardWeekendGame = nonStandardWeekendGame;
	}

	/**
	 * Gets the date tickets are available or null if is not known.
	 * 
	 * @return the date tickets are available or null if is not known
	 */
	public Date getTicketsAvailable() {
		return ticketsAvailable;
	}

	/**
	 * Sets the date tickets are available or null if is not known.
	 * 
	 * @param ticketsAvailable
	 *          the new date tickets are available or null if is not known
	 */
	public void setTicketsAvailable(Date ticketsAvailable) {
		this.ticketsAvailable = ticketsAvailable;
	}

	/**
	 * Checks if is true if game is editable, false otherwise.
	 * 
	 * @return the true if game is editable, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the true if game is editable, false otherwise.
	 * 
	 * @param enabled
	 *          the new true if game is editable, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
