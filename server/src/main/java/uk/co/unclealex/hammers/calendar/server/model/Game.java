/**
 * Copyright 2010-2012 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Objects;


/**
 * A game is the main model in i_application. It encapsulates all possible
 * information for a given game.
 * 
 * @author alex
 * 
 */
@Entity
@Table(name = "game", uniqueConstraints = @UniqueConstraint(columnNames = { "competition", "location", "opponents",
		"season" }))
@XmlRootElement
public class Game implements HasIdentity, Comparable<Game> {

	/**
	 * The primary key of the game.
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
	 * The season the game was played in.
	 */
	private int i_season;

	/**
	 * The {@link DateTime} the game was played.
	 */
	private DateTime i_dateTimePlayed;

	/**
	 * The {@link DateTime} that Bondholder tickets went on sale.
	 */
	private DateTime i_dateTimeBondholdersAvailable;

	/**
	 * The {@link DateTime} that priority point tickets went on sale.
	 */
	private DateTime i_dateTimePriorityPointPostAvailable;

	/**
	 * The {@link DateTime} that season ticker holder tickets went on sale.
	 */
	private DateTime i_dateTimeSeasonTicketsAvailable;

	/**
	 * The {@link DateTime} that Academy members' tickets went on sale.
	 */
	private DateTime i_dateTimeAcademyMembersAvailable;

	/**
	 * The {@link DateTime} that tickets went on general sale.
	 */
	private DateTime i_dateTimeGeneralSaleAvailable;

	/**
	 * The game's result.
	 */
	private String i_result;
	
	/**
	 * The game's attendence.
	 */
	private Integer i_attendence;
	
	/**
	 * The game's match report.
	 */
	private String i_matchReport;
	
	/**
	 * The TV channel that showed the match.
	 */
	private String i_televisionChannel;

	/**
	 * True if the game has been marked as attended, false otherwise.
	 */
	private boolean i_attended;

	/**
	 * Default constructor.
	 */
	protected Game() {
		super();
	}

	/**
	 * Instantiates a new game.
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
	 * @param bondholdersAvailable
	 *          the bondholders available
	 * @param priorityPointPostAvailable
	 *          the priority point post available
	 * @param seasonTicketsAvailable
	 *          the season tickets available
	 * @param academyMembersAvailable
	 *          the academy members available
	 * @param generalSaleAvailable
	 *          the general sale available
	 * @param result
	 *          the result
	 * @param attendence
	 *          the attendence
	 * @param matchReport
	 *          the match report
	 * @param televisionChannel
	 *          the television channel
	 * @param attended
	 *          the attended
	 */
	public Game(Integer id, Competition competition, Location location, String opponents, int season,
			DateTime datePlayed, DateTime bondholdersAvailable, DateTime priorityPointPostAvailable,
			DateTime seasonTicketsAvailable, DateTime academyMembersAvailable, DateTime generalSaleAvailable, String result,
			Integer attendence, String matchReport, String televisionChannel, boolean attended) {
		super();
		i_id = id;
		i_competition = competition;
		i_location = location;
		i_opponents = opponents;
		i_season = season;
		i_dateTimePlayed = datePlayed;
		i_dateTimeBondholdersAvailable = bondholdersAvailable;
		i_dateTimePriorityPointPostAvailable = priorityPointPostAvailable;
		i_dateTimeSeasonTicketsAvailable = seasonTicketsAvailable;
		i_dateTimeAcademyMembersAvailable = academyMembersAvailable;
		i_dateTimeGeneralSaleAvailable = generalSaleAvailable;
		i_result = result;
		i_attendence = attendence;
		i_matchReport = matchReport;
		i_televisionChannel = televisionChannel;
		i_attended = attended;
	}

	/**
	 * Get the {@link GameKey} that uniquely identifies i_game.
	 * @return The {@link GameKey} that uniquely identifies i_game
	 */
	@Transient
	public GameKey getGameKey() {
		return new GameKey(getCompetition(), getLocation(), getOpponents(), getSeason());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Game && compareTo((Game) obj) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Game o) {
		return getGameKey().compareTo(o.getGameKey());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(getCompetition(), getLocation(), getOpponents(), getSeason());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("[Opponents: %s, Location: %s, Competition: %s, Season %d]", getOpponents(), getLocation(),
				getCompetition(), getSeason());
	}

	/**
	 * {@inheritDoc}
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return i_id;
	}

	/**
	 * Sets the primary key of the game.
	 * 
	 * @param id
	 *          the new primary key of the game
	 */
	public void setId(Integer id) {
		i_id = id;
	}

	/**
	 * Gets the game's {@link Competition}.
	 * 
	 * @return the game's {@link Competition}
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
	@Column(nullable = false)
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
	 * Gets the season the game was played in.
	 * 
	 * @return the season the game was played in
	 */
	@Column(nullable = false)
	public int getSeason() {
		return i_season;
	}

	/**
	 * Sets the season the game was played in.
	 * 
	 * @param season
	 *          the new season the game was played in
	 */
	public void setSeason(int season) {
		i_season = season;
	}

	/**
	 * Gets the {@link DateTime} the game was played.
	 * 
	 * @return the {@link DateTime} the game was played
	 */
	public DateTime getDateTimePlayed() {
		return i_dateTimePlayed;
	}

	/**
	 * Sets the {@link DateTime} the game was played.
	 * 
	 * @param datePlayed
	 *          the new {@link DateTime} the game was played
	 */
	@Column(nullable = false)
	public void setDateTimePlayed(DateTime datePlayed) {
		i_dateTimePlayed = datePlayed;
	}

	/**
	 * Gets the game's result.
	 * 
	 * @return the game's result
	 */
	public String getResult() {
		return i_result;
	}

	/**
	 * Sets the game's result.
	 * 
	 * @param result
	 *          the new game's result
	 */
	public void setResult(String result) {
		i_result = result;
	}

	/**
	 * Gets the game's attendence.
	 * 
	 * @return the game's attendence
	 */
	public Integer getAttendence() {
		return i_attendence;
	}

	/**
	 * Sets the game's attendence.
	 * 
	 * @param attendence
	 *          the new game's attendence
	 */
	public void setAttendence(Integer attendence) {
		i_attendence = attendence;
	}

	/**
	 * Gets the game's match report.
	 * 
	 * @return the game's match report
	 */
	public String getMatchReport() {
		return i_matchReport;
	}

	/**
	 * Sets the game's match report.
	 * 
	 * @param matchReport
	 *          the new game's match report
	 */
	public void setMatchReport(String matchReport) {
		i_matchReport = matchReport;
	}

	/**
	 * Checks if is true if the game has been marked as attended, false otherwise.
	 * 
	 * @return the true if the game has been marked as attended, false otherwise
	 */
	public boolean isAttended() {
		return i_attended;
	}

	/**
	 * Sets the true if the game has been marked as attended, false otherwise.
	 * 
	 * @param attended
	 *          the new true if the game has been marked as attended, false
	 *          otherwise
	 */
	public void setAttended(boolean attended) {
		i_attended = attended;
	}

	/**
	 * Gets the {@link DateTime} that season ticker holder tickets went on sale.
	 * 
	 * @return the {@link DateTime} that season ticker holder tickets went on sale
	 */
	public DateTime getDateTimeSeasonTicketsAvailable() {
		return i_dateTimeSeasonTicketsAvailable;
	}

	/**
	 * Sets the {@link DateTime} that season ticker holder tickets went on sale.
	 * 
	 * @param seasonTicketsAvailable
	 *          the new {@link DateTime} that season ticker holder tickets went on
	 *          sale
	 */
	public void setDateTimeSeasonTicketsAvailable(DateTime seasonTicketsAvailable) {
		i_dateTimeSeasonTicketsAvailable = seasonTicketsAvailable;
	}

	/**
	 * Gets the {@link DateTime} that Bondholder tickets went on sale.
	 * 
	 * @return the {@link DateTime} that Bondholder tickets went on sale
	 */
	public DateTime getDateTimeBondholdersAvailable() {
		return i_dateTimeBondholdersAvailable;
	}

	/**
	 * Sets the {@link DateTime} that Bondholder tickets went on sale.
	 * 
	 * @param bondholdersAvailable
	 *          the new {@link DateTime} that Bondholder tickets went on sale
	 */
	public void setDateTimeBondholdersAvailable(DateTime bondholdersAvailable) {
		i_dateTimeBondholdersAvailable = bondholdersAvailable;
	}

	/**
	 * Gets the {@link DateTime} that priority point tickets went on sale.
	 * 
	 * @return the {@link DateTime} that priority point tickets went on sale
	 */
	public DateTime getDateTimePriorityPointPostAvailable() {
		return i_dateTimePriorityPointPostAvailable;
	}

	/**
	 * Sets the {@link DateTime} that priority point tickets went on sale.
	 * 
	 * @param priorityPointPostAvailable
	 *          the new {@link DateTime} that priority point tickets went on sale
	 */
	public void setDateTimePriorityPointPostAvailable(DateTime priorityPointPostAvailable) {
		i_dateTimePriorityPointPostAvailable = priorityPointPostAvailable;
	}

	/**
	 * Gets the {@link DateTime} that Academy members' tickets went on sale.
	 * 
	 * @return the {@link DateTime} that Academy members' tickets went on sale
	 */
	public DateTime getDateTimeAcademyMembersAvailable() {
		return i_dateTimeAcademyMembersAvailable;
	}

	/**
	 * Sets the {@link DateTime} that Academy members' tickets went on sale.
	 * 
	 * @param academyMembersAvailable
	 *          the new {@link DateTime} that Academy members' tickets went on
	 *          sale
	 */
	public void setDateTimeAcademyMembersAvailable(DateTime academyMembersAvailable) {
		i_dateTimeAcademyMembersAvailable = academyMembersAvailable;
	}

	/**
	 * Gets the {@link DateTime} that tickets went on general sale.
	 * 
	 * @return the {@link DateTime} that tickets went on general sale
	 */
	public DateTime getDateTimeGeneralSaleAvailable() {
		return i_dateTimeGeneralSaleAvailable;
	}

	/**
	 * Sets the {@link DateTime} that tickets went on general sale.
	 * 
	 * @param generalSaleAvailable
	 *          the new {@link DateTime} that tickets went on general sale
	 */
	public void setDateTimeGeneralSaleAvailable(DateTime generalSaleAvailable) {
		i_dateTimeGeneralSaleAvailable = generalSaleAvailable;
	}

	/**
	 * Gets the TV channel that showed the match.
	 * 
	 * @return the TV channel that showed the match
	 */
	public String getTelevisionChannel() {
		return i_televisionChannel;
	}

	/**
	 * Sets the TV channel that showed the match.
	 * 
	 * @param televisionChannel
	 *          the new TV channel that showed the match
	 */
	public void setTelevisionChannel(String televisionChannel) {
		i_televisionChannel = televisionChannel;
	}
}
