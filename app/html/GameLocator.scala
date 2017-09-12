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


import java.time.ZonedDateTime

import enumeratum._
import model.{Game, GameKey}

import scala.collection.immutable

/**
 * The base class for both types of game locator.
 *
 * @author alex
 */
sealed trait GameLocator {
  def matches(game: Game): Boolean
  val locatorType: LocatorType
}

sealed trait LocatorType extends EnumEntry
object LocatorType extends Enum[LocatorType] {

  val values: immutable.IndexedSeq[LocatorType] = findValues

  object PRIMARY_KEY extends LocatorType
  object DAY_PLAYED extends LocatorType

  implicit val ordering: Ordering[LocatorType] = Ordering.by(values.indexOf)
}

sealed abstract class AbstractGameLocator[E](
  /**
   * The unique type of the locator.
   */
  val locatorType: LocatorType,
  /**
   * The object used to locate a game.
   */
  val locator: E) extends GameLocator {
}

/**
 * A {@link GameLocator} that locates games using a {@link GameKey}.
 *
 * @author alex
 *
 */
case class GameKeyLocator(gameKey: GameKey) extends AbstractGameLocator[GameKey](LocatorType.PRIMARY_KEY, gameKey) {
  override def matches(game: Game): Boolean = game.gameKey == gameKey
}

/**
 * A {@link GameLocator} that locates games using the date they were played.
 *
 * @author alex
 *
 */
case class DatePlayedLocator(datePlayed: ZonedDateTime) extends AbstractGameLocator[ZonedDateTime](
  LocatorType.DAY_PLAYED, datePlayed) {
  override def matches(game: Game): Boolean = {
    game.at.contains(datePlayed)
  }
}

object GameLocator {
  implicit val gameLocatorOrdering: Ordering[GameLocator] = new Ordering[GameLocator] {

    private val gameKeyLocatorOrdering: Ordering[GameKeyLocator] = Ordering.by(_.gameKey)
    private val datePlayedLocatorOrdering: Ordering[DatePlayedLocator] = Ordering.by(_.datePlayed.toInstant)

    override def compare(gl1: GameLocator, gl2: GameLocator): Int = {
      (gl1, gl2) match {
        case (gkl1: GameKeyLocator, gkl2: GameKeyLocator) => gameKeyLocatorOrdering.compare(gkl1, gkl2)
        case (dpl1: DatePlayedLocator, dpl2: DatePlayedLocator) => datePlayedLocatorOrdering.compare(dpl1, dpl2)
        case (_gl1: GameLocator, _gl2: GameLocator) => LocatorType.ordering.compare(_gl1.locatorType, _gl2.locatorType)
      }
    }
  }
}