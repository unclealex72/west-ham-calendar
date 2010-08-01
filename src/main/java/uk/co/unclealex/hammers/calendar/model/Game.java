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
package uk.co.unclealex.hammers.calendar.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(
	name="game",
	uniqueConstraints = 
		@UniqueConstraint(columnNames = {"competition", "location", "opponents", "season"}))
public class Game implements Serializable {

	private Integer i_id;
	
	private Competition i_competition;
	private Location i_location;
	private String i_opponents;
	private int i_season;

	private Date i_datePlayed;
	private Date i_bondholdersAvailable;
	private Date i_priorityPointPostAvailable;
	private Date i_seasonTicketsAvailable;
	private Date i_academyMembersAvailable;
	private Date i_generalSaleAvailable;

	private String i_result;
	private Integer i_attendence;
	private String i_matchReport;
	private String i_televisionChannel;
	
	private boolean i_attended;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Game)) {
			return false;			
		}
		Game other = (Game) obj;
		return 
			getCompetition().equals(other.getCompetition()) && 
			getLocation().equals(other.getLocation()) && 
			getOpponents().equals(other.getOpponents());
	}
	
	@Override
	public int hashCode() {
		return 
			ObjectUtils.hashCode(getCompetition()) + 
			3 * ObjectUtils.hashCode(getLocation()) + 
			5 * ObjectUtils.hashCode(getOpponents());
	}

	@Id @GeneratedValue
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public Competition getCompetition() {
		return i_competition;
	}

	public void setCompetition(Competition competition) {
		i_competition = competition;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public Location getLocation() {
		return i_location;
	}

	public void setLocation(Location location) {
		i_location = location;
	}

	@Column(nullable=false)
	public String getOpponents() {
		return i_opponents;
	}

	public void setOpponents(String opponents) {
		i_opponents = opponents;
	}

	@Column(nullable=false)
	public int getSeason() {
		return i_season;
	}

	public void setSeason(int season) {
		i_season = season;
	}

	public Date getDatePlayed() {
		return i_datePlayed;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public void setDatePlayed(Date datePlayed) {
		i_datePlayed = datePlayed;
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

	public Date getSeasonTicketsAvailable() {
		return i_seasonTicketsAvailable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public void setSeasonTicketsAvailable(Date seasonTicketsAvailable) {
		i_seasonTicketsAvailable = seasonTicketsAvailable;
	}

	public Date getBondholdersAvailable() {
		return i_bondholdersAvailable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public void setBondholdersAvailable(Date bondholdersAvailable) {
		i_bondholdersAvailable = bondholdersAvailable;
	}

	public Date getPriorityPointPostAvailable() {
		return i_priorityPointPostAvailable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public void setPriorityPointPostAvailable(Date priorityPointPostAvailable) {
		i_priorityPointPostAvailable = priorityPointPostAvailable;
	}

	public Date getAcademyMembersAvailable() {
		return i_academyMembersAvailable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public void setAcademyMembersAvailable(Date academyMembersAvailable) {
		i_academyMembersAvailable = academyMembersAvailable;
	}

	public Date getGeneralSaleAvailable() {
		return i_generalSaleAvailable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public void setGeneralSaleAvailable(Date generalSaleAvailable) {
		i_generalSaleAvailable = generalSaleAvailable;
	}

	public String getTelevisionChannel() {
		return i_televisionChannel;
	}

	public void setTelevisionChannel(String televisionChannel) {
		i_televisionChannel = televisionChannel;
	}	
}
