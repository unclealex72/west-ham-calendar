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

package uk.co.unclealex.hammers.calendar

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * Helper classes used to make dates and times more readable in tests.
 * @author alex
 *
 */
sealed class Month(val name: String, val month: Int) {
  override def toString = name
  def apply(day: Int): MonthAndDay = MonthAndDay(day, this)
  def apply(day: Int, year: Int): Date = Date(apply(day), year)
}

case class MonthAndDay(val day: Int, val month: Month) {
  override def toString = s"$month $day"

  def toDateTime = Date(this, 1970).toDateTime
}

case class Date(val monthAndDay: MonthAndDay, val year: Int) {
  override def toString = s"$monthAndDay, $year"
  def at(hours: Int, minutes: Int) = Instant(this, hours, minutes)
  def toDateTime = at(0, 0) toDateTime

}

object Date {
  def apply(dateTime: DateTime): Date = Instant(dateTime).date

  implicit def asDateTime(date: Date) = date.toDateTime
}

case class Instant(val date: Date, val hours: Int, val minutes: Int) {
  override def toString = {
    def pad(i: Int) = (if (i < 10) "0" else "") + i
    s"$date ${pad(hours)}:${pad(minutes)}"
  }

  def toDateTime =
    new DateTime(date.year, date.monthAndDay.month.month, date.monthAndDay.day, hours, minutes, 0, 0, DateTimeZone.forID("Europe/London"))
}

object Instant {
  val months: List[Month] =
    List(January, February, March, April, May, June, July, August, September, October, November, December)

  def apply(dateTime: DateTime): Instant = {
    months find (month => month.month == dateTime.getMonthOfYear) match {
      case Some(month) => Instant(
        Date(MonthAndDay(dateTime.getDayOfMonth, month), dateTime.getYear),
        dateTime.getHourOfDay, dateTime.getMinuteOfHour)
      case None => throw new IllegalArgumentException(s"Cannot find a month for $dateTime")
    }
  }

  implicit def asDateTime(instant: Instant) = instant.toDateTime

}

case object January extends Month("Janurary", 1)
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
