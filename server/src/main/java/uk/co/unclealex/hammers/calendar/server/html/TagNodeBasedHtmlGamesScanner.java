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
 */

package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException;
import java.net.URI;
import java.util.SortedSet;

import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.dates.DateService;

/**
 * A base class for {@link HtmlGamesScanner}s that first parse a URL into an XML
 * document.
 * 
 * @author alex
 * 
 */
public abstract class TagNodeBasedHtmlGamesScanner implements HtmlGamesScanner {

	private static final Logger log = LoggerFactory.getLogger(TagNodeBasedHtmlGamesScanner.class);

	/**
	 * The {@link HtmlPageLoader} used to load web pages.
	 */
	private HtmlPageLoader i_htmlPageLoader;
	
	/**
	 * The {@link DateService} to use for date and time manipulation.
	 */
	private DateService i_dateService;

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<GameUpdateCommand> scan(URI uri) throws IOException {
		log.info("Scanning URI " + uri);
		TagNode tagNode = getHtmlPageLoader().loadPage(uri.toURL());
		return scan(uri, tagNode);
	}

	/**
	 * Scan a document for game updates.
	 * 
	 * @param uri
	 *          The URI of the page being scanned.
	 * @param tagNode
	 *          The document that is being scanned.
	 * @return A set of {@link GameUpdateCommand}s that need to be made.
	 * @throws IOException
	 *           If there are any network problems.
	 */
	abstract SortedSet<GameUpdateCommand> scan(URI uri, TagNode tagNode) throws IOException;

	public HtmlPageLoader getHtmlPageLoader() {
		return i_htmlPageLoader;
	}

	public void setHtmlPageLoader(HtmlPageLoader htmlPageLoader) {
		i_htmlPageLoader = htmlPageLoader;
	}

	public DateService getDateService() {
		return i_dateService;
	}

	public void setDateService(DateService dateService) {
		i_dateService = dateService;
	}

}
