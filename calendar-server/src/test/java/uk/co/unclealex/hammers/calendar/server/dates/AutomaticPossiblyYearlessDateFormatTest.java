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

import org.junit.Assert;
import org.junit.Test;



/**
 * The Class AutomaticPossiblyYearlessDateFormatTest.
 * 
 * @author alex
 */
public class AutomaticPossiblyYearlessDateFormatTest {

	/**
	 * Test at start.
	 */
	@Test
	public void testAtStart() {
		test("[yyyy ]MM dd, HH:mm", "yyyy MM dd, HH:mm", "MM dd, HH:mm");
	}

	/**
	 * Test at end.
	 */
	@Test
	public void testAtEnd() {
		test("HH:mm, MM dd[ yyyy]", "HH:mm, MM dd yyyy", "HH:mm, MM dd");
	}

	/**
	 * Test in middle.
	 */
	@Test
	public void testInMiddle() {
		test("MM dd[ yyyy], HH:mm", "MM dd yyyy, HH:mm", "MM dd, HH:mm");
	}

	/**
	 * Test nowhere.
	 */
	@Test
	public void testNowhere() {
		test("MM dd yyyy, HH:mm", "MM dd yyyy, HH:mm", null);
	}

	/**
	 * Test no year.
	 */
	@Test
	public void testNoYear() {
		test("MM dd, HH:mm", null, "MM dd, HH:mm");
	}

	/**
	 * Test that a year aware format is correctly converted to a yearless format.
	 * 
	 * @param dateFormat
	 *          The date format.
	 * @param expectedYearfulFormat
	 *          The expected yearful format.
	 * @param expectedYearlessFormat
	 *          The expected yearless format.
	 */
	protected void test(String dateFormat, String expectedYearfulFormat, String expectedYearlessFormat) {
		AutomaticPossiblyYearlessDateFormat automaticPossiblyYearlessDateFormat = new AutomaticPossiblyYearlessDateFormat(
				dateFormat);
		Assert.assertEquals("Format " + dateFormat + " did not have the correct yearful format.",
				expectedYearfulFormat, automaticPossiblyYearlessDateFormat.getDateFormatWithYear());
		Assert.assertEquals("Format " + dateFormat + " did not have the correct yearless format.",
				expectedYearlessFormat, automaticPossiblyYearlessDateFormat.getDateFormatWithoutYear());
	}
}
