/*
 * Copyright 2014 Alex Jones
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import argonaut._, Argonaut._, DecodeResult._
import org.joda.time.DateTime
import html._
import logging.{RemoteLogging, RemoteStream}

/**
 * The different types of tickets available from the website.
 */
sealed trait TicketType {

  /**
   * The name of this ticket type.
   */
  val name: TicketType.Name

  /**
   * The human readable label for this ticket type.
   */
  val label: String

  /**
   * True if this is the default ticket type, false otherwise.
   */
  val default: Boolean

  /**
   * HTML strings that match this ticket type
   * @param gameLocator
   * @param dateTime
   * @return
   */
  val tokens: Seq[String]

  def toTicketsUpdateCommand(gameLocator: GameLocator, dateTime: DateTime): TicketsUpdateCommand
}

sealed class AbstractTicketType(
                                   val name: TicketType.Name, val label: String,
                                   ticketUpdateCommandFactory: (GameLocator, DateTime) => TicketsUpdateCommand,
                                   val default: Boolean, firstToken: String, extraTokens: String*) extends TicketType {

  val tokens = Seq(firstToken) ++ extraTokens

  override def toTicketsUpdateCommand(gameLocator: GameLocator, dateTime: DateTime): TicketsUpdateCommand = {
    ticketUpdateCommandFactory(gameLocator, dateTime)
  }
}


case object BondholderTicketType extends AbstractTicketType(
  "Bondholder", "Bond holder", BondHolderTicketsUpdateCommand.apply _, false, "Bondholders")
case object PriorityPointTicketType extends AbstractTicketType(
  "PriorityPoint", "Priority point", PriorityPointTicketsUpdateCommand.apply _, true, "Priority Point Applications")
case object SeasonTicketType extends AbstractTicketType(
  "Season", "Season", SeasonTicketsUpdateCommand.apply _, false, "Season Ticket Holder General Sale", "Season Ticket Holder Additional")
case object AcademyTicketType extends AbstractTicketType(
  "Academy", "Academy member", AcademyTicketsUpdateCommand.apply _, false, "Members")
case object GeneralSaleTicketType extends AbstractTicketType(
  "GeneralSale", "General sale", GeneralSaleTicketsUpdateCommand.apply _, false, "General Sale")

object TicketType extends RemoteLogging {
  type Name = String

  // The list of all ticket types.
  val ticketTypes: List[TicketType] = List(
    BondholderTicketType, PriorityPointTicketType, SeasonTicketType, AcademyTicketType, GeneralSaleTicketType)

  def apply(token: String)(implicit remoteStream: RemoteStream) = logOnEmpty(ticketTypes.find(ticketType => ticketType.tokens.contains(token)), s"$token is not a valid ticket type")

  /**
   * Json Serialisation
   */
  implicit val TicketTypeEncodeJson: EncodeJson[TicketType] =
    jencode3L((tt: TicketType) => (tt.name, tt.label, tt.default))("name", "label", "default")

}

