/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.service;

import java.net.URL;
import java.util.Date;

import uk.co.unclealex.hammers.calendar.server.exception.UnparseableDateException;

public interface DateService {

	public Date parseDate(String date, URL referringUrl, String... dateFormat) throws UnparseableDateException;
	
	public Date parseYearlessDate(String date, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, String... dateFormats) throws UnparseableDateException;
	
	public Date addYearToDate(Date yearlessDate, Date yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate);

	public String printDate(String dateFormat, Date date);

	public Date parsePossiblyYearlessDate(String date, Date yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, String dateFormat, String yearlessDateFormat) throws UnparseableDateException;

	public Date parsePossiblyYearlessDate(String date, Date yearDeterminingDate,
			boolean yearDeterminingDateIsLaterThanTheDate, URL referringUrl, String[] dateFormat, String[] yearlessDateFormat) throws UnparseableDateException;
}
