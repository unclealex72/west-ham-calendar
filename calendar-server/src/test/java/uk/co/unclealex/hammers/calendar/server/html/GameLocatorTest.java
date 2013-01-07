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

package uk.co.unclealex.hammers.calendar.server.html;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;


/**
 * Test that game locator comparators are well behaved.
 * @author alex
 * 
 */
public class GameLocatorTest {

	/**
	 * Test game key is lower.
	 */
	@Test
	public void testGameKeyIsLower() {
		GameLocator gameKeyLocator = GameLocator.gameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents",
				2012));
		GameLocator datePlayedLocator = GameLocator.datePlayedLocator(new DateTime());
		int cmp = gameKeyLocator.compareTo(datePlayedLocator);
		Assert.assertTrue("The game key comparator was not found to be smaller.", cmp < 0);
	}

	/**
	 * Test date played is higher.
	 */
	@Test
	public void testDatePlayedIsHigher() {
		GameLocator gameKeyLocator = GameLocator.gameKeyLocator(new GameKey(Competition.FACP, Location.AWAY, "Opponents",
				2012));
		GameLocator datePlayedLocator = GameLocator.datePlayedLocator(new DateTime());
		int cmp = datePlayedLocator.compareTo(gameKeyLocator);
		Assert.assertTrue("The date played comparator was not found to be larget.", cmp > 0);
	}

	/**
	 * Test date played respects date time comparison.
	 */
	@Test
	public void testDatePlayedRespectsDateTimeComparison() {
		DateTime lowerDateTime = new DateTime(2000, 5, 1, 0, 0, 0, 0);
		DateTime higherDateTime = new DateTime(2001, 5, 1, 0, 0, 0, 0);
		GameLocator lowerDatePlayedLocator = GameLocator.datePlayedLocator(lowerDateTime);
		GameLocator higherDatePlayedLocator = GameLocator.datePlayedLocator(higherDateTime);
		Assert.assertEquals("Comparing lower to higher failed.", lowerDateTime.compareTo(higherDateTime),
				lowerDatePlayedLocator.compareTo(higherDatePlayedLocator));
		Assert.assertEquals("Comparing higher to lower failed.", higherDateTime.compareTo(lowerDateTime),
				higherDatePlayedLocator.compareTo(lowerDatePlayedLocator));
		Assert.assertEquals("Comparing higher to higher failed.", higherDateTime.compareTo(higherDateTime),
				higherDatePlayedLocator.compareTo(higherDatePlayedLocator));
	}

	/**
	 * Test game key respects game key comparison.
	 */
	@Test
	public void testGameKeyRespectsGameKeyComparison() {
		GameKey lowerGameKey = new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2011);
		GameKey higherGameKey = new GameKey(Competition.FACP, Location.AWAY, "Opponents", 2012);
		GameLocator lowerGameKeyLocator = GameLocator.gameKeyLocator(lowerGameKey);
		GameLocator higherGameKeyLocator = GameLocator.gameKeyLocator(higherGameKey);
		Assert.assertEquals("Comparing lower to higher failed.", lowerGameKey.compareTo(higherGameKey),
				lowerGameKeyLocator.compareTo(higherGameKeyLocator));
		Assert.assertEquals("Comparing higher to lower failed.", higherGameKey.compareTo(lowerGameKey),
				higherGameKeyLocator.compareTo(lowerGameKeyLocator));
		Assert.assertEquals("Comparing higher to higher failed.", higherGameKey.compareTo(higherGameKey),
				higherGameKeyLocator.compareTo(higherGameKeyLocator));
	}
}
