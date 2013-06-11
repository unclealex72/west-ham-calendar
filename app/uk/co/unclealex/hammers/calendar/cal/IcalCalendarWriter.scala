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

package uk.co.unclealex.hammers.calendar.cal

import java.io.Writer
import scala.math.BigDecimal._
import org.joda.time.{ DateTime => JodaDateTime }
import uk.co.unclealex.hammers.calendar.model.Location._
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.{ Calendar => ICalendar }
import net.fortuna.ical4j.model.{ DateTime => IDateTime }
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.Summary
import net.fortuna.ical4j.model.property.Url
import java.net.URI
import scala.collection.JavaConversions._
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.util.UidGenerator
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.property.Location
import net.fortuna.ical4j.model.property.BusyType._
import net.fortuna.ical4j.model.property.Transp._
import net.fortuna.ical4j.model.property.Geo
import net.fortuna.ical4j.model.property.Attach
/**
 * @author alex
 *
 */
class IcalCalendarWriter extends CalendarWriter {

  val outputter = new CalendarOutputter(true)

  val timezone: TimeZone =
    TimeZoneRegistryFactory.getInstance.createRegistry().getTimeZone("Europe/London")

  def write(calendar: Calendar, writer: Writer): Unit = {
    val ical = new ICalendar
    List(
      new ProdId("-//unclealex.co.uk//West Ham Calendar 6.0//EN"),
      Version.VERSION_2_0,
      CalScale.GREGORIAN) foreach (ical.getProperties().add)
    calendar.events map (toVEvent) foreach (ical.getComponents().add)
    outputter output (ical, writer)
  }

  def toVEvent(event: Event): VEvent = {
    val properties: Seq[Event => Traversable[Property]] =
      Seq(asStart, asEnd, asSummary, /*asDescription, */ asTimezone, /*asLocation, asAttachments, asBusy,*/ asUid)
    fluent(new VEvent)(ve => properties.foreach(f => f(event).foreach(ve.getProperties.add)))
  }

  def asStart: Event => Property = event => new DtStart(event.dateTime)
  def asEnd: Event => Property = event => new DtEnd(event.dateTime.plus(event.duration))
  def asUid: Event => Property = event => new Uid(event.id)
  def asLocation: Event => Option[Property] = event => event.geoLocation map { gl => new Location(gl.name) }
  def asGeoLocation: Event => Option[Property] = event => event.geoLocation map { gl => new Geo(gl.latitude, gl.longitude) }
  def asBusy: Event => Property = event => if (event.busy) OPAQUE else TRANSPARENT
  def asAttachments: Event => Seq[Property] =
    event => Seq(event.geoLocation map (_.url), event.matchReport).flatten.map(url => new Attach(new URI(url)))
  def asSummary: Event => Property = { event =>
    val swapOnAway: Pair[String, String] => Pair[String, String] = { p =>
      event.location match {
        case HOME => p
        case AWAY => p.swap
      }
    }
    val teams = swapOnAway("West Ham", event.opponents)
    new Summary(s"${teams._1} vs ${teams._2} (${event.competition.name})")
  }

  def asTimezone: Event => Property = _ => timezone.getTimeZoneId
  def asDescription: Event => Option[Property] = { event =>
    val descriptionLine: Pair[String, Option[Any]] => Option[String] = { parts =>
      parts._2 map (value => s"${parts._1}: $value")
    }
    val descriptions = Seq(
      "Result" -> event.result,
      "Attendence" -> event.attendence,
      "Match Report" -> event.matchReport,
      "Location" -> event.geoLocation.map(gl => s"${gl.name}")) map descriptionLine
    if (descriptions isEmpty) None else Some(new Description(descriptions.flatten mkString "\n"))
  }

  implicit def ical(dateTime: JodaDateTime): IDateTime = {
    fluent(new IDateTime(dateTime.getMillis)) { dt =>
      dt.setTimeZone(timezone)
    }
  }

  implicit def timezone(timeZone: TimeZone): VTimeZone = timeZone.getVTimeZone
  implicit def singleProperty: (Event => Property) => (Event => Traversable[Property]) = f => e => Some(f(e))
  implicit def optionalProperty: (Event => Option[Property]) => (Event => Traversable[Property]) = f => e => f(e)
  implicit def bigDecimal: BigDecimal => java.math.BigDecimal = bd => new java.math.BigDecimal(bd.toString)

  def fluent[E](value: E)(block: E => Any): E = {
    block(value)
    value
  }
}