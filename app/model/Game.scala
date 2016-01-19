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

import java.sql.Timestamp

import dates.DateTimeImplicits._
import model.Location._
import org.joda.time.DateTime
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.dsl.CompositeKey4

/**
 * A persistable unit that represents an advertised West Ham game.
 */
case class Game(
  /**
   * The primary key of the game.
   */
  val id: Long,
  /**
   * The game's {@link Competition}.
   */
  @Column("competition") val _competition: String,
  /**
   * The game's {@link Location}.
   */
  @Column("location") val _location: String,
  /**
   * The game's opponents.
   */
  val opponents: String,
  /**
   * The season the game was played in.
   */
  val season: Int,
  /**
   * The {@link DateTime} the game was played.
   */
  var at: Option[Timestamp],
  /**
   * The {@link DateTime} that Bondholder tickets went on sale.
   */
  @Column("bondholders") var bondholdersAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that priority point tickets went on sale.
   */
  @Column("prioritypoint") var priorityPointAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that season ticker holder tickets went on sale.
   */
  @Column("seasontickets") var seasonTicketsAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that Academy members' tickets went on sale.
   */
  @Column("academymembers") var academyMembersAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that Academy members' postal tickets went on sale.
   */
  @Column("academymemberspostal") var academyMembersPostalAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that tickets went on general sale.
   */
  @Column("generalsale") var generalSaleAvailable: Option[Timestamp],
  /**
   * The {@link DateTime} that tickets went on postal general sale.
   */
  @Column("generalsalepostal") var generalSalePostalAvailable: Option[Timestamp],
  /**
   * The game's result.
   */
  var result: Option[String],
  /**
   * The game's attendence.
   */
  var attendence: Option[Int],
  /**
   * The game's match report.
   */
  @Column("report") var matchReport: Option[String],
  /**
   * The TV channel that showed the match.
   */
  @Column("tvchannel") var televisionChannel: Option[String],

  /**
   * True if the game has been marked as attended, false otherwise.
   */
  var attended: Option[Boolean],
  /**
   * The date and time at which this game entry was originally created.
   */
  @Column("datecreated") var dateCreated: Timestamp,
  /**
   * The date and time at which this game entry was last updated.
   */
  @Column("lastupdated") var lastUpdated: DateTime) extends KeyedEntity[Long] {

  /**
   * Squeryl constructor
   */

  protected def this() =
    this(
      0, Competition.PREM, Location.HOME, "", 0,
      Some(new DateTime()), Some(new DateTime()), Some(new DateTime()), Some(new DateTime()),
      Some(new DateTime()), Some(new DateTime()), Some(new DateTime()), Some(new DateTime()),
      Some("result"), Some(0), Some("matchReport"), Some("televionChannel"), Some(false), null, null)
  /**
   * Create a new game from a business key.
   */
  def this(gameKey: GameKey) =
    this(
      0, gameKey.competition, gameKey.location, gameKey.opponents, gameKey.season,
      None, None, None, None, None, None, None, None, None, None, None, None, Some(false), null, null)

  def competition: Competition = _competition

  def location: Location = _location

  /**
   * Get the unique business key for this game.
   */
  def gameKey: GameKey = GameKey(competition, location, opponents, season)

  def gameKeyComposite = CompositeKey4(_competition, _location, opponents, season)
}

object Game {

  /**
   * Create a new game
   */
  def apply(gameKey: GameKey) = new Game(gameKey)
}