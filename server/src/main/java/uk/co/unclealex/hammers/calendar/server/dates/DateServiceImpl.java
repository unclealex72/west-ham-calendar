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
package uk.co.unclealex.hammers.calendar.server.dates;

import java.util.List;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.DayOfWeekIgnoringChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * The default implementation of {@link DateService}.
 * 
 * @author alex
 * 
 */
public class DateServiceImpl implements DateService {

	/** The Constant THREE_PM. */
	private static final int THREE_PM = 15;

	/**
	 * The default (and only) time zone.
	 */
	private static final DateTimeZone EUROPE_LONDON = DateTimeZone.forID("Europe/London");

	/**
	 * The default (and only) chronology with the default time zone.
	 */
	private static final Chronology DEFAULT_CHRONOLOGY = ISOChronology.getInstance(EUROPE_LONDON);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTime parsePossiblyYearlessDate(final String date, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, PossiblyYearlessDateFormat... possiblyYearlessDateFormats)
			throws UnparseableDateException {
		Function<String, DateTime> parseFunction = new Function<String, DateTime>() {
			@Override
			public DateTime apply(String dateFormat) {
				return parseDate(date, dateFormat);
			}
		};
		DateTime dateTime = findOrParsePossiblyYearlessDate(parseFunction, yearDeterminingDate,
				yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats);
		if (dateTime == null) {
			throwException("parse", date, possiblyYearlessDateFormats);
		}
		return dateTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTime findPossiblyYearlessDate(final String date, DateTime yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, PossiblyYearlessDateFormat... possiblyYearlessDateFormats)
			throws UnparseableDateException {
		Function<String, DateTime> findFunction = new Function<String, DateTime>() {
			@Override
			public DateTime apply(String dateFormat) {
				return findDate(date, dateFormat);
			}
		};
		DateTime dateTime = findOrParsePossiblyYearlessDate(findFunction, yearDeterminingDate,
				yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormats);
		if (dateTime == null) {
			throwException("parse", date, possiblyYearlessDateFormats);
		}
		return dateTime;
	}

	/**
	 * Throw an {@link UnparseableDateException}.
	 * 
	 * @param verb
	 *          The verb to use in the message.
	 * @param dateTime
	 *          The supplied string.
	 * @param possiblyYearlessDateFormats
	 *          The supplied date formats.
	 * @throws UnparseableDateException
	 *           Always thrown.
	 */
	protected void throwException(String verb, String dateTime, PossiblyYearlessDateFormat[] possiblyYearlessDateFormats)
			throws UnparseableDateException {
		List<String> dateFormats = Lists.newArrayList();
		for (PossiblyYearlessDateFormat possiblyYearlessDateFormat : possiblyYearlessDateFormats) {
			dateFormats.add(possiblyYearlessDateFormat.getDateFormatWithYear());
			dateFormats.add(possiblyYearlessDateFormat.getDateFormatWithoutYear());
		}
		throw new UnparseableDateException("Could not " + verb + " the string '" + dateTime
				+ " using any of the following formats: "
				+ Joiner.on(", ").join(Iterables.filter(dateFormats, Predicates.notNull())));
	}

	/**
	 * Parse a date that may or may not have a year, taking the year from another
	 * date if need be.
	 * 
	 * @param parseOrFindFunction
	 *          A function that, given a date format string, will either try to
	 *          fully parse or find a {@link DateTime}.
	 * @param yearDeterminingDate
	 *          The date to used to determine the year.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the date is earlier than the year determining date, false
	 *          otherwise.
	 * @param possiblyYearlessDateFormats
	 *          The formats to try.
	 * @return The parsed date or null if the date could not be parsed.
	 */
	protected DateTime findOrParsePossiblyYearlessDate(Function<String, DateTime> parseOrFindFunction,
			DateTime yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate,
			PossiblyYearlessDateFormat... possiblyYearlessDateFormats) {
		for (PossiblyYearlessDateFormat possiblyYearlessDateFormat : possiblyYearlessDateFormats) {
			DateTime dateTime = findOrParsePossiblyYearlessDate(parseOrFindFunction, yearDeterminingDate,
					yearDeterminingDateIsLaterThanTheDate, possiblyYearlessDateFormat);
			if (dateTime != null) {
				return dateTime;
			}
		}
		return null;
	}

	/**
	 * Parse a date that may or may not have a year, taking the year from another
	 * date if need be.
	 * 
	 * @param parseOrFindFunction
	 *          A function that, given a date format string, will either try to
	 *          fully parse or find a {@link DateTime}.
	 * @param yearDeterminingDate
	 *          The date to used to determine the year.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the date is earlier than the year determining date, false
	 *          otherwise.
	 * @param possiblyYearlessDateFormat
	 *          The format to try.
	 * @return The parsed date or null if the date could not be parsed.
	 */
	protected DateTime findOrParsePossiblyYearlessDate(Function<String, DateTime> parseOrFindFunction,
			DateTime yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate,
			PossiblyYearlessDateFormat possiblyYearlessDateFormat) {
		String dateFormatWithYear = possiblyYearlessDateFormat.getDateFormatWithYear();
		DateTime dateTime = dateFormatWithYear == null ? null : parseOrFindFunction.apply(dateFormatWithYear);
		if (dateTime == null) {
			String dateFormatWithoutYear = possiblyYearlessDateFormat.getDateFormatWithoutYear();
			if (dateFormatWithoutYear != null) {
				dateTime = findOrParseYearlessDate(parseOrFindFunction, yearDeterminingDate,
						yearDeterminingDateIsLaterThanTheDate, dateFormatWithoutYear);
			}
		}
		return dateTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTime parseDate(String date, String dateFormat) {
		DateTimeFormatter formatter = makeFormatter(dateFormat);
		return parseDate(date, formatter);
	}

	/**
	 * A quick method to parse a date and swallow illegal format exceptions.
	 * 
	 * @param date
	 *          The date string to format.
	 * @param formatter
	 *          The {@link DateTimeFormatter} to use.
	 * @return The date if parsing is possible, null otherwise.
	 */
	protected DateTime parseDate(String date, DateTimeFormatter formatter) {
		try {
			return formatter.parseDateTime(date).withChronology(DEFAULT_CHRONOLOGY);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateTime findDate(String date, String dateFormat) {
		DateTimeFormatter formatter = makeFormatter(dateFormat);
		int maxLength = formatter.getParser().estimateParsedLength();
		return findDate(date, formatter, maxLength);
	}

	/**
	 * Find a date within a large string.
	 * 
	 * @param date
	 *          The large string to search.
	 * @param formatter
	 *          The {@link DateTimeFormatter} to use.
	 * @param maxLength
	 *          The maximum length the date can be according to
	 * @return A date or null if none could be found.
	 *         {@link DateTimeParser#estimateParsedLength()}
	 */
	protected DateTime findDate(String date, DateTimeFormatter formatter, int maxLength) {
		if (date.isEmpty()) {
			return null;
		}
		for (int length = Math.min(maxLength, date.length()); length >= 0; length--) {
			String str = date.substring(0, length);
			DateTime dateTime = parseDate(str, formatter);
			if (dateTime != null) {
				return dateTime;
			}
		}
		return findDate(date.substring(1), formatter, maxLength);
	}

	/**
	 * Create a {@link DateTimeFormatter} from a given date format string.
	 * 
	 * @param dateFormat
	 *          The date format string to use for formatting.
	 * @return A {@link DateTimeFormatter} that will ignore days of the week and
	 *         will always return dates in the Europe/London timezone.
	 */
	protected DateTimeFormatter makeFormatter(String dateFormat) {
		return DateTimeFormat.forPattern(dateFormat).withZone(EUROPE_LONDON)
				.withChronology(new DayOfWeekIgnoringChronology(DEFAULT_CHRONOLOGY));
	}

	/**
	 * Find or parse yearless date.
	 * 
	 * @param parseOrFindFunction
	 *          A function that, given a date format string, will either try to
	 *          fully parse or find a {@link DateTime}.
	 * @param yearDeterminingDate
	 *          The date that determines the current year.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the parsed date is in the past, false otherwise.
	 * @param dateFormatWithoutYear
	 *          The date format to use.
	 * @return The parsed date or null if no date could be parsed.
	 */
	protected DateTime findOrParseYearlessDate(Function<String, DateTime> parseOrFindFunction,
			DateTime yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, String dateFormatWithoutYear) {
		DateTime dateTime = parseOrFindFunction.apply(dateFormatWithoutYear);
		if (dateTime != null) {
			dateTime = withYear(yearDeterminingDate, yearDeterminingDateIsLaterThanTheDate, dateTime);
		}
		return dateTime;
	}

	/**
	 * Add a year to a {@link DateTime}.
	 * 
	 * @param yearDeterminingDate
	 *          The {@link DateTime} that should be used to determine the year to
	 *          add.
	 * @param yearDeterminingDateIsLaterThanTheDate
	 *          True if the year determining date is later than the date the year
	 *          needs to be added to, false otherwise.
	 * @param dateTime The original {@link DateTime}.
	 * @return A {@link DateTime} with a new year.
	 */
	protected DateTime withYear(DateTime yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate,
			DateTime dateTime) {
		dateTime = dateTime.withYear(yearDeterminingDate.getYear());
		if (yearDeterminingDateIsLaterThanTheDate && yearDeterminingDate.isBefore(dateTime)) {
			dateTime = dateTime.minusYears(1);
		}
		else if (!yearDeterminingDateIsLaterThanTheDate && yearDeterminingDate.isAfter(dateTime)) {
			dateTime = dateTime.plusYears(1);
		}
		return dateTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWeekday(DateTime dateTime) {
		int dayOfWeek = dateTime.getDayOfWeek();
		return dayOfWeek != DateTimeConstants.SATURDAY && dayOfWeek != DateTimeConstants.SUNDAY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isThreeOClockOnASaturday(DateTime dateTime) {
		return dateTime.getDayOfWeek() == DateTimeConstants.SATURDAY && dateTime.getHourOfDay() == THREE_PM
				&& dateTime.getMinuteOfHour() == 0;
	}
}
