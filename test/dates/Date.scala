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

package dates

import java.time.{Instant, LocalDateTime, ZoneId, ZonedDateTime}

import dates.Date.asZonedDateTime

/**
 * Helper classes used to make dates and times more readable in tests.
 * @author alex
 *
 */
sealed class Month(val name: String, val month: Int) {
  override def toString: String = name
  def apply(day: Int): MonthAndDay = MonthAndDay(day, this)
  def apply(day: Int, year: Int): Date = Date(apply(day), year)
}

case class MonthAndDay(day: Int, month: Month) {
  override def toString = s"$month $day"

  def toZonedDateTime: ZonedDateTime = Date(this, 1970).toZonedDateTime
}

case class Date(monthAndDay: MonthAndDay, year: Int) {
  override def toString = s"$monthAndDay, $year"
  def at(hours: Int, minutes: Int): ZonedDateTime = TimeOfDay(this, hours, minutes).toZonedDateTime
  def at(hours: Int): Hour = Hour(this, hours)
  def toZonedDateTime: ZonedDateTime = at(0, 0)
}

case class Hour(date: Date, hours: Int) {

  def am: ZonedDateTime = toZonedDateTime(0)
  def pm: ZonedDateTime = toZonedDateTime(12)

  private def toZonedDateTime(hourModifier: Int) = TimeOfDay(date, hours + hourModifier, 0).toZonedDateTime
}
object Date {
  def apply(zonedDateTime: ZonedDateTime): Date = TimeOfDay(zonedDateTime).date

  implicit def asZonedDateTime(date: Date): ZonedDateTime = date.toZonedDateTime
  implicit def asInstant(date: Date): Instant = asZonedDateTime(date).toInstant
}

case class TimeOfDay(date: Date, hours: Int, minutes: Int) {
  override def toString: String = {
    def pad(i: Int) = (if (i < 10) "0" else "") + i
    s"$date ${pad(hours)}:${pad(minutes)}"
  }

  def toZonedDateTime: ZonedDateTime = {
    val monthAndDay = date.monthAndDay
    LocalDateTime.of(
      date.year, monthAndDay.month.month, monthAndDay.day, hours, minutes).atZone(ZoneId.of("Europe/London"))
  }
}

object TimeOfDay {
  val months: List[Month] =
    List(January, February, March, April, May, June, July, August, September, October, November, December)

  def apply(zonedDateTime: ZonedDateTime): TimeOfDay = {
    months find (month => month.month == zonedDateTime.getMonthValue) match {
      case Some(month) => TimeOfDay(
        Date(MonthAndDay(zonedDateTime.getDayOfMonth, month), zonedDateTime.getYear),
        zonedDateTime.getHour, zonedDateTime.getMinute)
      case None => throw new IllegalArgumentException(s"Cannot find a month for $zonedDateTime")
    }
  }

  implicit def asZonedDateTime(timeOfDay: TimeOfDay): ZonedDateTime = timeOfDay.toZonedDateTime
  implicit def asInstant(timeOfDay: TimeOfDay): Instant = asZonedDateTime(timeOfDay).toInstant

}

case object January extends Month("January", 1)
case object February extends Month("February", 2)
case object March extends Month("March", 3)
case object April extends Month("April", 4)
case object May extends Month("May", 5)
case object June extends Month("June", 6)
case object July extends Month("July", 7)
case object August extends Month("August", 8)
case object September extends Month("September", 9)
case object October extends Month("October", 10)
case object November extends Month("November", 11)
case object December extends Month("December", 12)
