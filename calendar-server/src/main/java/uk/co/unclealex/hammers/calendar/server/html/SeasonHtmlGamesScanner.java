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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.dates.UnparseableDateException;
import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

/**
 * An {@link HtmlGamesScanner} that scans the season's fixtures page for game
 * information.
 * 
 * @author alex
 * 
 */
public class SeasonHtmlGamesScanner extends StatefulDomBasedHtmlGamesScanner {

  /** The logger for this class. */
  private static final Logger log = LoggerFactory.getLogger(SeasonHtmlGamesScanner.class);

  /**
   * {@inheritDoc}
   */
  @Override
  protected Scanner createScanner(final URI uri, final TagNode tagNode) {
    return new SeasonScanner(uri, tagNode);
  }

  /**
   * The {@link Scanner} that scans the season's fixtures page for game
   * information.
   * 
   * @author alex
   * 
   */
  class SeasonScanner extends Scanner {

    /**
     * The current season.
     */
    private int season;

    /**
     * The current month.
     */
    private String month;

    /**
     * The {@link DateTime} the season started.
     */
    private DateTime startOfSeason;

    /**
     * Instantiates a new season scanner.
     * 
     * @param uri
     *          the uri
     * @param tagNode
     *          the tag node
     */
    public SeasonScanner(final URI uri, final TagNode tagNode) {
      super(uri, tagNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scan() throws IOException {
      updateSeason();
      TagNode tableTagNode;
      try {
        tableTagNode = (TagNode) getTagNode().evaluateXPath("//table[@class='fixtureList']")[0];
      }
      catch (final XPatherException e) {
        throw new IOException(e);
      }
      final TagNodeFilter tableRowFilter = new TagNodeFilter() {

        @Override
        public boolean apply(final TagNode tagNode) {
          return "tr".equals(tagNode.getName());
        }
      };
      class ClassContainsPredicate implements Predicate<TagNode> {
        String target;

        public ClassContainsPredicate(final String target) {
          super();
          this.target = target;
        }

        @Override
        public boolean apply(final TagNode tagNode) {
          final String classes = tagNode.getAttributeByName("class");
          return classes != null && classes.contains(target);
        }
      }
      final Predicate<TagNode> isMonthRowPredicate = new ClassContainsPredicate("rowHeader");
      final Predicate<TagNode> isGameRow = new ClassContainsPredicate("fixture");
      for (final TagNode row : tableRowFilter.list(tableTagNode)) {
        if (isMonthRowPredicate.apply(row)) {
          updateMonth(row);
        }
        else if (isGameRow.apply(row)) {
          updateGame(row);
        }
      }
    }

    /**
     * Find which season page represents.
     */
    protected void updateSeason() {
      final Pattern seasonPattern = Pattern.compile("s\\.prop3=\"([0-9]+)\"");
      new TagNodeWalker(getTagNode()) {

        @Override
        public void execute(final TagNode tagNode) {
          if (getSeason() == 0 && "script".equals(tagNode.getName())) {
            final String text = TagNodeUtils.normaliseText(tagNode);
            final Matcher matcher = seasonPattern.matcher(text);
            if (matcher.find()) {
              final int season = Integer.valueOf(matcher.group(1));
              log.info("Found season " + season);
              setSeason(season);
              setStartOfSeason(getDateService().parseDate("01/07/" + season, "dd/MM/yyyy"));
            }
          }
        }
      };
    }

    /**
     * Update the month.
     * 
     * @param row
     *          The table row containing the month.
     */
    protected void updateMonth(final TagNode row) {
      final TagNode child = row.findElementByName("td", false);
      final String month = TagNodeUtils.normaliseText(child);
      log.info("Found " + month + " " + getSeason());
      setMonth(month);
    }

    /**
     * Update a game.
     * 
     * @param row
     *          The current row in the fixtures table.
     */
    protected void updateGame(final TagNode row) {
      final Iterator<TagNode> tds = Iterators.forArray(row.getElementsByName("td", false));
      String date = TagNodeUtils.normaliseTextToNull(tds.next());
      date = date.replaceAll("[^0-9]", "");
      date = Strings.padStart(date, 2, '0');
      final String time = TagNodeUtils.normaliseTextToNull(tds.next());

      final Location location =
          "H".equals(TagNodeUtils.normaliseTextToNull(tds.next())) ? Location.HOME : Location.AWAY;
      TagNode opponentsEl = tds.next();
      // Could be in a link
      final TagNode opponentsLink = opponentsEl.findElementByName("a", false);
      if (opponentsLink != null) {
        opponentsEl = opponentsLink;
      }
      final String opponents = TagNodeUtils.normaliseTextToNull(opponentsEl);
      final Competition competition = Competition.findByToken(TagNodeUtils.normaliseTextToNull(tds.next()));
      final GameKey gameKey = new GameKey(competition, location, opponents, getSeason());
      log.info("Found game key " + gameKey);
      final String datePlayedString = Joiner.on(" ").join(date, time, getMonth());
      DateTime datePlayed;
      try {
        datePlayed =
            getDateService().parsePossiblyYearlessDate(
                datePlayedString,
                getStartOfSeason(),
                false,
                new String[] { ("dd HH:mm MMMM[ yyyy]") });
      }
      catch (final UnparseableDateException e) {
        log.warn("Cannot parse date " + datePlayedString + " for game " + gameKey, e);
        return;
      }
      final GameLocator gameKeyLocator = GameLocator.gameKeyLocator(gameKey);
      final GameUpdateCommand datePlayedGameUpdateCommand = GameUpdateCommand.datePlayed(gameKeyLocator, datePlayed);
      tds.next(); // Move past the W/L/D token.
      final GameUpdateCommand resultGameUpdateCommand =
          GameUpdateCommand.result(gameKeyLocator, TagNodeUtils.normaliseTextToNull(tds.next()));
      final String attendenceText = TagNodeUtils.normaliseTextToNull(tds.next());
      Integer attendence;
      final URI uri = getUri();
      try {
        attendence =
            (attendenceText == null || "00".equals(attendenceText)) ? null : NumberFormat
                .getIntegerInstance(Locale.UK)
                .parse(attendenceText)
                .intValue();
      }
      catch (final ParseException e) {
        final StringBuilder sb = new StringBuilder();
        for (final byte by : attendenceText.getBytes()) {
          int i;
          if (by < 0) {
            i = 256 + by;
          }
          else {
            i = by;
          }
          sb.append("0x").append(Integer.toHexString(i));
        }
        log.warn("Cannot parse attendance " + sb.toString() + " on page " + uri, e);
        attendence = null;
      }
      final GameUpdateCommand attendenceUpdateCommand = GameUpdateCommand.attendence(gameKeyLocator, attendence);
      tds.next(); // Move past the league table token.
      final TagNode matchReportTd = tds.next();
      final TagNode matchReportLink = matchReportTd.findElementByName("a", false);
      String matchReport;
      if (matchReportLink != null) {
        final String matchReportPath = matchReportLink.getAttributeByName("href");
        final URI matchReportUri = uri.resolve(matchReportPath);
        matchReport = matchReportUri.toString();
      }
      else {
        matchReport = null;
      }
      final GameUpdateCommand matchReportUpdateCommand = GameUpdateCommand.matchReport(gameKeyLocator, matchReport);
      getGameUpdateCommands().addAll(
          Arrays.asList(
              datePlayedGameUpdateCommand,
              resultGameUpdateCommand,
              attendenceUpdateCommand,
              matchReportUpdateCommand));
    }

    /**
     * Gets the current season.
     * 
     * @return the current season
     */
    public int getSeason() {
      return season;
    }

    /**
     * Gets the current month.
     * 
     * @return the current month
     */
    public String getMonth() {
      return month;
    }

    /**
     * Sets the current month.
     * 
     * @param month
     *          the new current month
     */
    public void setMonth(final String month) {
      this.month = month;
    }

    /**
     * Gets the {@link DateTime} the season started.
     * 
     * @return the {@link DateTime} the season started
     */
    public DateTime getStartOfSeason() {
      return startOfSeason;
    }

    /**
     * Sets the {@link DateTime} the season started.
     * 
     * @param startOfSeason
     *          the new {@link DateTime} the season started
     */
    public void setStartOfSeason(final DateTime startOfSeason) {
      this.startOfSeason = startOfSeason;
    }

    /**
     * Sets the current season.
     * 
     * @param season
     *          the new current season
     */
    public void setSeason(final int season) {
      this.season = season;
    }
  }
}