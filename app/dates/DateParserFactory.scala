/**
 * Copyright 2010-2013 Alex Jones
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

import cats.data.NonEmptyList

/**
 * This service contains methods for parsing dates in various formats on the
 * West Ham website. All returned {@link ZonedDateTime} objects will have their timezone explicitly set
 * to Europe/London.
 *
 * @author alex
 *
 */
trait DateParserFactory {

  def forSeason(year: Int, possiblyYearlessDateFormats: NonEmptyList[String]): DateParser

  def forSeason(year: Int, possiblyYearlessDateFormat: String, possiblyYearlessDateFormats: String*): DateParser = {
    forSeason(year, NonEmptyList.of(possiblyYearlessDateFormat, possiblyYearlessDateFormats :_*))
  }
}
