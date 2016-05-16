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

import org.joda.time.DateTime

import model.GameKey
import dates.DateTimeImplicits._

import enumeratum._

import scala.reflect.ClassTag

/**
 * The base class for both types of game locator.
 *
 * @param <E>
 *          The type of object that will be used to locate the game.
 * @author alex
 */

/**
 * A game locator is an abstract class that presents a visitor so services can
 * decide how to locate an existing game.
 *
 * @author alex
 *  /**
 * Accept a {@link GameLocatorVisitor}.
 * @param visitor The {@link GameLocatorVisitor} to accept.
 * */
 * public abstract void accept(GameLocatorVisitor visitor);
 *
 *
 */
sealed trait GameLocator extends Ordered[GameLocator]

sealed trait LocatorType extends EnumEntry
object LocatorType extends Enum[LocatorType] {

  val values = findValues

  object PRIMARY_KEY extends LocatorType
  object DATETIME_PLAYED extends LocatorType

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
  val locator: E)(implicit ord: Ordering[E]) extends GameLocator {

  private def ordering: Ordering[AbstractGameLocator[E]] = Ordering.by { agl => (agl.locatorType, agl.locator) }
  override def compare(that: GameLocator): Int = {
    that match {
      case tht: AbstractGameLocator[E] => ordering.compare(this, tht)
    }
  }
}

/**
 * A {@link GameLocator} that locates games using a {@link GameKey}.
 *
 * @author alex
 *
 */
case class GameKeyLocator(val gameKey: GameKey) extends AbstractGameLocator[GameKey](LocatorType.PRIMARY_KEY, gameKey)

/**
 * A {@link GameLocator} that locates games using the date they were played.
 *
 * @author alex
 *
 */
case class DatePlayedLocator(val datePlayed: DateTime) extends AbstractGameLocator[DateTime](
  LocatorType.DATETIME_PLAYED, datePlayed)
