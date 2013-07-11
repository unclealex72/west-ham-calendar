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
import scala.collection.SortedSet
import scala.collection.JavaConversions._
import uk.co.unclealex.hammers.calendar.logging.RemoteStream

/**
 * A base class for {@link HtmlGamesScanner}s that read a page and then
 * delegates scanning to a set of other pages that are links off of the original
 * page.
 *
 * @author alex
 *
 */
class DelegatingHtmlGamesScanner(
  /**
   * The {@link HtmlPageLoader} used to load web pages.
   */
  htmlPageLoader: HtmlPageLoader,
  /**
   * The {@link DateService} to use for date and time manipulation.
   */
  dateService: DateService,
  /**
   * The {@link LinkHarvester} used to find the links on a main page.
   */
  linkHarvester: LinkHarvester,
  /**
   * The {@link HtmlGamesScanner} used to find games on the child pages.
   */
  htmlGamesScanner: HtmlGamesScanner) extends TagNodeBasedHtmlGamesScanner(htmlPageLoader, dateService) {
  /**
   * {@inheritDoc}
   */
  def scan(uri: URI, tagNode: TagNode)(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    linkHarvester.harvestLinks(uri, tagNode).flatMap(htmlGamesScanner.scan _)
  }
}
