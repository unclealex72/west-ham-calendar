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

package uk.co.unclealex.hammers.calendar.html

import java.net.URI

import org.htmlcleaner.TagNode
/**
 * The interface for classes that collect the links for the {@link DelegatingHtmlGamesScanner}.
 * @author alex
 *
 */
trait LinkHarvester {

  /**
   * Harvest links from a page.
   * @param pageUri The URI of the page from which links are being harvested.
   * @param tagNode The document of the page.
   * @return A list of links to further scan for games.
   * @throws IOException Thrown if there are any issues reading the document.
   */
  def harvestLinks(pageUri: URI, tagNode: TagNode): List[URI]
}
