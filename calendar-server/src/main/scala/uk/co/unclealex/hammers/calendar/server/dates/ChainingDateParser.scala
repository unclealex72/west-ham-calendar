/**
 * Copyright 2013 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.dates

import org.joda.time.DateTime

/**
 * A date parser that will chain through a set of given date parsers until a date can be parsed or found.
 * @author alex
 *
 */
class ChainingDateParser(
  /**
   * The sequence of date parsers that will be used to find or parse dates.
   */
  dateParsers: Seq[DateParser]) extends DateParser {

  override def parse(str: String) = traverse((dp: DateParser) => dp parse str)

  override def find(str: String) = traverse((dp: DateParser) => dp find str)

  /**
   * Traverse through each date parser until a non-None result is found.
   */
  protected def traverse(f: DateParser => Traversable[DateTime]): Option[DateTime] = {
    dateParsers.toStream.flatMap(f).headOption
  }
}