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

import scala.math.Ordered
import org.joda.time.DateTime
import uk.co.unclealex.hammers.calendar.model.Game
import com.typesafe.scalalogging.slf4j.Logging

/**
 * A class used to encapsulate a possible update to a game as directed by an.
 *
 * {@link HtmlGamesScanner}.
 *
 * @author alex
 */
sealed abstract class GameUpdateCommand(
  /**
   * The locator to use to locate the game.
   */
  val gameLocator: GameLocator) extends Ordered[GameUpdateCommand] {

  /**
   * Update a game. No check is made to see if the correct game is being
   * updated.
   *
   * @param game
   *          The game to update.
   * @return True if the game was updated, false otherwise.
   */
  def update(game: Game): Boolean
}

sealed class UpdateType(val position: Int, val description: String)
// The type for date played updates.
case object DATE_PLAYED extends UpdateType(0, "date played")
// The type for result updates.
case object RESULT extends UpdateType(1, "result")
// The type for attendence updates.
case object ATTENDENCE extends UpdateType(2, "attendence")
// The type for match report updates.
case object MATCH_REPORT extends UpdateType(3, "match report")
// The type for television channel updates.
case object TELEVISION_CHANNEL extends UpdateType(4, "television channel")
// The type for attendence updates.
case object ATTENDED extends UpdateType(5, "attendance flag")
// The type for changing the Bondholder ticket selling dates.
case object BONDHOLDER_TICKETS extends UpdateType(6, "bondholder ticket selling date")
// The type for changing the priority point ticket selling dates.
case object PRIORITY_POINT_POST_TICKETS extends UpdateType(7, "priority ticket selling date")
// The type for changing the season ticket holder ticket selling dates.
case object SEASON_TICKETS extends UpdateType(8, "season ticket selling date")
// The type for changing the Academy members' ticket selling dates.
case object ACADEMY_TICKETS extends UpdateType(9, "academy ticket selling date")
// The type for changing the general sale ticket selling dates.
case object GENERAL_SALE_TICKETS extends UpdateType(10, "general sale ticket selling date")

sealed abstract class BaseGameUpdateCommand[V](
  /**
   * The type of update (used for ordering).
   */
  val updateType: UpdateType,
  /**
   * The locator to use to locate the game.
   */
  override val gameLocator: GameLocator,
  /**
   * The new date played value.
   */
  val newValue: Option[V]) extends GameUpdateCommand(gameLocator) with Logging {

  override def update(game: Game): Boolean = {
    newValue match {
      case Some(newValue) => {
        if (newValue != currentValue(game)) {
          logger info s"Updating the ${updateType.description} to $newValue for game ${game.getGameKey}"
          setNewValue(game, newValue)
          true
        } else {
          false
        }
      }
      case None => false
    }
  }

  def compare(other: GameUpdateCommand): Int = {
    other match {
      case otherBase: BaseGameUpdateCommand[V] => {
        val cmp = gameLocator.compare(otherBase.gameLocator)
        if (cmp == 0) updateType.position - otherBase.updateType.position else cmp
      }
    }
  }

  /**
   * Gets the current value.
   *
   * @param game
   *          The game to check.
   * @return The current value of the current game.
   */
  protected def currentValue(game: Game): V

  /**
   * Alter a game.
   *
   * @param game
   *          The game to alter.
   */
  protected def setNewValue(game: Game, newValue: V)
}
/**
 * A {@link GameUpdateCommand} that updates a game's date played value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newDatePlayed
 *          The new date played value.
 * @return A {@link GameUpdateCommand} that updates a game's date played
 *         value.
 */
case class DatePlayedUpdateCommand(
  override val gameLocator: GameLocator, newDatePlayed: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](DATE_PLAYED, gameLocator, newDatePlayed) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimePlayed

  override def setNewValue(game: Game, newDatePlayed: DateTime) = game.setDateTimePlayed(newDatePlayed)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's result value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newResult
 *          The new result.
 * @return A {@link GameUpdateCommand} that updates a game's result value.
 */
case class ResultUpdateCommand(
  override val gameLocator: GameLocator, newResult: Option[String]) extends BaseGameUpdateCommand[String](RESULT, gameLocator, newResult) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: String) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getResult

  override def setNewValue(game: Game, newResult: String) = game.setResult(newResult)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's attendence value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newAttendence
 *          The new attendence.
 * @return A {@link GameUpdateCommand} that updates a game's attendence value.
 */
case class AttendenceUpdateCommand(
  override val gameLocator: GameLocator, newAttendence: Option[Int]) extends BaseGameUpdateCommand[Int](ATTENDENCE, gameLocator, newAttendence) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: Int) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getAttendence

  override def setNewValue(game: Game, newAttendence: Int) = game.setAttendence(newAttendence)

}

/**
 * Create a {@link GameUpdateCommand} that updates a game's match report
 * value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newMatchReport
 *          The new match report.
 * @return A {@link GameUpdateCommand} that updates a game's match report
 *         value.
 */
case class MatchReportUpdateCommand(
  override val gameLocator: GameLocator, newMatchReport: Option[String]) extends BaseGameUpdateCommand[String](MATCH_REPORT, gameLocator, newMatchReport) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: String) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getMatchReport

  override def setNewValue(game: Game, newMatchReport: String) = game.setMatchReport(newMatchReport)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's television channel
 * value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newTelevisionChannel
 *          The new television channel.
 * @return A {@link GameUpdateCommand} that updates a game's television
 *         channel value.
 */
case class TelevisionChannelUpdateCommand(
  override val gameLocator: GameLocator, newTelevisionChannel: Option[String]) extends BaseGameUpdateCommand[String](TELEVISION_CHANNEL, gameLocator, newTelevisionChannel) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: String) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getTelevisionChannel

  override def setNewValue(game: Game, newTelevisionChannel: String) = game.setTelevisionChannel(newTelevisionChannel)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's attended value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newAttended
 *          The new attended value.
 * @return A {@link GameUpdateCommand} that updates a game's attended value.
 */
case class AttendedUpdateCommand(
  override val gameLocator: GameLocator, newAttended: Option[Boolean]) extends BaseGameUpdateCommand[Boolean](ATTENDED, gameLocator, newAttended) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: Boolean) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.isAttended

  override def setNewValue(game: Game, newAttended: Boolean) = game.setAttended(newAttended)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's bondholder tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newBondHolderTicketsAvailable
 *          The new date for bond holder ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's bondholder
 *         availability tickets date.
 */
case class BondHolderTicketsUpdateCommand(
  override val gameLocator: GameLocator, newBondHolderTicketsAvailable: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](BONDHOLDER_TICKETS, gameLocator, newBondHolderTicketsAvailable) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimeBondholdersAvailable
  override def setNewValue(game: Game, newBondHolderTicketsAvailable: DateTime) =
    game.setDateTimeBondholdersAvailable(newBondHolderTicketsAvailable)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's priority points
 * tickets availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newPriorityPointTicketsAvailable
 *          The new date for priority point ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's priority points
 *         tickets availability date.
 */
case class PriorityPointTicketsUpdateCommand(
  override val gameLocator: GameLocator, newPriorityPointPostAvailable: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](PRIORITY_POINT_POST_TICKETS, gameLocator, newPriorityPointPostAvailable) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimePriorityPointPostAvailable
  override def setNewValue(game: Game, newPriorityPointPostAvailable: DateTime) =
    game.setDateTimePriorityPointPostAvailable(newPriorityPointPostAvailable)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's season tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newSeasonTicketsAvailable
 *          The new date for season ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's season ticket
 *         availability date.
 */
case class SeasonTicketsUpdateCommand(
  override val gameLocator: GameLocator, newSeasonTicketsAvailable: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](SEASON_TICKETS, gameLocator, newSeasonTicketsAvailable) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimeSeasonTicketsAvailable
  override def setNewValue(game: Game, newSeasonTicketsAvailable: DateTime) =
    game.setDateTimeSeasonTicketsAvailable(newSeasonTicketsAvailable)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's academy tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newAcademyTicketsAvailable
 *          The new date for academy ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's academy ticket
 *         availability date.
 */
case class AcademyTicketsUpdateCommand(
  override val gameLocator: GameLocator, newAcademyTicketsAvailable: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](ACADEMY_TICKETS, gameLocator, newAcademyTicketsAvailable) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimeAcademyMembersAvailable
  override def setNewValue(game: Game, newAcademyTicketsAvailable: DateTime) =
    game.setDateTimeAcademyMembersAvailable(newAcademyTicketsAvailable)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's general sale
 * tickets availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newGeneralSaleTicketsAvailable
 *          The new date for general sale ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's general sale
 *         ticket availability date.
 */
case class GeneralSaleTicketsUpdateCommand(
  override val gameLocator: GameLocator, newGeneralSaleTicketsAvailable: Option[DateTime]) extends BaseGameUpdateCommand[DateTime](GENERAL_SALE_TICKETS, gameLocator, newGeneralSaleTicketsAvailable) {

  //TODO Remove
  def this(gameLocator: GameLocator, value: DateTime) = this(gameLocator, Some(value))

  override def currentValue(game: Game) = game.getDateTimeGeneralSaleAvailable
  override def setNewValue(game: Game, newGeneralSaleTicketsAvailable: DateTime) =
    game.setDateTimeGeneralSaleAvailable(newGeneralSaleTicketsAvailable)
}
