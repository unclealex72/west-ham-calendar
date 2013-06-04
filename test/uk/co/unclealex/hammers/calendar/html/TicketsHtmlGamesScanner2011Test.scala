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

import uk.co.unclealex.hammers.calendar.February
import uk.co.unclealex.hammers.calendar.Instant
import uk.co.unclealex.hammers.calendar.January

/**
 * Tests for ticketing information from 2011.
 *
 * @author alex
 */
class TicketsHtmlGamesScanner2011Test extends AbstractTicketsHtmlGamesScannerTest(2011) {

  "The tickets for Blackpool away" should {
    "tickets-blackpool-away.html" on (February(18, 2012) at (15, 0)) expects {
      gameLocator =>
        List(
          PriorityPointTicketsUpdateCommand(gameLocator, January(25, 2012) at (10, 0)),
          BondHolderTicketsUpdateCommand(gameLocator, January(26, 2012) at (9, 0)),
          SeasonTicketsUpdateCommand(gameLocator, January(28, 2012) at (9, 0)),
          AcademyTicketsUpdateCommand(gameLocator, January(30, 2012) at (9, 0)),
          GeneralSaleTicketsUpdateCommand(gameLocator, February(1, 2012) at (9, 0)))
    }
  }

  "The tickets for Southampton at home" should {
    "tickets-southampton-home.html" on (February(14, 2012) at (19, 45)) expects {
      gameLocator =>
        List(
          SeasonTicketsUpdateCommand(gameLocator, January(16, 2012) at (9, 0)),
          AcademyTicketsUpdateCommand(gameLocator, January(10, 2012) at (9, 0)),
          GeneralSaleTicketsUpdateCommand(gameLocator, January(17, 2012) at (9, 0)))
    }
  }

  "The tickets for Peterborough away" should {
    "tickets-peterborough-away.html" on (February(25, 2012) at (12, 45)) expects (_ => List())
  }
}
