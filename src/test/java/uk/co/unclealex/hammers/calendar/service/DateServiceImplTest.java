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
package uk.co.unclealex.hammers.calendar.service;

import java.util.Date;

import junit.framework.TestCase;
import uk.co.unclealex.hammers.calendar.exception.UnparseableDateException;

public class DateServiceImplTest extends TestCase {

	public void testSameYearPast() throws UnparseableDateException {
		testYearless("10/03", "05/02/2008", false, "10/03/2008");
	}
	
	public void testSameYearFuture() throws UnparseableDateException {
		testYearless("10/03", "05/02/2008", false, "10/03/2008");
	}
	
	public void testNextYear() throws UnparseableDateException {
		testYearless("10/01", "05/12/2008", false, "10/01/2009");
	}

	public void testPreviousYear() throws UnparseableDateException {
		testYearless("10/12", "05/01/2008", true, "10/12/2007");
	}

	public void testYearless(String yearlessDate, String yearDeterminingDate, boolean yearDeterminingDateIsLaterThanTheDate, String expectedDate) throws UnparseableDateException {
		DateServiceImpl dateService = new DateServiceImpl();
		Date ydd = dateService.parseDate("dd/MM/yyyy", yearDeterminingDate, null);
		Date date = dateService.parseYearlessDate("dd/MM", yearlessDate, ydd, yearDeterminingDateIsLaterThanTheDate, null);
		String actualDate = dateService.printDate("dd/MM/yyyy", date);
		assertEquals("The wrong date was created.", expectedDate, actualDate);
	}
}
