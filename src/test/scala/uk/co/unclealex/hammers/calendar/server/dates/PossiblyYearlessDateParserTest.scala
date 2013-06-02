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

import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Test;
import org.specs2.mutable.Specification

/**
 * The Class PossiblyYearlessDateParserTest.
 *
 * @author alex
 */
class PossiblyYearlessDateParserTest extends Specification {

  "An optional year at the start" should {
    "[yyyy ]MM dd, HH:mm" mustProduce (DoesNotNeedYear("yyyy MM dd, HH:mm"), DoesNeedYear("MM dd, HH:mm"))
  }

  /**
   * Test at end.
   */
  "An optional year at the end" should {
    "HH:mm, MM dd[ yyyy]" mustProduce (DoesNotNeedYear("HH:mm, MM dd yyyy"), DoesNeedYear("HH:mm, MM dd"))
  }

  /**
   * Test in middle.
   */
  "An optional year in the middle" should {
    "MM dd[ yyyy], HH:mm" mustProduce (DoesNotNeedYear("MM dd yyyy, HH:mm"), DoesNeedYear("MM dd, HH:mm"))
  }

  /**
   * Test nowhere.
   */
  "An explicitly required year" should {
    "MM dd yyyy, HH:mm" mustProduce (DoesNotNeedYear("MM dd yyyy, HH:mm"))
  }

  /**
   * Test no year.
   */
  "An explicitly not required year" should {
    "MM dd, HH:mm" mustProduce (new DoesNeedYear("MM dd, HH:mm"))
  }

  implicit class DateFormatImplicits(dateFormat: String) {
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
    def mustProduce(expectedFormats: NeedsYear*): Unit = {
      val possiblyYearlessDateParser = new PossiblyYearlessDateParser(new DateTime(), false, dateFormat)
      possiblyYearlessDateParser.dateFormats.toList mustEqual expectedFormats.toList
    }
  }
}
