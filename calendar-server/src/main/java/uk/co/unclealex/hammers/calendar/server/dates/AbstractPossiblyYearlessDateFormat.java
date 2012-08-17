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


/**
 * A {@link PossiblyYearlessDateFormat} that can be supplied with a year aware date format.
 * @author alex
 *
 */
public abstract class AbstractPossiblyYearlessDateFormat implements PossiblyYearlessDateFormat {

	/**
	 * The year aware date format to use.
	 */
	private final String i_dateFormatWithYear;

	/**
	 * Instantiates a new abstract possibly yearless date format.
	 * 
	 * @param dateFormatWithYear
	 *          The year aware date format to use.
	 */
	public AbstractPossiblyYearlessDateFormat(String dateFormatWithYear) {
		super();
		i_dateFormatWithYear = dateFormatWithYear;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDateFormatWithYear() {
		return i_dateFormatWithYear;
	}
	
	
}
