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
  val newValue: V) extends GameUpdateCommand(gameLocator) with Logging {

  override def update(game: Game): Boolean = {
    if (Some(newValue) != currentValue(game)) {
      logger info s"Updating the ${updateType.description} to $newValue for game ${game.gameKey}"
      setNewValue(game)
      true
    } else {
      false
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
  protected def currentValue(game: Game): Option[V]

  /**
   * Alter a game.
   *
   * @param game
   *          The game to alter.
   */
  protected def setNewValue(game: Game)
}
/**
 * A {@link GameUpdateCommand} that updates a game's date played value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date played value.
 * @return A {@link GameUpdateCommand} that updates a game's date played
 *         value.
 */
case class DatePlayedUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends BaseGameUpdateCommand[DateTime](DATE_PLAYED, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimePlayed

  override def setNewValue(game: Game) = game.dateTimePlayed = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's result value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new result.
 * @return A {@link GameUpdateCommand} that updates a game's result value.
 */
case class ResultUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: String) extends BaseGameUpdateCommand[String](RESULT, gameLocator, newValue) {

  override def currentValue(game: Game) = game.result

  override def setNewValue(game: Game) = game.result = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's attendence value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new attendence.
 * @return A {@link GameUpdateCommand} that updates a game's attendence value.
 */
case class AttendenceUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: Int) extends BaseGameUpdateCommand[Int](ATTENDENCE, gameLocator, newValue) {

  override def currentValue(game: Game) = game.attendence

  override def setNewValue(game: Game) = game.attendence = Some(newValue)

}

/**
 * Create a {@link GameUpdateCommand} that updates a game's match report
 * value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new match report.
 * @return A {@link GameUpdateCommand} that updates a game's match report
 *         value.
 */
case class MatchReportUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: String) extends BaseGameUpdateCommand[String](MATCH_REPORT, gameLocator, newValue) {

  override def currentValue(game: Game) = game.matchReport

  override def setNewValue(game: Game) = game.matchReport = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's television channel
 * value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new television channel.
 * @return A {@link GameUpdateCommand} that updates a game's television
 *         channel value.
 */
case class TelevisionChannelUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: String) extends BaseGameUpdateCommand[String](TELEVISION_CHANNEL, gameLocator, newValue) {

  override def currentValue(game: Game) = game.televisionChannel

  override def setNewValue(game: Game) = game.televisionChannel = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's attended value.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new attended value.
 * @return A {@link GameUpdateCommand} that updates a game's attended value.
 */
case class AttendedUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: Boolean) extends BaseGameUpdateCommand[Boolean](ATTENDED, gameLocator, newValue) {

  override def currentValue(game: Game) = game.attended

  override def setNewValue(game: Game) = game.attended = Some(newValue)
}

/**
 * The parent class of all ticketing update commands. This basically restricts the value type to be updated to DateTime.
 */
sealed abstract class TicketsUpdateCommand(
  override val updateType: UpdateType, override val gameLocator: GameLocator, override val newValue: DateTime)
  extends BaseGameUpdateCommand[DateTime](updateType, gameLocator, newValue)

/**
 * Create a {@link GameUpdateCommand} that updates a game's bondholder tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date for bond holder ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's bondholder
 *         availability tickets date.
 */
case class BondHolderTicketsUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends TicketsUpdateCommand(BONDHOLDER_TICKETS, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimeBondholdersAvailable
  override def setNewValue(game: Game) = game.dateTimeBondholdersAvailable = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's priority points
 * tickets availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date for priority point ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's priority points
 *         tickets availability date.
 */
case class PriorityPointTicketsUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends TicketsUpdateCommand(PRIORITY_POINT_POST_TICKETS, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimePriorityPointPostAvailable
  override def setNewValue(game: Game) = game.dateTimePriorityPointPostAvailable = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's season tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date for season ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's season ticket
 *         availability date.
 */
case class SeasonTicketsUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends TicketsUpdateCommand(SEASON_TICKETS, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimeSeasonTicketsAvailable
  override def setNewValue(game: Game) = game.dateTimeSeasonTicketsAvailable = Some(newValue)
}

/**
 * Create a {@link GameUpdateCommand} that updates a game's academy tickets
 * availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date for academy ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's academy ticket
 *         availability date.
 */
case class AcademyTicketsUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends TicketsUpdateCommand(ACADEMY_TICKETS, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimeAcademyMembersAvailable
  override def setNewValue(game: Game) = game.dateTimeAcademyMembersAvailable = Some(newValue)

}

/**
 * Create a {@link GameUpdateCommand} that updates a game's general sale
 * tickets availability date.
 *
 * @param gameLocator
 *          The locator to use to locate the game.
 * @param newValue
 *          The new date for general sale ticket availabilty.
 * @return A {@link GameUpdateCommand} that updates a game's general sale
 *         ticket availability date.
 */
case class GeneralSaleTicketsUpdateCommand(
  override val gameLocator: GameLocator, override val newValue: DateTime) extends TicketsUpdateCommand(GENERAL_SALE_TICKETS, gameLocator, newValue) {

  override def currentValue(game: Game) = game.dateTimeGeneralSaleAvailable
  override def setNewValue(game: Game) = game.dateTimeGeneralSaleAvailable = Some(newValue)
}
