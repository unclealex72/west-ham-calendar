/**
 * Copyright 2011 Alex Jones
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

import org.cdmckay.coffeedom.Content;
import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.filter.Filter;
import org.cdmckay.coffeedom.xpath.XPath;
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
import com.google.common.collect.Iterables;

/**
 * @author alex
 * 
 */
public class SeasonHtmlGamesScanner extends StatefulDomBasedHtmlGamesScanner {

	private final static Logger log = LoggerFactory.getLogger(SeasonHtmlGamesScanner.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Scanner createScanner(URI uri, Document document) {
		return new SeasonScanner(uri, document);
	}
	
	class SeasonScanner extends Scanner {
		private int i_season;

		private final XPath i_mainTableXpath = XPath.newInstance("//table[@class='fixtureList']");
		private String i_month;
		private DateTime i_startOfSeason;

		public SeasonScanner(URI uri, Document document) {
			super(uri, document);
		}

		@Override
		public void scan() throws IOException {
			updateSeason();
			Element tableElement = (Element) getMainTableXpath().selectSingleNode(getDocument());
			Filter tableRowFilter = new Filter() {
				@Override
				public boolean matches(Object object) {
					return object instanceof Element && "tr".equals(((Element) object).getName());
				}
			};
			class ClassContainsPredicate implements Predicate<Element> {
				String target;

				public ClassContainsPredicate(String target) {
					super();
					this.target = target;
				}

				@Override
				public boolean apply(Element element) {
					String classes = element.getAttributeValue("class");
					return classes != null && classes.contains(target);
				}
			}
			;
			Predicate<Element> isMonthRowPredicate = new ClassContainsPredicate("rowHeader");
			Predicate<Element> isGameRow = new ClassContainsPredicate("fixture");
			for (Element row : Iterables.filter(tableElement.getDescendants(tableRowFilter), Element.class)) {
				if (isMonthRowPredicate.apply(row)) {
					updateMonth(row);
				}
				else if (isGameRow.apply(row)) {
					updateGame(row);
				}
			}
		}

		/**
		 * Find which season this page represents.
		 */
		protected void updateSeason() {
			final Pattern seasonPattern = Pattern.compile("s\\.prop3=\"([0-9]+)\"");
			Filter filter = new Filter() {
				@Override
				public boolean matches(Object object) {
					if (getSeason() == 0 && object instanceof Element && "script".equals(((Element) object).getName())) {
						String text = ((Element) object).getText();
						Matcher matcher = seasonPattern.matcher(text);
						if (matcher.find()) {
							int season = Integer.valueOf(matcher.group(1));
							log.info("Found season " + season);
							setSeason(season);
							setStartOfSeason(getDateService().parseDate("01/07/" + season, "dd/MM/yyyy"));
						}
					}
					return true;
				}
			};
			for (@SuppressWarnings("unused") Content content : getDocument().getDescendants(filter)) {
				// Do nothing
			}
		}
		
		/**
		 * Update the month.
		 * 
		 * @param row
		 *          The table row containing the month.
		 */
		protected void updateMonth(Element row) {
			Element child = row.getChild("td");
			String month = child.getTextNormalize();
			log.info("Found " + month + " " + getSeason());
			setMonth(month);
		}

		/**
		 * @param row
		 * @param gameUpdateCommands
		 * @throws UnparseableDateException
		 */
		protected void updateGame(Element row) {
			Iterator<Element> tds = row.getChildren("td").iterator();
			String date = normaliseTextToNull(tds.next());
			date = date.replaceAll("[^0-9]", "");
			date = Strings.padStart(date, 2, '0');
			String time = normaliseTextToNull(tds.next());

			Location location = "H".equals(normaliseTextToNull(tds.next())) ? Location.HOME : Location.AWAY;
			Element opponentsEl = tds.next();
			// Could be in a link
			Element opponentsLink = opponentsEl.getChild("a");
			if (opponentsLink != null) {
				opponentsEl = opponentsLink;
			}
			String opponents = normaliseTextToNull(opponentsEl);
			Competition competition = Competition.findByToken(normaliseTextToNull(tds.next()));
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
					normaliseTextToNull(tds.next()));
			String attendenceText = normaliseTextToNull(tds.next());
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
			Element matchReportTd = tds.next();
			Element matchReportLink = matchReportTd.getChild("a");
			String matchReport;
			if (matchReportLink != null) {
				String matchReportPath = matchReportLink.getAttributeValue("href");
				URI matchReportUri = uri.resolve(matchReportPath);
				matchReport = matchReportUri.toString();
			}
			else {
				matchReport = null;
			}
			GameUpdateCommand matchReportUpdateCommand = GameUpdateCommand.matchReport(gameKeyLocator, matchReport);
			getGameUpdateCommands().addAll(Arrays.asList(datePlayedGameUpdateCommand, resultGameUpdateCommand,
					attendenceUpdateCommand, matchReportUpdateCommand));
		}

		public int getSeason() {
			return i_season;
		}

		public XPath getMainTableXpath() {
			return i_mainTableXpath;
		}

		public String getMonth() {
			return i_month;
		}

		public void setMonth(String month) {
			i_month = month;
		}

		public DateTime getStartOfSeason() {
			return i_startOfSeason;
		}

		public void setStartOfSeason(DateTime startOfSeason) {
			i_startOfSeason = startOfSeason;
		}

		public void setSeason(int season) {
			i_season = season;
		}
	}
}
