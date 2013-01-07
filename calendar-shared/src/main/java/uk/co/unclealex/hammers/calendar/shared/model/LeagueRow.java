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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;

/**
 * A {@link LeagueRow} is a row in the league of results against opponents
 * during a season.
 * 
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
   * The team who are the opponents for row.
   */
  private String team;

  /**
   * The number of games played for row.
   */
  private int played;

  /**
   * The number of games won for row.
   */
  private int won;

  /**
   * The number of games drawn for row.
   */
  private int drawn;

  /**
   * The number of games lost for row.
   */
  private int lost;

  /**
   * The number of goals scored for row.
   */
  private int goalsFor;

  /**
   * The number of goals conceded for row.
   */
  private int against;

  /**
   * The number of points for row.
   */
  private int points;

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
  public LeagueRow(final String team, final int goalsFor, final int goalsAgainst) {
    setTeam(team);
    addGame(goalsFor, goalsAgainst);
  }

  /**
   * Add a game to row.
   * 
   * @param goalsFor
   *          The number of goals scored.
   * @param goalsAgainst
   *          The number of goals conceded.
   */
  public void addGame(final int goalsFor, final int goalsAgainst) {
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
    setPoints(getPoints() + points);
    setPlayed(getPlayed() + 1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final LeagueRow o) {
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
   * Get the goal difference for row.
   * 
   * @return The goal difference for row.
   */
  public int getGoalDifference() {
    return getFor() - getAgainst();
  }

  /**
   * Gets the team who are the opponents for row.
   * 
   * @return the team who are the opponents for row
   */
  public String getTeam() {
    return team;
  }

  /**
   * Sets the team who are the opponents for row.
   * 
   * @param team
   *          the new team who are the opponents for row
   */
  public void setTeam(final String team) {
    this.team = team;
  }

  /**
   * Gets the number of games played for row.
   * 
   * @return the number of games played for row
   */
  public int getPlayed() {
    return played;
  }

  /**
   * Sets the number of games played for row.
   * 
   * @param played
   *          the new number of games played for row
   */
  public void setPlayed(final int played) {
    this.played = played;
  }

  /**
   * Gets the number of games won for row.
   * 
   * @return the number of games won for row
   */
  public int getWon() {
    return won;
  }

  /**
   * Sets the number of games won for row.
   * 
   * @param won
   *          the new number of games won for row
   */
  public void setWon(final int won) {
    this.won = won;
  }

  /**
   * Gets the number of games drawn for row.
   * 
   * @return the number of games drawn for row
   */
  public int getDrawn() {
    return drawn;
  }

  /**
   * Sets the number of games drawn for row.
   * 
   * @param drawn
   *          the new number of games drawn for row
   */
  public void setDrawn(final int drawn) {
    this.drawn = drawn;
  }

  /**
   * Gets the number of games lost for row.
   * 
   * @return the number of games lost for row
   */
  public int getLost() {
    return lost;
  }

  /**
   * Sets the number of games lost for row.
   * 
   * @param lost
   *          the new number of games lost for row
   */
  public void setLost(final int lost) {
    this.lost = lost;
  }

  /**
   * Gets the number of goals scored for row.
   * 
   * @return the number of goals scored for row
   */
  public int getFor() {
    return goalsFor;
  }

  /**
   * Sets the number of goals scored for row.
   * 
   * @param goalsFor
   *          the new number of goals scored for row
   */
  public void setFor(final int goalsFor) {
    this.goalsFor = goalsFor;
  }

  /**
   * Gets the number of goals conceded for row.
   * 
   * @return the number of goals conceded for row
   */
  public int getAgainst() {
    return against;
  }

  /**
   * Sets the number of goals conceded for row.
   * 
   * @param against
   *          the new number of goals conceded for row
   */
  public void setAgainst(final int against) {
    this.against = against;
  }

  /**
   * Gets the number of points for row.
   * 
   * @return the number of points for row
   */
  public int getPoints() {
    return points;
  }

  /**
   * Sets the number of points for row.
   * 
   * @param points
   *          the new number of points for row
   */
  public void setPoints(final int points) {
    this.points = points;
  }
}
