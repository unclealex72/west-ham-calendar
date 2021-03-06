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

import enumeratum.EnumEntry
import json.JsonEnum

import scala.collection.immutable

/**
 * The different types of tickets available from the website.
 */
sealed trait TicketType extends EnumEntry {

  /**
   * The name of this ticket type.
   */
  val name: String

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
   */
  val tokens: Seq[String]
}

sealed abstract class AbstractTicketType(
                                   val name: String, val label: String,
                                   val default: Boolean, firstToken: String, extraTokens: String*) extends TicketType {

  val tokens: Seq[String] = Seq(firstToken) ++ extraTokens
  override def entryName: String = name.toLowerCase

}


object TicketType extends JsonEnum[TicketType] {

  val values: immutable.IndexedSeq[TicketType] = findValues

  case object PriorityPointTicketType extends AbstractTicketType(
    "PriorityPoint", "Priority point", true, "Priority Point Applications", "ST Holders with", "Season Ticket Holders with")
  case object BondholderTicketType extends AbstractTicketType(
    "Bondholder", "Bond holder", false, "Bondholders")
  case object SeasonTicketType extends AbstractTicketType(
    "Season", "Season", false, "Season Ticket Holder")
  case object AcademyTicketType extends AbstractTicketType(
    "Academy", "Academy member", false, "Members", "Claret")
  case object GeneralSaleTicketType extends AbstractTicketType(
    "GeneralSale", "General sale", false, "General Sale")

  def within(text: String): Either[String, TicketType] = {
    val maybeTicketType = values.find{ ticketType =>
      ticketType.tokens.exists(token => text.contains(token))
    }
    maybeTicketType.toRight(s"'$text' does not contain a valid ticket type")
  }
}
