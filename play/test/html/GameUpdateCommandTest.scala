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

package html;

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import dates.{September, Date}
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import model.Game
import model.GameKey
import model.Competition
import model.Location
import org.specs2.mutable.Specification
import Date._
import scala.reflect.ClassTag
import dates.DateTimeImplicits._

/**
 * The Class GameUpdateCommandTest.
 *
 * @author alex
 */
class GameUpdateCommandTest extends Specification {

  val DEFAULT_COMPETITION: Competition = Competition.FACP
  val DEFAULT_LOCATION: Location = Location.HOME
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
  val DEFAULT_ATTENDANCE = 100000
  val DEFAULT_MATCH_REPORT = "Good"
  val DEFAULT_TELEVISION_CHANNEL = "BBC"
  val DEFAULT_ATTENDED = false

  /**
   * Test date played.
   */
  "Updating the date played" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (datePlayed: DateTime) => DatePlayedUpdateCommand(gameLocator, datePlayed),
      _.at,
      DEFAULT_DATE_PLAYED,
      DEFAULT_DATE_PLAYED plusHours 1,
      _.copy(at = Some(DEFAULT_DATE_PLAYED plusHours 1).map(_.toDateTime)))
  }

  "Updating the result" should {
    testGameUpdateCommand(
      gameLocator => (result: String) => ResultUpdateCommand(gameLocator, result),
      _.result,
      DEFAULT_RESULT,
      "1" + DEFAULT_RESULT,
      _.copy(result = Some("1" + DEFAULT_RESULT)))
  }

  "Updating the attendence" should {
    testGameUpdateCommand(
      gameLocator => (attendence: Int) => AttendenceUpdateCommand(gameLocator, attendence),
      _.attendance,
      DEFAULT_ATTENDANCE,
      DEFAULT_ATTENDANCE * 2,
      _.copy(attendance = Some(DEFAULT_ATTENDANCE * 2)))
  }

  "Updating the match report" should {
    testGameUpdateCommand(
      gameLocator => (matchReport: String) => MatchReportUpdateCommand(gameLocator, matchReport),
      _.matchReport,
      DEFAULT_MATCH_REPORT,
      DEFAULT_MATCH_REPORT + "!",
      _.copy(matchReport = Some(DEFAULT_MATCH_REPORT + "!")))
  }

  "Updating the television channel" should {
    testGameUpdateCommand(
      gameLocator => (televisionChannel: String) => TelevisionChannelUpdateCommand(gameLocator, televisionChannel),
      _.televisionChannel,
      DEFAULT_TELEVISION_CHANNEL,
      DEFAULT_TELEVISION_CHANNEL + "!",
      _.copy(televisionChannel = Some(DEFAULT_TELEVISION_CHANNEL + "!")))
  }

  "Updating the attended flag" should {
    testGameUpdateCommand(
      gameLocator => (attended: Boolean) => AttendedUpdateCommand(gameLocator, attended),
      _.attended,
      DEFAULT_ATTENDED,
      !DEFAULT_ATTENDED,
      _.copy(attended = Some(!DEFAULT_ATTENDED)))
  }

  "Updating the bond holder ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => BondHolderTicketsUpdateCommand(gameLocator, saleDate),
      _.bondholdersAvailable,
      DEFAULT_BONDHOLDERS_AVAILABLE,
      DEFAULT_BONDHOLDERS_AVAILABLE plusDays 1,
      _.copy(bondholdersAvailable = Some(DEFAULT_BONDHOLDERS_AVAILABLE plusDays 1)))
  }

  "Updating the priority point ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => PriorityPointTicketsUpdateCommand(gameLocator, saleDate),
      _.priorityPointAvailable,
      DEFAULT_PRIORITY_POINT_AVAILABLE,
      DEFAULT_PRIORITY_POINT_AVAILABLE plusDays 1,
      _.copy(priorityPointAvailable = Some(DEFAULT_PRIORITY_POINT_AVAILABLE plusDays 1)))
  }

  "Updating the season ticket holders' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => SeasonTicketsUpdateCommand(gameLocator, saleDate),
      _.seasonTicketsAvailable,
      DEFAULT_SEASON_TICKETS_AVAILABLE,
      DEFAULT_SEASON_TICKETS_AVAILABLE plusDays 1,
      _.copy(seasonTicketsAvailable = Some(DEFAULT_SEASON_TICKETS_AVAILABLE plusDays 1)))
  }

  "Updating the academy members' ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => AcademyTicketsUpdateCommand(gameLocator, saleDate),
      _.academyMembersAvailable,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE,
      DEFAULT_ACADEMY_TICKETS_AVAILABLE plusDays 1,
      _.copy(academyMembersAvailable = Some(DEFAULT_ACADEMY_TICKETS_AVAILABLE plusDays 1)))
  }

  "Updating the academy members' postal ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => AcademyPostalTicketsUpdateCommand(gameLocator, saleDate),
      _.academyMembersPostalAvailable,
      DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE,
      DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE plusDays 1,
      _.copy(academyMembersPostalAvailable = Some(DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE plusDays 1)))
  }

  "Updating the general ticket sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => GeneralSaleTicketsUpdateCommand(gameLocator, saleDate),
      _.generalSaleAvailable,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE,
      DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE plusDays 1,
      _.copy(generalSaleAvailable = Some(DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE plusDays 1)))
  }

  "Updating the general ticket postal sale date" should {
    testGameUpdateCommand[DateTime](
      gameLocator => (saleDate: DateTime) => GeneralSalePostalTicketsUpdateCommand(gameLocator, saleDate),
      _.generalSalePostalAvailable,
      DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE,
      DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE plusDays 1,
      _.copy(generalSalePostalAvailable = Some(DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE plusDays 1)))
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
    newValue: E,
    expectedResult: Game => Game)(implicit ct: ClassTag[E]) = {
    val updateCommandFactory = (game: Game) => gameUpdateCommandFactory(GameKeyLocator(game.gameKey))
    "not change for equal values" in {
      val game = createFullyPopulatedGame
      val gameUpdateCommand = updateCommandFactory(game)(currentValue)
      gameUpdateCommand.update(game) must beNone
    }
    "change for different values" in {
      val game = createFullyPopulatedGame
      val gameUpdateCommand =
        updateCommandFactory(game)(newValue)
      gameUpdateCommand.update(game) must beSome(expectedResult(game))
    }
  }

  /**
   * Creates the fully populated game.
   *
   * @return the game
   */
  def createFullyPopulatedGame: Game = {
    return new Game(
      id = 1,
      location = DEFAULT_LOCATION,
      season = DEFAULT_SEASON,
      competition = DEFAULT_COMPETITION,
      opponents = DEFAULT_OPPONENTS,
      at = Some(DEFAULT_DATE_PLAYED.toDateTime),
      attended = Some(DEFAULT_ATTENDED),
      result = Some(DEFAULT_RESULT),
      attendance = Some(DEFAULT_ATTENDANCE),
      matchReport = Some(DEFAULT_MATCH_REPORT),
      televisionChannel = Some(DEFAULT_TELEVISION_CHANNEL),
      bondholdersAvailable = Some(DEFAULT_BONDHOLDERS_AVAILABLE.toDateTime),
      priorityPointAvailable = Some(DEFAULT_PRIORITY_POINT_AVAILABLE.toDateTime),
      seasonTicketsAvailable = Some(DEFAULT_SEASON_TICKETS_AVAILABLE.toDateTime),
      academyMembersAvailable = Some(DEFAULT_ACADEMY_TICKETS_AVAILABLE.toDateTime),
      academyMembersPostalAvailable = Some(DEFAULT_ACADEMY_POSTAL_TICKETS_AVAILABLE.toDateTime),
      generalSaleAvailable = Some(DEFAULT_GENERAL_SALE_TICKETS_AVAILABLE.toDateTime),
      generalSalePostalAvailable = Some(DEFAULT_GENERAL_SALE_POSTAL_TICKETS_AVAILABLE.toDateTime),
      lastUpdated = DEFAULT_UPDATE_DATE,
      dateCreated = DEFAULT_UPDATE_DATE)
  }

}
