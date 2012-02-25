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

@Entity
@Table(name = "game", uniqueConstraints = @UniqueConstraint(columnNames = { "competition", "location", "opponents",
		"season" }))
@XmlRootElement
public class Game implements HasIdentity, Comparable<Game> {

	private Integer i_id;

	private Competition i_competition;
	private Location i_location;
	private String i_opponents;
	private int i_season;

	private DateTime i_dateTimePlayed;
	private DateTime i_dateTimeBondholdersAvailable;
	private DateTime i_dateTimePriorityPointPostAvailable;
	private DateTime i_dateTimeSeasonTicketsAvailable;
	private DateTime i_dateTimeAcademyMembersAvailable;
	private DateTime i_dateTimeGeneralSaleAvailable;

	private String i_result;
	private Integer i_attendence;
	private String i_matchReport;
	private String i_televisionChannel;

	private boolean i_attended;


	/**
	 * Default constructor.
	 */
	protected Game() {
		super();
	}

	/**
	 * 
	 * @param id
	 * @param competition
	 * @param location
	 * @param opponents
	 * @param season
	 * @param datePlayed
	 * @param bondholdersAvailable
	 * @param priorityPointPostAvailable
	 * @param seasonTicketsAvailable
	 * @param academyMembersAvailable
	 * @param generalSaleAvailable
	 * @param result
	 * @param attendence
	 * @param matchReport
	 * @param televisionChannel
	 * @param attended
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


	@Transient
	public GameKey getGameKey() {
		return new GameKey(getCompetition(), getLocation(), getOpponents(), getSeason());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Game && compareTo((Game) obj) == 0;
	}

	@Override
	public int compareTo(Game o) {
		return getGameKey().compareTo(o.getGameKey());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getCompetition(), getLocation(), getOpponents(), getSeason());
	}

	@Override
	public String toString() {
		return String.format("[Opponents: %s, Location: %s, Competition: %s, Season %d]", getOpponents(), getLocation(),
				getCompetition(), getSeason());
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Competition getCompetition() {
		return i_competition;
	}

	public void setCompetition(Competition competition) {
		i_competition = competition;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Location getLocation() {
		return i_location;
	}

	public void setLocation(Location location) {
		i_location = location;
	}

	@Column(nullable = false)
	public String getOpponents() {
		return i_opponents;
	}

	public void setOpponents(String opponents) {
		i_opponents = opponents;
	}

	@Column(nullable = false)
	public int getSeason() {
		return i_season;
	}

	public void setSeason(int season) {
		i_season = season;
	}

	public DateTime getDateTimePlayed() {
		return i_dateTimePlayed;
	}

	@Column(nullable = false)
	public void setDateTimePlayed(DateTime datePlayed) {
		i_dateTimePlayed = datePlayed;
	}

	public String getResult() {
		return i_result;
	}

	public void setResult(String result) {
		i_result = result;
	}

	public Integer getAttendence() {
		return i_attendence;
	}

	public void setAttendence(Integer attendence) {
		i_attendence = attendence;
	}

	public String getMatchReport() {
		return i_matchReport;
	}

	public void setMatchReport(String matchReport) {
		i_matchReport = matchReport;
	}

	public boolean isAttended() {
		return i_attended;
	}

	public void setAttended(boolean attended) {
		i_attended = attended;
	}

	public DateTime getDateTimeSeasonTicketsAvailable() {
		return i_dateTimeSeasonTicketsAvailable;
	}

	public void setDateTimeSeasonTicketsAvailable(DateTime seasonTicketsAvailable) {
		i_dateTimeSeasonTicketsAvailable = seasonTicketsAvailable;
	}

	public DateTime getDateTimeBondholdersAvailable() {
		return i_dateTimeBondholdersAvailable;
	}

	public void setDateTimeBondholdersAvailable(DateTime bondholdersAvailable) {
		i_dateTimeBondholdersAvailable = bondholdersAvailable;
	}

	public DateTime getDateTimePriorityPointPostAvailable() {
		return i_dateTimePriorityPointPostAvailable;
	}

	public void setDateTimePriorityPointPostAvailable(DateTime priorityPointPostAvailable) {
		i_dateTimePriorityPointPostAvailable = priorityPointPostAvailable;
	}

	public DateTime getDateTimeAcademyMembersAvailable() {
		return i_dateTimeAcademyMembersAvailable;
	}

	public void setDateTimeAcademyMembersAvailable(DateTime academyMembersAvailable) {
		i_dateTimeAcademyMembersAvailable = academyMembersAvailable;
	}

	public DateTime getDateTimeGeneralSaleAvailable() {
		return i_dateTimeGeneralSaleAvailable;
	}

	public void setDateTimeGeneralSaleAvailable(DateTime generalSaleAvailable) {
		i_dateTimeGeneralSaleAvailable = generalSaleAvailable;
	}

	public String getTelevisionChannel() {
		return i_televisionChannel;
	}

	public void setTelevisionChannel(String televisionChannel) {
		i_televisionChannel = televisionChannel;
	}
}
