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

import java.lang.{Iterable => JIterable}
import java.net.URI
import java.util.Collections
import java.util.{List => JList}
import java.util.{SortedSet => JSortedSet}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

import org.htmlcleaner.TagNode
import org.specs2.mutable.Specification

import com.google.common.collect.Lists
import com.google.common.collect.Sets

import uk.co.unclealex.hammers.calendar.dates.DateServiceImpl
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Location
/**
 * Tests for the DelegatingHtmlGamesScanner class.
 *
 * @author alex
 */
class DelegatingHtmlGamesScannerTest extends Specification {

  "The delegating HTML games scanner" should {
    "delegate its results" in {
      val htmlGamesScanner = new HtmlGamesScanner() {
        var season = 2012

        override def scan(uri: URI): JSortedSet[GameUpdateCommand] = {
          val gameUpdateCommand =
            MatchReportUpdateCommand(new GameKeyLocator(new GameKey(
              Competition.FACP,
              Location.HOME,
              "Them",
              season)), Some(uri.toString))
          season = season + 1
          Sets.newTreeSet(Collections.singleton(gameUpdateCommand))
        }
      }

      val linkHarvester = new LinkHarvester() {

        override def harvestLinks(pageUri: URI, tagNode: TagNode): JIterable[URI] = {
          val links: JList[URI] = Lists.newArrayList()
          new TagNodeWalker(tagNode) {
            override def execute(tagNode: TagNode) {
              val href = tagNode.getAttributeByName("href")
              if (href != null) {
                links.add(URI.create(href))
              }
            }
          }
          links
        }
      }
      val delegatingHtmlGamesScanner = new DelegatingHtmlGamesScanner(
        new HtmlPageLoaderImpl, new DateServiceImpl, linkHarvester, htmlGamesScanner)
      val uri = getClass().getClassLoader().getResource("delegate.xml").toURI
      val actualGameUpdateCommands = delegatingHtmlGamesScanner.scan(uri).asScala.toList
      val expectedGameUpdateCommands = (1 to 4).toList map { (index: Int) =>
        MatchReportUpdateCommand(new GameKeyLocator(new GameKey(
          Competition.FACP,
          Location.HOME,
          "Them",
          index + 2011)), Some(s"${index}.html"))
      }
      actualGameUpdateCommands must be equalTo (expectedGameUpdateCommands)
    }
  }
}
