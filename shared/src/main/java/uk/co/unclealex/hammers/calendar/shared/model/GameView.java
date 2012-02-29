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
	private Integer i_id;
	
	/**
	 * The game's {@link Competition}.
	 */
	private Competition i_competition;
	
	/**
	 * The game's {@link Location}.
	 */
	private Location i_location;
	
	/**
	 * The game's opponents.
	 */
	private String i_opponents;
	
	/**
	 * The game's season.
	 */
	private int i_season;

	/**
	 * The date and time the game was played.
	 */
	private Date i_datePlayed;

	/**
	 * The game's result or null if it has yet to be played.
	 */
	private String i_result;
	
	/**
	 * The game's attendance or null if it has yet to be played.
	 */
	private Integer i_attendence;

	/**
	 * The game's match report or null if it has yet to be played.
	 */
	private String i_matchReport;

	/**
	 * The game's television channel or null if it has not been televised.
	 */
	private String i_televisionChannel;
	

	/**
	 * The date tickets are available or null if i_is not known.
	 */
	private Date i_ticketsAvailable;
	
	/**
	 * True if i_game has been attended, false otherwise.
	 */
	private boolean i_attended;
	
	/**
	 * True if i_game is being played during the week, false otherwise.
	 */
	private boolean i_weekGame;
	

	/**
	 * True if i_game is being played at the weekend but not at 3pm, false otherwise.
	 */
	private boolean i_nonStandardWeekendGame;
	
	/**
	 * True if i_game is editable, false otherwise.
	 */
	private boolean i_enabled;
	
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
		i_id = id;
		i_competition = competition;
		i_location = location;
		i_opponents = opponents;
		i_season = season;
		i_datePlayed = datePlayed;
		i_result = result;
		i_attendence = attendence;
		i_matchReport = matchReport;
		i_televisionChannel = televisionChannel;
		i_ticketsAvailable = ticketsAvailable;
		i_attended = attended;
		i_weekGame = weekGame;
		i_nonStandardWeekendGame = nonStandardWeekendGame;
		i_enabled = enabled;
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
		return i_id;
	}

	/**
	 * Sets the game's id.
	 * 
	 * @param id
	 *          the new game's id
	 */
	public void setId(Integer id) {
		i_id = id;
	}

	/**
	 * Gets the game's {@link Competition}.
	 * 
	 * @return the game's {@link Competition}
	 */
	public Competition getCompetition() {
		return i_competition;
	}

	/**
	 * Sets the game's {@link Competition}.
	 * 
	 * @param competition
	 *          the new game's {@link Competition}
	 */
	public void setCompetition(Competition competition) {
		i_competition = competition;
	}

	/**
	 * Gets the game's {@link Location}.
	 * 
	 * @return the game's {@link Location}
	 */
	public Location getLocation() {
		return i_location;
	}

	/**
	 * Sets the game's {@link Location}.
	 * 
	 * @param location
	 *          the new game's {@link Location}
	 */
	public void setLocation(Location location) {
		i_location = location;
	}

	/**
	 * Gets the game's opponents.
	 * 
	 * @return the game's opponents
	 */
	public String getOpponents() {
		return i_opponents;
	}

	/**
	 * Sets the game's opponents.
	 * 
	 * @param opponents
	 *          the new game's opponents
	 */
	public void setOpponents(String opponents) {
		i_opponents = opponents;
	}

	/**
	 * Gets the game's season.
	 * 
	 * @return the game's season
	 */
	public int getSeason() {
		return i_season;
	}

	/**
	 * Sets the game's season.
	 * 
	 * @param season
	 *          the new game's season
	 */
	public void setSeason(int season) {
		i_season = season;
	}

	/**
	 * Gets the date and time the game was played.
	 * 
	 * @return the date and time the game was played
	 */
	public Date getDatePlayed() {
		return i_datePlayed;
	}

	/**
	 * Sets the date and time the game was played.
	 * 
	 * @param datePlayed
	 *          the new date and time the game was played
	 */
	public void setDatePlayed(Date datePlayed) {
		i_datePlayed = datePlayed;
	}

	/**
	 * Gets the game's result or null if it has yet to be played.
	 * 
	 * @return the game's result or null if it has yet to be played
	 */
	public String getResult() {
		return i_result;
	}

	/**
	 * Sets the game's result or null if it has yet to be played.
	 * 
	 * @param result
	 *          the new game's result or null if it has yet to be played
	 */
	public void setResult(String result) {
		i_result = result;
	}

	/**
	 * Gets the game's attendance or null if it has yet to be played.
	 * 
	 * @return the game's attendance or null if it has yet to be played
	 */
	public Integer getAttendence() {
		return i_attendence;
	}

	/**
	 * Sets the game's attendance or null if it has yet to be played.
	 * 
	 * @param attendence
	 *          the new game's attendance or null if it has yet to be played
	 */
	public void setAttendence(Integer attendence) {
		i_attendence = attendence;
	}

	/**
	 * Gets the game's match report or null if it has yet to be played.
	 * 
	 * @return the game's match report or null if it has yet to be played
	 */
	public String getMatchReport() {
		return i_matchReport;
	}

	/**
	 * Sets the game's match report or null if it has yet to be played.
	 * 
	 * @param matchReport
	 *          the new game's match report or null if it has yet to be played
	 */
	public void setMatchReport(String matchReport) {
		i_matchReport = matchReport;
	}

	/**
	 * Gets the game's television channel or null if it has not been televised.
	 * 
	 * @return the game's television channel or null if it has not been televised
	 */
	public String getTelevisionChannel() {
		return i_televisionChannel;
	}

	/**
	 * Sets the game's television channel or null if it has not been televised.
	 * 
	 * @param televisionChannel
	 *          the new game's television channel or null if it has not been
	 *          televised
	 */
	public void setTelevisionChannel(String televisionChannel) {
		i_televisionChannel = televisionChannel;
	}

	/**
	 * Checks if is true if i_game has been attended, false otherwise.
	 * 
	 * @return the true if i_game has been attended, false otherwise
	 */
	public boolean isAttended() {
		return i_attended;
	}

	/**
	 * Sets the true if i_game has been attended, false otherwise.
	 * 
	 * @param attended
	 *          the new true if i_game has been attended, false otherwise
	 */
	public void setAttended(boolean attended) {
		i_attended = attended;
	}

	/**
	 * Checks if is true if i_game is being played during the week, false
	 * otherwise.
	 * 
	 * @return the true if i_game is being played during the week, false
	 *         otherwise
	 */
	public boolean isWeekGame() {
		return i_weekGame;
	}

	/**
	 * Sets the true if i_game is being played during the week, false
	 * otherwise.
	 * 
	 * @param weekGame
	 *          the new true if i_game is being played during the week, false
	 *          otherwise
	 */
	public void setWeekGame(boolean weekGame) {
		i_weekGame = weekGame;
	}

	/**
	 * Checks if is true if i_game is being played at the weekend but not at
	 * 3pm, false otherwise.
	 * 
	 * @return the true if i_game is being played at the weekend but not at
	 *         3pm, false otherwise
	 */
	public boolean isNonStandardWeekendGame() {
		return i_nonStandardWeekendGame;
	}

	/**
	 * Sets the true if i_game is being played at the weekend but not at 3pm,
	 * false otherwise.
	 * 
	 * @param nonStandardWeekendGame
	 *          the new true if i_game is being played at the weekend but not
	 *          at 3pm, false otherwise
	 */
	public void setNonStandardWeekendGame(boolean nonStandardWeekendGame) {
		i_nonStandardWeekendGame = nonStandardWeekendGame;
	}

	/**
	 * Gets the date tickets are available or null if i_is not known.
	 * 
	 * @return the date tickets are available or null if i_is not known
	 */
	public Date getTicketsAvailable() {
		return i_ticketsAvailable;
	}

	/**
	 * Sets the date tickets are available or null if i_is not known.
	 * 
	 * @param ticketsAvailable
	 *          the new date tickets are available or null if i_is not known
	 */
	public void setTicketsAvailable(Date ticketsAvailable) {
		i_ticketsAvailable = ticketsAvailable;
	}

	/**
	 * Checks if is true if i_game is editable, false otherwise.
	 * 
	 * @return the true if i_game is editable, false otherwise
	 */
	public boolean isEnabled() {
		return i_enabled;
	}

	/**
	 * Sets the true if i_game is editable, false otherwise.
	 * 
	 * @param enabled
	 *          the new true if i_game is editable, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		i_enabled = enabled;
	}
}
