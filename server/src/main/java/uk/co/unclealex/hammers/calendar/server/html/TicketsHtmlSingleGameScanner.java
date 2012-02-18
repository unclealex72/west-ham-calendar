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
import java.util.List;

import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.filter.Filter;
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
 * @author alex
 * 
 */
public class TicketsHtmlSingleGameScanner extends StatefulDomBasedHtmlGamesScanner {

	private final static Logger log = LoggerFactory.getLogger(TicketsHtmlSingleGameScanner.class);

	public static final String BOND_HOLDER_PATTERN = "Bondholders";
	public static final String PRIORITY_POINT_PATTERN = "Priority Point";
	public static final String SEASON_TICKET_PATTERN = "Season Ticket ";
	public static final String ACADEMY_MEMBER_PATTERN = "Academy Members ";
	public static final String GENERAL_SALE_PATTERN = "General Sale";

	@Override
	protected Scanner createScanner(URI uri, Document document) {
		return new TicketsScanner(uri, document);
	}
	
	class TicketsScanner extends Scanner {
		private GameLocator i_gameLocator;
		private DateTime i_dateTimePlayed;

		
		public TicketsScanner(URI uri, Document document) {
			super(uri, document);
		}

		abstract class ParsingAction {
			private final String i_containedText;
			private final PossiblyYearlessDateFormat[] i_possiblyYearlessDateFormats;

			public ParsingAction(String containedString, String... possiblyYearlessDateFormats) {
				super();
				i_containedText = containedString;
				PossiblyYearlessDateFormat[] pydfs = new PossiblyYearlessDateFormat[possiblyYearlessDateFormats.length];
				for (int idx = 0; idx < possiblyYearlessDateFormats.length; idx++) {
					pydfs[idx] = new AutomaticPossiblyYearlessDateFormat(possiblyYearlessDateFormats[idx]);
				}
				i_possiblyYearlessDateFormats = pydfs;
			}

			void execute(String dateText) {
				try {
					DateTime dateTime = parseDateTime(dateText);
					if (dateTime == null) {
						log.debug("Could not find a date in for URL " + getUri() + " in text " + dateText);
					}
					else {
						execute(dateTime);
					}
				}
				catch (UnparseableDateException e) {
					log.debug("Could not find a date in for URL " + getUri() + " in text " + dateText, e);
				}
			}

			abstract DateTime parseDateTime(String dateText) throws UnparseableDateException;

			abstract void execute(DateTime dateTime);

			/**
			 * @return the containedString
			 */
			public String getContainedText() {
				return i_containedText;
			}

			public PossiblyYearlessDateFormat[] getPossiblyYearlessDateFormats() {
				return i_possiblyYearlessDateFormats;
			}
		}

		class GameDatePlayedParsingAction extends ParsingAction {

			public GameDatePlayedParsingAction() {
				super("k/o", "EEEE dd MMMM yyyy - hha", "EEEE d MMMM yyyy - hha", "EEEE dd MMMM yyyy - hh.mma",
						"EEEE d MMMM yyyy - hh.mma");
			}

			@Override
			DateTime parseDateTime(String dateText) throws UnparseableDateException {
				return getDateService().findPossiblyYearlessDate(dateText, new DateTime(), false,
						getPossiblyYearlessDateFormats());
			}

			@Override
			public void execute(DateTime dateTime) {
				log.info("The game with tickets at URL " + getUri() + " is being played at " + dateTime);
				setDateTimePlayed(dateTime);
				setGameLocator(GameLocator.datePlayedLocator(dateTime));
			}
		}

		abstract class TicketParsingAction extends ParsingAction {

			public TicketParsingAction(String containedString) {
				super(containedString, "hha EEE dd MMM", "ha EEE dd MMM", "hha EEE d MMM", "ha EEE d MMM");
			}

			@Override
			DateTime parseDateTime(String dateText) throws UnparseableDateException {
				return getDateService().findPossiblyYearlessDate(dateText, getDateTimePlayed(), true,
						getPossiblyYearlessDateFormats());
			}

			@Override
			public void execute(DateTime dateTime) {
				log.info("Found ticket type " + getContainedText().trim() + " for game at " + getDateTimePlayed()
						+ " being sold at " + dateTime);
				GameUpdateCommand gameUpdateCommand = createGameUpdateCommand(getGameLocator(), dateTime);
				getGameUpdateCommands().add(gameUpdateCommand);
			}

			protected abstract GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime);
		}

		class BondHoldersTicketParsingAction extends TicketParsingAction {

			public BondHoldersTicketParsingAction() {
				super(BOND_HOLDER_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.bondHolderTickets(gameLocator, dateTime);
			}
		}

		class PriorityPointTicketParsingAction extends TicketParsingAction {

			public PriorityPointTicketParsingAction() {
				super(PRIORITY_POINT_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.priorityPointTickets(gameLocator, dateTime);
			}
		}

		class SeasonTicketParsingAction extends TicketParsingAction {

			public SeasonTicketParsingAction() {
				super(SEASON_TICKET_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.seasonTickets(gameLocator, dateTime);
			}
		}

		class AcademyMemberTicketParsingAction extends TicketParsingAction {

			public AcademyMemberTicketParsingAction() {
				super(ACADEMY_MEMBER_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.academyTickets(gameLocator, dateTime);
			}
		}

		class GeneralSaleTicketParsingAction extends TicketParsingAction {

			public GeneralSaleTicketParsingAction() {
				super(GENERAL_SALE_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.generalSaleTickets(gameLocator, dateTime);
			}
		}

		@Override
		public void scan() throws IOException {
			List<ParsingAction> parsingActions = Lists
					.newArrayList(new GameDatePlayedParsingAction(), new BondHoldersTicketParsingAction(),
							new PriorityPointTicketParsingAction(), new SeasonTicketParsingAction(),
							new AcademyMemberTicketParsingAction(), new GeneralSaleTicketParsingAction());
			Filter filter = new Filter() {
				@Override
				public boolean matches(Object object) {
					return object instanceof Element;
				}
			};
			for (Element el : Iterables.filter(getDocument().getDescendants(filter), Element.class)) {
				final String text = normaliseTextToNull(el);
				if (text != null) {
					Predicate<ParsingAction> parsingActionPredicate = new Predicate<ParsingAction>() {
						@Override
						public boolean apply(ParsingAction parsingAction) {
							return text.contains(parsingAction.getContainedText());
						}
					};
					ParsingAction parsingAction = Iterables.find(parsingActions, parsingActionPredicate, null);
					if (parsingAction != null) {
						String textForDate = text.replace(parsingAction.getContainedText(), "");
						parsingAction.execute(textForDate);
					}
				}
			}
		}

		public GameLocator getGameLocator() {
			return i_gameLocator;
		}

		public void setGameLocator(GameLocator gameLocator) {
			i_gameLocator = gameLocator;
		}

		public DateTime getDateTimePlayed() {
			return i_dateTimePlayed;
		}

		public void setDateTimePlayed(DateTime dateTimePlayed) {
			i_dateTimePlayed = dateTimePlayed;
		}
	}
}