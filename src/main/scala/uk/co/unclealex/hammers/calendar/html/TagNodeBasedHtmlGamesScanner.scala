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

package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException
import java.net.URI
import org.htmlcleaner.TagNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.unclealex.hammers.calendar.dates.DateService
import com.typesafe.scalalogging.slf4j.Logging

/**
 * A base class for {@link HtmlGamesScanner}s that first parse a URL into an XML
 * document.
 *
 * @author alex
 *
 */
abstract class TagNodeBasedHtmlGamesScanner(
  /**
   * The {@link HtmlPageLoader} used to load web pages.
   */
  htmlPageLoader: HtmlPageLoader,
  /**
   * The {@link DateService} to use for date and time manipulation.
   */
  dateService: DateService) extends HtmlGamesScanner with Logging {

  override def scan(uri: URI): List[GameUpdateCommand] = {
    logger info s"Scanning URI $uri"
    val tagNode = htmlPageLoader.loadPage(uri.toURL())
    scan(uri, tagNode) distinct
  }

  /**
   * Scan a document for game updates.
   *
   * @param uri
   *          The URI of the page being scanned.
   * @param tagNode
   *          The document that is being scanned.
   * @return A set of {@link GameUpdateCommand}s that need to be made.
   * @throws IOException
   *           If there are any network problems.
   */
  def scan(uri: URI, tagNode: TagNode): List[GameUpdateCommand]
}
