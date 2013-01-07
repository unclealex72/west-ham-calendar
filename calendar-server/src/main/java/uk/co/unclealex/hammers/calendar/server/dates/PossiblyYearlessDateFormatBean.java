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

package uk.co.unclealex.hammers.calendar.server.dates;


/**
 * A {@link PossiblyYearlessDateFormat} that is instantiated with both a year aware and year unaware date format.
 * @author alex
 *
 */
public class PossiblyYearlessDateFormatBean extends AbstractPossiblyYearlessDateFormat {

	/**
	 * The yearless date format to use.
	 */
	private final String dateFormatWithoutYear;

	/**
	 * Instantiates a new possibly yearless date format bean.
	 * 
	 * @param dateFormatWithYear
	 *          The year aware date format to use.
	 * @param dateFormatWithoutYear
	 *          The year unaware date format to use.
	 */
	public PossiblyYearlessDateFormatBean(String dateFormatWithYear, String dateFormatWithoutYear) {
		super(dateFormatWithYear);
		this.dateFormatWithoutYear = dateFormatWithoutYear;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDateFormatWithoutYear() {
		return dateFormatWithoutYear;
	}
	
	

}
