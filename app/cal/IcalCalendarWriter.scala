/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */

package cal

import java.io.Writer
import java.net.URI
import javax.inject.Inject

import dates.NowService
import model.Location._
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Status._
import net.fortuna.ical4j.model.property.Transp._
import net.fortuna.ical4j.model.property.{Attach, CalScale, Created, Description, DtEnd, DtStamp, DtStart, LastModified, Location, Method, ProdId, Sequence, Summary, Uid, Version, XProperty}
import net.fortuna.ical4j.model.{Calendar => ICalendar, DateTime => IDateTime, Property, PropertyList}
import org.joda.time.{DateTime => JodaDateTime}

import scala.language.implicitConversions

/*
 * A calendar writer that creates iCal calendars that are compatible with at least Google and Mozilla Thunderbird
 * @author alex
 *
 */
class IcalCalendarWriter @Inject() (
  /**
   * The service used to get the curent time for DTSTAMP properties.
   */
  nowService: NowService) extends CalendarWriter {

  /**
   * The calendar outputter that acutally outputs the calendar.
   */
  val outputter = new CalendarOutputter(false)

  def mimeType = "text/calendar"

  def write(calendar: Calendar, writer: Writer, linkFactory: LinkFactory): Unit = {
    val ical = new ICalendar
    List(
      new ProdId("-//unclealex.co.uk//West Ham Calendar 6.0//EN"),
      Version.VERSION_2_0,
      Method.PUBLISH,
      new XProperty("X-WR-CALNAME", calendar.title),
      new XProperty("X-WR-CALDESC", calendar.title),
      new XProperty("X-WR-TIMEZONE", "Europe/London"),
      CalScale.GREGORIAN) foreach ical.getProperties().add
    calendar.events.map(toVEvent(linkFactory)).foreach(ical.getComponents().add)
    outputter.output(ical, writer)
  }

  /**
   * Output a single event.
   */
  def toVEvent(linkFactory: LinkFactory)(event: Event): VEvent = {
    val properties: Seq[Event => Traversable[Property]] =
      Seq(DTSTART, DTEND, DTSTAMP, UID, CREATED, DESCRIPTION, LAST_MODIFIED, LOCATION, SEQUENCE, STATUS, SUMMARY, MATCH_REPORT, LOCATION_URL(linkFactory), TRANSP)
    fluent(new VEvent(new PropertyList))(ve => properties.foreach(f => f(event).foreach(ve.getProperties.add)))
  }

  /**
   * Create a DTSTART property
   */
  def DTSTART: Event => Property = event => new DtStart(event.dateTime)

  /**
   * Create a DTEND property
   */
  def DTEND: Event => Property = event => new DtEnd(event.dateTime.plus(event.duration))

  /**
   * Create a DTSAMP property
   */
  def DTSTAMP: Event => Property = event => new DtStamp(nowService.now)

  /**
   * Create a UID property
   */
  def UID: Event => Property = event => new Uid(s"${event.id}@calendar.unclealex.co.uk")

  /**
   * Create a CREATED property
   */
  def CREATED: Event => Property = event => new Created(event.dateCreated)

  /**
   * Create a DESCRIPTION property
   */
  def DESCRIPTION: Event => Option[Property] = { event =>
    val descriptionLine: ((String, Option[Any])) => Option[String] = { parts =>
      parts._2.map(value => s"${parts._1}: $value")
    }
    val descriptions = Seq(
      "Result" -> event.result,
      "Attendence" -> event.attendence,
      "Match Report" -> event.matchReport) map descriptionLine
    if (descriptions.isEmpty) None else Some(new Description(descriptions.flatten.mkString("\n")))
  }

  /**
   * Create a LAST-MODIFIED property
   */
  def LAST_MODIFIED: Event => Property = event => new LastModified(event.lastUpdated)

  /**
   * Create a LOCATION property
   */
  def LOCATION: Event => Option[Property] = event => event.geoLocation map { gl => new Location(gl.name) }

  /**
   * Create a SEQUENCE property
   */
  def SEQUENCE: Event => Property = _ => new Sequence(0)

  /**
   * Create a STATUS property
   */
  def STATUS: Event => Property = _ => VEVENT_CONFIRMED

  /**
   * Create a TRANSP property
   */
  def TRANSP: Event => Property = event => if (event.busy) OPAQUE else TRANSPARENT

  /**
   * Create a SUMMARY property
   */
  def SUMMARY: Event => Property = { event =>
    val swapOnAway: ((String, String)) => (String, String) = { p =>
      event.location match {
        case HOME => p
        case AWAY => p.swap
      }
    }
    val teams = swapOnAway("West Ham", event.opponents)
    new Summary(s"${teams._1} vs ${teams._2} (${event.competition.name})")
  }

  /**
   * Create an ATTACH property
   */
  def MATCH_REPORT: Event => Option[Property] = event => event.matchReport.map(url => new Attach(new URI(url)))

  def LOCATION_URL(linkFactory: LinkFactory): Event => Option[Property] = event =>
    Some(new Attach(linkFactory.locationLink(event.gameId)))

  /**
   * A small utility that allows a mutable object to be created, mutated and then returned.
   */
  def fluent[E](value: E)(block: E => Any): E = {
    block(value)
    value
  }

  // Implicits

  /**
   * An implicit used to create a Ical DateTime from a Joda DateTime
   */
  implicit def ical(dateTime: JodaDateTime): IDateTime = {
    fluent(new IDateTime(dateTime.getMillis)) { dt =>
      dt.setUtc(true)
    }
  }

  /**
   * An implicit to convert Scala BigDecimals into Java BigDecimals
   */
  implicit def bigDecimal: BigDecimal => java.math.BigDecimal = bd => new java.math.BigDecimal(bd.toString)

  /**
   * Implicits to allow the property generating functions to either return an optional or single property instead
   * of a sequence of properties.
   */
  implicit def singleProperty: (Event => Property) => (Event => Traversable[Property]) = f => e => Some(f(e))
  implicit def optionalProperty: (Event => Option[Property]) => (Event => Traversable[Property]) = f => e => f(e)

}