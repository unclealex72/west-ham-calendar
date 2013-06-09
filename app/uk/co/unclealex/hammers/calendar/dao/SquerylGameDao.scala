/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.hammers.calendar.dao

import SquerylEntryPoint._
import uk.co.unclealex.hammers.calendar.model.Game
import org.squeryl.Schema
import org.joda.time.DateTime
import scala.collection.SortedSet
import uk.co.unclealex.hammers.calendar.model.Location
import uk.co.unclealex.hammers.calendar.model.Competition

/**
 * @author alex
 *
 */
object SquerylGameDao extends Schema with GameDao with Transactional {

  val games = table[Game]
  
  /**
   * Column constraints
   */
  on(games)(g => declare(
    g.id is (autoIncremented),
    g.gameKeyComposite is (unique)))

  /**
   * Run code within a transaction.
   * @param block The code to run.
   */
  def tx[T](block: GameDao => T): T = inTransaction { block(this) }

  def store(game: Game): Game = {
    games.insertOrUpdate(game)
    game
  }
  
  def findById(id: Long): Option[Game] = games.lookup(id)

  def findByDatePlayed(datePlayed: DateTime): Option[Game] = games.where(g => g.dateTimePlayed === Some(datePlayed))

  def getAllForSeason(season: Int): List[Game] = from(games)(g => where(g.season === season) select(g) orderBy(g.dateTimePlayed))

  def getAllSeasons: SortedSet[Int] = from(games)(g => select (g.season)).distinct

  def findByBusinessKey(competition: Competition, location: Location, opponents: String, season: Int): Option[Game] =
    games.where(g => g.gameKeyComposite === (competition.persistableToken, location.persistableToken, opponents, season))

  def getLatestSeason: Option[Int] = from(games)(g => compute(max(g.season)))

  def getAllForSeasonAndLocation(season: Int, location: Location): List[Game] =
    from(games)(g => where(g.season === season and g._location === location.persistableToken) select(g) orderBy(g.dateTimePlayed))

  def getAll: List[Game] = from(games)(select(_))
  
}