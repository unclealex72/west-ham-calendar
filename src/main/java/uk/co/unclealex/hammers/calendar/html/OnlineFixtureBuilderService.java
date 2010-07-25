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

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.html.builder.GameBuilderAction;
import uk.co.unclealex.hammers.calendar.html.builder.GameBuilderActionFactory;
import uk.co.unclealex.hammers.calendar.html.builder.GameBuilderInformation;
import uk.co.unclealex.hammers.calendar.model.Game;

/**
 * A fixture builder service that just queries the online calendar every time it is requested.
 * @author alex
 *
 */
@Transactional
public class OnlineFixtureBuilderService extends AbstractFixtureBuilderService implements Runnable {

	private static final Logger log = Logger.getLogger(OnlineFixtureBuilderService.class);
	
	private HtmlDocumentService i_htmlDocumentService;
	private UrlExtractorService i_urlExtractorService;
	private TicketBuilderService i_ticketBuilderService;
	
	private FixtureTableExtractorService<Document> i_fixtureTableExtractorService;
	private GameBuilderActionFactory i_gameBuilderActionFactory;

	@Override
	public void run() {
		try {
			buildAll();
		}
		catch (IOException e) {
			log.error("Could not build a list of games.", e);
		}
	}
	@SuppressWarnings("unchecked")
	public List<Game> build(int year, URL u, final Boolean attended) throws IOException {
		String content = getHtmlDocumentService().load(u);
		
		GameBuilderInformation gameBuilderInformation = new GameBuilderInformation(year, u);
		Document document = getFixtureTableExtractorService().extractFixtureTable(content);
		Map<String, GameBuilderAction> gameBuilderActions = new HashMap<String, GameBuilderAction>();
		GameBuilderAction singleGameBuilderAction = 
			getGameBuilderActionFactory().createSingleGameBuilderAction(gameBuilderInformation);
		GameBuilderAction monthBuilderAction = 
			getGameBuilderActionFactory().createMonthBuilderAction(gameBuilderInformation);
		gameBuilderActions.put("rowHeader", monthBuilderAction);
		gameBuilderActions.put("rowDark", singleGameBuilderAction);
		gameBuilderActions.put("rowLight", singleGameBuilderAction);
		
		List<Element> tableRowElements = document.getRootElement().getChildren("tr");
		for (Element tableRowElement : tableRowElements) {
			String clazz = tableRowElement.getAttributeValue("class");
			if (clazz != null) {
				GameBuilderAction action = gameBuilderActions.get(clazz);
				if (action != null) {
					action.build(tableRowElement.getChildren("td"), u);
				}
			}
		}
		List<Game> games;
		if (attended == null) {
			games = gameBuilderInformation.getGames(); 
		}
		else {
			games = new LinkedList<Game>();
			Predicate<Game> predicate = new Predicate<Game>() {
				@Override
				public boolean evaluate(Game game) {
					return game.isAttended() == attended.booleanValue();
				}
			};
			CollectionUtils.selectRejected(gameBuilderInformation.getGames(), predicate, games);
		}
		return games;
	}

	@Override
	public List<Game> buildAll(Boolean attended) throws IOException {
		UrlExtractorService urlExtractorService = getUrlExtractorService();
		
		URL homePage = urlExtractorService.getHomePage();
		URL defaultFixtureListUrl = urlExtractorService.getDefaultFixtureListUrl(homePage);
		Map<Integer, URL> fixtureListUrls = urlExtractorService.getFixtureListUrls(defaultFixtureListUrl);
		SortedMap<Integer, URL> sortedFixtureListUrls =
			new TreeMap<Integer, URL>(
				new Comparator<Integer>() { 
					@Override
					public int compare(Integer o1, Integer o2) {
						return -o1.compareTo(o2);
					}
				});
		sortedFixtureListUrls.putAll(fixtureListUrls);
		List<Game> allGames = new LinkedList<Game>();
		for (Map.Entry<Integer, URL> entry : sortedFixtureListUrls.entrySet()) {
			int season = entry.getKey();
			URL fixturesUrl = entry.getValue();
			allGames.addAll(build(season, fixturesUrl, null));
		}
		getTicketBuilderService().decorateWithTicketInformation();
		return allGames;
	}
	
	public HtmlDocumentService getHtmlDocumentService() {
		return i_htmlDocumentService;
	}

	public void setHtmlDocumentService(HtmlDocumentService htmlDocumentService) {
		i_htmlDocumentService = htmlDocumentService;
	}

	public FixtureTableExtractorService<Document> getFixtureTableExtractorService() {
		return i_fixtureTableExtractorService;
	}

	public void setFixtureTableExtractorService(
			FixtureTableExtractorService<Document> fixtureTableExtractorService) {
		i_fixtureTableExtractorService = fixtureTableExtractorService;
	}

	public GameBuilderActionFactory getGameBuilderActionFactory() {
		return i_gameBuilderActionFactory;
	}

	public void setGameBuilderActionFactory(
			GameBuilderActionFactory gameBuilderActionFactory) {
		i_gameBuilderActionFactory = gameBuilderActionFactory;
	}

	public UrlExtractorService getUrlExtractorService() {
		return i_urlExtractorService;
	}

	public void setUrlExtractorService(UrlExtractorService urlExtractorService) {
		i_urlExtractorService = urlExtractorService;
	}

	public TicketBuilderService getTicketBuilderService() {
		return i_ticketBuilderService;
	}

	public void setTicketBuilderService(TicketBuilderService ticketBuilderService) {
		i_ticketBuilderService = ticketBuilderService;
	}

}
