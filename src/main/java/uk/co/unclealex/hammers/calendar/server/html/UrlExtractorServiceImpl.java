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
package uk.co.unclealex.hammers.calendar.server.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

import uk.co.unclealex.hammers.calendar.server.exception.UnparseableDateException;
import uk.co.unclealex.hammers.calendar.server.service.DateService;

public class UrlExtractorServiceImpl implements UrlExtractorService {

	private static final Logger log = Logger.getLogger(UrlExtractorServiceImpl.class);
	
	private URL i_homePage;
	private HtmlDocumentService i_htmlDocumentService;
	private FixtureTableExtractorService<Document> i_fixtureTableExtractorService;
	private DateService i_dateService;
	
	@Override
	public URL getDefaultFixtureListUrl(URL homePage) throws IOException {
		return getHomePageDerivedUrl(homePage, "Fixtures &amp; Results");
	}
	
	@Override
	public URL getTicketInformationUrl(URL homePage) throws IOException {
		return getHomePageDerivedUrl(homePage, "Ticket News");
	}
	
	public URL getHomePageDerivedUrl(URL homePage, String identifyingText) throws IOException {
		String document = getHtmlDocumentService().load(homePage);
		Pattern pattern = Pattern.compile("uri: \"(.*?)\"");
		BufferedReader reader = new BufferedReader(new StringReader(document));
		String line;
		URL defaultFixtureListUrl = null;
		while ((line = reader.readLine()) != null) {
			if (line.contains(identifyingText)) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find(0)) {
					MatchResult result = matcher.toMatchResult();
					defaultFixtureListUrl  = new URL(homePage, result.group(1));
				}
			}
		}
		return defaultFixtureListUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, URL> getFixtureListUrls(URL fixtureListUrl) throws IOException {
		Map<Integer, URL> fixtureListUrls = new TreeMap<Integer, URL>();
		Document fixtureTable = 
			getFixtureTableExtractorService().extractFixtureTable(getHtmlDocumentService().load(fixtureListUrl));
		Filter optionsFilter = new Filter() {
			@Override
			public boolean matches(Object obj) {
				return (obj instanceof Element) && "option".equalsIgnoreCase(((Element) obj).getName());
			}
		};
		Pattern yearPattern = Pattern.compile("(\\d+)/\\d+");
		for (Iterator<Element> iter = (Iterator<Element>) fixtureTable.getDescendants(optionsFilter); iter.hasNext(); ) {
			Element element = iter.next();
			Matcher matcher = yearPattern.matcher(element.getText());
			if (matcher.find(0)) {
				int year = Integer.parseInt(matcher.group(1));
				String url = element.getAttributeValue("value");
				fixtureListUrls.put(year, new URL(fixtureListUrl, url));
			}
		}
		return fixtureListUrls;
	}
	
	@Override
	public Map<Date, URL> getTicketInformationUrls(URL ticketInformationUrl)
			throws IOException {
		Map<Date, URL> urlsByGameDate = new TreeMap<Date, URL>();
		DateService dateService = getDateService();
		String[] fmts = new String[] { "dd MMMM yyyy - hh.mma", "dd MMMM yyyy - hha" };
		String[] yearlessFmt = new String[] { "dd MMMM - hh.mma", "dd MMMM - hha" };
		Date now = new Date();
		Pattern pattern = Pattern.compile(
				"href=\"(.+)\".*(?:Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)\\s+(.*?(?:am|pm|noon))");
		String document = getHtmlDocumentService().load(ticketInformationUrl);
		BufferedReader reader = new BufferedReader(new StringReader(document));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					String url = matcher.group(1);
					String dateString = matcher.group(2);
					// Remove the day number prefix
					dateString = dateString.replaceFirst("(\\d+)(st|nd|rd|th)", "$1");
					try {
						Date date = dateService.parsePossiblyYearlessDate(dateString, now, false, ticketInformationUrl, fmts, yearlessFmt);
						urlsByGameDate.put(date, new URL(ticketInformationUrl, url));
					}
					catch (UnparseableDateException e) {
						log.warn("Cannot parse date string " + dateString, e);
					}
				}
			}
			return urlsByGameDate;
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	public URL getHomePage() {
		return i_homePage;
	}

	public void setHomePage(URL homePage) {
		i_homePage = homePage;
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

	public DateService getDateService() {
		return i_dateService;
	}

	public void setDateService(DateService dateService) {
		i_dateService = dateService;
	}

}
