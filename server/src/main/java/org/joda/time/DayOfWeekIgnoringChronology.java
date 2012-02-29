/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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
package org.joda.time;

import java.util.Locale;


/**
 * A chronology that ignores changes to the day of the week.
 * @author alex
 *
 */
public class DayOfWeekIgnoringChronology extends Chronology {
	
	/**
	 * The chronology to delegate to.
	 */
	private Chronology chronology;

	/**
	 * Create a new chronology.
	 * @param delegateChronology The chronology to delegate to.
	 */
	public DayOfWeekIgnoringChronology(final Chronology delegateChronology) {
		super();
		chronology = delegateChronology;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return chronology.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeZone getZone() {
		return chronology.getZone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Chronology withUTC() {
		return new DayOfWeekIgnoringChronology(chronology.withUTC());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Chronology withZone(DateTimeZone zone) {
		return new DayOfWeekIgnoringChronology(chronology.withZone(zone));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DayOfWeekIgnoringChronology
				&& chronology.equals(((DayOfWeekIgnoringChronology) obj).chronology);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int millisOfDay) {
		return chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, millisOfDay);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour,
			int secondOfMinute, int millisOfSecond) {
		return chronology.getDateTimeMillis(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
				millisOfSecond);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getDateTimeMillis(long instant, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
		return chronology.getDateTimeMillis(instant, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate(ReadablePartial partial, int[] values) {
		chronology.validate(partial, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] get(ReadablePartial partial, long instant) {
		return chronology.get(partial, instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long set(ReadablePartial partial, long instant) {
		return chronology.set(partial, instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] get(ReadablePeriod period, long startInstant, long endInstant) {
		return chronology.get(period, startInstant, endInstant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] get(ReadablePeriod period, long duration) {
		return chronology.get(period, duration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long add(ReadablePeriod period, long instant, int scalar) {
		return chronology.add(period, instant, scalar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long add(long instant, long duration, int scalar) {
		return chronology.add(instant, duration, scalar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField millis() {
		return chronology.millis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField millisOfSecond() {
		return chronology.millisOfSecond();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField millisOfDay() {
		return chronology.millisOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField seconds() {
		return chronology.seconds();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField secondOfMinute() {
		return chronology.secondOfMinute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField secondOfDay() {
		return chronology.secondOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField minutes() {
		return chronology.minutes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField minuteOfHour() {
		return chronology.minuteOfHour();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField minuteOfDay() {
		return chronology.minuteOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField hours() {
		return chronology.hours();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField hourOfDay() {
		return chronology.hourOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField clockhourOfDay() {
		return chronology.clockhourOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField halfdays() {
		return chronology.halfdays();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField hourOfHalfday() {
		return chronology.hourOfHalfday();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField clockhourOfHalfday() {
		return chronology.clockhourOfHalfday();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField halfdayOfDay() {
		return chronology.halfdayOfDay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField days() {
		return chronology.days();
	}

	/**
	 * Explicitly ignore the day of week.
	 * @return An {@link IgnoringDateTimeField}
	 */
	@Override
	public DateTimeField dayOfWeek() {
		return new IgnoringDateTimeField(chronology.dayOfWeek());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField dayOfMonth() {
		return chronology.dayOfMonth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField dayOfYear() {
		return chronology.dayOfYear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField weeks() {
		return chronology.weeks();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField weekOfWeekyear() {
		return chronology.weekOfWeekyear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField weekyears() {
		return chronology.weekyears();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField weekyear() {
		return chronology.weekyear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField weekyearOfCentury() {
		return chronology.weekyearOfCentury();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField months() {
		return chronology.months();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField monthOfYear() {
		return chronology.monthOfYear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField years() {
		return chronology.years();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField year() {
		return chronology.year();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField yearOfEra() {
		return chronology.yearOfEra();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField yearOfCentury() {
		return chronology.yearOfCentury();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField centuries() {
		return chronology.centuries();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField centuryOfEra() {
		return chronology.centuryOfEra();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField eras() {
		return chronology.eras();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeField era() {
		return chronology.era();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	
	/**
	 * The {@link DateTimeField} to delegate to.
	 */
	private DateTimeField dateTimeField;
	
	/**
	 * Create a new instance.
	 * @param dateTimeFieldDelegate The {@link DateTimeField} to delegate to.
	 */
	public IgnoringDateTimeField(DateTimeField dateTimeFieldDelegate) {
		super();
		dateTimeField = dateTimeFieldDelegate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTimeFieldType getType() {
		return dateTimeField.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return dateTimeField.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return dateTimeField.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSupported() {
		return dateTimeField.isSupported();
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLenient() {
		return dateTimeField.isLenient();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(long instant) {
		return dateTimeField.get(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText(long instant, Locale locale) {
		return dateTimeField.getAsText(instant, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText(long instant) {
		return dateTimeField.getAsText(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText(ReadablePartial partial, int fieldValue, Locale locale) {
		return dateTimeField.getAsText(partial, fieldValue, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return dateTimeField.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText(ReadablePartial partial, Locale locale) {
		return dateTimeField.getAsText(partial, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsText(int fieldValue, Locale locale) {
		return dateTimeField.getAsText(fieldValue, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsShortText(long instant, Locale locale) {
		return dateTimeField.getAsShortText(instant, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsShortText(long instant) {
		return dateTimeField.getAsShortText(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsShortText(ReadablePartial partial, int fieldValue, Locale locale) {
		return dateTimeField.getAsShortText(partial, fieldValue, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsShortText(ReadablePartial partial, Locale locale) {
		return dateTimeField.getAsShortText(partial, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAsShortText(int fieldValue, Locale locale) {
		return dateTimeField.getAsShortText(fieldValue, locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long add(long instant, int value) {
		return dateTimeField.add(instant, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long add(long instant, long value) {
		return dateTimeField.add(instant, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] add(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.add(instant, fieldIndex, values, valueToAdd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] addWrapPartial(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.addWrapPartial(instant, fieldIndex, values, valueToAdd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long addWrapField(long instant, int value) {
		return dateTimeField.addWrapField(instant, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] addWrapField(ReadablePartial instant, int fieldIndex, int[] values, int valueToAdd) {
		return dateTimeField.addWrapField(instant, fieldIndex, values, valueToAdd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDifference(long minuendInstant, long subtrahendInstant) {
		return dateTimeField.getDifference(minuendInstant, subtrahendInstant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getDifferenceAsLong(long minuendInstant, long subtrahendInstant) {
		return dateTimeField.getDifferenceAsLong(minuendInstant, subtrahendInstant);
	}

	/**
	 * <p>This implementation explicitly does nothing.</p>
	 * {@inheritDoc}
	 */
	@Override
	public long set(long instant, int value) {
		return instant;
	}

	/**
	 * <p>This implementation explicitly does nothing.</p>
	 * {@inheritDoc}
	 */
	@Override
	public int[] set(ReadablePartial instant, int fieldIndex, int[] values, int newValue) {
		return values;
	}

	/**
	 * <p>This implementation explicitly does nothing.</p>
	 * {@inheritDoc}
	 */
	@Override
	public long set(long instant, String text, Locale locale) {
		return instant;
	}

	/**
	 * <p>This implementation explicitly does nothing.</p>
	 * {@inheritDoc}
	 */
	@Override
	public long set(long instant, String text) {
		return instant;
	}

	/**
	 * <p>This implementation explicitly does nothing.</p>
	 * {@inheritDoc}
	 */
	@Override
	public int[] set(ReadablePartial instant, int fieldIndex, int[] values, String text, Locale locale) {
		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField getDurationField() {
		return dateTimeField.getDurationField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField getRangeDurationField() {
		return dateTimeField.getRangeDurationField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeap(long instant) {
		return dateTimeField.isLeap(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLeapAmount(long instant) {
		return dateTimeField.getLeapAmount(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DurationField getLeapDurationField() {
		return dateTimeField.getLeapDurationField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumValue() {
		return dateTimeField.getMinimumValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumValue(long instant) {
		return dateTimeField.getMinimumValue(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumValue(ReadablePartial instant) {
		return dateTimeField.getMinimumValue(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinimumValue(ReadablePartial instant, int[] values) {
		return dateTimeField.getMinimumValue(instant, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumValue() {
		return dateTimeField.getMaximumValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumValue(long instant) {
		return dateTimeField.getMaximumValue(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumValue(ReadablePartial instant) {
		return dateTimeField.getMaximumValue(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumValue(ReadablePartial instant, int[] values) {
		return dateTimeField.getMaximumValue(instant, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumTextLength(Locale locale) {
		return dateTimeField.getMaximumTextLength(locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaximumShortTextLength(Locale locale) {
		return dateTimeField.getMaximumShortTextLength(locale);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long roundFloor(long instant) {
		return dateTimeField.roundFloor(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long roundCeiling(long instant) {
		return dateTimeField.roundCeiling(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long roundHalfFloor(long instant) {
		return dateTimeField.roundHalfFloor(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long roundHalfCeiling(long instant) {
		return dateTimeField.roundHalfCeiling(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long roundHalfEven(long instant) {
		return dateTimeField.roundHalfEven(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long remainder(long instant) {
		return dateTimeField.remainder(instant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return dateTimeField.toString();
	}
	
	
}
