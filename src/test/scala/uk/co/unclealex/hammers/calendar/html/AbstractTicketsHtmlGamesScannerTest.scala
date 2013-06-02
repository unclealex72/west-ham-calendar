/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author alex
 *
 */

package uk.co.unclealex.hammers.calendar.html

import uk.co.unclealex.hammers.calendar.Instant
import uk.co.unclealex.hammers.calendar.dates.DateServiceImpl
import org.specs2.mutable.Specification
import scala.collection.JavaConverters._

/**
 * Test that the Tickets HTML scanner works.
 * @author alex
 *
 */
abstract class AbstractTicketsHtmlGamesScannerTest(val year: Int) extends Specification {

  /**
   * The actual test runner.
   */
  case class GameResourceAtInstant(gameResource: String, instant: Instant) {

    def expects(expectations: GameLocator => List[GameUpdateCommand]) {
      val ticketsHtmlSingleGameScanner =
        new TicketsHtmlSingleGameScanner(new HtmlPageLoaderImpl, new DateServiceImpl, year)
      val url = getClass().getClassLoader().getResource(s"html/tickets/${year}/${gameResource}")
      val actualGameUpdateCommands = ticketsHtmlSingleGameScanner.scan(url.toURI).asScala.toList
      "have the correct ticket information" in {
        actualGameUpdateCommands.sorted must be equalTo (expectations(DatePlayedLocator(instant)).sorted)
      }
    }
  }

  /**
   * Syntactic sugar.
   */
  implicit class GameResourceImplicits(gameResource: String) {

    def on(instant: Instant) = GameResourceAtInstant(gameResource, instant)
  }

}