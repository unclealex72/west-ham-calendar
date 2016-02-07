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
package org.joda.time

import java.util.Locale

/**
 * A chronology that ignores changes to the day of the week.
 * @author alex
 *
 */
class DayOfWeekIgnoringChronology(
  /**
   * The chronology to delegate to.
   */
  val chronology: Chronology) extends Chronology {

  override def hashCode = chronology.hashCode

  def getZone: DateTimeZone = chronology.getZone

  def withUTC = new DayOfWeekIgnoringChronology(chronology.withUTC)

  def withZone(zone: DateTimeZone) = new DayOfWeekIgnoringChronology(chronology.withZone(zone))

  override def equals(obj: Any) =
    obj.isInstanceOf[DayOfWeekIgnoringChronology] && chronology == obj.asInstanceOf[DayOfWeekIgnoringChronology].chronology

  def getDateTimeMillis(year: Int, monthOfYear: Int, dayOfMonth: Int, millisOfDay: Int) = chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, millisOfDay)

  def getDateTimeMillis(year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, minuteOfHour: Int,
    secondOfMinute: Int, millisOfSecond: Int) = chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
    millisOfSecond)

  def getDateTimeMillis(instant: Long, hourOfDay: Int, minuteOfHour: Int, secondOfMinute: Int, millisOfSecond: Int) = chronology.getDateTimeMillis(instant, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond)

  def validate(partial: ReadablePartial, values: Array[Int]) = chronology.validate(partial, values)

  def get(partial: ReadablePartial, instant: Long) = chronology.get(partial, instant)

  def set(partial: ReadablePartial, instant: Long) = chronology.set(partial, instant)

  def get(period: ReadablePeriod, startInstant: Long, endInstant: Long) = chronology.get(period, startInstant, endInstant)

  def get(period: ReadablePeriod, duration: Long) = chronology.get(period, duration)

  def add(period: ReadablePeriod, instant: Long, scalar: Int) = chronology.add(period, instant, scalar)

  def add(instant: Long, duration: Long, scalar: Int) = chronology.add(instant, duration, scalar)

  def millis = chronology.millis

  def millisOfSecond = chronology.millisOfSecond

  def millisOfDay = chronology.millisOfDay

  def seconds = chronology.seconds

  def secondOfMinute = chronology.secondOfMinute

  def secondOfDay = chronology.secondOfDay

  def minutes = chronology.minutes

  def minuteOfHour = chronology.minuteOfHour

  def minuteOfDay = chronology.minuteOfDay

  def hours = chronology.hours

  def hourOfDay = chronology.hourOfDay

  def clockhourOfDay = chronology.clockhourOfDay

  def halfdays = chronology.halfdays

  def hourOfHalfday = chronology.hourOfHalfday

  def clockhourOfHalfday = chronology.clockhourOfHalfday

  def halfdayOfDay = chronology.halfdayOfDay

  def days = chronology.days

  /**
   * Explicitly ignore the day of week.
   * @return An {@link IgnoringDateTimeField}
   */

  def dayOfWeek = new IgnoringDateTimeField(chronology.dayOfWeek)

  def dayOfMonth = chronology.dayOfMonth

  def dayOfYear = chronology.dayOfYear

  def weeks = chronology.weeks

  def weekOfWeekyear = chronology.weekOfWeekyear

  def weekyears = chronology.weekyears

  def weekyear = chronology.weekyear

  def weekyearOfCentury = chronology.weekyearOfCentury

  def months = chronology.months

  def monthOfYear = chronology.monthOfYear

  def years = chronology.years

  def year = chronology.year

  def yearOfEra = chronology.yearOfEra

  def yearOfCentury = chronology.yearOfCentury

  def centuries = chronology.centuries

  def centuryOfEra = chronology.centuryOfEra

  def eras = chronology.eras

  def era = chronology.era

  override def toString = chronology.toString
}

/**
 * A {@link DateTimeField} that does nothing when called by a parser.
 * @author alex
 *
 */
class IgnoringDateTimeField(
  /**
   * The {@link DateTimeField} to delegate to.
   */
  dateTimeField: DateTimeField) extends DateTimeField {

  def getType = dateTimeField.getType

  def getName = dateTimeField.getName

  override def hashCode = dateTimeField.hashCode

  def isSupported = dateTimeField.isSupported

  def isLenient = dateTimeField.isLenient

  def get(instant: Long) = dateTimeField.get(instant)

  def getAsText(instant: Long, locale: Locale) = dateTimeField.getAsText(instant, locale)

  def getAsText(instant: Long) = dateTimeField.getAsText(instant)

  def getAsText(partial: ReadablePartial, fieldValue: Int, locale: Locale) = dateTimeField.getAsText(partial, fieldValue, locale)

  override def equals(obj: Any) = dateTimeField.equals(obj)

  def getAsText(partial: ReadablePartial, locale: Locale) = dateTimeField.getAsText(partial, locale)

  def getAsText(fieldValue: Int, locale: Locale) = dateTimeField.getAsText(fieldValue, locale)

  def getAsShortText(instant: Long, locale: Locale) = dateTimeField.getAsShortText(instant, locale)

  def getAsShortText(instant: Long) = dateTimeField.getAsShortText(instant)

  def getAsShortText(partial: ReadablePartial, fieldValue: Int, locale: Locale) = dateTimeField.getAsShortText(partial, fieldValue, locale)

  def getAsShortText(partial: ReadablePartial, locale: Locale) = dateTimeField.getAsShortText(partial, locale)

  def getAsShortText(fieldValue: Int, locale: Locale) = dateTimeField.getAsShortText(fieldValue, locale)

  def add(instant: Long, value: Int) = dateTimeField.add(instant, value)

  def add(instant: Long, value: Long) = dateTimeField.add(instant, value)

  def add(instant: ReadablePartial, fieldIndex: Int, values: Array[Int], valueToAdd: Int) = dateTimeField.add(instant, fieldIndex, values, valueToAdd)

  def addWrapPartial(instant: ReadablePartial, fieldIndex: Int, values: Array[Int], valueToAdd: Int) = dateTimeField.addWrapPartial(instant, fieldIndex, values, valueToAdd)

  def addWrapField(instant: Long, value: Int) = dateTimeField.addWrapField(instant, value)

  def addWrapField(instant: ReadablePartial, fieldIndex: Int, values: Array[Int], valueToAdd: Int) = dateTimeField.addWrapField(instant, fieldIndex, values, valueToAdd)

  def getDifference(minuendInstant: Long, subtrahendInstant: Long) = dateTimeField.getDifference(minuendInstant, subtrahendInstant)

  def getDifferenceAsLong(minuendInstant: Long, subtrahendInstant: Long) = dateTimeField.getDifferenceAsLong(minuendInstant, subtrahendInstant)

  def set(instant: Long, value: Int) = instant

  def set(instant: ReadablePartial, fieldIndex: Int, values: Array[Int], newValue: Int) = values

  def set(instant: Long, text: String, locale: Locale) = instant

  def set(instant: Long, text: String) = instant

  def set(instant: ReadablePartial, fieldIndex: Int, values: Array[Int], text: String, locale: Locale) = values

  def getDurationField = dateTimeField.getDurationField

  def getRangeDurationField = dateTimeField.getRangeDurationField

  def isLeap(instant: Long) = dateTimeField.isLeap(instant)

  def getLeapAmount(instant: Long) = dateTimeField.getLeapAmount(instant)

  def getLeapDurationField = dateTimeField.getLeapDurationField

  def getMinimumValue = dateTimeField.getMinimumValue

  def getMinimumValue(instant: Long) = dateTimeField.getMinimumValue(instant)

  def getMinimumValue(instant: ReadablePartial) = dateTimeField.getMinimumValue(instant)

  def getMinimumValue(instant: ReadablePartial, values: Array[Int]) = dateTimeField.getMinimumValue(instant, values)

  def getMaximumValue = dateTimeField.getMaximumValue

  def getMaximumValue(instant: Long) = dateTimeField.getMaximumValue(instant)

  def getMaximumValue(instant: ReadablePartial) = dateTimeField.getMaximumValue(instant)

  def getMaximumValue(instant: ReadablePartial, values: Array[Int]) = dateTimeField.getMaximumValue(instant, values)

  def getMaximumTextLength(locale: Locale) = dateTimeField.getMaximumTextLength(locale)

  def getMaximumShortTextLength(locale: Locale) = dateTimeField.getMaximumShortTextLength(locale)

  def roundFloor(instant: Long) = dateTimeField.roundFloor(instant)

  def roundCeiling(instant: Long) = dateTimeField.roundCeiling(instant)

  def roundHalfFloor(instant: Long) = dateTimeField.roundHalfFloor(instant)

  def roundHalfCeiling(instant: Long) = dateTimeField.roundHalfCeiling(instant)

  def roundHalfEven(instant: Long) = dateTimeField.roundHalfEven(instant)

  def remainder(instant: Long) = dateTimeField.remainder(instant)

  override def toString = dateTimeField.toString
}
