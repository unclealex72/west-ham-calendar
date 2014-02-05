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
  val DEFAULT_PRIORITY_POINT_AVAILABLE = September(5, 1974)
  val DEFAULT_SEASON_TICKETS_AVAILABLE = September(5, 1975)
  val DEFAULT_ACADEMY_TICKETS_AVAILABLE = September(5, 1976)
  val DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE = September(5, 1979)
  val DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE = September(5, 1977)
  val DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE = September(5, 1980)
  val DEFAULT_UPDATE_DATE = September(5, 2013) at (9, 12)
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
      gameLocator => (datePlayed: DateTime) => DatePlayedUpdateCommand(gameLocator, datePlayed),
      game => game.at,
      DEFAULT_DATE_PLAYED,
      DEFAULT_DATE_PLAYED plusHours 1)
  }

  "Updating the result" should {
    testGameUpdateCommand(
      gameLocator => (result: String) => ResultUpdateCommand(gameLocator, result),
      game => game.result,
      DEFAULT_RESULT,
      "1" + DEFAULT_RESULT)
  }

  "Updating the attendence" should {
    testGameUpdateCommand(
      gameLocator => (attendence: Int) => AttendenceUpdateCommand(gameLocator, attendence),
      game => game.attendence,
      DEFAULT_ATTENDENCE,
      DEFAULT_ATTENDENCE * 2)
  }

  "Updating the match report" should {
    testGameUpdateCommand(
      gameLocator => (matchReport: String) => MatchReportUpdateCommand(gameLocator, matchReport),
      game => game.matchReport,
      DEFAULT_MATCH_REPORT,
      DEFAULT_MATCH_REPORT + "!")
  }

  "Updating the television channel" should {
    testGameUpdateCommand(
      gameLocator => (televisionChannel: String) => TelevisionChannelUpdateCommand(gameLocator, televisionChannel),
      game => game.televisionChannel,
      DEFAULT_TELEVISION_CHANNEL,
      DEFAULT_TELEVISION_CHANNEL + "!")
  }

  "Updating the attended flag" should {
    testGameUpdateCommand(
      gameLocator => (attended: Boolean) => AttendedUpdateCommand(gameLocator, attended),
      game => game.attended,
      DEFAULT_ATTENDED,
      !DEFAULT_ATTENDED)
  }

  "Updating the bond holder ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => BondHolderTicketsUpdateCommand(gameLocator, saleDate),
      game => game.bondholdersAvailable,
      DEFAULT_BONDHOLDERS_AVAILABLE,
      DEFAULT_BONDHOLDERS_AVAILABLE plusDays 1)
  }

  "Updating the priority point ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => PriorityPointTicketsUpdateCommand(gameLocator, saleDate),
      game => game.priorityPointAvailable,
      DEFAULT_PRIORITY_POINT_AVAILABLE,
      DEFAULT_PRIORITY_POINT_AVAILABLE plusDays 1)
  }

  "Updating the season ticket holders' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => SeasonTicketsUpdateCommand(gameLocator, saleDate),
      game => game.seasonTicketsAvailable,
      DEFAULT_SEASON_TICKETS_AVAILABLE,
      DEFAULT_SEASON_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the academy members' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => AcademyTicketsUpdateCommand(gameLocator, saleDate),
      game => game.academyMembersAvailable,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the academy members' postal ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => AcademyPostalTicketsUpdateCommand(gameLocator, saleDate),
      game => game.academyMembersPostalAvailable,
      DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE,
      DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the general ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => GeneralSaleTicketsUpdateCommand(gameLocator, saleDate),
      game => game.generalSaleAvailable,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE plusDays 1)
  }

  "Updating the general ticket postal sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => GeneralSalePostalTicketsUpdateCommand(gameLocator, saleDate),
      game => game.generalSalePostalAvailable,
      DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE,
      DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE plusDays 1)
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
    gameUpdateCommandFactory: GameLocator => E => GameUpdateCommand,
    valueFactory: Game => Option[E],
    currentValue: E,
    newValue: E)(implicit ct: ClassTag[E]) = {
    val updateCommandFactory = (game: Game) => gameUpdateCommandFactory(GameKeyLocator(game.gameKey))
    "not change for equal values" in {
      val game = createFullyPopulatedGame
      val gameUpdateCommand = updateCommandFactory(game)(currentValue)
      gameUpdateCommand update game should be equalTo (false)
      valueFactory(game) should be equalTo (Some(currentValue))
    }
    "change for different values" in {
      val game = createFullyPopulatedGame
      val gameUpdateCommand =
        updateCommandFactory(game)(newValue)
      gameUpdateCommand update game should be equalTo (true)
      valueFactory.apply(game) should be equalTo (Some(newValue))
    }
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
      Some(DEFAULT_PRIORITY_POINT_AVAILABLE),
      Some(DEFAULT_SEASON_TICKETS_AVAILABLE),
      Some(DEFAULT_ACADEMY_TICKETS_AVAILABLE),
      Some(DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE),
      Some(DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE),
      Some(DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE),
      Some(DEFAULT_RESULT),
      Some(DEFAULT_ATTENDENCE),
      Some(DEFAULT_MATCH_REPORT),
      Some(DEFAULT_TELEVISION_CHANNEL),
      Some(DEFAULT_ATTENDED),
      DEFAULT_UPDATE_DATE,
      DEFAULT_UPDATE_DATE)
  }

}
