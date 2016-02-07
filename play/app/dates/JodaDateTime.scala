/**
 * Copyright 2013 Alex Jones
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
package dates

import org.joda.time.DateTimeZone
import org.joda.time.chrono.ISOChronology
import org.joda.time.{DateTime => JDateTime}
import java.util.Date
/**
 * A companion object for Joda DateTime that ensures all created dates have the correct time zone and chronology.
 * @author alex
 *
 */
object JodaDateTime {

  /**
   * The default (and only) time zone.
   */
  val EUROPE_LONDON = DateTimeZone.forID("Europe/London")

  /**
   * The default (and only) chronology with the default time zone.
   */
  val DEFAULT_CHRONOLOGY = ISOChronology.getInstance(EUROPE_LONDON)

  /**
   * Create a new date time with the correct chronology.
   */
  def apply(m: Long) = new JDateTime(m).withChronology(DEFAULT_CHRONOLOGY)

  /**
   * Create a new date time with the correct chronology.
   */
  def apply(d: Date) = new JDateTime(d).withChronology(DEFAULT_CHRONOLOGY)
}