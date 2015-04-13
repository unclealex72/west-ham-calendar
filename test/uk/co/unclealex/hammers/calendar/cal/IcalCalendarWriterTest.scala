/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.hammers.calendar.cal

import org.specs2.mutable.Specification
import uk.co.unclealex.hammers.calendar._
import uk.co.unclealex.hammers.calendar.model.Competition._
import uk.co.unclealex.hammers.calendar.model.Location._
import uk.co.unclealex.hammers.calendar.geo.GeoLocation._
import org.joda.time.Duration
import uk.co.unclealex.hammers.calendar.dates.NowService
import scala.collection.SortedSet
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @author alex
 *
 */
class IcalCalendarWriterTest extends Specification {

  "Tottingham at home" should {
    "be outputted correctly" in {
      val tottinghamHome = new Event(
        id = "1",
        competition = PREM,
        location = HOME,
        geoLocation = Some(WEST_HAM),
        opponents = "Tottingham",
        dateTime = September(5, 2013) at (15, 0),
        duration = 2 hours,
        result = None,
        attendence = None,
        matchReport = None,
        televisionChannel = None,
        busy = true,
        dateCreated = September(4, 2013) at (10, 0),
        lastUpdated = September(5, 2013) at (11, 0))
      print(tottinghamHome) must be equalTo (expectedTottinghamHome.replace("\n", "\r\n").trim)
    }
  }

  "Southampton away" should {
    "be outputted correctly" in {
      val southamptonAway = new Event(
        id = "2",
        competition = FACP,
        location = AWAY,
        geoLocation = Some(SOUTHAMPTON),
        opponents = "Southampton",
        dateTime = October(5, 2013) at (15, 0),
        duration = 2 hours,
        result = None,
        attendence = None,
        matchReport = None,
        televisionChannel = None,
        busy = false,
        dateCreated = October(4, 2013) at (10, 0),
        lastUpdated = October(5, 2013) at (11, 0))
      print(southamptonAway) must be equalTo (expectedSouthamptonAway.replace("\n", "\r\n").trim)
    }
  }

  "Liverpool away" should {
    "be outputted correctly" in {
      val liverpoolAway = new Event(
        id = "2",
        competition = PREM,
        location = AWAY,
        geoLocation = Some(LIVERPOOL),
        opponents = "Liverpool",
        dateTime = October(5, 2013) at (15, 0),
        duration = 1 hour,
        result = Some("0-3"),
        attendence = Some(25000),
        matchReport = Some("http://awesthammatchreport.com/match/report/westham-vs-liverpool-away"),
        televisionChannel = None,
        busy = false,
        dateCreated = October(4, 2013) at (10, 0),
        lastUpdated = October(5, 2013) at (11, 0))
      print(liverpoolAway) must be equalTo (expectedLiverpoolAway.replace("\n", "\r\n").trim)
    }
  }

  def print(event: Event): String = {
    val nowService = new NowService() {
      def now = June(11, 2013) at (19, 3)
    }
    val icalCalendarWriter = new IcalCalendarWriter(nowService)
    val writer = new StringWriter
    icalCalendarWriter.write(Calendar("id", "title", SortedSet(event)), writer)
    writer.toString.trim
  }

  val expectedTottinghamHome = """
BEGIN:VCALENDAR
PRODID:-//unclealex.co.uk//West Ham Calendar 6.0//EN
VERSION:2.0
METHOD:PUBLISH
X-WR-CALNAME:title
X-WR-CALDESC:title
X-WR-TIMEZONE:Europe/London
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTSTART:20130905T140000Z
DTEND:20130905T160000Z
DTSTAMP:20130611T180300Z
UID:1@calendar.unclealex.co.uk
CREATED:20130904T090000Z
DESCRIPTION:
LAST-MODIFIED:20130905T100000Z
LOCATION:The Boleyn Ground
SEQUENCE:0
STATUS:CONFIRMED
SUMMARY:West Ham vs Tottingham (Premiership)
TRANSP:OPAQUE
END:VEVENT
END:VCALENDAR"""

  val expectedSouthamptonAway = """
BEGIN:VCALENDAR
PRODID:-//unclealex.co.uk//West Ham Calendar 6.0//EN
VERSION:2.0
METHOD:PUBLISH
X-WR-CALNAME:title
X-WR-CALDESC:title
X-WR-TIMEZONE:Europe/London
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTSTART:20131005T140000Z
DTEND:20131005T160000Z
DTSTAMP:20130611T180300Z
UID:2@calendar.unclealex.co.uk
CREATED:20131004T090000Z
DESCRIPTION:
LAST-MODIFIED:20131005T100000Z
LOCATION:St Mary's Stadium
SEQUENCE:0
STATUS:CONFIRMED
SUMMARY:Southampton vs West Ham (FA Cup)
TRANSP:TRANSPARENT
END:VEVENT
END:VCALENDAR
"""

  val expectedLiverpoolAway = """
BEGIN:VCALENDAR
PRODID:-//unclealex.co.uk//West Ham Calendar 6.0//EN
VERSION:2.0
METHOD:PUBLISH
X-WR-CALNAME:title
X-WR-CALDESC:title
X-WR-TIMEZONE:Europe/London
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTSTART:20131005T140000Z
DTEND:20131005T150000Z
DTSTAMP:20130611T180300Z
UID:2@calendar.unclealex.co.uk
CREATED:20131004T090000Z
DESCRIPTION:Result: 0-3\nAttendence: 25000\nMatch Report: http://awestham
 matchreport.com/match/report/westham-vs-liverpool-away
LAST-MODIFIED:20131005T100000Z
LOCATION:Anfield
SEQUENCE:0
STATUS:CONFIRMED
SUMMARY:Liverpool vs West Ham (Premiership)
ATTACH:http://awesthammatchreport.com/match/report/westham-vs-liverpool-a
 way
TRANSP:TRANSPARENT
END:VEVENT
END:VCALENDAR
"""

  implicit class IntegerImplicits(hrs: Int) {
    def hour = Duration.standardHours(hrs)
    def hours = Duration.standardHours(hrs)
  }
}