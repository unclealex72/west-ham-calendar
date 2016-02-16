/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation  extends Competition(0, ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0  extends Competition(0, the
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
package models

import enumeratum._
import json.JsonEnum

/**
 * The different competitions that West Ham have taken part in.
 *
 * @author alex
 *
 */
sealed trait Competition extends EnumEntry {
  /**
   * The English name of this competition.
   */
  val name: String
  /**
   * The tokens that are used within the West Ham website to identify this competition.
   */
  val tokens: Seq[String]

  /**
   * A flag that determines whether this competition is a primary English league competition or not.
   */
  def isLeague: Boolean

}

sealed abstract class AbstractCompetition(val name: String, val isLeague: Boolean, val tokens: String*) extends Competition

/**
 * League based competitions.
 */
sealed abstract class LeagueCompetition(override val name: String, override val tokens: String*) extends AbstractCompetition(name, true, tokens: _*)

/**
 * Cup based competitions.
 */
sealed abstract class CupCompetition(override val name: String, override val tokens: String*) extends AbstractCompetition(name, false, tokens: _*)

object Competition extends JsonEnum[Competition] {

  val values = findValues

  /**
   * The FA Premiership.
   */
  case object PREM extends LeagueCompetition("Premiership", "Barclays Premier League")

  /**
   * The League Cup.
   */
  case object LGCP extends CupCompetition("League Cup", "English Capital One Cup")

  /**
   * The FA Cup.
   */
  case object FACP extends CupCompetition("FA Cup", "English FA Cup")

  /**
   * The Championship.
   */
  case object FLC extends LeagueCompetition("Championship", "FLC", "FLD1")

  /**
   * The Championship play-offs.
   */
  case object FLCPO extends CupCompetition("Play-Offs", "FLD1 P/O", "FLC P/O")

  /**
   * Friendly matches.
   */
  case object FRIENDLY extends CupCompetition("Friendly", "Pre-Season Match", "Betway Cup", "Mark Noble Testimonial")

  /**
   * The EUROPA League.
   */
  case object EUROPA extends CupCompetition("UEFA Europa League", "UEFA Europa League", "UEFA Europa League Qualifying")

  def apply(token: String): Either[String, Competition] = {
    values.find(_.tokens.contains(token)).toRight(s"$token is not a valid competition token.")
  }

}
