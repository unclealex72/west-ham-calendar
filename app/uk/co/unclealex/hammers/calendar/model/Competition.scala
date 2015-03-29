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
package uk.co.unclealex.hammers.calendar.model

import uk.co.unclealex.hammers.calendar.logging.{RemoteLogging, RemoteStream}

/**
 * The different competitions that West Ham have taken part in.
 *
 * @author alex
 *
 */
sealed trait Competition extends Competition.Value {
  /**
   * The English name of this competition.
   */
  val name: String
  /**
   * The tokens that are used within the West Ham website to identify this competition.
   */
  val tokens: List[String]

  /**
   * A flag that determines whether this competition is a primary English league competition or not.
   */
  def isLeague: Boolean

}

/**
 * League based competitions.
 */
sealed trait LeagueCompetition extends Competition {
  override def isLeague = true
}

/**
 * Cup based competitions.
 */
sealed trait CupCompetition extends Competition {

  override def isLeague = false
}

object Competition extends PersistableEnumeration[Competition] with RemoteLogging {
  /**
   * The FA Premiership.
   */
  case object PREM extends LeagueCompetition {
    val persistableToken = "PREM"
    val name = "Premiership"
    val tokens = List("Barclays Premier League")
  }
  PREM

  /**
   * The League Cup.
   */
  case object LGCP extends CupCompetition {
    val persistableToken = "LGCP"
    val name = "League Cup"
    val tokens = List("English Capital One Cup")
  }
  LGCP

  /**
   * The FA Cup.
   */
  case object FACP extends CupCompetition {
    val persistableToken = "FACP"
    val name = "FA Cup"
    val tokens = List("English FA Cup")
  }
  FACP

  /**
   * The Championship.
   */
  case object FLC extends LeagueCompetition {
    val persistableToken = "FLC"
    val name = "Championship"
    val tokens = List("FLC", "FLD1")
  }
  FLC

  /**
   * The Championship play-offs.
   */
  case object FLCPO extends CupCompetition {
    val persistableToken = "FLCPO"
    val name = "Play-Offs"
    val tokens = List("FLD1 P/O", "FLC P/O")
  }
  FLCPO

  def apply(token: String)(implicit remoteStream: RemoteStream): Option[Competition] = {
    logOnEmpty(values.find(_.tokens contains token), s"$token is not a valid competition token.")
  }

}
