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
}

abstract class AbstractTicketType(val name: TicketType.Name, val label: String, val default: Boolean = false) extends TicketType

case object BondholderTicketType extends AbstractTicketType("Bondholder", "Bond holder")
case object PriorityPointTicketType extends AbstractTicketType("PriorityPoint", "Priority point", true)
case object SeasonTicketType extends AbstractTicketType("Season", "Season")
case object AcademyTicketType extends AbstractTicketType("Academy", "Academy member")
case object AcademyPostalTicketType extends AbstractTicketType("AcademyPostal", "Academy member postal")
case object GeneralSaleTicketType extends AbstractTicketType("GeneralSale", "General sale")
case object GeneralSalePostalTicketType extends AbstractTicketType("GeneralSalePostal", "General sale postal")

object TicketType {
  type Name = String

  // The list of all ticket types.
  val ticketTypes = List(
    BondholderTicketType, PriorityPointTicketType, SeasonTicketType, AcademyTicketType,
    AcademyPostalTicketType, GeneralSaleTicketType, GeneralSalePostalTicketType)

  /**
   * Json Serialisation
   */
  implicit val TicketTypeEncodeJson: EncodeJson[TicketType] =
    jencode3L((tt: TicketType) => (tt.name, tt.label, tt.default))("name", "label", "default")

}

