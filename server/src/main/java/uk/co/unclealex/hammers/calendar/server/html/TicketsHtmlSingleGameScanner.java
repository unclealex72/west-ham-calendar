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
 * @author alex
 * 
 */
public class TicketsHtmlSingleGameScanner extends StatefulDomBasedHtmlGamesScanner {

	private final static Logger log = LoggerFactory.getLogger(TicketsHtmlSingleGameScanner.class);

	/**
	 * The text that indicates the ticket selling date is for Bondholders.
	 */
	public static final String BOND_HOLDER_PATTERN = "Bondholders";
	
	/**
	 * The text that indicates the ticket selling date is for priority point holders.
	 */
	public static final String PRIORITY_POINT_PATTERN = "Priority Point";
	
	/**
	 * The text that indicates the ticket selling date is for season ticket holders.
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

	@Override
	protected Scanner createScanner(URI uri, TagNode tagNode) {
		return new TicketsScanner(uri, tagNode);
	}
	
	/**
	 * A {@link Scanner} that scans a page for ticket sale dates.
	 * @author alex
	 *
	 */
	class TicketsScanner extends Scanner {
		
		/**
		 * The currently found {@link GameLocator}.
		 */
		private GameLocator i_gameLocator;
		
		/**
		 * The currently found {@link DateTime} for the game played.
		 */
		private DateTime i_dateTimePlayed;

		
		public TicketsScanner(URI uri, TagNode tagNode) {
			super(uri, tagNode);
		}

		/**
		 * A parsing action is used to parse segments of text on a web page and, if a matching string is found,
		 * a date is searched for and an action is executed.
		 * @author alex
		 *
		 */
		abstract class ParsingAction {
			
			/**
			 * The text that must be contained in the segment of the web page.
			 */
			private final String i_containedText;
			
			/**
			 * An array of date formats to look for a date to associate with this action.
			 */
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

			/**
			 * Search for or parse text for a {@link DateTime} and, if one is found, do something.
			 * @param dateText The dateText to search for or parse a date time.
			 */
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

			/**
			 * Parse or search for a {@link DateTime}.
			 * @param dateText The text to search for or parse.
			 * @return The found {@link DateTime} or nul if none could be found.
			 * @throws UnparseableDateException
			 */
			abstract DateTime parseDateTime(String dateText) throws UnparseableDateException;

			/**
			 * With the premise that a {@link DateTime} has been found in a segment of the web page, do something with
			 * that information.
			 * @param dateTime The {@link DateTime} that has been found.
			 */
			abstract void execute(DateTime dateTime);

			public String getContainedText() {
				return i_containedText;
			}

			public PossiblyYearlessDateFormat[] getPossiblyYearlessDateFormats() {
				return i_possiblyYearlessDateFormats;
			}
		}

		/**
		 * A {@link ParsingAction} that looks for the date and time the game was played. This is identified by looking
		 * for the string "k/o" and the date and time then precedes. that.
		 * @author alex
		 *
		 */
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

			/**
			 * Make sure that {@link DateTime} the game was played and the corresponding {@link GameLocator} are populated
			 * so that any {@link TicketParsingAction}s know which game to use to create a {@link GameUpdateCommand}.
			 * 
			 * @param dateTime The found {@link DateTime}.
			 * 
			 */
			public void execute(DateTime dateTime) {
				log.info("The game with tickets at URL " + getUri() + " is being played at " + dateTime);
				setDateTimePlayed(dateTime);
				setGameLocator(GameLocator.datePlayedLocator(dateTime));
			}
		}

		/**
		 * A {@link ParsingAction} that looks for a ticket selling date (as identified by a string) and then creates
		 * a {@link GameUpdateCommand} to be stored.
		 * 
		 * @author alex
		 *
		 */
		abstract class TicketParsingAction extends ParsingAction {

			/**
			 * Create a new {@link TicketParsingAction}.
			 * @param containedText The "magic text" which will identify which type of ticket is being sold.
			 */
			public TicketParsingAction(String containedText) {
				super(containedText, "hha EEE dd MMM", "ha EEE dd MMM", "hha EEE d MMM", "ha EEE d MMM");
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

			/**
			 * Create the {@link GameUpdateCommand} that associates a game with a ticket sale date.
			 * @param gameLocator The {@link GameLocator} created by the {@link GameDatePlayedParsingAction}.
			 * @param dateTime The {@link DateTime} parsed in this text.
			 * @return A {@link GameUpdateCommand} that describes the update required.
			 */
			protected abstract GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime);
		}

		/**
		 * The {@link TicketParsingAction} that looks for Bondholder tickets.
		 * @author alex
		 *
		 */
		class BondHoldersTicketParsingAction extends TicketParsingAction {

			public BondHoldersTicketParsingAction() {
				super(BOND_HOLDER_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.bondHolderTickets(gameLocator, dateTime);
			}
		}

		/**
		 * The {@link TicketParsingAction} that looks for Priority pointholder tickets.
		 * @author alex
		 *
		 */
		class PriorityPointTicketParsingAction extends TicketParsingAction {

			public PriorityPointTicketParsingAction() {
				super(PRIORITY_POINT_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.priorityPointTickets(gameLocator, dateTime);
			}
		}

		/**
		 * The {@link TicketParsingAction} that looks for season ticket holder tickets.
		 * @author alex
		 *
		 */

		class SeasonTicketParsingAction extends TicketParsingAction {

			public SeasonTicketParsingAction() {
				super(SEASON_TICKET_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.seasonTickets(gameLocator, dateTime);
			}
		}

		/**
		 * The {@link TicketParsingAction} that looks for Academy members' tickets.
		 * @author alex
		 *
		 */
		class AcademyMemberTicketParsingAction extends TicketParsingAction {

			public AcademyMemberTicketParsingAction() {
				super(ACADEMY_MEMBER_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.academyTickets(gameLocator, dateTime);
			}
		}

		/**
		 * The {@link TicketParsingAction} that looks for general sale tickets.
		 * @author alex
		 *
		 */
		class GeneralSaleTicketParsingAction extends TicketParsingAction {

			public GeneralSaleTicketParsingAction() {
				super(GENERAL_SALE_PATTERN);
			}

			@Override
			protected GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime dateTime) {
				return GameUpdateCommand.generalSaleTickets(gameLocator, dateTime);
			}
		}

		/**
		 * Scan the page, first looking for the date the game was played and then looking for any ticket selling dates.
		 * @throws IOException
		 */
		public void scan() throws IOException {
			List<ParsingAction> parsingActions = Lists
					.newArrayList(new GameDatePlayedParsingAction(), new BondHoldersTicketParsingAction(),
							new PriorityPointTicketParsingAction(), new SeasonTicketParsingAction(),
							new AcademyMemberTicketParsingAction(), new GeneralSaleTicketParsingAction());
			TagNodeFilter filter = new TagNodeFilter() {
				
				@Override
				public boolean apply(TagNode tagNode) {
					return true;
				}
			};
			for (TagNode tagNode : filter.list(getTagNode())) {
				final String text = TagNodeUtils.normaliseTextToNull(tagNode);
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