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

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.google.common.base.Function;

/**
 * The Class TicketsHtmlGamesScanner2011Test.
 * 
 * @author alex
 */
public class TicketsHtmlGamesScanner2012Test extends AbstractTicketsHtmlGamesScannerTest {

  public TicketsHtmlGamesScanner2012Test() {
    super(2012);
  }

  @Test
  public void testNorwichAway() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Bondholders - from 9am on Thursday 16 August</DIV>
                new BondHolderTicketsUpdateCommand(gameLocator, dateOf(16, 8, 2012, 9, 0)),
                // <DIV>Priority Point Postal Application - to receive by 10am
                // on Wednesday 15 August for process by Friday 17 August</DIV>
                new PriorityPointTicketsUpdateCommand(gameLocator, dateOf(15, 8, 2012, 10, 0)),
                // <DIV>Season Ticket General Sale - from 9am on Saturday 18
                // August</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(18, 8, 2012, 9, 0)),
                // <DIV>Academy Members - from 9am on Monday 20 August </DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(20, 8, 2012, 9, 0)),
                // <DIV>General Sale - from 9am on Tuesday 21
                // August</DIV></TD></TR>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(21, 8, 2012, 9, 0)) };
          }
        };
    test("tickets-norwich-away.html", dateOf(15, 9, 2012, 12, 45), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testQprAway() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Bondholders - from 9am on Thursday 23 August</DIV>
                new BondHolderTicketsUpdateCommand(gameLocator, dateOf(23, 8, 2012, 9, 0)),
                // <DIV>Priority Point Postal Application - to receive by 10am
                // on Wednesday 22 August for process by Friday 24 August</DIV>
                new PriorityPointTicketsUpdateCommand(gameLocator, dateOf(22, 8, 2012, 10, 0)),
                // <DIV>Season Ticket General Sale - from 9am on Saturday 25
                // August</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(25, 8, 2012, 9, 0)),
                // <DIV>Academy Members - from 9am on Tuesday 28 August </DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(28, 8, 2012, 9, 0)),
                // <DIV>General Sale - from 9am on Wednesday 29
                // August</DIV></TD></TR>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(29, 8, 2012, 9, 0)) };
          }
        };
    test("tickets-qpr-away.html", dateOf(1, 10, 2012, 20, 0), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testSwanseaAway() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {};
          }
        };
    test("tickets-swansea-away.html", dateOf(25, 8, 2012, 12, 45), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testVillaHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Academy Members - Postal/Telephone/Online Bookings -
                // from 9am on Tue 17 July</DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(17, 7, 2012, 9, 0)),
                // <DIV>Season Ticket Holders Additional Ticket - from 9am on
                // Mon 20 Aug</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(23, 7, 2012, 9, 0)),
                // <DIV>General Sale - from 9am Tue 24 July</DIV>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(24, 7, 2012, 9, 0)) };
          }
        };
    test("tickets-villa-home.html", dateOf(18, 8, 2012, 15, 0), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testFulhamHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Academy Members - Postal/Telephone/Online Bookings -
                // from 9am on Tue 17 July</DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(17, 7, 2012, 9, 0)),
                // <DIV>Season Ticket Holders Additional Ticket - from 9am on
                // Mon 23 July</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(23, 7, 2012, 9, 0)),
                // <DIV>General Sale - from 9am Tue 24 July</DIV>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(24, 7, 2012, 9, 0)) };
          }
        };
    test("tickets-fulham-home.html", dateOf(1, 9, 2012, 12, 45), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testArsenalHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Academy Members - Postal/Telephone/Online Bookings -
                // from 9am on Tue 7 Aug</DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(7, 8, 2012, 9, 0)),
                // <DIV>Season Ticket Holders Additional Ticket - from 9am on
                // Mon 20 Aug</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(20, 8, 2012, 9, 0)),
                // <DIV>General Sale - from 9am Tue 21 Aug</DIV>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(21, 8, 2012, 9, 0)) };
          }
        };
    test("tickets-arsenal-home.html", dateOf(6, 10, 2012, 17, 30), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testManCityHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV>Academy Members - Postal/Telephone/Online Bookings -
                // from 9am on Tue 18 Sept</DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(18, 9, 2012, 9, 0)),
                // <DIV>Season Ticket Holders Additional Ticket - from 9am on
                // Mon 1 Oct</DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(1, 10, 2012, 9, 0)),
                // <DIV>General Sale - from 9am Tue 2 Oct</DIV>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(2, 10, 2012, 9, 0)) };
          }
        };
    test("tickets-mancity-home.html", dateOf(3, 11, 2012, 17, 30), expectedGameUpdateCommandsFunction);
  }

  @Test
  public void testCreweHome() throws IOException, URISyntaxException {
    final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction =
        new Function<GameLocator, GameUpdateCommand[]>() {
          @Override
          public GameUpdateCommand[] apply(final GameLocator gameLocator) {
            return new GameUpdateCommand[] {
                // <DIV><STRONG>Season Ticket Holders can apply for tickets from
                // 12.00noon on Fri 17 Aug*.</STRONG></DIV>
                new SeasonTicketsUpdateCommand(gameLocator, dateOf(17, 8, 2012, 12, 0)),
                // <DIV>Academy Members can apply for tickets from 9.00am on Mon
                // 20 Aug.</DIV>
                new AcademyTicketsUpdateCommand(gameLocator, dateOf(20, 8, 2012, 9, 0)),
                // <DIV><STRONG>General Sale tickets will be made available from
                // 9.00am on Wed 22 Aug.</STRONG></DIV>
                new GeneralSaleTicketsUpdateCommand(gameLocator, dateOf(22, 8, 2012, 9, 0)) };
          }
        };
    test("tickets-crewe-home.html", dateOf(28, 8, 2012, 19, 45), expectedGameUpdateCommandsFunction);
  }

}
