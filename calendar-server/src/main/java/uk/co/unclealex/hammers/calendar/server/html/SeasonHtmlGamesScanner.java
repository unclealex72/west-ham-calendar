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

import uk.co.unclealex.hammers.calendar.server.dates.AutomaticPossiblyYearlessDateFormat;
import uk.co.unclealex.hammers.calendar.server.dates.PossiblyYearlessDateFormat;
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
	protected Scanner createScanner(URI uri, TagNode tagNode) {
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
		public SeasonScanner(URI uri, TagNode tagNode) {
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
			catch (XPatherException e) {
				throw new IOException(e);
			}
			TagNodeFilter tableRowFilter = new TagNodeFilter() {

				@Override
				public boolean apply(TagNode tagNode) {
					return "tr".equals(tagNode.getName());
				}
			};
			class ClassContainsPredicate implements Predicate<TagNode> {
				String target;

				public ClassContainsPredicate(String target) {
					super();
					this.target = target;
				}

				@Override
				public boolean apply(TagNode tagNode) {
					String classes = tagNode.getAttributeByName("class");
					return classes != null && classes.contains(target);
				}
			}
			Predicate<TagNode> isMonthRowPredicate = new ClassContainsPredicate("rowHeader");
			Predicate<TagNode> isGameRow = new ClassContainsPredicate("fixture");
			for (TagNode row : tableRowFilter.list(tableTagNode)) {
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
				public void execute(TagNode tagNode) {
					if (getSeason() == 0 && "script".equals(tagNode.getName())) {
						String text = TagNodeUtils.normaliseText(tagNode);
						Matcher matcher = seasonPattern.matcher(text);
						if (matcher.find()) {
							int season = Integer.valueOf(matcher.group(1));
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
		protected void updateMonth(TagNode row) {
			TagNode child = row.findElementByName("td", false);
			String month = TagNodeUtils.normaliseText(child);
			log.info("Found " + month + " " + getSeason());
			setMonth(month);
		}

		/**
		 * Update a game.
		 * 
		 * @param row
		 *          The current row in the fixtures table.
		 */
		protected void updateGame(TagNode row) {
			Iterator<TagNode> tds = Iterators.forArray(row.getElementsByName("td", false));
			String date = TagNodeUtils.normaliseTextToNull(tds.next());
			date = date.replaceAll("[^0-9]", "");
			date = Strings.padStart(date, 2, '0');
			String time = TagNodeUtils.normaliseTextToNull(tds.next());

			Location location = "H".equals(TagNodeUtils.normaliseTextToNull(tds.next())) ? Location.HOME : Location.AWAY;
			TagNode opponentsEl = tds.next();
			// Could be in a link
			TagNode opponentsLink = opponentsEl.findElementByName("a", false);
			if (opponentsLink != null) {
				opponentsEl = opponentsLink;
			}
			String opponents = TagNodeUtils.normaliseTextToNull(opponentsEl);
			Competition competition = Competition.findByToken(TagNodeUtils.normaliseTextToNull(tds.next()));
			GameKey gameKey = new GameKey(competition, location, opponents, getSeason());
			log.info("Found game key " + gameKey);
			String datePlayedString = Joiner.on(" ").join(date, time, getMonth());
			PossiblyYearlessDateFormat pydf = new AutomaticPossiblyYearlessDateFormat("dd HH:mm MMMM[ yyyy]");
			DateTime datePlayed;
			try {
				datePlayed = getDateService().parsePossiblyYearlessDate(datePlayedString, getStartOfSeason(), false, pydf);
			}
			catch (UnparseableDateException e) {
				log.warn("Cannot parse date " + datePlayedString + " for game " + gameKey, e);
				return;
			}
			GameLocator gameKeyLocator = GameLocator.gameKeyLocator(gameKey);
			GameUpdateCommand datePlayedGameUpdateCommand = GameUpdateCommand.datePlayed(gameKeyLocator, datePlayed);
			tds.next(); // Move past the W/L/D token.
			GameUpdateCommand resultGameUpdateCommand = GameUpdateCommand.result(gameKeyLocator,
					TagNodeUtils.normaliseTextToNull(tds.next()));
			String attendenceText = TagNodeUtils.normaliseTextToNull(tds.next());
			Integer attendence;
			URI uri = getUri();
			try {
				attendence = (attendenceText == null || "00".equals(attendenceText)) ? null : NumberFormat
						.getIntegerInstance(Locale.UK).parse(attendenceText).intValue();
			}
			catch (ParseException e) {
				StringBuilder sb = new StringBuilder();
				for (byte by : attendenceText.getBytes()) {
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
			GameUpdateCommand attendenceUpdateCommand = GameUpdateCommand.attendence(gameKeyLocator, attendence);
			tds.next(); // Move past the league table token.
			TagNode matchReportTd = tds.next();
			TagNode matchReportLink = matchReportTd.findElementByName("a", false);
			String matchReport;
			if (matchReportLink != null) {
				String matchReportPath = matchReportLink.getAttributeByName("href");
				URI matchReportUri = uri.resolve(matchReportPath);
				matchReport = matchReportUri.toString();
			}
			else {
				matchReport = null;
			}
			GameUpdateCommand matchReportUpdateCommand = GameUpdateCommand.matchReport(gameKeyLocator, matchReport);
			getGameUpdateCommands().addAll(
					Arrays.asList(datePlayedGameUpdateCommand, resultGameUpdateCommand, attendenceUpdateCommand,
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
		public void setMonth(String month) {
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
		public void setStartOfSeason(DateTime startOfSeason) {
			this.startOfSeason = startOfSeason;
		}

		/**
		 * Sets the current season.
		 * 
		 * @param season
		 *          the new current season
		 */
		public void setSeason(int season) {
			this.season = season;
		}
	}
}
