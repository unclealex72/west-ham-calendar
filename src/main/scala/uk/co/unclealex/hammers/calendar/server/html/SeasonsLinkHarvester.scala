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

package uk.co.unclealex.hammers.calendar.server.html

import java.net.URI
import org.htmlcleaner.TagNode
import TagNodeImplicits._
import com.google.common.base.Strings
import com.typesafe.scalalogging.slf4j.Logging

/**
 * Find all the links for every season's fixtures.
 * @author alex
 *
 */
class SeasonsLinkHarvester extends ElementLinkHarvester("option") with Logging {

  val seasonRegex = ".*[0-9]+/[0-9]+.*".r

  protected def checkForLink(uri: URI, tagNode: TagNode): Option[URI] = {
    val value = Strings.nullToEmpty(tagNode.getAttributeByName("value"))
    val optionText = tagNode.normalisedText
    optionText match {
      case seasonRegex() => {
        val linkUri = uri.resolve(value);
        logger info s"Found link $linkUri  for $optionText"
        Some(linkUri)
      }
      case _ => None
    }
  }
}
