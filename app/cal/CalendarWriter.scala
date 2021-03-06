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

package cal

import java.io.Writer

/**
 * A trait for classes that can output a calendar to an external source.
 * @author alex
 *
 */
trait CalendarWriter {

  /**
   * Write a calendar to an underlying writer.
   * @param calendar The calendar to writer.
   * @param writer The writer to write the calendar to.
   */
  def write(calendar: Calendar, writer: Writer, linkFactory: LinkFactory): Unit

  /**
   * The mime type of calendars created by this class.
   */
  def mimeType: String
}