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
 * An interface for objects that can hold two date formats: one with the year and one without.
 * @author alex
 *
 */
public interface PossiblyYearlessDateFormat {

	/**
	 * Gets the date format with year.
	 * 
	 * @return The date format to use with a year.
	 */
	String getDateFormatWithYear();
	
	/**
	 * Gets the date format without year.
	 * 
	 * @return The date format to use without a year.
	 */
	String getDateFormatWithoutYear();
}
