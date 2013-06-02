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


import com.google.common.base.Objects;

/**
 * A game is the main model in application. It encapsulates all possible
 * information for a given game.
 * 
 * @author alex
 * 
 */
@Entity
@Table(name = "game", uniqueConstraints = @UniqueConstraint(columnNames = {
    "competition",
    "location",
    "opponents",
    "season" }))
@XmlRootElement
public class Game implements HasIdentity, Comparable<Game> {

  /**
   * The primary key of the game.
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
   * The season the game was played in.
   */
  private int season;

  /**
   * The {@link DateTime} the game was played.
   */
  private DateTime dateTimePlayed;

  /**
   * The {@link DateTime} that Bondholder tickets went on sale.
   */
  private DateTime dateTimeBondholdersAvailable;

  /**
   * The {@link DateTime} that priority point tickets went on sale.
   */
  private DateTime dateTimePriorityPointPostAvailable;

  /**
   * The {@link DateTime} that season ticker holder tickets went on sale.
   */
  private DateTime dateTimeSeasonTicketsAvailable;

  /**
   * The {@link DateTime} that Academy members' tickets went on sale.
   */
  private DateTime dateTimeAcademyMembersAvailable;

  /**
   * The {@link DateTime} that tickets went on general sale.
   */
  private DateTime dateTimeGeneralSaleAvailable;

  /**
   * The game's result.
   */
  private String result;

  /**
   * The game's attendence.
   */
  private Integer attendence;

  /**
   * The game's match report.
   */
  private String matchReport;

  /**
   * The TV channel that showed the match.
   */
  private String televisionChannel;

  /**
   * True if the game has been marked as attended, false otherwise.
   */
  private boolean attended;

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
  public Game(
      final Integer id,
      final Competition competition,
      final Location location,
      final String opponents,
      final int season,
      final DateTime datePlayed,
      final DateTime bondholdersAvailable,
      final DateTime priorityPointPostAvailable,
      final DateTime seasonTicketsAvailable,
      final DateTime academyMembersAvailable,
      final DateTime generalSaleAvailable,
      final String result,
      final Integer attendence,
      final String matchReport,
      final String televisionChannel,
      final boolean attended) {
    super();
    this.id = id;
    this.competition = competition;
    this.location = location;
    this.opponents = opponents;
    this.season = season;
    dateTimePlayed = datePlayed;
    dateTimeBondholdersAvailable = bondholdersAvailable;
    dateTimePriorityPointPostAvailable = priorityPointPostAvailable;
    dateTimeSeasonTicketsAvailable = seasonTicketsAvailable;
    dateTimeAcademyMembersAvailable = academyMembersAvailable;
    dateTimeGeneralSaleAvailable = generalSaleAvailable;
    this.result = result;
    this.attendence = attendence;
    this.matchReport = matchReport;
    this.televisionChannel = televisionChannel;
    this.attended = attended;
  }

  public Game(final Competition competition, final Location location, final String opponents, final int season) {
    super();
    this.competition = competition;
    this.location = location;
    this.opponents = opponents;
    this.season = season;
  }

  /**
   * Get the {@link GameKey} that uniquely identifies game.
   * 
   * @return The {@link GameKey} that uniquely identifies game
   */
  @Transient
  public GameKey getGameKey() {
    return new GameKey(getCompetition(), getLocation(), getOpponents(), getSeason());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return obj instanceof Game && compareTo((Game) obj) == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Game o) {
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
    return String.format(
        "[Opponents: %s, Location: %s, Competition: %s, Season %d]",
        getOpponents(),
        getLocation(),
        getCompetition(),
        getSeason());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Id
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  /**
   * Sets the primary key of the game.
   * 
   * @param id
   *          the new primary key of the game
   */
  public void setId(final Integer id) {
    this.id = id;
  }

  /**
   * Gets the game's {@link Competition}.
   * 
   * @return the game's {@link Competition}
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public Competition getCompetition() {
    return competition;
  }

  /**
   * Sets the game's {@link Competition}.
   * 
   * @param competition
   *          the new game's {@link Competition}
   */
  public void setCompetition(final Competition competition) {
    this.competition = competition;
  }

  /**
   * Gets the game's {@link Location}.
   * 
   * @return the game's {@link Location}
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public Location getLocation() {
    return location;
  }

  /**
   * Sets the game's {@link Location}.
   * 
   * @param location
   *          the new game's {@link Location}
   */
  public void setLocation(final Location location) {
    this.location = location;
  }

  /**
   * Gets the game's opponents.
   * 
   * @return the game's opponents
   */
  @Column(nullable = false)
  public String getOpponents() {
    return opponents;
  }

  /**
   * Sets the game's opponents.
   * 
   * @param opponents
   *          the new game's opponents
   */
  public void setOpponents(final String opponents) {
    this.opponents = opponents;
  }

  /**
   * Gets the season the game was played in.
   * 
   * @return the season the game was played in
   */
  @Column(nullable = false)
  public int getSeason() {
    return season;
  }

  /**
   * Sets the season the game was played in.
   * 
   * @param season
   *          the new season the game was played in
   */
  public void setSeason(final int season) {
    this.season = season;
  }

  /**
   * Gets the {@link DateTime} the game was played.
   * 
   * @return the {@link DateTime} the game was played
   */
  public DateTime getDateTimePlayed() {
    return dateTimePlayed;
  }

  /**
   * Sets the {@link DateTime} the game was played.
   * 
   * @param datePlayed
   *          the new {@link DateTime} the game was played
   */
  @Column(nullable = false)
  public void setDateTimePlayed(final DateTime datePlayed) {
    dateTimePlayed = datePlayed;
  }

  /**
   * Gets the game's result.
   * 
   * @return the game's result
   */
  public String getResult() {
    return result;
  }

  /**
   * Sets the game's result.
   * 
   * @param result
   *          the new game's result
   */
  public void setResult(final String result) {
    this.result = result;
  }

  /**
   * Gets the game's attendence.
   * 
   * @return the game's attendence
   */
  public Integer getAttendence() {
    return attendence;
  }

  /**
   * Sets the game's attendence.
   * 
   * @param attendence
   *          the new game's attendence
   */
  public void setAttendence(final Integer attendence) {
    this.attendence = attendence;
  }

  /**
   * Gets the game's match report.
   * 
   * @return the game's match report
   */
  public String getMatchReport() {
    return matchReport;
  }

  /**
   * Sets the game's match report.
   * 
   * @param matchReport
   *          the new game's match report
   */
  public void setMatchReport(final String matchReport) {
    this.matchReport = matchReport;
  }

  /**
   * Checks if is true if the game has been marked as attended, false otherwise.
   * 
   * @return the true if the game has been marked as attended, false otherwise
   */
  public boolean isAttended() {
    return attended;
  }

  /**
   * Sets the true if the game has been marked as attended, false otherwise.
   * 
   * @param attended
   *          the new true if the game has been marked as attended, false
   *          otherwise
   */
  public void setAttended(final boolean attended) {
    this.attended = attended;
  }

  /**
   * Gets the {@link DateTime} that season ticker holder tickets went on sale.
   * 
   * @return the {@link DateTime} that season ticker holder tickets went on sale
   */
  public DateTime getDateTimeSeasonTicketsAvailable() {
    return dateTimeSeasonTicketsAvailable;
  }

  /**
   * Sets the {@link DateTime} that season ticker holder tickets went on sale.
   * 
   * @param seasonTicketsAvailable
   *          the new {@link DateTime} that season ticker holder tickets went on
   *          sale
   */
  public void setDateTimeSeasonTicketsAvailable(final DateTime seasonTicketsAvailable) {
    dateTimeSeasonTicketsAvailable = seasonTicketsAvailable;
  }

  /**
   * Gets the {@link DateTime} that Bondholder tickets went on sale.
   * 
   * @return the {@link DateTime} that Bondholder tickets went on sale
   */
  public DateTime getDateTimeBondholdersAvailable() {
    return dateTimeBondholdersAvailable;
  }

  /**
   * Sets the {@link DateTime} that Bondholder tickets went on sale.
   * 
   * @param bondholdersAvailable
   *          the new {@link DateTime} that Bondholder tickets went on sale
   */
  public void setDateTimeBondholdersAvailable(final DateTime bondholdersAvailable) {
    dateTimeBondholdersAvailable = bondholdersAvailable;
  }

  /**
   * Gets the {@link DateTime} that priority point tickets went on sale.
   * 
   * @return the {@link DateTime} that priority point tickets went on sale
   */
  public DateTime getDateTimePriorityPointPostAvailable() {
    return dateTimePriorityPointPostAvailable;
  }

  /**
   * Sets the {@link DateTime} that priority point tickets went on sale.
   * 
   * @param priorityPointPostAvailable
   *          the new {@link DateTime} that priority point tickets went on sale
   */
  public void setDateTimePriorityPointPostAvailable(final DateTime priorityPointPostAvailable) {
    dateTimePriorityPointPostAvailable = priorityPointPostAvailable;
  }

  /**
   * Gets the {@link DateTime} that Academy members' tickets went on sale.
   * 
   * @return the {@link DateTime} that Academy members' tickets went on sale
   */
  public DateTime getDateTimeAcademyMembersAvailable() {
    return dateTimeAcademyMembersAvailable;
  }

  /**
   * Sets the {@link DateTime} that Academy members' tickets went on sale.
   * 
   * @param academyMembersAvailable
   *          the new {@link DateTime} that Academy members' tickets went on
   *          sale
   */
  public void setDateTimeAcademyMembersAvailable(final DateTime academyMembersAvailable) {
    dateTimeAcademyMembersAvailable = academyMembersAvailable;
  }

  /**
   * Gets the {@link DateTime} that tickets went on general sale.
   * 
   * @return the {@link DateTime} that tickets went on general sale
   */
  public DateTime getDateTimeGeneralSaleAvailable() {
    return dateTimeGeneralSaleAvailable;
  }

  /**
   * Sets the {@link DateTime} that tickets went on general sale.
   * 
   * @param generalSaleAvailable
   *          the new {@link DateTime} that tickets went on general sale
   */
  public void setDateTimeGeneralSaleAvailable(final DateTime generalSaleAvailable) {
    dateTimeGeneralSaleAvailable = generalSaleAvailable;
  }

  /**
   * Gets the TV channel that showed the match.
   * 
   * @return the TV channel that showed the match
   */
  public String getTelevisionChannel() {
    return televisionChannel;
  }

  /**
   * Sets the TV channel that showed the match.
   * 
   * @param televisionChannel
   *          the new TV channel that showed the match
   */
  public void setTelevisionChannel(final String televisionChannel) {
    this.televisionChannel = televisionChannel;
  }
}
