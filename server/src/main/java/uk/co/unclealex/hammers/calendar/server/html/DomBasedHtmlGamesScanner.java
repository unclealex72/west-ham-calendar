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
import java.util.SortedSet;

import org.cdmckay.coffeedom.CoffeeDOMException;
import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.server.dates.DateService;

import com.google.common.base.Strings;

/**
 * A base class for {@link HtmlGamesScanner}s that first parse a URL into an XML
 * document.
 * 
 * @author alex
 * 
 */
public abstract class DomBasedHtmlGamesScanner implements HtmlGamesScanner {

	private static final Logger log = LoggerFactory.getLogger(DomBasedHtmlGamesScanner.class);
	
	private HtmlPageLoader i_htmlPageLoader;
	private DateService i_dateService;

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<GameUpdateCommand> scan(URI uri) throws IOException {
		try {
			log.info("Scanning URI " + uri);
			Document document = getHtmlPageLoader().loadPage(uri.toURL());
			return scan(uri, document);
		}
		catch (CoffeeDOMException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Scan a document for game updates.
	 * 
	 * @param uri
	 *          The URI of the page being scanned.
	 * @param document
	 *          The document that is being scanned.
	 * @return A set of {@link GameUpdateCommand}s that need to be made.
	 * @throws IOException
	 *           If there are any network problems.
	 */
	protected abstract SortedSet<GameUpdateCommand> scan(URI uri, Document document) throws IOException;

	/**
	 * Normalise all whitespace (including non-breaking) and trim.
	 * 
	 * @param el
	 *          The element whose text should be normalised.
	 * @return The normalised text of the element.
	 */
	protected String normaliseText(Element el) {
		return el.getTextTrim().replace('\u00a0', ' ').replace("\\s+", " ").trim();
	}

	/**
	 * Normalise all whitespace (including non-breaking) and trim.
	 * 
	 * @param el
	 *          The element whose text should be normalised.
	 * @return The normalised text of the element or null if the normalised text
	 *         is empty.
	 */
	protected String normaliseTextToNull(Element el) {
		return Strings.emptyToNull(normaliseText(el));
	}

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
