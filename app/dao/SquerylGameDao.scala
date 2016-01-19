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
package dao

import javax.inject.Inject

import dao.CalendarSchema._
import dao.SquerylEntryPoint._
import dates.DateTimeImplicits._
import dates.NowService
import model.{Competition, Game, Location}
import org.joda.time.DateTime
import org.squeryl.dsl.ast.LogicalBoolean
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption, SearchOption}

import scala.collection.SortedSet

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SquerylGameDao @Inject() (
  /**
   * The now service to get the current times for updates and creates.
   */
  nowService: NowService) extends GameDao with Transactional {

  /**
   * Run code within a transaction.
   * @param block The code to run.
   */
  def tx[T](block: GameDao => T): T = inTransaction { block(this) }

  def store(game: Game): Game = {
    val now = nowService.now
    if (game.dateCreated == null) {
      game.dateCreated = now
    }
    game.lastUpdated = now
    games.insertOrUpdate(game)
    game
  }

  def findById(id: Long): Option[Game] = games.lookup(id)

  def findByDatePlayed(datePlayed: DateTime): Option[Game] = games.where(g => g.at === Some(convertToJdbc(datePlayed)))

  def getAllForSeason(season: Int): List[Game] = from(games)(g => where(g.season === season) select (g) orderBy (g.at))

  def getAllSeasons: SortedSet[Int] = from(games)(g => select(g.season)).distinct

  def findByBusinessKey(competition: Competition, location: Location, opponents: String, season: Int): Option[Game] =
    games.where(g => g.gameKeyComposite === (competition.persistableToken, location.persistableToken, opponents, season))

  def getLatestSeason: Option[Int] = from(games)(g => compute(max(g.season)))

  def getAllForSeasonAndLocation(season: Int, location: Location): List[Game] =
    from(games)(g => where(g.season === season and g._location === location.persistableToken) select (g) orderBy (g.at))

  def getAll: List[Game] = from(games)(g => select(g) orderBy (g.at))

  def searchPredicate(searchOption: SearchOption): Option[Game => LogicalBoolean] = {
    searchOption match {
      case AttendedSearchOption.ANY => None
      case AttendedSearchOption.ATTENDED => Some(g => g.attended === Some(true))
      case AttendedSearchOption.UNATTENDED => Some(g => g.attended === Some(false))
      case LocationSearchOption.ANY => None
      case LocationSearchOption.HOME => Some(g => g._location === Location.HOME.persistableToken)
      case LocationSearchOption.AWAY => Some(g => g._location === Location.AWAY.persistableToken)
      case GameOrTicketSearchOption.GAME => None
      case GameOrTicketSearchOption.BONDHOLDERS => Some(g => g.bondholdersAvailable isNotNull)
      case GameOrTicketSearchOption.PRIORITY_POINT => Some(g => g.priorityPointAvailable isNotNull)
      case GameOrTicketSearchOption.SEASON => Some(g => g.seasonTicketsAvailable isNotNull)
      case GameOrTicketSearchOption.ACADEMY => Some(g => g.academyMembersAvailable isNotNull)
      case GameOrTicketSearchOption.ACADEMY_POSTAL => Some(g => g.academyMembersPostalAvailable isNotNull)
      case GameOrTicketSearchOption.GENERAL_SALE => Some(g => g.generalSaleAvailable isNotNull)
      case GameOrTicketSearchOption.GENERAL_SALE_POSTAL => Some(g => g.generalSalePostalAvailable isNotNull)
    }
  }

  def search(
    attendedSearchOption: AttendedSearchOption,
    locationSearchOption: LocationSearchOption,
    gameOrTicketSearchOption: GameOrTicketSearchOption): List[Game] = {
    val predicates = List(attendedSearchOption, locationSearchOption, gameOrTicketSearchOption).flatMap(searchPredicate)
    if (predicates.isEmpty) {
      getAll
    } else {
      val predicate = (g: Game) => predicates.map(p => p(g)).reduce((p1, p2) => p1 and p2)
      from(games)(g => where(predicate(g)) select (g) orderBy (g.at))
    }
  }
}