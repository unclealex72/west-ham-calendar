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
import org.joda.time.DateTime
import com.typesafe.scalalogging.slf4j.Logging
import TagNodeImplicits.Implicits
import uk.co.unclealex.hammers.calendar.dates.DateService
import scala.collection.SortedSet
/**
 * A {@link HtmlGamesScanner} that scans a page for ticket sales.
 *
 * @author alex
 *
 */
class TicketsHtmlSingleGameScanner(
  /**
   * The {@link HtmlPageLoader} used to load web pages.
   */
  htmlPageLoader: HtmlPageLoader,
  /**
   * The {@link DateService} to use for date and time manipulation.
   */
  dateService: DateService,
  season: Int) extends StatefulDomBasedHtmlGamesScanner(htmlPageLoader, dateService) with Logging {

  /**
   * The text that indicates the ticket selling date is for Bondholders.
   */
  val BOND_HOLDER_PATTERN = "Bondholders";

  /**
   * The text that indicates the ticket selling date is for priority point
   * holders.
   */
  val PRIORITY_POINT_PATTERN = "Priority Point";

  /**
   * The text that indicates the ticket selling date is for season ticket
   * holders.
   */
  val SEASON_TICKET_PATTERN = "Season Ticket ";

  /**
   * The text that indicates the ticket selling date is for Academy members.
   */
  val ACADEMY_MEMBER_PATTERN = "Academy Members ";

  /**
   * The text that indicates the ticket selling date is for general sale.
   */
  val GENERAL_SALE_PATTERN = "General Sale";

  /**
   * {@inheritDoc}
   */
  @Override
  protected override def createScanner(uri: URI, tagNode: TagNode): Scanner =
    new TicketsScanner(uri, tagNode)

  /**
   * A {@link Scanner} that scans a page for ticket sale dates.
   *
   * @author alex
   *
   */
  class TicketsScanner(uri: URI, tagNode: TagNode) extends Scanner(uri, tagNode) {

    /**
     * The currently found {@link GameLocator}.
     */
    var gameLocator: Option[GameLocator] = None

    /**
     * The currently found {@link DateTime} for the game played.
     */
    var dateTimePlayed: Option[DateTime] = None

    /**
     * A parsing action is used to parse segments of text on a web page and, if
     * a matching string is found, a date is searched for and an action is
     * executed.
     *
     * @author alex
     *
     */
    abstract class ParsingAction(
      /**
       * The text that must be contained in the segment of the web page.
       */
      val containedText: String,
      /**
       * An array of date formats to look for a date to associate with action.
       */
      val possiblyYearlessDateFormats: String*) {

      def this(containedText: String, possiblyYearlessDateFormats: List[String]) = this(containedText, possiblyYearlessDateFormats: _*)
      /**
       * Search for or parse text for a {@link DateTime} and, if one is found,
       * do something.
       *
       * @param dateText
       *          The dateText to search for or parse a date time.
       */
      def execute(dateText: String): Option[GameUpdateCommand] = {
        parseDateTime(dateText) match {
          case Some(dateTime) => execute(dateTime)
          case None => {
            logger debug s"Could not find a date for URL $uri in text $dateText"
            None
          }
        }
      }

      /**
       * Parse or search for a {@link DateTime}.
       *
       * @param dateText
       *          The text to search for or parse.
       * @return The found {@link DateTime} or nul if none could be found.
       * @throws UnparseableDateException
       */
      def parseDateTime(dateText: String): Option[DateTime]

      /**
       * With the premise that a {@link DateTime} has been found in a segment of
       * the web page, do something with that information.
       *
       * @param dateTime
       *          The {@link DateTime} that has been found.
       */
      def execute(dateTime: DateTime): Option[GameUpdateCommand]

    }

    /**
     * A {@link ParsingAction} that looks for the date and time the game was
     * played. This is identified by looking for the string "k/o" and the date
     * and time then precedes. that.
     *
     * @author alex
     *
     */
    case object GameDatePlayedParsingAction extends ParsingAction("k/o", {
      val formats = List("EEEE dd MMMM yyyy - hha", "EEEE d MMMM yyyy - hha",
        "EEEE dd MMMM yyyy - hh.mma", "EEEE d MMMM yyyy - hh.mma")
      val dateSuffixes = List("", "'st'", "'nd'", "'rd'", "'th'")
      for (format <- formats; dateSuffix <- dateSuffixes) yield format.replace("d ", s"d${dateSuffix} ")
    }) {

      override def parseDateTime(dateText: String): Option[DateTime] = {
        dateService.findDate(dateText, possiblyYearlessDateFormats: _*)
      }

      /**
       * Make sure that {@link DateTime} the game was played and the
       * corresponding {@link GameLocator} are populated so that any
       * {@link TicketParsingAction}s know which game to use to create a
       * {@link GameUpdateCommand}.
       *
       * @param dateTime
       *          The found {@link DateTime}.
       *
       */
      def execute(dateTime: DateTime): Option[GameUpdateCommand] = {
        logger info s"The game with tickets at URL $uri is being played at $dateTime"
        dateTimePlayed = Some(dateTime)
        gameLocator = Some(DatePlayedLocator(dateTime))
        None
      }
    }

    /**
     * A {@link ParsingAction} that looks for a ticket selling date (as
     * identified by a string) and then creates a {@link GameUpdateCommand} to
     * be stored.
     *
     * @author alex
     *
     */
    abstract class TicketParsingAction(containedText: String) extends ParsingAction(
      containedText,
      "hha EEE dd MMM",
      "ha EEE dd MMM",
      "hha EEE d MMM",
      "ha EEE d MMM",
      "hha 'on' EEEE dd MMMM",
      "hh.mma 'on' EEE dd MMM",
      "HH'noon on' EEEE dd MMMM",
      "hh.mma'noon on' EEE dd MMM") {

      /**
       * {@inheritDoc}
       */
      @Override
      override def parseDateTime(dateText: String): Option[DateTime] = {
        dateService.findPossiblyYearlessDate(
          dateText,
          dateTimePlayed.get,
          true,
          possiblyYearlessDateFormats: _*) map { dateTime =>
            // Account for noon being interpreted as midnight
            if (dateTime.getHourOfDay == 0) dateTime withHourOfDay 12 else dateTime
          }
      }

      override def execute(dateTime: DateTime): Option[GameUpdateCommand] = {
        logger info s"Found ticket type ${containedText.trim()} for game at ${dateTimePlayed.get} being sold at $dateTime"
        Some(createGameUpdateCommand(gameLocator.get, Some(dateTime)))
      }

      /**
       * Create the {@link GameUpdateCommand} that associates a game with a
       * ticket sale date.
       *
       * @param gameLocator
       *          The {@link GameLocator} created by the
       *          {@link GameDatePlayedParsingAction}.
       * @param dateTime
       *          The {@link DateTime} parsed in text.
       * @return A {@link GameUpdateCommand} that describes the update required.
       */
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]): GameUpdateCommand
    }

    /**
     * The {@link TicketParsingAction} that looks for Bondholder tickets.
     *
     * @author alex
     *
     */
    case object BondHoldersTicketParsingAction extends TicketParsingAction(BOND_HOLDER_PATTERN) {
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]) =
        BondHolderTicketsUpdateCommand(gameLocator, dateTime)
    }

    /**
     * The {@link TicketParsingAction} that looks for Priority pointholder
     * tickets.
     *
     * @author alex
     *
     */
    case object PriorityPointTicketParsingAction extends TicketParsingAction(PRIORITY_POINT_PATTERN) {
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]) =
        PriorityPointTicketsUpdateCommand(gameLocator, dateTime)
    }

    /**
     * The {@link TicketParsingAction} that looks for season ticket holder
     * tickets.
     *
     * @author alex
     *
     */
    case object SeasonTicketParsingAction extends TicketParsingAction(SEASON_TICKET_PATTERN) {
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]) =
        SeasonTicketsUpdateCommand(gameLocator, dateTime)
    }

    /**
     * The {@link TicketParsingAction} that looks for Academy members' tickets.
     *
     * @author alex
     *
     */
    case object AcademyMemberTicketParsingAction extends TicketParsingAction(ACADEMY_MEMBER_PATTERN) {
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]) =
        AcademyTicketsUpdateCommand(gameLocator, dateTime)
    }

    /**
     * The {@link TicketParsingAction} that looks for general sale tickets.
     *
     * @author alex
     *
     */
    case object GeneralSaleTicketParsingAction extends TicketParsingAction(GENERAL_SALE_PATTERN) {
      protected def createGameUpdateCommand(gameLocator: GameLocator, dateTime: Option[DateTime]) =
        GeneralSaleTicketsUpdateCommand(gameLocator, dateTime)
    }

    /**
     * Scan the page, first looking for the date the game was played and then
     * looking for any ticket selling dates.
     *
     * @throws IOException
     *           Signals that an I/O exception has occurred.
     */
    override def scan: List[GameUpdateCommand] = {
      val parsingActions = List(
        GameDatePlayedParsingAction,
        BondHoldersTicketParsingAction,
        PriorityPointTicketParsingAction,
        SeasonTicketParsingAction,
        AcademyMemberTicketParsingAction,
        GeneralSaleTicketParsingAction)
      val filter = new TagNodeFilter(_ => true)
      filter.list(tagNode) flatMap { tagNode =>
        val text = tagNode.normalisedText
        text match {
          case "" => List.empty
          case _ => {
            val parsingAction = parsingActions.find(pa => text.contains(pa.containedText))
            val newGameUpdateCommands = parsingAction.flatMap { parsingAction =>
              val textForDate = text.replace(parsingAction.containedText, "")
              parsingAction.execute(textForDate)
            }
            newGameUpdateCommands
          }
        }
      }
    }
  }
}
