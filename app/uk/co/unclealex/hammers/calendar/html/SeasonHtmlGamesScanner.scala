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
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale
import org.htmlcleaner.TagNode
import org.joda.time.DateTime
import org.slf4j.Logger
import TagNodeImplicits._
import uk.co.unclealex.hammers.calendar.dates.DateService
import uk.co.unclealex.hammers.calendar.dates.UnparseableDateException
import uk.co.unclealex.hammers.calendar.model.Competition
import uk.co.unclealex.hammers.calendar.model.GameKey
import uk.co.unclealex.hammers.calendar.model.Location
import javax.inject.Inject

/**
 * An {@link HtmlGamesScanner} that scans the season's fixtures page for game
 * information.
 *
 * @author alex
 *
 */
class SeasonHtmlGamesScanner @Inject() (
  /**
   * The {@link HtmlPageLoader} used to load web pages.
   */
  htmlPageLoader: HtmlPageLoader,
  /**
   * The {@link DateService} to use for date and time manipulation.
   */
  dateService: DateService) extends StatefulDomBasedHtmlGamesScanner(htmlPageLoader, dateService) {

  protected override def createScanner(uri: URI, tagNode: TagNode) =
    new SeasonScanner(uri, tagNode)

  /**
   * The {@link Scanner} that scans the season's fixtures page for game
   * information.
   *
   * @author alex
   *
   */
  class SeasonScanner(uri: URI, tagNode: TagNode)
    extends Scanner(uri, tagNode) {

    /**
     * The current season.
     */
    var season: Option[Integer] = None

    /**
     * The current month.
     */
    var month: Option[String] = None

    /**
     * The {@link DateTime} the season started.
     */
    var startOfSeason: Option[DateTime] = None

    def scan: List[GameUpdateCommand] = {
      updateSeason
      val tableTagNode = tagNode.evaluateXPath("//table[@class='fixtureList']")(0).asInstanceOf[TagNode]
      val tableRowFilter = new TagNodeFilter(tg => "tr" == tg.getName)

      val classContainsPredicate = { className: String =>
        tagNode: TagNode =>
          val classes = tagNode.getAttributeByName("class")
          classes != null && classes.contains(className)
      }
      val isMonthRowPredicate = classContainsPredicate("rowHeader")
      val isGameRow = classContainsPredicate("fixture")
      tableRowFilter.list(tableTagNode).flatMap { row =>
        if (isMonthRowPredicate(row)) {
          updateMonth(row)
          List.empty
        } else if (isGameRow(row)) {
          updateGame(row)
        } else {
          List.empty
        }
      }
    }

    /**
     * Find which season page represents.
     */
    def updateSeason: Unit = {
      val seasonPattern = """s\.prop3="([0-9]+)""".r.unanchored
      TagNodeWalker.walk { tagNode =>
        if (season.isEmpty && "script" == tagNode.getName) {
          tagNode.normalisedText match {
            case seasonPattern(year) => {
              logger info s"Found season $year"
              season = Some(Integer.parseInt(year))
              startOfSeason = dateService.parseDate(s"01/07/$year", "dd/MM/yyyy")
            }
            case _ => // Ignore the line
          }
        }
        None
      }(tagNode)
    }

    /**
     * Update the month.
     *
     * @param row
     *          The table row containing the month.
     */
    def updateMonth(row: TagNode): Unit = {
      val child = row.findElementByName("td", false);
      val month = child.normalisedText
      logger info s"Found $month ${season.get}"
      this.month = Some(month)
    }

    def padStart(str: String)(implicit minLength: Int, padChar: Char): String = {
      if (str.length < minLength) {
        padStart(padChar + str)
      } else {
        str
      }
    }
    /**
     * Update a game.
     *
     * @param row
     *          The current row in the fixtures table.
     */
    def updateGame(row: TagNode): List[GameUpdateCommand] = {
      val tds: Iterator[TagNode] = row.getElementsByName("td", false).toSeq.iterator
      val date = padStart(tds.next.normalisedText.replaceAll("[^0-9]", ""))(2, '0')
      val time = tds.next.normalisedText
      val location = if ("H" == tds.next.normalisedText) Location.HOME else Location.AWAY
      val opponentsElOrLink = tds.next
      // Could be in a link
      val opponentsEl = Option(opponentsElOrLink.findElementByName("a", false)).getOrElse(opponentsElOrLink)
      val opponents = opponentsEl.normalisedText
      val competition = Competition.findByToken(tds.next.normalisedText)
      val gameKey = new GameKey(competition, location, opponents, season.get)
      logger info s"Found game key $gameKey"
      val datePlayedString = List(date, time, month.get).mkString(" ")
      dateService.parsePossiblyYearlessDate(
        datePlayedString,
        startOfSeason.get,
        false,
        "dd HH:mm MMMM[ yyyy]") match {
          case None => {
            logger warn s"Cannot parse date $datePlayedString for game $gameKey"
            List.empty
          }
          case Some(datePlayed) => {
            val gameKeyLocator = GameKeyLocator(gameKey)
            val datePlayedGameUpdateCommand: Option[GameUpdateCommand] =
              Some(DatePlayedUpdateCommand(gameKeyLocator, datePlayed))
            tds.next(); // Move past the W/L/D token.
            val resultGameUpdateCommand =
              tds.next.optionalNormalisedText map (text => ResultUpdateCommand(gameKeyLocator, text))
            val attendenceText = tds.next().normalisedText
            val attendence: Option[Int] = {
              try {
                if (attendenceText.isEmpty || "00" == attendenceText) None else {
                  Some(NumberFormat.getIntegerInstance(Locale.UK)
                    .parse(attendenceText)
                    .intValue())
                }
              } catch {
                case e: ParseException => {
                  val invalidData =
                    attendenceText.getBytes.map(by => (if (by < 0) 256 + by else by).asInstanceOf[Int]).map(i =>
                      "0x" + Integer.toHexString(i)).mkString
                  logger warn (s"Cannot parse attendance $invalidData on page $uri", e)
                  None
                }
              }
            }
            val attendenceUpdateCommand =
              attendence map (attendence => AttendenceUpdateCommand(gameKeyLocator, attendence))
            tds.next(); // Move past the league table token.
            val matchReportTd = tds.next();
            val matchReportLink = Option(matchReportTd.findElementByName("a", false))
            val matchReport = matchReportLink.map { matchReportLink =>
              val matchReportPath = matchReportLink.getAttributeByName("href")
              val matchReportUri = uri.resolve(matchReportPath)
              matchReportUri.toString
            }
            val matchReportUpdateCommand =
              matchReport map (matchReport => MatchReportUpdateCommand(gameKeyLocator, matchReport))
            val newUpdateCommands =
              List.empty ++ datePlayedGameUpdateCommand ++ resultGameUpdateCommand ++ attendenceUpdateCommand ++
                matchReportUpdateCommand
            logger debug s"Adding game update commands $newUpdateCommands"
            newUpdateCommands
          }
        }
    }
  }
}
