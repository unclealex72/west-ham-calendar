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

package dates

import logging.{RemoteLogging, RemoteStream}
import org.joda.time.DateTime

/**
 * An interface for objects that parse dates from strings or find dates embedded within strings.
 * @author alex
 *
 */
trait DateParser {

  /**
   * Parse a date from a string.
   * @return The parsed {@link DateTime} or None if the date could not be parsed.
   */
  def parse(str: String): Option[DateTime]

  /**
   * Find a date within a string.
   * @return The found {@link DateTime} or None if the date could not be found.
   */
  def find(str: String): Option[DateTime]

  def logFailures: LoggingDateParser = new LoggingDateParser with RemoteLogging {

    override def parse(str: String)(implicit remoteStream: RemoteStream): Option[DateTime] =
      execute(DateParser.this.parse, str, s"Cannot parse $str as a date")

    override def find(str: String)(implicit remoteStream: RemoteStream): Option[DateTime] =
      execute(DateParser.this.find, str, s"Cannot find a date in $str")

    def execute(f: String => Option[DateTime], str: String, failureMessage: String)(implicit remoteStream: RemoteStream): Option[DateTime] = {
      logOnEmpty(f(str), failureMessage)
    }
  }
}

trait LoggingDateParser {

  /**
   * Parse a date from a string.
   * @return The parsed {@link DateTime} or None if the date could not be parsed.
   */
  def parse(str: String)(implicit remoteStream: RemoteStream): Option[DateTime]

  /**
   * Find a date within a string.
   * @return The found {@link DateTime} or None if the date could not be found.
   */
  def find(str: String)(implicit remoteStream: RemoteStream): Option[DateTime]

}