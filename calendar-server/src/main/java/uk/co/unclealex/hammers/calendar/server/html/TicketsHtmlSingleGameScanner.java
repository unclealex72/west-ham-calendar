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

package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.htmlcleaner.TagNode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.dates.AutomaticPossiblyYearlessDateFormat;
import uk.co.unclealex.hammers.calendar.server.dates.PossiblyYearlessDateFormat;
import uk.co.unclealex.hammers.calendar.server.dates.UnparseableDateException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A {@link HtmlGamesScanner} that scans a page for ticket sales.
 * 
 * @author alex
 * 
 */
public class TicketsHtmlSingleGameScanner extends StatefulDomBasedHtmlGamesScanner {

  /** The logger for this class. */
  private final static Logger log = LoggerFactory.getLogger(TicketsHtmlSingleGameScanner.class);

  /**
   * The text that indicates the ticket selling date is for Bondholders.
   */
  public static final String BOND_HOLDER_PATTERN = "Bondholders";

  /**
   * The text that indicates the ticket selling date is for priority point
   * holders.
   */
  public static final String PRIORITY_POINT_PATTERN = "Priority Point";

  /**
   * The text that indicates the ticket selling date is for season ticket
   * holders.
   */
  public static final String SEASON_TICKET_PATTERN = "Season Ticket ";

  /**
   * The text that indicates the ticket selling date is for Academy members.
   */
  public static final String ACADEMY_MEMBER_PATTERN = "Academy Members ";

  /**
   * The text that indicates the ticket selling date is for general sale.
   */
  public static final String GENERAL_SALE_PATTERN = "General Sale";

  /**
   * {@inheritDoc}
   */
  @Override
  protected Scanner createScanner(final URI uri, final TagNode tagNode) {
    return new TicketsScanner(uri, tagNode);
  }

  /**
   * A {@link Scanner} that scans a page for ticket sale dates.
   * 
   * @author alex
   * 
   */
  class TicketsScanner extends Scanner {

    /**
     * The currently found {@link GameLocator}.
     */
    private GameLocator gameLocator;

    /**
     * The currently found {@link DateTime} for the game played.
     */
    private DateTime dateTimePlayed;

    /**
     * Instantiates a new tickets scanner.
     * 
     * @param uri
     *          the uri
     * @param tagNode
     *          the tag node
     */
    public TicketsScanner(final URI uri, final TagNode tagNode) {
      super(uri, tagNode);
    }

    /**
     * A parsing action is used to parse segments of text on a web page and, if
     * a matching string is found, a date is searched for and an action is
     * executed.
     * 
     * @author alex
     * 
     */
    abstract class ParsingAction {

      /**
       * The text that must be contained in the segment of the web page.
       */
      private final String containedText;

      /**
       * An array of date formats to look for a date to associate with action.
       */
      private final PossiblyYearlessDateFormat[] possiblyYearlessDateFormats;

      /**
       * Instantiates a new parsing action.
       * 
       * @param containedString
       *          the contained string
       * @param possiblyYearlessDateFormats
       *          the possibly yearless date formats
       */
      public ParsingAction(final String containedString, final String... possiblyYearlessDateFormats) {
        super();
        containedText = containedString;
        final PossiblyYearlessDateFormat[] pydfs = new PossiblyYearlessDateFormat[possiblyYearlessDateFormats.length];
        for (int idx = 0; idx < possiblyYearlessDateFormats.length; idx++) {
          pydfs[idx] = new AutomaticPossiblyYearlessDateFormat(possiblyYearlessDateFormats[idx]);
        }
        this.possiblyYearlessDateFormats = pydfs;
      }

      /**
       * Search for or parse text for a {@link DateTime} and, if one is found,
       * do something.
       * 
       * @param dateText
       *          The dateText to search for or parse a date time.
       */
      void execute(final String dateText) {
        try {
          final DateTime dateTime = parseDateTime(dateText);
          if (dateTime == null) {
            log.debug("Could not find a date in for URL " + getUri() + " in text " + dateText);
          }
          else {
            execute(dateTime);
          }
        }
        catch (final UnparseableDateException e) {
          log.debug("Could not find a date in for URL " + getUri() + " in text " + dateText, e);
        }
      }

      /**
       * Parse or search for a {@link DateTime}.
       * 
       * @param dateText
       *          The text to search for or parse.
       * @return The found {@link DateTime} or nul if none could be found.
       * @throws UnparseableDateException
       *           the unparseable date exception
       */
      abstract DateTime parseDateTime(String dateText) throws UnparseableDateException;

      /**
       * With the premise that a {@link DateTime} has been found in a segment of
       * the web page, do something with that information.
       * 
       * @param dateTime
       *          The {@link DateTime} that has been found.
       */
      abstract void execute(DateTime dateTime);

      /**
       * Gets the text that must be contained in the segment of the web page.
       * 
       * @return the text that must be contained in the segment of the web page
       */
      public String getContainedText() {
        return containedText;
      }

      /**
       * Gets the an array of date formats to look for a date to associate with
       * action.
       * 
       * @return the an array of date formats to look for a date to associate
       *         with action
       */
      public PossiblyYearlessDateFormat[] getPossiblyYearlessDateFormats() {
        return possiblyYearlessDateFormats;
      }
    }

    /**
     * A {@link ParsingAction} that looks for the date and time the game was
     * played. This is identified by looking for the string "k/o" and the date
     * and time then precedes. that.
     * 
     * @author alex
     * 
     */
    class GameDatePlayedParsingAction extends ParsingAction {

      /**
       * Instantiates a new game date played parsing action.
       */
      public GameDatePlayedParsingAction() {
        super(
            "k/o",
            "EEEE dd MMMM yyyy - hha",
            "EEEE d MMMM yyyy - hha",
            "EEEE dd MMMM yyyy - hh.mma",
            "EEEE d MMMM yyyy - hh.mma");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      DateTime parseDateTime(final String dateText) throws UnparseableDateException {
        return getDateService().findPossiblyYearlessDate(
            dateText,
            new DateTime(),
            false,
            getPossiblyYearlessDateFormats());
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
      @Override
      public void execute(final DateTime dateTime) {
        log.info("The game with tickets at URL " + getUri() + " is being played at " + dateTime);
        setDateTimePlayed(dateTime);
        setGameLocator(GameLocator.datePlayedLocator(dateTime));
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
    abstract class TicketParsingAction extends ParsingAction {

      /**
       * Create a new {@link TicketParsingAction}.
       * 
       * @param containedText
       *          The "magic text" which will identify which type of ticket is
       *          being sold.
       */
      public TicketParsingAction(final String containedText) {
        super(
            containedText,
            "hha EEE dd MMM",
            "ha EEE dd MMM",
            "hha EEE d MMM",
            "ha EEE d MMM",
            "hha 'on' EEEE dd MMMM",
            "hh.mma 'on' EEE dd MMM",
            "HH'noon on' EEEE dd MMMM",
            "hh.mma'noon on' EEE dd MMM");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      DateTime parseDateTime(final String dateText) throws UnparseableDateException {
        return getDateService().findPossiblyYearlessDate(
            dateText,
            getDateTimePlayed(),
            true,
            getPossiblyYearlessDateFormats());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void execute(final DateTime dateTime) {
        log.info("Found ticket type "
            + getContainedText().trim()
            + " for game at "
            + getDateTimePlayed()
            + " being sold at "
            + dateTime);
        final GameUpdateCommand gameUpdateCommand = createGameUpdateCommand(getGameLocator(), dateTime);
        getGameUpdateCommands().add(gameUpdateCommand);
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
      protected abstract GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime);
    }

    /**
     * The {@link TicketParsingAction} that looks for Bondholder tickets.
     * 
     * @author alex
     * 
     */
    class BondHoldersTicketParsingAction extends TicketParsingAction {

      /**
       * Instantiates a new bond holders ticket parsing action.
       */
      public BondHoldersTicketParsingAction() {
        super(BOND_HOLDER_PATTERN);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected GameUpdateCommand createGameUpdateCommand(final GameLocator gameLocator, final DateTime dateTime) {
        return GameUpdateCommand.bondHolderTickets(gameLocator, dateTime);
      }
    }

    /**
     * The {@link TicketParsingAction} that looks for Priority pointholder
     * tickets.
     * 
     * @author alex
     * 
     */
    class PriorityPointTicketParsingAction extends TicketParsingAction {

      /**
       * Instantiates a new priority point ticket parsing action.
       */
      public PriorityPointTicketParsingAction() {
        super(PRIORITY_POINT_PATTERN);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected GameUpdateCommand createGameUpdateCommand(final GameLocator gameLocator, final DateTime dateTime) {
        return GameUpdateCommand.priorityPointTickets(gameLocator, dateTime);
      }
    }

    /**
     * The {@link TicketParsingAction} that looks for season ticket holder
     * tickets.
     * 
     * @author alex
     * 
     */

    class SeasonTicketParsingAction extends TicketParsingAction {

      /**
       * Instantiates a new season ticket parsing action.
       */
      public SeasonTicketParsingAction() {
        super(SEASON_TICKET_PATTERN);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected GameUpdateCommand createGameUpdateCommand(final GameLocator gameLocator, final DateTime dateTime) {
        return GameUpdateCommand.seasonTickets(gameLocator, dateTime);
      }
    }

    /**
     * The {@link TicketParsingAction} that looks for Academy members' tickets.
     * 
     * @author alex
     * 
     */
    class AcademyMemberTicketParsingAction extends TicketParsingAction {

      /**
       * Instantiates a new academy member ticket parsing action.
       */
      public AcademyMemberTicketParsingAction() {
        super(ACADEMY_MEMBER_PATTERN);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected GameUpdateCommand createGameUpdateCommand(final GameLocator gameLocator, final DateTime dateTime) {
        return GameUpdateCommand.academyTickets(gameLocator, dateTime);
      }
    }

    /**
     * The {@link TicketParsingAction} that looks for general sale tickets.
     * 
     * @author alex
     * 
     */
    class GeneralSaleTicketParsingAction extends TicketParsingAction {

      /**
       * Instantiates a new general sale ticket parsing action.
       */
      public GeneralSaleTicketParsingAction() {
        super(GENERAL_SALE_PATTERN);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected GameUpdateCommand createGameUpdateCommand(final GameLocator gameLocator, final DateTime dateTime) {
        return GameUpdateCommand.generalSaleTickets(gameLocator, dateTime);
      }
    }

    /**
     * Scan the page, first looking for the date the game was played and then
     * looking for any ticket selling dates.
     * 
     * @throws IOException
     *           Signals that an I/O exception has occurred.
     */
    @Override
    public void scan() throws IOException {
      final List<ParsingAction> parsingActions =
          Lists.newArrayList(
              new GameDatePlayedParsingAction(),
              new BondHoldersTicketParsingAction(),
              new PriorityPointTicketParsingAction(),
              new SeasonTicketParsingAction(),
              new AcademyMemberTicketParsingAction(),
              new GeneralSaleTicketParsingAction());
      final TagNodeFilter filter = new TagNodeFilter() {

        @Override
        public boolean apply(final TagNode tagNode) {
          return true;
        }
      };
      for (final TagNode tagNode : filter.list(getTagNode())) {
        final String text = TagNodeUtils.normaliseTextToNull(tagNode);
        if (text != null) {
          final Predicate<ParsingAction> parsingActionPredicate = new Predicate<ParsingAction>() {
            @Override
            public boolean apply(final ParsingAction parsingAction) {
              return text.contains(parsingAction.getContainedText());
            }
          };
          final ParsingAction parsingAction = Iterables.find(parsingActions, parsingActionPredicate, null);
          if (parsingAction != null) {
            final String textForDate = text.replace(parsingAction.getContainedText(), "");
            parsingAction.execute(textForDate);
          }
        }
      }
    }

    /**
     * Gets the currently found {@link GameLocator}.
     * 
     * @return the currently found {@link GameLocator}
     */
    public GameLocator getGameLocator() {
      return gameLocator;
    }

    /**
     * Sets the currently found {@link GameLocator}.
     * 
     * @param gameLocator
     *          the new currently found {@link GameLocator}
     */
    public void setGameLocator(final GameLocator gameLocator) {
      this.gameLocator = gameLocator;
    }

    /**
     * Gets the currently found {@link DateTime} for the game played.
     * 
     * @return the currently found {@link DateTime} for the game played
     */
    public DateTime getDateTimePlayed() {
      return dateTimePlayed;
    }

    /**
     * Sets the currently found {@link DateTime} for the game played.
     * 
     * @param dateTimePlayed
     *          the new currently found {@link DateTime} for the game played
     */
    public void setDateTimePlayed(final DateTime dateTimePlayed) {
      this.dateTimePlayed = dateTimePlayed;
    }
  }
}