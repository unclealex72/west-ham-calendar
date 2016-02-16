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
package model

import dates.NowService
import models.{Location, Competition}
import org.joda.time.DateTime

import scala.language.implicitConversions

/**
 * A persistable unit that represents an advertised West Ham game.
 */
/** Entity class storing rows of table Game
  *
  *  @param opponents Database column opponents SqlType(varchar), Length(128,true)
  *  @param academyMembersAvailable Database column academymembers SqlType(timestamp), Default(None)
  *  @param bondholdersAvailable Database column bondholders SqlType(timestamp), Default(None)
  *  @param attended Database column attended SqlType(bool), Default(None)
  *  @param result Database column result SqlType(varchar), Length(128,true), Default(None)
  *  @param competition Database column competition SqlType(varchar), Length(128,true)
  *  @param season Database column season SqlType(int4)
  *  @param attendance Database column attendence SqlType(int4), Default(None)
  *  @param priorityPointAvailable Database column prioritypoint SqlType(timestamp), Default(None)
  *  @param at Database column at SqlType(timestamp), Default(None)
  *  @param id Database column id SqlType(int8), PrimaryKey
  *  @param location Database column location SqlType(varchar), Length(128,true)
  *  @param televisionChannel Database column tvchannel SqlType(varchar), Length(128,true), Default(None)
  *  @param matchReport Database column report SqlType(text), Default(None)
  *  @param seasonTicketsAvailable Database column seasontickets SqlType(timestamp), Default(None)
  *  @param generalSaleAvailable Database column generalsale SqlType(timestamp), Default(None)
  *  @param dateCreated Database column datecreated SqlType(timestamp)
  *  @param lastUpdated Database column lastupdated SqlType(timestamp)
  *  @param academyMembersPostalAvailable Database column academymemberspostal SqlType(timestamp), Default(None)
  *  @param generalSalePostalAvailable Database column generalsalepostal SqlType(timestamp), Default(None) */
case class Game(
                 id: Long,
                 location: Location,
                 season: Int,
                 opponents: String,
                 competition: Competition,
                 at: Option[DateTime] = None,
                 attended: Option[Boolean] = None,
                 result: Option[String] = None,
                 attendance: Option[Int] = None,
                 matchReport: Option[String] = None,
                 televisionChannel: Option[String] = None,
                 academyMembersAvailable: Option[DateTime] = None,
                 bondholdersAvailable: Option[DateTime] = None,
                 priorityPointAvailable: Option[DateTime] = None,
                 seasonTicketsAvailable: Option[DateTime] = None,
                 generalSaleAvailable: Option[DateTime] = None,
                 homeTeamImageLink: Option[String] = None,
                 awayTeamImageLink: Option[String] = None,
                 competitionImageLink: Option[String] = None,
                 dateCreated: DateTime,
                 lastUpdated: DateTime) {

  /**
   * Get the unique business key for this game.
   */
  def gameKey: GameKey = GameKey(competition, location, opponents, season)
}

object Game {

  def tupled = (Game.apply _).tupled

  def gameKey(gameKey: GameKey)(implicit nowService: NowService): Game = {
    val now = nowService.now
    new Game(
      id = 0,
      competition = gameKey.competition,
      location = gameKey.location,
      opponents = gameKey.opponents,
      season = gameKey.season, dateCreated = now, lastUpdated = now)
  }

  def gameKey(competition: Competition, location: Location, opponents: String, season: Int)(implicit nowService: NowService): Game = {
    gameKey(GameKey(competition, location, opponents, season))
  }
}