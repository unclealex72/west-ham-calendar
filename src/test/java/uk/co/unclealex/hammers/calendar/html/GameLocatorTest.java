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

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.html.DatePlayedLocator;
import uk.co.unclealex.hammers.calendar.html.GameKeyLocator;
import uk.co.unclealex.hammers.calendar.html.GameLocator;
import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.GameKey;
import uk.co.unclealex.hammers.calendar.model.Location;

/**
 * Test that game locator comparators are well behaved.
 * 
 * @author alex
 * 
 */
public class GameLocatorTest {

  /**
   * Test game key is lower.
   */
  @Test
  public void testGameKeyIsLower() {
    final GameLocator gameKeyLocator =
        new GameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012));
    final GameLocator datePlayedLocator = new DatePlayedLocator(new DateTime());
    final int cmp = gameKeyLocator.compareTo(datePlayedLocator);
    Assert.assertTrue("The game key comparator was not found to be smaller.", cmp < 0);
  }

  /**
   * Test date played is higher.
   */
  @Test
  public void testDatePlayedIsHigher() {
    final GameLocator gameKeyLocator =
        new GameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012));
    final GameLocator datePlayedLocator = new DatePlayedLocator(new DateTime());
    final int cmp = datePlayedLocator.compareTo(gameKeyLocator);
    Assert.assertTrue("The date played comparator was not found to be larget.", cmp > 0);
  }

  /**
   * Test date played respects date time comparison.
   */
  @Test
  public void testDatePlayedRespectsDateTimeComparison() {
    final DateTime lowerDateTime = new DateTime(2000, 5, 1, 0, 0, 0, 0);
    final DateTime higherDateTime = new DateTime(2001, 5, 1, 0, 0, 0, 0);
    final GameLocator lowerDatePlayedLocator = new DatePlayedLocator(lowerDateTime);
    final GameLocator higherDatePlayedLocator = new DatePlayedLocator(higherDateTime);
    Assert.assertEquals(
        "Comparing lower to higher failed.",
        lowerDateTime.compareTo(higherDateTime),
        lowerDatePlayedLocator.compareTo(higherDatePlayedLocator));
    Assert.assertEquals(
        "Comparing higher to lower failed.",
        higherDateTime.compareTo(lowerDateTime),
        higherDatePlayedLocator.compareTo(lowerDatePlayedLocator));
    Assert.assertEquals(
        "Comparing higher to higher failed.",
        higherDateTime.compareTo(higherDateTime),
        higherDatePlayedLocator.compareTo(higherDatePlayedLocator));
  }

  /**
   * Test game key respects game key comparison.
   */
  @Test
  public void testGameKeyRespectsGameKeyComparison() {
    final GameKey lowerGameKey = new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2011);
    final GameKey higherGameKey = new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012);
    final GameLocator lowerGameKeyLocator = new GameKeyLocator(lowerGameKey);
    final GameLocator higherGameKeyLocator = new GameKeyLocator(higherGameKey);
    Assert.assertEquals(
        "Comparing lower to higher failed.",
        lowerGameKey.compareTo(higherGameKey),
        lowerGameKeyLocator.compareTo(higherGameKeyLocator));
    Assert.assertEquals(
        "Comparing higher to lower failed.",
        higherGameKey.compareTo(lowerGameKey),
        higherGameKeyLocator.compareTo(lowerGameKeyLocator));
    Assert.assertEquals(
        "Comparing higher to higher failed.",
        higherGameKey.compareTo(higherGameKey),
        higherGameKeyLocator.compareTo(higherGameKeyLocator));
  }
}
