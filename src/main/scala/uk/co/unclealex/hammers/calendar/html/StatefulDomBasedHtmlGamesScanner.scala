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

import java.net.URI

import org.htmlcleaner.TagNode

import uk.co.unclealex.hammers.calendar.dates.DateService

/**
 * A base class for {@link HtmlGamesScanner}s that require state. Really, is to avoid writing Spring factories.
 * @author alex
 *
 */
abstract class StatefulDomBasedHtmlGamesScanner(
  /**
   * The {@link HtmlPageLoader} used to load web pages.
   */
  htmlPageLoader: HtmlPageLoader,
  /**
   * The {@link DateService} to use for date and time manipulation.
   */
  dateService: DateService) extends TagNodeBasedHtmlGamesScanner(htmlPageLoader, dateService) {

  /**
   * {@inheritDoc}
   */
  override def scan(uri: URI, tagNode: TagNode): List[GameUpdateCommand] = {
    val scanner = createScanner(uri, tagNode)
    scanner.scan()
  }

  /**
   * Create a scanner to scan the games.
   * @param uri The URI of the page being scanned.
   * @param tagNode The XML tagNode to scan.
   * @return A scanner as described above.
   */
  protected def createScanner(uri: URI, tagNode: TagNode): Scanner

  /**
   * An abstract class to allow for scanning state to be stored, mainly so that there is
   * no need to muck around with spring-based factories.
   * @author alex
   *
   */
  abstract class Scanner(
    /**
     * The URI of the page being scanned.
     */
    uri: URI,
    /**
     * The top-level {@link TagNode} of the page being scanned.
     */
    tagNode: TagNode) {

    /**
     * Scan the page for any information.
     *
     * @throws IOException
     *           Signals that an I/O exception has occurred.
     */
    def scan(): List[GameUpdateCommand]
  }
}
