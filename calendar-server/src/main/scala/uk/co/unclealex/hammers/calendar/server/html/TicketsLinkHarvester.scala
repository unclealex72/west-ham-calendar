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

package uk.co.unclealex.hammers.calendar.server.html;

import java.net.URI
import org.htmlcleaner.TagNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.google.common.base.Strings;
import com.typesafe.scalalogging.slf4j.Logging

/**
 * A link harvester that harvests links for ticket selling dates.
 * @author alex
 *
 */
class TicketsLinkHarvester extends ElementLinkHarvester("a") with Logging {

  val ticketsLinkRegex = "/articles/[0-9]+/(?:h|a)-v-.+".r
  /**
   * {@inheritDoc}
   */
  override protected def checkForLink(uri: URI, tagNode: TagNode): Option[URI] = {
    val href = Strings.nullToEmpty(tagNode.getAttributeByName("href"))
    href match {
      case ticketsLinkRegex() => {
        val linkUri = uri.resolve(href);
        logger.info("Found tickets link " + linkUri);
        Some(linkUri)
      }
      case _ => None
    }
  }
}
