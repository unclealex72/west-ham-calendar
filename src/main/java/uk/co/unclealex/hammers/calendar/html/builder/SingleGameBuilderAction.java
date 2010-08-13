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
package uk.co.unclealex.hammers.calendar.html.builder;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.exception.UnparseableDateException;
import uk.co.unclealex.hammers.calendar.model.Competition;
import uk.co.unclealex.hammers.calendar.model.Game;
import uk.co.unclealex.hammers.calendar.model.Location;
import uk.co.unclealex.hammers.calendar.model.Month;
import uk.co.unclealex.hammers.calendar.service.DateService;
import uk.co.unclealex.hammers.calendar.service.GameService;

@Transactional
public class SingleGameBuilderAction extends AbstractGameBuilderAction {

	private GameService i_gameService;
	private DateService i_dateService;
	
	private static String DATE_FORMAT = "yyyy MMMM dd HH:mm";
	private static final Logger log = Logger.getLogger(SingleGameBuilderAction.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void build(List<Element> tableCellElements, URL referringUrl) throws IOException {
		/*
		 * The following elements are expected:
		 * 
		 * 1) The date in the format EEE dd,
		 * 2) The start time in the format HH:mm,
		 * 3) H for home, A for away,
		 * 4) The link to the opponents or just the opponents name,
		 * 5) The competition,
		 * 6) Whether the game was won, drawn or lost,
		 * 7) The final score,
		 * 8) The match attendence,
		 * 9) A link to the league table at the time of the match,
		 * 10) A link to the match report.
		 */
		Iterator<Element> iter = tableCellElements.iterator();
		Element dateElement = iter.next();
		Element startTimeElement = iter.next();
		Element destinationElement = iter.next();
		Element opponentsElement = iter.next();
		Element competitionElement = iter.next();
		Element wonDrawnOrLostElement = iter.next();
		Element scoreElement = iter.next();
		Element attendenceElement = iter.next();
		iter.next();
		Element matchReportElement = iter.next();
		
		// Firstly, ignore any postponed games.
		String score = scoreElement.getTextNormalize();
		if ("P-P".equals(score)) {
			return;
		}
		
		// Work out when the game was played.
		String date = StringUtils.split(dateElement.getTextNormalize())[1];
		String time = startTimeElement.getTextNormalize();
		if ("TBC".equals(time)) {
			time = "15:00";
		}
		GameBuilderInformation gameBuilderInformation = getGameBuilderInformation();
		Month month = gameBuilderInformation.getMonth();
		int season = gameBuilderInformation.getYear();
		String datePlayedText = 
			(season + month.getYearOffset()) + " " + month.getName() + " " + date + " " + time; 
		Date datePlayed = null;
		try {
			datePlayed = getDateService().parseDate(DATE_FORMAT, datePlayedText, referringUrl);
		}
		catch (UnparseableDateException e) {
			log.warn("Cannot parse date text " + datePlayedText, e);
		}
		
		// Was the game home or away?
		Location location = "H".equals(destinationElement.getTextNormalize())?Location.HOME:Location.AWAY;
		
		// Who were the opponents?
		String opponents = opponentsElement.getChildTextNormalize("a");
		if (opponents == null) {
			opponents = opponentsElement.getText().trim();
		}
		
		// Which competition?
		Competition competition = Competition.findByToken(competitionElement.getTextNormalize());

		Game game = getGameService().findOrCreateGame(competition, location, opponents, season);
		game.setDatePlayed(datePlayed);
		
		// What was the result? From here on leave as null if the game has yet to be played.
		if (!score.isEmpty()) {
			String wonDrawnOrLost = wonDrawnOrLostElement.getTextNormalize();
			game.setResult(wonDrawnOrLost + " " + score);
		}
		
		// What was the attendence?
		String attendence = attendenceElement.getTextNormalize();
		if (!attendence.isEmpty()) {
			try {
				game.setAttendence(((Long) NumberFormat.getIntegerInstance().parse(attendence)).intValue());
			}
			catch (ParseException e) {
				// If we can't parse the attendence, don't worry about it.
			}
		}
		List<Element> matchReportChildren = matchReportElement.getChildren("a");
		if (!matchReportChildren.isEmpty()) {
			Element matchReportLink = matchReportChildren.get(0);
			game.setMatchReport(new URL(gameBuilderInformation.getUrl(), matchReportLink.getAttributeValue("href")).toString());
		}
		
		gameBuilderInformation.getGames().add(game);
		getGameService().storeGame(game);
	}

	public GameService getGameService() {
		return i_gameService;
	}

	public void setGameService(GameService gameService) {
		i_gameService = gameService;
	}

	public DateService getDateService() {
		return i_dateService;
	}

	public void setDateService(DateService dateService) {
		i_dateService = dateService;
	}
}
