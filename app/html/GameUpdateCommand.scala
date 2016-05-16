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
package html

import com.typesafe.scalalogging.slf4j.StrictLogging
import enumeratum.{EnumEntry, _}
import model.Game
import models.GameResult
import org.joda.time.DateTime

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
  val gameLocator: GameLocator) {

  /**
   * Update a game. No check is made to see if the correct game is being
   * updated.
   *
   * @param game
   *          The game to update.
   * @return The updated game if it was updated or none otherwise.
   */
  def update(game: Game): Option[Game]
}

sealed abstract class UpdateType(val description: String) extends EnumEntry
object UpdateType extends Enum[UpdateType] {

  val values = findValues

  // The type for date played updates.
  case object DATE_PLAYED extends UpdateType("date played")
  // The type for result updates.
  case object RESULT extends UpdateType("result")
  // The type for attendence updates.
  case object ATTENDENCE extends UpdateType("attendence")
  // The type for match report updates.
  case object MATCH_REPORT extends UpdateType("match report")
  // The type for television channel updates.
  case object TELEVISION_CHANNEL extends UpdateType("television channel")
  // The type for attendence updates.
  case object ATTENDED extends UpdateType("attendance flag")
  // The type for home team image updates.
  case object HOME_TEAM_IMAGE_LINK extends UpdateType("home team image")
  // The type for away team image updates.
  case object AWAY_TEAM_IMAGE_LINK extends UpdateType("away team image")
  // The type for competition image updates.
  case object COMPETITION_IMAGE_LINK extends UpdateType("competition image")
  // The type for changing the Bondholder ticket selling dates.
  case object BONDHOLDER_TICKETS extends UpdateType("bondholder ticket selling date")
  // The type for changing the priority point ticket selling dates.
  case object PRIORITY_POINT_POST_TICKETS extends UpdateType("priority ticket selling date")
  // The type for changing the season ticket holder ticket selling dates.
  case object SEASON_TICKETS extends UpdateType("season ticket selling date")
  // The type for changing the Academy members' ticket selling dates.
  case object ACADEMY_TICKETS extends UpdateType("academy ticket selling date")
  // The type for changing the general sale ticket selling dates.
  case object GENERAL_SALE_TICKETS extends UpdateType("general sale ticket selling date")

  implicit val ordering: Ordering[UpdateType] = Ordering.by(values.indexOf)
}

import html.UpdateType._

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
  val newValue: V) extends GameUpdateCommand(gameLocator) with StrictLogging {

  override def update(game: Game): Option[Game] = {
    if (!currentValue(game).contains(newValue)) {
      logger info s"Updating the ${updateType.description} to $newValue for game ${game.gameKey}"
      Some(setNewValue(game))
    } else {
      None
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
  protected def setNewValue(game: Game): Game
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

  override def currentValue(game: Game) = game.at

  override def setNewValue(game: Game) = game.copy(at = Some(newValue))
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
  override val gameLocator: GameLocator, override val newValue: GameResult) extends BaseGameUpdateCommand[GameResult](RESULT, gameLocator, newValue) {

  override def currentValue(game: Game) = game.result

  override def setNewValue(game: Game) = game.copy(result = Some(newValue))
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

  override def currentValue(game: Game) = game.attendance

  override def setNewValue(game: Game) = game.copy(attendance = Some(newValue))

}

/**
  * Create a {@link GameUpdateCommand} that updates a game's home team image link value.
  *
  * @param gameLocator
  *          The locator to use to locate the game.
  * @param newValue
  *          The new home team image url.
  */
case class HomeTeamImageLinkCommand(
                                    override val gameLocator: GameLocator,
                                    override val newValue: String) extends BaseGameUpdateCommand[String](HOME_TEAM_IMAGE_LINK, gameLocator, newValue) {

  override def currentValue(game: Game) = game.homeTeamImageLink

  override def setNewValue(game: Game) = game.copy(homeTeamImageLink = Some(newValue))

}

/**
  * Create a {@link GameUpdateCommand} that updates a game's away team image link value.
  *
  * @param gameLocator
  *          The locator to use to locate the game.
  * @param newValue
  *          The new away team image url.
  */
case class AwayTeamImageLinkCommand(
                                     override val gameLocator: GameLocator,
                                     override val newValue: String) extends BaseGameUpdateCommand[String](AWAY_TEAM_IMAGE_LINK, gameLocator, newValue) {

  override def currentValue(game: Game) = game.awayTeamImageLink

  override def setNewValue(game: Game) = game.copy(awayTeamImageLink = Some(newValue))

}

/**
  * Create a {@link GameUpdateCommand} that updates a game's away team image link value.
  *
  * @param gameLocator
  *          The locator to use to locate the game.
  * @param newValue
  *          The new away team image url.
  */
case class CompetitionImageLinkCommand(
                                     override val gameLocator: GameLocator,
                                     override val newValue: String) extends BaseGameUpdateCommand[String](COMPETITION_IMAGE_LINK, gameLocator, newValue) {

  override def currentValue(game: Game) = game.competitionImageLink

  override def setNewValue(game: Game) = game.copy(competitionImageLink = Some(newValue))

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

  override def setNewValue(game: Game) = game.copy(matchReport = Some(newValue))
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

  override def setNewValue(game: Game) = game.copy(televisionChannel = Some(newValue))
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

  override def currentValue(game: Game) = Some(game.attended)

  override def setNewValue(game: Game) = game.copy(attended = newValue)
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

  override def currentValue(game: Game) = game.bondholdersAvailable
  override def setNewValue(game: Game) = game.copy(bondholdersAvailable = Some(newValue))
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

  override def currentValue(game: Game) = game.priorityPointAvailable
  override def setNewValue(game: Game) = game.copy(priorityPointAvailable = Some(newValue))
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

  override def currentValue(game: Game) = game.seasonTicketsAvailable
  override def setNewValue(game: Game) = game.copy(seasonTicketsAvailable = Some(newValue))
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

  override def currentValue(game: Game) = game.academyMembersAvailable
  override def setNewValue(game: Game) = game.copy(academyMembersAvailable = Some(newValue))

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

  override def currentValue(game: Game) = game.generalSaleAvailable
  override def setNewValue(game: Game) = game.copy(generalSaleAvailable = Some(newValue))
}
