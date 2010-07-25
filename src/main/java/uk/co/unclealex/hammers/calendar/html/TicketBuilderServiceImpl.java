/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.dao.GameDao;
import uk.co.unclealex.hammers.calendar.exception.UnparseableDateException;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;
import uk.co.unclealex.hammers.calendar.service.DateService;
import uk.co.unclealex.hammers.calendar.service.GameService;

@Transactional
public class TicketBuilderServiceImpl implements TicketBuilderService {

	private static final Logger log = Logger.getLogger(TicketBuilderServiceImpl.class);
	
	private HtmlDocumentService i_htmlDocumentService;
	private UrlExtractorService i_urlExtractorService;
	private GameService i_gameService;
	private GameDao i_gameDao;
	private DateService i_dateService;
	
	private int i_ticketOfficeOpeningHour;
	
	public void decorateWithTicketInformation() throws IOException {
		
		UrlExtractorService urlExtractorService = getUrlExtractorService();
		GameDao gameDao = getGameDao();
		GameService gameService = getGameService();
		
		URL homePage = urlExtractorService.getHomePage();
		URL ticketInformationUrl = urlExtractorService.getTicketInformationUrl(homePage);
		Map<Date, URL> ticketInformationUrls = urlExtractorService.getTicketInformationUrls(ticketInformationUrl);
		
		for (Map.Entry<Date, URL> entry : ticketInformationUrls.entrySet()) {
			Date datePlayed = entry.getKey();
			URL ticketUrl = entry.getValue();
			Game game = gameDao.findByDatePlayed(datePlayed);
			if (game != null) {
				BufferedReader reader = new BufferedReader(new StringReader(getHtmlDocumentService().load(ticketUrl)));
				String line;
				while ((line = reader.readLine()) != null) {
					if (game.getLocation() == Location.HOME) {
						populateHomeTicketDates(game, line, ticketUrl);
					}
					else {
						populateAwayTicketDates(game, line, ticketUrl);
					}
				}
				reader.close();
				gameService.storeGame(game);
			}
		}
	}

	protected void populateHomeTicketDates(Game game, String line, URL ticketUrl) {
		populateTicketDates(game, line, ticketUrl, new HomeAcademyTicketPopulator(), new HomeGeneralSaleTicketPopulator());
	}

	protected void populateAwayTicketDates(Game game, String line, URL ticketUrl) {
		populateTicketDates(
				game, line, ticketUrl, 
				new AwayBondHolderTicketPopulator(), new AwayPriorityPointTicketPopulator(), 
				new AwaySeasonTicketTicketPopulator(), new AwayAcademyTicketPopulator(), new AwayGeneralSaleTicketPopulator());
	}

	protected void populateTicketDates(Game game, String line, URL ticketUrl, TicketPopulator... ticketPopulators) {
		boolean found = false;
		for (int idx = 0; !found && idx < ticketPopulators.length; idx++) {
			found = ticketPopulators[idx].populateDate(game, line, ticketUrl);
		}
	}

	protected abstract class TicketPopulator {
		private String i_regex;
		private String i_dateFormat;
		
		public TicketPopulator(String regex, String dateFormat) {
			super();
			i_regex = regex;
			i_dateFormat = dateFormat;
		}
		
		public boolean populateDate(Game game, String line, URL ticketUrl) {
			Pattern pattern = Pattern.compile(getRegex());
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String date = matcher.group(1);
				try {
					Date ticketDate = getDateService().parseYearlessDate(getDateFormat(), date, game.getDatePlayed(), true, ticketUrl);
					if (ticketDate != null) {
						populateDate(game, openingHours(ticketDate));
						return true;
					}
				}
				catch (UnparseableDateException e) {
					log.warn(
							"Cannot parse the ticketing date (" + date + ") for the game on " + new SimpleDateFormat("dd/MM/yyyy").format(date), e);
				}
			}
			return false;
		}

		public abstract void populateDate(Game game, Date ticketDate);
		
		public String getRegex() {
			return i_regex;
		}

		public String getDateFormat() {
			return i_dateFormat;
		}
	}
	
	public abstract class HomeTicketPopulator extends TicketPopulator {

		public HomeTicketPopulator(String prefix) {
			super(prefix + " ([a-zA-Z]{3,} \\d+ [a-zA-Z]+)", "EEE dd MMMM");
		}
		
	}
	public class HomeAcademyTicketPopulator extends HomeTicketPopulator {
		public HomeAcademyTicketPopulator() {
			super("Academy Members - Postal/Telephone/Online Bookings - from \\d+\\.\\d+.m on");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setAcademyMembersAvailable(ticketDate);
		}
	}
	
	public class HomeGeneralSaleTicketPopulator extends HomeTicketPopulator {
		public HomeGeneralSaleTicketPopulator() {
			super("General Sale - from \\d+\\.\\d+.m");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setGeneralSaleAvailable(ticketDate);
		}
	}

	public abstract class AwayTicketPopulator extends TicketPopulator {

		public AwayTicketPopulator(String prefix) {
			super(prefix + " ([a-zA-Z]{3,} \\d+ [a-zA-Z]+)", "EEE dd MMMM");
		}
		
	}

	public class AwayBondHolderTicketPopulator extends AwayTicketPopulator {
		public AwayBondHolderTicketPopulator() {
			super("Bondholders -");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setBondholdersAvailable(ticketDate);
		}
	}

	public class AwayPriorityPointTicketPopulator extends AwayTicketPopulator {
		public AwayPriorityPointTicketPopulator() {
			super("Priority Point Post Applications - Receive by 10.00am");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setPriorityPointPostAvailable(ticketDate);
		}
	}

	public class AwaySeasonTicketTicketPopulator extends AwayTicketPopulator {
		public AwaySeasonTicketTicketPopulator() {
			super("Season Ticket General Sale -");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setSeasonTicketsAvailable(ticketDate);
		}
	}

	public class AwayAcademyTicketPopulator extends AwayTicketPopulator {
		public AwayAcademyTicketPopulator() {
			super("Academy Members -");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setAcademyMembersAvailable(ticketDate);
		}
	}

	public class AwayGeneralSaleTicketPopulator extends AwayTicketPopulator {
		public AwayGeneralSaleTicketPopulator() {
			super("General Sale -");
		}
		
		@Override
		public void populateDate(Game game, Date ticketDate) {
			game.setGeneralSaleAvailable(ticketDate);
		}
	}

	protected Date openingHours(Date ticketDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(ticketDate);
		calendar.set(Calendar.HOUR_OF_DAY, getTicketOfficeOpeningHour());
		for (int field : new int[] { Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND }) {
			calendar.set(field, 0);
		}
		return calendar.getTime();
	}


	public int getTicketOfficeOpeningHour() {
		return i_ticketOfficeOpeningHour;
	}

	public void setTicketOfficeOpeningHour(int ticketOfficeOpeningHour) {
		i_ticketOfficeOpeningHour = ticketOfficeOpeningHour;
	}

	public HtmlDocumentService getHtmlDocumentService() {
		return i_htmlDocumentService;
	}

	public void setHtmlDocumentService(HtmlDocumentService htmlDocumentService) {
		i_htmlDocumentService = htmlDocumentService;
	}

	public UrlExtractorService getUrlExtractorService() {
		return i_urlExtractorService;
	}

	public void setUrlExtractorService(UrlExtractorService urlExtractorService) {
		i_urlExtractorService = urlExtractorService;
	}

	public GameService getGameService() {
		return i_gameService;
	}

	public void setGameService(GameService gameService) {
		i_gameService = gameService;
	}

	public GameDao getGameDao() {
		return i_gameDao;
	}

	public void setGameDao(GameDao gameDao) {
		i_gameDao = gameDao;
	}

	public DateService getDateService() {
		return i_dateService;
	}

	public void setDateService(DateService dateService) {
		i_dateService = dateService;
	}
}
