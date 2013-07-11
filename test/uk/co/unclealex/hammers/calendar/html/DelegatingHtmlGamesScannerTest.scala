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
import org.specs2.mutable.Specification
import uk.co.unclealex.hammers.calendar.dates.DateServiceImpl
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Location
import uk.co.unclealex.hammers.calendar.log.SimpleRemoteStream
import uk.co.unclealex.hammers.calendar.logging.RemoteStream
/**
 * Tests for the DelegatingHtmlGamesScanner class.
 *
 * @author alex
 */
class DelegatingHtmlGamesScannerTest extends Specification with SimpleRemoteStream {

  "The delegating HTML games scanner" should {
    "delegate its results" in {
      val htmlGamesScanner = new HtmlGamesScanner() {
        var season = 2012

        override def scan(uri: URI)(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
          val gameUpdateCommand =
            MatchReportUpdateCommand(new GameKeyLocator(new GameKey(
              Competition.FACP,
              Location.HOME,
              "Them",
              season)), uri.toString)
          season = season + 1
          List(gameUpdateCommand)
        }
      }

      val linkHarvester = new LinkHarvester() {
        override def harvestLinks(pageUri: URI, tagNode: TagNode): List[URI] = {
          TagNodeWalker.walk { tagNode =>
            Option(tagNode.getAttributeByName("href")) map (href => URI.create(href))
          }(tagNode)
        }
      }
      val delegatingHtmlGamesScanner = new DelegatingHtmlGamesScanner(
        new HtmlPageLoaderImpl, new DateServiceImpl, linkHarvester, htmlGamesScanner)
      val uri = getClass().getClassLoader().getResource("delegate.xml").toURI
      val actualGameUpdateCommands = delegatingHtmlGamesScanner.scan(uri)
      val expectedGameUpdateCommands: List[GameUpdateCommand] = (1 to 4).toList map { (index: Int) =>
        MatchReportUpdateCommand(new GameKeyLocator(new GameKey(
          Competition.FACP,
          Location.HOME,
          "Them",
          index + 2011)), s"${index}.html")
      }
      actualGameUpdateCommands.sorted must be equalTo (expectedGameUpdateCommands.sorted)
    }
  }
}
