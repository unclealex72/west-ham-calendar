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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;
import java.util.Date;

public class GameView implements Serializable, Comparable<GameView> {

	private Integer i_id;
	
	private Competition i_competition;
	private Location i_location;
	private String i_opponents;
	private int i_season;

	private Date i_datePlayed;

	private String i_result;
	private Integer i_attendence;
	private String i_matchReport;
	private String i_televisionChannel;
	private Date i_ticketsAvailable;
	
	private boolean i_attended;
	private boolean i_weekGame;
	private boolean i_nonStandardWeekendGame;
	private boolean i_enabled;
	
	public GameView() {
		super();
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GameView o) {
		return getDatePlayed().compareTo(o.getDatePlayed());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GameView) && compareTo((GameView) obj) == 0;
	}
	
	@Override
	public int hashCode() {
		return getDatePlayed().hashCode();
	}

	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}

	public Competition getCompetition() {
		return i_competition;
	}

	public void setCompetition(Competition competition) {
		i_competition = competition;
	}

	public Location getLocation() {
		return i_location;
	}

	public void setLocation(Location location) {
		i_location = location;
	}

	public String getOpponents() {
		return i_opponents;
	}

	public void setOpponents(String opponents) {
		i_opponents = opponents;
	}

	public int getSeason() {
		return i_season;
	}

	public void setSeason(int season) {
		i_season = season;
	}

	public Date getDatePlayed() {
		return i_datePlayed;
	}

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

	public String getTelevisionChannel() {
		return i_televisionChannel;
	}

	public void setTelevisionChannel(String televisionChannel) {
		i_televisionChannel = televisionChannel;
	}

	public boolean isAttended() {
		return i_attended;
	}

	public void setAttended(boolean attended) {
		i_attended = attended;
	}

	public boolean isWeekGame() {
		return i_weekGame;
	}

	public void setWeekGame(boolean weekGame) {
		i_weekGame = weekGame;
	}

	public boolean isNonStandardWeekendGame() {
		return i_nonStandardWeekendGame;
	}

	public void setNonStandardWeekendGame(boolean nonStandardWeekendGame) {
		i_nonStandardWeekendGame = nonStandardWeekendGame;
	}

	public Date getTicketsAvailable() {
		return i_ticketsAvailable;
	}

	public void setTicketsAvailable(Date ticketsAvailable) {
		i_ticketsAvailable = ticketsAvailable;
	}

	public boolean isEnabled() {
		return i_enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		i_enabled = enabled;
	}
}
