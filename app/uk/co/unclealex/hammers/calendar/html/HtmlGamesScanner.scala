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

/**
 * The common interface for services that scan web pages for details on West Ham games. Such details can either
 * be when games are played, the result, what games are televised or when tickets are available. Scanners can
 * either scan one HTML page for all game information or be responsible for scanning a page for information and
 * then delegating to other scanners for found URI links.
 * @author alex
 *
 */
trait HtmlGamesScanner {

  /**
   * Scan a web page.
   * @param uri The URI of the page to scan.
   * @return A set of changes found.
   * @throws IOException Thrown if there are any network problems.
   */
  def scan(uri: URI): List[GameUpdateCommand]
}
