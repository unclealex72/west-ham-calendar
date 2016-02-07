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

package html;

import dates.{May, Instant}
import org.joda.time.DateTime
import org.specs2.mutable.Specification

import Instant.asDateTime
import model.Competition
import model.GameKey
import model.Location
/**
 * Test that game locator comparators are well behaved.
 *
 * @author alex
 *
 */
class GameLocatorTest extends Specification {

  "The two different types of game locators" should {
    val gameKeyLocator: GameLocator =
      new GameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012))
    val datePlayedLocator: GameLocator = new DatePlayedLocator(new DateTime())
    "order GameKeyLocators before DatePlayedLocators" in {
      gameKeyLocator must be lessThan (datePlayedLocator)
    }
    "order DatePlayedLocators after GameKeyLocators" in {
      datePlayedLocator must be greaterThan (gameKeyLocator)
    }
  }

  "Comparing DatePlayedLocators" should {
    val lowerDatePlayedLocator: GameLocator = DatePlayedLocator(May(1, 2000) at (9, 30))
    val higherDatePlayedLocator: GameLocator = DatePlayedLocator(May(3, 2000) at (9, 30))
    "be greater than when the date is greater than" in {
      higherDatePlayedLocator must be greaterThan (lowerDatePlayedLocator)
    }
    "be less than when the date is less than" in {
      lowerDatePlayedLocator must be lessThan (higherDatePlayedLocator)
    }
    "be equal when the dates are equal" in {
      higherDatePlayedLocator.compare(higherDatePlayedLocator) must be equalTo (0)
    }
  }

  "Comparing GameKeyLocators" should {
    val lowerGameKeyLocator: GameLocator = GameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2011))
    val higherGameKeyLocator: GameLocator = GameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012))
    "be greater than when the game key is greater than" in {
      higherGameKeyLocator must be greaterThan (lowerGameKeyLocator)
    }
    "be less than when the game key is less than" in {
      lowerGameKeyLocator must be lessThan (higherGameKeyLocator)
    }
    "be equal when the game keys are equal" in {
      higherGameKeyLocator.compare(higherGameKeyLocator) must be equalTo (0)
    }
  }
}
