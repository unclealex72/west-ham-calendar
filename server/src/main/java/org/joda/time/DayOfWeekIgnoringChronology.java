/**
 * Copyright 2011 Alex Jones
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
package org.joda.time;

import java.util.Locale;

/**
 * A chronology that ignores changes to the day of the week.
 * @author alex
 *
 */
public class DayOfWeekIgnoringChronology extends Chronology {
	private Chronology chronology;

	public DayOfWeekIgnoringChronology(Chronology chronology) {
		super();
		this.chronology = chronology;
	}

	public int hashCode() {
		return chronology.hashCode();
	}

	public DateTimeZone getZone() {
		return chronology.getZone();
	}

	public Chronology withUTC() {
		return new DayOfWeekIgnoringChronology(chronology.withUTC());
	}

	public Chronology withZone(DateTimeZone zone) {
		return new DayOfWeekIgnoringChronology(chronology.withZone(zone));
	}

	public boolean equals(Object obj) {
		return obj instanceof DayOfWeekIgnoringChronology
				&& chronology.equals(((DayOfWeekIgnoringChronology) obj).chronology);
	}

	public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int millisOfDay) {
		return chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, millisOfDay);
	}

	public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour,
			int secondOfMinute, int millisOfSecond) {
		return chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
				millisOfSecond);
	}

	public long getDateTimeMillis(long instant, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
		return chronology.getDateTimeMillis(instant, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
	}

	public void validate(ReadablePartial partial, int[] values) {
		chronology.validate(partial, values);
	}

	public int[] get(ReadablePartial partial, long instant) {
		return chronology.get(partial, instant);
	}

	public long set(ReadablePartial partial, long instant) {
		return chronology.set(partial, instant);
	}

	public int[] get(ReadablePeriod period, long startInstant, long endInstant) {
		return chronology.get(period, startInstant, endInstant);
	}

	public int[] get(ReadablePeriod period, long duration) {
		return chronology.get(period, duration);
	}

	public long add(ReadablePeriod period, long instant, int scalar) {
		return chronology.add(period, instant, scalar);
	}

	public long add(long instant, long duration, int scalar) {
		return chronology.add(instant, duration, scalar);
	}

	public DurationField millis() {
		return chronology.millis();
	}

	public DateTimeField millisOfSecond() {
		return chronology.millisOfSecond();
	}

	public DateTimeField millisOfDay() {
		return chronology.millisOfDay();
	}

	public DurationField seconds() {
		return chronology.seconds();
	}

	public DateTimeField secondOfMinute() {
		return chronology.secondOfMinute();
	}

	public DateTimeField secondOfDay() {
		return chronology.secondOfDay();
	}

	public DurationField minutes() {
		return chronology.minutes();
	}

	public DateTimeField minuteOfHour() {
		return chronology.minuteOfHour();
	}

	public DateTimeField minuteOfDay() {
		return chronology.minuteOfDay();
	}

	public DurationField hours() {
		return chronology.hours();
	}

	public DateTimeField hourOfDay() {
		return chronology.hourOfDay();
	}

	public DateTimeField clockhourOfDay() {
		return chronology.clockhourOfDay();
	}

	public DurationField halfdays() {
		return chronology.halfdays();
	}

	public DateTimeField hourOfHalfday() {
		return chronology.hourOfHalfday();
	}

	public DateTimeField clockhourOfHalfday() {
		return chronology.clockhourOfHalfday();
	}

	public DateTimeField halfdayOfDay() {
		return chronology.halfdayOfDay();
	}

	public DurationField days() {
		return chronology.days();
	}

	public DateTimeField dayOfWeek() {
		return new IgnoringDateTimeField(chronology.dayOfWeek());
	}

	public DateTimeField dayOfMonth() {
		return chronology.dayOfMonth();
	}

	public DateTimeField dayOfYear() {
		return chronology.dayOfYear();
	}

	public DurationField weeks() {
		return chronology.weeks();
	}

	public DateTimeField weekOfWeekyear() {
		return chronology.weekOfWeekyear();
	}

	public DurationField weekyears() {
		return chronology.weekyears();
	}

	public DateTimeField weekyear() {
		return chronology.weekyear();
	}

	public DateTimeField weekyearOfCentury() {
		return chronology.weekyearOfCentury();
	}

	public DurationField months() {
		return chronology.months();
	}

	public DateTimeField monthOfYear() {
		return chronology.monthOfYear();
	}

	public DurationField years() {
		return chronology.years();
	}

	public DateTimeField year() {
		return chronology.year();
	}

	public DateTimeField yearOfEra() {
		return chronology.yearOfEra();
	}

	public DateTimeField yearOfCentury() {
		return chronology.yearOfCentury();
	}

	public DurationField centuries() {
		return chronology.centuries();
	}

	public DateTimeField centuryOfEra() {
		return chronology.centuryOfEra();
	}

	public DurationField eras() {
		return chronology.eras();
	}

	public DateTimeField era() {
		return chronology.era();
	}

	public String toString() {
		return chronology.toString();
	}
}

/**
 * A {@link DateTimeField} that does nothing when called by a parser.
 * @author alex
 *
 */
class IgnoringDateTimeField extends DateTimeField {
	DateTimeField dateTimeField;
	
	public IgnoringDateTimeField(DateTimeField dateTimeField) {
		super();
		this.dateTimeField = dateTimeField;
	}

	public DateTimeFieldType getType() {
		return dateTimeField.getType();
	}

	public String getName() {
		return dateTimeField.getName();
	}

	public int hashCode() {
		return dateTimeField.hashCode();
	}

	public boolean isSupported() {
		return dateTimeField.isSupported();
	}

	
	public boolean isLenient() {
		return dateTimeField.isLenient();
	}

	public int get(long instant) {
		return dateTimeField.get(instant);
	}

	public String getAsText(long instant, Locale locale) {
		return dateTimeField.getAsText(instant, locale);
	}

	public String getAsText(long instant) {
		return dateTimeField.getAsText(instant);
	}

	public String getAsText(ReadablePartial partial, int fieldValue, Locale locale) {
		return dateTimeField.getAsText(partial, fieldValue, locale);
	}

	public boolean equals(Object obj) {
		return dateTimeField.equals(obj);
	}

	public String getAsText(ReadablePartial partial, Locale locale) {
		return dateTimeField.getAsText(partial, locale);
	}

	public String getAsText(int fieldValue, Locale locale) {
		return dateTimeField.getAsText(fieldValue, locale);
	}

	public String getAsShortText(long instant, Locale locale) {
		return dateTimeField.getAsShortText(instant, locale);
	}

	public String getAsShortText(long instant) {
		return dateTimeField.getAsShortText(instant);
	}

	public String getAsShortText(ReadablePartial partial, int fieldValue, Locale locale) {
		return dateTimeField.getAsShortText(partial, fieldValue, locale);
	}

	public String getAsShortText(ReadablePartial partial, Locale locale) {
		return dateTimeField.getAsShortText(partial, locale);
	}

	public String getAsShortText(int fieldValue, Locale locale) {
		return dateTimeField.getAsShortText(fieldValue, locale);
	}

	public long add(long instant, int value) {
		return dateTimeField.add(instant, value);
	}

	public long add(long instant, long value) {
		return dateTimeField.add(instant, value);
	}

	public int[] add(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.add(instant, fieldIndex, values, valueToAdd);
	}

	public int[] addWrapPartial(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.addWrapPartial(instant, fieldIndex, values, valueToAdd);
	}

	public long addWrapField(long instant, int value) {
		return dateTimeField.addWrapField(instant, value);
	}

	public int[] addWrapField(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.addWrapField(instant, fieldIndex, values, valueToAdd);
	}

	public int getDifference(long minuendInstant, long subtrahendInstant) {
		return dateTimeField.getDifference(minuendInstant, subtrahendInstant);
	}

	public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
		return dateTimeField.getDifferenceAsLong(minuendInstant, subtrahendInstant);
	}

	public long set(long instant, int value) {
		return dateTimeField.set(instant, value);
	}

	public int[] set(ReadablePartial instant, int fieldIndex, int[] values, int newValue) {
		return dateTimeField.set(instant, fieldIndex, values, newValue);
	}

	public long set(long instant, String text, Locale locale) {
		return instant;
	}

	public long set(long instant, String text) {
		return instant;
	}

	public int[] set(ReadablePartial instant, int fieldIndex, int[] values, String text, Locale locale) {
		return values;
	}

	public DurationField getDurationField() {
		return dateTimeField.getDurationField();
	}

	public DurationField getRangeDurationField() {
		return dateTimeField.getRangeDurationField();
	}

	public boolean isLeap(long instant) {
		return dateTimeField.isLeap(instant);
	}

	public int getLeapAmount(long instant) {
		return dateTimeField.getLeapAmount(instant);
	}

	public DurationField getLeapDurationField() {
		return dateTimeField.getLeapDurationField();
	}

	public int getMinimumValue() {
		return dateTimeField.getMinimumValue();
	}

	public int getMinimumValue(long instant) {
		return dateTimeField.getMinimumValue(instant);
	}

	public int getMinimumValue(ReadablePartial instant) {
		return dateTimeField.getMinimumValue(instant);
	}

	public int getMinimumValue(ReadablePartial instant, int[] values) {
		return dateTimeField.getMinimumValue(instant, values);
	}

	public int getMaximumValue() {
		return dateTimeField.getMaximumValue();
	}

	public int getMaximumValue(long instant) {
		return dateTimeField.getMaximumValue(instant);
	}

	public int getMaximumValue(ReadablePartial instant) {
		return dateTimeField.getMaximumValue(instant);
	}

	public int getMaximumValue(ReadablePartial instant, int[] values) {
		return dateTimeField.getMaximumValue(instant, values);
	}

	public int getMaximumTextLength(Locale locale) {
		return dateTimeField.getMaximumTextLength(locale);
	}

	public int getMaximumShortTextLength(Locale locale) {
		return dateTimeField.getMaximumShortTextLength(locale);
	}

	public long roundFloor(long instant) {
		return dateTimeField.roundFloor(instant);
	}

	public long roundCeiling(long instant) {
		return dateTimeField.roundCeiling(instant);
	}

	public long roundHalfFloor(long instant) {
		return dateTimeField.roundHalfFloor(instant);
	}

	public long roundHalfCeiling(long instant) {
		return dateTimeField.roundHalfCeiling(instant);
	}

	public long roundHalfEven(long instant) {
		return dateTimeField.roundHalfEven(instant);
	}

	public long remainder(long instant) {
		return dateTimeField.remainder(instant);
	}

	public String toString() {
		return dateTimeField.toString();
	}
	
	
}
