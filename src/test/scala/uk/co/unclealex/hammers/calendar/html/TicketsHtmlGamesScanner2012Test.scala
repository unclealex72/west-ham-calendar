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
 * Tests for tickets in the 2012 season.
 *
 * @author alex
 */
class TicketsHtmlGamesScanner2012Test extends AbstractTicketsHtmlGamesScannerTest(2012) {

  "The tickets for Norwich away" should {
    "tickets-norwich-away.html" on (September(15, 2012) at (12, 45)) expects { gameLocator =>
      List(
        // <DIV>Bondholders - from 9am on Thursday 16 August</DIV>
        BondHolderTicketsUpdateCommand(gameLocator, Some(August(16, 2012) at (9, 0))),
        // <DIV>Priority Point Postal Application - to receive by 10am
        // on Wednesday 15 August for process by Friday 17 August</DIV>
        PriorityPointTicketsUpdateCommand(gameLocator, Some(August(15, 2012) at (10, 0))),
        // <DIV>Season Ticket General Sale - from 9am on Saturday 18
        // August</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(August(18, 2012) at (9, 0))),
        // <DIV>Academy Members - from 9am on Monday 20 August </DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(August(20, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am on Tuesday 21
        // August</DIV></TD></TR>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(August(21, 2012) at (9, 0))))
    }
  }

  "The tickets for QPR away" should {
    "tickets-qpr-away.html" on (October(1, 2012) at (20, 0)) expects { gameLocator =>
      List(
        // <DIV>Bondholders - from 9am on Thursday 23 August</DIV>
        BondHolderTicketsUpdateCommand(gameLocator, Some(August(23, 2012) at (9, 0))),
        // <DIV>Priority Point Postal Application - to receive by 10am
        // on Wednesday 22 August for process by Friday 24 August</DIV>
        PriorityPointTicketsUpdateCommand(gameLocator, Some(August(22, 2012) at (10, 0))),
        // <DIV>Season Ticket General Sale - from 9am on Saturday 25
        // August</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(August(25, 2012) at (9, 0))),
        // <DIV>Academy Members - from 9am on Tuesday 28 August </DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(August(28, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am on Wednesday 29
        // August</DIV></TD></TR>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(August(29, 2012) at (9, 0))))
    }
  }

  "The tickets for Swansea away" should {
    "tickets-swansea-away.html" on (August(25, 2012) at (12, 45)) expects (_ => List())
  }

  "The tickets for Aston Villa at home" should {
    "tickets-villa-home.html" on (August(18, 2012) at (15, 0)) expects { gameLocator =>
      List( // <DIV>Academy Members - Postal/Telephone/Online Bookings -
        // from 9am on Tue 17 July</DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(July(17, 2012) at (9, 0))),
        // <DIV>Season Ticket Holders Additional Ticket - from 9am on
        // Mon 20 Aug</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(July(23, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am Tue 24 July</DIV>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(July(24, 2012) at (9, 0))))
    }
  }

  "The tickets for Fulham at home" should {
    "tickets-fulham-home.html" on (September(1, 2012) at (12, 45)) expects { gameLocator =>
      List( // <DIV>Academy Members - Postal/Telephone/Online Bookings -
        // from 9am on Tue 17 July</DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(July(17, 2012) at (9, 0))),
        // <DIV>Season Ticket Holders Additional Ticket - from 9am on
        // Mon 23 July</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(July(23, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am Tue 24 July</DIV>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(July(24, 2012) at (9, 0))))
    }
  }

  "The tickets for Arsenal at home" should {
    "tickets-arsenal-home.html" on (October(6, 2012) at (17, 30)) expects { gameLocator =>
      List( // <DIV>Academy Members - Postal/Telephone/Online Bookings -
        // from 9am on Tue 7 Aug</DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(August(7, 2012) at (9, 0))),
        // <DIV>Season Ticket Holders Additional Ticket - from 9am on
        // Mon 20 Aug</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(August(20, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am Tue 21 Aug</DIV>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(August(21, 2012) at (9, 0))))
    }
  }

  "The tickets for Man City at home" should {
    "tickets-mancity-home.html" on (November(3, 2012) at (17, 30)) expects { gameLocator =>
      List(
        // <DIV>Academy Members - Postal/Telephone/Online Bookings -
        // from 9am on Tue 18 Sept</DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(September(18, 2012) at (9, 0))),
        // <DIV>Season Ticket Holders Additional Ticket - from 9am on
        // Mon 1 Oct</DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(October(1, 2012) at (9, 0))),
        // <DIV>General Sale - from 9am Tue 2 Oct</DIV>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(October(2, 2012) at (9, 0))))
    }
  }

  "The tickets for Crewe at home" should {
    "tickets-crewe-home.html" on (August(28, 2012) at (19, 45)) expects { gameLocator =>
      List(
        // <DIV><STRONG>Season Ticket Holders can apply for tickets from
        // 12.00noon on Fri 17 Aug*.</STRONG></DIV>
        SeasonTicketsUpdateCommand(gameLocator, Some(August(17, 2012) at (12, 0))),
        // <DIV>Academy Members can apply for tickets from 9.00am on Mon
        // 20 Aug.</DIV>
        AcademyTicketsUpdateCommand(gameLocator, Some(August(20, 2012) at (9, 0))),
        // <DIV><STRONG>General Sale tickets will be made available from
        // 9.00am on Wed 22 Aug.</STRONG></DIV>
        GeneralSaleTicketsUpdateCommand(gameLocator, Some(August(22, 2012) at (9, 0))))
    }
  }
}
