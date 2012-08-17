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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;


/**
 * A {@link PossiblyYearlessDateFormat} that contains the optional year defining
 * characters in square brackets.
 * 
 * @author alex
 * 
 */
public class AutomaticPossiblyYearlessDateFormat extends PossiblyYearlessDateFormatBean {

	/**
	 * The regular expression to find and remove the year part of format strings.
	 */
	private static final Pattern YEAR_STRIPPING_PATTERN = Pattern.compile("(.*?)\\[(.+)\\](.*)");

	/**
	 * Create a yearful date format.
	 * 
	 * @param dateFormat
	 *          The date format to parse.
	 * @return The date format with years.
	 */
	protected static String yearful(String dateFormat) {
		if (!dateFormat.contains("y")) {
			return null;
		}
		else {
			String makeFormat = makeFormat(dateFormat, 1, 2, 3);
			return makeFormat == null ? dateFormat : makeFormat;
		}
	}

	/**
	 * Create a yearless date format.
	 * 
	 * @param dateFormat
	 *          The date format to parse.
	 * @return The date format without years.
	 */
	protected static String yearless(String dateFormat) {
		return dateFormat.contains("y") ? makeFormat(dateFormat, 1, 3) : dateFormat;
	}

	/**
	 * Create a date format.
	 * 
	 * @param dateFormat
	 *          The date format to alter.
	 * @param groups
	 *          The matched groups that make up the format with respect to
	 * @return A new date format. {@link #YEAR_STRIPPING_PATTERN}.
	 */
	protected static String makeFormat(String dateFormat, Integer... groups) {
		final Matcher matcher = YEAR_STRIPPING_PATTERN.matcher(dateFormat);
		if (matcher.matches()) {
			Function<Integer, String> groupFunction = new Function<Integer, String>() {
				@Override
				public String apply(Integer group) {
					return matcher.group(group);
				}
			};
			return Joiner.on("").join(Iterables.transform(Arrays.asList(groups), groupFunction));
		}
		else {
			return null;
		}
	}

	/**
	 * The date format string to use to parse dates.
	 */
	private final String i_dateFormat;

	/**
	 * Instantiates a new automatic possibly yearless date format.
	 * 
	 * @param dateFormat
	 *          the date format
	 */
	public AutomaticPossiblyYearlessDateFormat(String dateFormat) {
		super(yearful(dateFormat), yearless(dateFormat));
		i_dateFormat = dateFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[:" + getDateFormat() + ":]";
	}

	/**
	 * Gets the date format string to use to parse dates.
	 * 
	 * @return the date format string to use to parse dates
	 */
	public String getDateFormat() {
		return i_dateFormat;
	}

}
