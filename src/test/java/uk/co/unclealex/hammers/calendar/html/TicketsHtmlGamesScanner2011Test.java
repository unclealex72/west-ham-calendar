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

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import uk.co.unclealex.hammers.calendar.html.AcademyTicketsUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.BondHolderTicketsUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.GameLocator;
import uk.co.unclealex.hammers.calendar.html.GameUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.GeneralSaleTicketsUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.PriorityPointTicketsUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.SeasonTicketsUpdateCommand;

import com.google.common.base.Function;

/**
 * The Class TicketsHtmlGamesScanner2011Test.
 * 
 * @author alex
 */
public class TicketsHtmlGamesScanner2011Test extends AbstractTicketsHtmlGamesScannerTest {

  public TicketsHtmlGamesScanner2011Test() {
    super(2011);
  }

  /**
   * Test blackpool away.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  @Test
  public void testBlackpoolAway() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                new PriorityPointTicketsUpdateCommand(gameLocator, dateOf(25, 1, 2012, 10, 0)),
                new BondHolderTicketsUpdateCommand(gameLocator, dateOf(26, 1, 2012, 9, 0)),
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(28, 1, 2012, 9, 0)),
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(30, 1, 2012, 9, 0)),
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(1, 2, 2012, 9, 0)) };
          }
        };
    test("tickets-blackpool-away.html", dateOf(18, 2, 2012, 15, 0), expectedGameUpdateCommandsFunction);
  }

  /**
   * Test southampton home.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  @Test
  public void testSouthamptonHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(16, 1, 2012, 9, 0)),
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(10, 1, 2012, 9, 0)),
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(17, 1, 2012, 9, 0)) };
          }
        };
    test("tickets-southampton-home.html", dateOf(14, 2, 2012, 19, 45), expectedGameUpdateCommandsFunction);
  }

  /**
   * Test palace home.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  @Test
  public void testPalaceHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[0];
          }
        };
    test("tickets-palace-home.html", dateOf(25, 2, 2012, 12, 45), expectedGameUpdateCommandsFunction);
  }

  /**
   * Test peterborough away.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  @Test
  public void testPeterboroughAway() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[0];
          }
        };
    test("tickets-peterborough-away.html", dateOf(25, 2, 2012, 12, 45), expectedGameUpdateCommandsFunction);
  }
}
