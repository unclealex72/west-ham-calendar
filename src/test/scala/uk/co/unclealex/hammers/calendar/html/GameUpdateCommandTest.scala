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

package uk.co.unclealex.hammers.calendar.html;

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.Location
import org.specs2.mutable.Specification
import uk.co.unclealex.hammers.calendar.Date._
import uk.co.unclealex.hammers.calendar.September
import scala.reflect.ClassTag

/**
 * The Class GameUpdateCommandTest.
 *
 * @author alex
 */
class GameUpdateCommandTest extends Specification {

  val DEFAULT_COMPETITION = Competition.FACP
  val DEFAULT_LOCATION = Location.HOME
  val DEFAULT_OPPONENTS = "Them"
  val DEFAULT_SEASON = 2012
  val DEFAULT_DATE_PLAYED = September(5, 1972)
  val DEFAULT_BONDHOLDERS_AVAILABLE = September(5, 1973)
  val DEFAULT_PRIORITY_POINT_POST_AVAILABLE = September(5, 1974)
  val DEFAULT_SEASON_TICKETS_AVAILABLE = September(5, 1975)
  val DEFAULT_ACADEMY_TICKETS_AVAILABLE = September(5, 1976)
  val DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE = September(5, 1977)
  val DEFAULT_RESULT = "1-0"
  val DEFAULT_ATTENDENCE = 100000
  val DEFAULT_MATCH_REPORT = "Good"
  val DEFAULT_TELEVISION_CHANNEL = "BBC"
  val DEFAULT_ATTENDED = false

  /**
   * Test date played.
   */
  "Updating the date played" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (datePlayed: Option[DateTime]) => DatePlayedUpdateCommand(gameLocator, datePlayed),
      game => game.dateTimePlayed,
      DEFAULT_DATE_PLAYED,
      DEFAULT_DATE_PLAYED plusHours 1)
  }

  "Updating the result" should {
    testGameUpdateCommand(
      gameLocator => (result: Option[String]) => ResultUpdateCommand(gameLocator, result),
      game => game.result,
      DEFAULT_RESULT,
      "1" + DEFAULT_RESULT)
  }

  "Updating the attendence" should {
    testGameUpdateCommand(
      gameLocator => (attendence: Option[Int]) => AttendenceUpdateCommand(gameLocator, attendence),
      game => game.attendence,
      DEFAULT_ATTENDENCE,
      DEFAULT_ATTENDENCE * 2)
  }

  "Updating the match report" should {
    testGameUpdateCommand(
      gameLocator => (matchReport: Option[String]) => MatchReportUpdateCommand(gameLocator, matchReport),
      game => game.matchReport,
      DEFAULT_MATCH_REPORT,
      DEFAULT_MATCH_REPORT + "!")
  }

  "Updating the television channel" should {
    testGameUpdateCommand(
      gameLocator => (televisionChannel: Option[String]) => TelevisionChannelUpdateCommand(gameLocator, televisionChannel),
      game => game.televisionChannel,
      DEFAULT_TELEVISION_CHANNEL,
      DEFAULT_TELEVISION_CHANNEL + "!")
  }

  "Updating the attended flag" should {
    testGameUpdateCommand(
      gameLocator => (attended: Option[Boolean]) => AttendedUpdateCommand(gameLocator, attended),
      game => game.attended,
      DEFAULT_ATTENDED,
      !DEFAULT_ATTENDED)
  }

  "Updating the bond holder ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: Option[DateTime]) => BondHolderTicketsUpdateCommand(gameLocator, saleDate),
      game => game.dateTimeBondholdersAvailable,
      DEFAULT_BONDHOLDERS_AVAILABLE,
      DEFAULT_BONDHOLDERS_AVAILABLE plusDays 1)
  }

  "Updating the priority point ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: Option[DateTime]) => PriorityPointTicketsUpdateCommand(gameLocator, saleDate),
      game => game.dateTimePriorityPointPostAvailable,
      DEFAULT_PRIORITY_POINT_POST_AVAILABLE,
      DEFAULT_PRIORITY_POINT_POST_AVAILABLE plusDays 1)
  }

  "Updating the season ticket holders' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: Option[DateTime]) => SeasonTicketsUpdateCommand(gameLocator, saleDate),
      game => game.dateTimeSeasonTicketsAvailable,
      DEFAULT_SEASON_TICKETS_AVAILABLE,
      DEFAULT_SEASON_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the academy members' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: Option[DateTime]) => AcademyTicketsUpdateCommand(gameLocator, saleDate),
      game => game.dateTimeAcademyMembersAvailable,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the general ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: Option[DateTime]) => GeneralSaleTicketsUpdateCommand(gameLocator, saleDate),
      game => game.dateTimeGeneralSaleAvailable,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE plusDays 1)
  }

  /**
   * Test a game update command.
   *
   * @param gameUpdateCommandFactory
   *          the game update command factory
   * @param valueFunction
   *          the value function
   * @param currentValue
   *          the current value
   * @param newValue
   *          the new value
   */
  def testGameUpdateCommand[E](
    gameUpdateCommandFactory: GameLocator => Option[E] => GameUpdateCommand,
    valueFactory: Game => Option[E],
    currentValue: E,
    newValue: E)(implicit ct: ClassTag[E]) {
    val updateCommandFactory = (game: Game) => gameUpdateCommandFactory(GameKeyLocator(game.gameKey))
    "not change for None values" in {
      testNoChangeForNone(updateCommandFactory, valueFactory)
    }
    "not change for equal values" in {
      testNoChangeForEqualValue(updateCommandFactory, valueFactory, currentValue)
    }
    "change for different values" in {
      testChangeForDifferentValues(updateCommandFactory, valueFactory, newValue)
    }
  }

  /**
   * Test no change for null.
   *
   * @param <E>
   *          the element type
   * @param gameUpdateCommandFactory
   *          the game update command factory
   * @param valueFunction
   *          the value function
   */
  def testNoChangeForNone[E](
    gameUpdateCommandFactory: Game => Option[E] => GameUpdateCommand,
    valueFactory: Game => Option[E]) {
    val game = createFullyPopulatedGame
    val gameUpdateCommand = gameUpdateCommandFactory(game)(None: Option[E])
    gameUpdateCommand update game must be equalTo (false)
    valueFactory(game) must not be equalTo(None)
  }

  /**
   * Test no change for equal value.
   *
   * @param <E>
   *          the element type
   * @param gameUpdateCommandFactory
   *          the game update command factory
   * @param valueFunction
   *          the value function
   * @param currentValue
   *          the current value
   */
  def testNoChangeForEqualValue[E](
    gameUpdateCommandFactory: Game => Option[E] => GameUpdateCommand,
    valueFactory: Game => Option[E],
    currentValue: E) {
    val game = createFullyPopulatedGame
    val gameUpdateCommand = gameUpdateCommandFactory(game)(Some(currentValue))
    gameUpdateCommand update game should be equalTo (false)
    valueFactory(game) should be equalTo (Some(currentValue))
  }

  /**
   * Test change for different values.
   *
   * @param <E>
   *          the element type
   * @param gameUpdateCommandFactory
   *          the game update command factory
   * @param valueFunction
   *          the value function
   * @param newValue
   *          the new value
   */
  def testChangeForDifferentValues[E](
    gameUpdateCommandFactory: Game => Option[E] => GameUpdateCommand,
    valueFactory: Game => Option[E],
    newValue: E) {
    val game = createFullyPopulatedGame
    val gameUpdateCommand =
      gameUpdateCommandFactory(game)(Some(newValue))
    gameUpdateCommand update game should be equalTo (true)
    valueFactory.apply(game) should be equalTo (Some(newValue))
  }

  /**
   * Creates the fully populated game.
   *
   * @return the game
   */
  def createFullyPopulatedGame: Game = {
    return new Game(
      1,
      DEFAULT_COMPETITION,
      DEFAULT_LOCATION,
      DEFAULT_OPPONENTS,
      DEFAULT_SEASON,
      Some(DEFAULT_DATE_PLAYED),
      Some(DEFAULT_BONDHOLDERS_AVAILABLE),
      Some(DEFAULT_PRIORITY_POINT_POST_AVAILABLE),
      Some(DEFAULT_SEASON_TICKETS_AVAILABLE),
      Some(DEFAULT_ACADEMY_TICKETS_AVAILABLE),
      Some(DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE),
      Some(DEFAULT_RESULT),
      Some(DEFAULT_ATTENDENCE),
      Some(DEFAULT_MATCH_REPORT),
      Some(DEFAULT_TELEVISION_CHANNEL),
      Some(DEFAULT_ATTENDED))
  }

}
