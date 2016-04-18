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
package dao

import dates.NowService
import models.{Location, Competition}
import org.joda.time.DateTime
import model.Game
import scala.collection.SortedSet
import search.SearchOption
import search.GameOrTicketSearchOption
import search.AttendedSearchOption
import search.LocationSearchOption

import scala.concurrent.Future

/**
  * The data access object for {@link Game}s.
 *
 * @author alex
 */
trait GameDao {

  /**
   * Update a game
   */
  def store(game: Game)(implicit nowService: NowService): Future[Game]

  /**
   * Find a game by its ID.
   */
  def findById(id: Long): Future[Option[Game]]

  /**
   * Find a game by the {@link DateTime} it was played.
    *
    * @param datePlayed The {@link DateTime} to search for.
   * @return The {@link Game} played at the given {@link DateTime} or null if one could not be found.
   */
  def findByDatePlayed(datePlayed: DateTime): Future[Option[Game]]

  /**
   * Get all the {@link Game}s for a given season.
    *
    * @param season The season to search for.
   * @return All the {@link Game}s during the given season.
   */
  def getAllForSeason(season: Int): Future[List[Game]]

  /**
   * Find all the seasons known so far.
    *
    * @return All the known seasons.
   */
  def getAllSeasons: Future[SortedSet[Int]]

  /**
   * Find a game by its {@link Competition}, {@link Location}, opponents and season. Together, these are guaranteed to
   * uniquely define a {@link Game}.
    *
    * @param competition The {@link Competition} to search for.
   * @param location The {@link Location} to search for.
   * @param opponents The opponents to search for.
   * @param season The season to search for.
   * @return The uniquely defined {@link Game} if it exists or null otherwise.
   */
  def findByBusinessKey(competition: Competition, location: Location, opponents: String, season: Int): Future[Option[Game]]

  /**
   * Get the latest known season.
    *
    * @return The latest known season.
   */
  def getLatestSeason: Future[Option[Int]]

  /**
   * Get all {@link Game}s for a given season and {@link Location}.
    *
    * @param season The season to search for.
   * @param location The {@link Location} to search for.
   * @return All {@link Game}s with the given season and {@link Location}.
   */
  def getAllForSeasonAndLocation(season: Int, location: Location): Future[List[Game]]

  /**
   * Search for games that match all the search options provided.
   */
  def search(
    attendedSearchOption: AttendedSearchOption, 
    locationSearchOption: LocationSearchOption, 
    gameOrTicketSearchOption: GameOrTicketSearchOption): Future[List[Game]]

  /**
   * Get all known games.
   */
  def getAll: Future[List[Game]]

  //Coming soon!
  //def getAllModifiedSince(lastModified: DateTime): Future[List[Game]]

  /**
    * Get all home and away logos.
    * @return
    */
  def getAllTeamLogos: Future[Set[String]]

  /**
    * Get all competition logos.
    * @return
    */
  def getAllCompetitionLogos: Future[Set[String]]
}

