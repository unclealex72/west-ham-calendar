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

package uk.co.unclealex.hammers.calendar.server.dates;

/**
 * A {@link PossiblyYearlessDateFormat} that is instantiated with both a year aware and year unaware date format.
 * @author alex
 *
 */
public class PossiblyYearlessDateFormatBean extends AbstractPossiblyYearlessDateFormat {

	private final String i_dateFormatWithoutYear;

	/**
	 * @param dateFormatWithYear The year aware date format to use.
	 * @param dateFormatWithoutYear The year unaware date format to use.
	 */
	public PossiblyYearlessDateFormatBean(String dateFormatWithYear, String dateFormatWithoutYear) {
		super(dateFormatWithYear);
		i_dateFormatWithoutYear = dateFormatWithoutYear;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDateFormatWithoutYear() {
		return i_dateFormatWithoutYear;
	}
	
	

}
