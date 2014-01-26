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

import uk.co.unclealex.hammers.calendar._

/**
 * Tests for tickets in the 2013 season.
 *
 * @author alex
 */
class TicketsHtmlGamesScanner2013Test extends AbstractTicketsHtmlGamesScannerTest(2013) {

  "The tickets for Chelsea away" should {
    //Wednesday 29 January 2014 - 7.45pm
    "tickets-chelsea-away.html" on (January(29, 2014) at (19, 45)) expects { gameLocator =>
      List(
        //<P>Priority Post: Tuesday 31 December to be processed by Friday 3 January.
        PriorityPointTicketsUpdateCommand(gameLocator, December(31, 2013) at (9, 0)),
        //<BR>Bond Holders and Corporate: Thursday 2 January
        BondHolderTicketsUpdateCommand(gameLocator, January(2, 2014) at (9, 0)),
        //<BR>Season Ticket General Sale: Saturday 4 January
        SeasonTicketsUpdateCommand(gameLocator, January(4, 2014) at (9, 0)),
        //<BR>Members' Sale: Monday 6 January
        AcademyTicketsUpdateCommand(gameLocator, January(6, 2014) at (9, 0)),
        //<BR>General Sale: Tuesday 7 January</P>
        GeneralSaleTicketsUpdateCommand(gameLocator, January(7, 2014) at (9, 0)))
    }
  }

  "The tickets for Everton away" should {
    //Saturday 1 March&nbsp;2014, 3pm
    "tickets-everton-away.html" on (March(1, 2014) at (15, 0)) expects { gameLocator =>
      List(
        //<P>Priority Post: Wednesday 29 January (to be processed by Friday 31 January
        PriorityPointTicketsUpdateCommand(gameLocator, January(29, 2014) at (9, 0)),
        //<BR>Corporate/Bond Holders: Thursday 30 January
        BondHolderTicketsUpdateCommand(gameLocator, January(30, 2014) at (9, 0)),
        //<BR>Season Ticket General Sale: Saturday 1 February
        SeasonTicketsUpdateCommand(gameLocator, February(1, 2014) at (9, 0)),
        //<BR>Members Sale: Monday&nbsp;3 February
        AcademyTicketsUpdateCommand(gameLocator, February(3, 2014) at (9, 0)),
        //<BR>General Sale: Tuesday 4 February</P>
        GeneralSaleTicketsUpdateCommand(gameLocator, February(4, 2014) at (9, 0)))
    }
  }

  "The tickets for Aston Villa away" should {
    //Saturday 8 February&nbsp;2014, 3pm
    "tickets-villa-away.html" on (February(8, 2014) at (15, 0)) expects { gameLocator =>
      List(
        //<p>Priority Post: Wednesday 15 January (to be processed by Friday 17 January
        PriorityPointTicketsUpdateCommand(gameLocator, January(15, 2014) at (9, 0)),
        //<br>Corporate/Bond Holders: Thursday 16 January
        BondHolderTicketsUpdateCommand(gameLocator, January(16, 2014) at (9, 0)),
        //<br>Season Ticket General Sale: Saturday 18 January
        SeasonTicketsUpdateCommand(gameLocator, January(18, 2014) at (9, 0)),
        //<br>Members Sale: Monday 20 January
        AcademyTicketsUpdateCommand(gameLocator, January(20, 2014) at (9, 0)),
        //<br>General Sale: Tuesday 21 January</p>
        GeneralSaleTicketsUpdateCommand(gameLocator, January(21, 2014) at (9, 0)))
    }
  }

  "The tickets for Hull at home" should {
    //Saturday 8 March 2014, 3pm
    "tickets-hull-home.html" on (March(8, 2014) at (15, 0)) expects { gameLocator =>
      List(
        //<P>Academy Members Postal Applications - Monday 27 January
        AcademyPostalTicketsUpdateCommand(gameLocator, January(27, 2014) at (9, 0)),
        //<BR>Academy Members - Tuesday&nbsp;28 January
        AcademyTicketsUpdateCommand(gameLocator, January(28, 2014) at (9, 0)),
        //<BR>Season Ticket Holders Additional Ticket - Monday 2 February
        SeasonTicketsUpdateCommand(gameLocator, February(2, 2014) at (9, 0)),
        //<BR>General Sale Postal Applications - Monday 2 February
        GeneralSalePostalTicketsUpdateCommand(gameLocator, February(2, 2014) at (9, 0)),
        //<BR>General Sale - Tuesday 3 February
        GeneralSaleTicketsUpdateCommand(gameLocator, February(3, 2014) at (9, 0)))
    }
  }

  "The tickets for Southampton at home" should {
    //Saturday 22 February 2014, 3pm
    "tickets-southampton-home.html" on (February(22, 2014) at (15, 0)) expects { gameLocator =>
      List(
        //<P>Academy Members Postal Applications - Monday 6 January
        AcademyPostalTicketsUpdateCommand(gameLocator, January(6, 2014) at (9, 0)),
        //<BR>Academy Members - Tuesday 7 January
        AcademyTicketsUpdateCommand(gameLocator, January(7, 2014) at (9, 0)),
        //<BR>Season Ticket Holders Additional Ticket - Monday 20 January
        SeasonTicketsUpdateCommand(gameLocator, January(20, 2014) at (9, 0)),
        //<BR>General Sale Postal Applications - Monday 20 January
        GeneralSalePostalTicketsUpdateCommand(gameLocator, January(20, 2014) at (9, 0)),
        //<BR>General Sale - Tuesday 21 January
        GeneralSaleTicketsUpdateCommand(gameLocator, January(21, 2014) at (9, 0)))
    }
  }
}
