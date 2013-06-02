/**
 * Copyright 2013 Alex Jones
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
 * @author unclealex72
 *
 */

package uk.co.unclealex.hammers.calendar.server.html

import org.specs2.mutable.Specification
import uk.co.unclealex.hammers.calendar.server.dates.DateServiceImpl
import scala.collection.JavaConverters._
import uk.co.unclealex.hammers.calendar.server.model.GameKey
/**
 * @author alex
 *
 */
class SeasonHtmlGamesScannerTest extends Specification with SeasonHtmlGamesScannerExpectations {

  "Scanning for all the games in 2007" should {
    scan(2007, expectedGameUpdateCommandsFor2007)
  }

  "Scanning for all the games in 2011" should {
    scan(2011, expectedGameUpdateCommandsFor2011)
  }

  def scan(season: Int, expectedGameUpdateCommands: List[GameUpdateCommand]) {
    val seasonHtmlGamesScanner = new SeasonHtmlGamesScanner(new HtmlPageLoaderImpl, new DateServiceImpl)
    val actualGameUpdateCommands = seasonHtmlGamesScanner.scan(
      getClass.getClassLoader.getResource(s"html/fixtures-${season}.html").toURI()).asScala.toList
    "encompass all the games" in {
      actualGameUpdateCommands diff expectedGameUpdateCommands must be equalTo (List())
    }
    val grouper: GameUpdateCommand => GameKey = { gameUpdateCommand =>
      gameUpdateCommand.gameLocator match {
        case locator: GameKeyLocator => locator.gameKey
        case _ => throw new IllegalArgumentException(s"Cannot find a game key for $gameUpdateCommand")
      }
    }
    val groupedActualGameUpdateCommands = actualGameUpdateCommands groupBy grouper
    expectedGameUpdateCommands groupBy grouper foreach {
      case (gameKey, expectedGameUpdateCommands) =>
        s"find ${gameKey.getOpponents} at ${gameKey.getLocation} in ${gameKey.getCompetition}" in {
          groupedActualGameUpdateCommands get gameKey must be equalTo (Some(expectedGameUpdateCommands))
        }
    }
  }
}