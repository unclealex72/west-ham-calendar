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

import org.joda.time.format.ISODateTimeFormat
import argonaut._, Argonaut._, DecodeResult._
import org.joda.time.DateTime

/**
 * Serialisation and deserialisation into ISO 8601 UTC strings.
 * @author alex
 *
 */
object DateTimeJsonCodec {

  private val formatter = ISODateTimeFormat.dateTime
  
  implicit def dateTimeJsonField: DateTime => JsonField = formatter.print
  
  implicit val DateTimeJsonCodec: CodecJson[DateTime] = CodecJson(
    dt => jString(dt),
    c => c.focus.string match {
      case Some(str) => {
        try {
          ok(formatter.parseDateTime(str))
        }
        catch {
          case e: IllegalArgumentException => fail(e.getMessage, c.history)
        }
      }
     case None => fail("Expected a string for a date time.", c.history)
    }
  )
  
  
}