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

import org.joda.time.DateTime
import com.google.common.base.Objects
import uk.co.unclealex.hammers.calendar.model.GameKey;

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
sealed abstract class GameLocator extends Ordered[GameLocator]

/**
 * An enumeration to allow different types of game locators to be explicitly ordered.
 */
object LocatorType extends Enumeration {
  type LocatorType = Value
  val /**
     * The locator for finding by primary key.
     */ PRIMARY_KEY, /**
     * The locator for finding by date time played.
     */ DATETIME_PLAYED = Value
}

sealed abstract class InternalGameLocator[E](
  /**
   * The unique type of the locator.
   */
  private val locatorType: LocatorType.Value,
  /**
   * The object used to locate a game.
   */
  private val locator: E)(implicit ord: Ordering[E]) extends GameLocator {

  def compare(other: GameLocator): Int = {
    other match {
      case o: InternalGameLocator[E] => {
        val cmp = locatorType.id - o.locatorType.id
        if (cmp == 0) ord.compare(locator, o.locator) else cmp
      }
    }
  }
}

/**
 * A {@link GameLocator} that locates games using a {@link GameKey}.
 * @author alex
 *
 */
case class GameKeyLocator(val gameKey: GameKey) extends InternalGameLocator[GameKey](LocatorType.PRIMARY_KEY, gameKey)

/**
 * A {@link GameLocator} that locates games using the date they were played.
 * @author alex
 *
 */
case class DatePlayedLocator(val datePlayed: DateTime) extends InternalGameLocator[DateTime](
  LocatorType.DATETIME_PLAYED, datePlayed)(Ordering.by((dt: DateTime) => dt.getMillis))
