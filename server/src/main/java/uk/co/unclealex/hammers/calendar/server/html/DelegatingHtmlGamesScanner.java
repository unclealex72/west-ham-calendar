/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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
import java.util.List;
import java.util.SortedSet;

import org.htmlcleaner.TagNode;

import com.google.common.collect.Sets;


/**
 * A base class for {@link HtmlGamesScanner}s that read a page and then
 * delegates scanning to a set of other pages that are links off of the original
 * page.
 * 
 * @author alex
 * 
 */
public class DelegatingHtmlGamesScanner extends TagNodeBasedHtmlGamesScanner {

	/**
	 * The {@link LinkHarvester} used to find the links on a main page.
	 */
	private LinkHarvester i_linkHarvester;

	/**
	 * The {@link HtmlGamesScanner} used to find games on the child pages.
	 */
	private HtmlGamesScanner i_htmlGamesScanner;

	/**
	 * {@inheritDoc}
	 */
	@Override
	SortedSet<GameUpdateCommand> scan(URI uri, TagNode tagNode) throws IOException {
		SortedSet<GameUpdateCommand> gameUpdateCommands = Sets.newTreeSet();
		List<URI> links = getLinkHarvester().harvestLinks(uri, tagNode);
		for (URI link : links) {
			gameUpdateCommands.addAll(getHtmlGamesScanner().scan(link));
		}
		return gameUpdateCommands;
	}

	/**
	 * Gets the {@link LinkHarvester} used to find the links on a main page.
	 * 
	 * @return the {@link LinkHarvester} used to find the links on a main page
	 */
	public LinkHarvester getLinkHarvester() {
		return i_linkHarvester;
	}

	/**
	 * Sets the {@link LinkHarvester} used to find the links on a main page.
	 * 
	 * @param linkHarvester
	 *          the new {@link LinkHarvester} used to find the links on a main
	 *          page
	 */
	public void setLinkHarvester(LinkHarvester linkHarvester) {
		i_linkHarvester = linkHarvester;
	}

	/**
	 * Gets the {@link HtmlGamesScanner} used to find games on the child pages.
	 * 
	 * @return the {@link HtmlGamesScanner} used to find games on the child pages
	 */
	public HtmlGamesScanner getHtmlGamesScanner() {
		return i_htmlGamesScanner;
	}

	/**
	 * Sets the {@link HtmlGamesScanner} used to find games on the child pages.
	 * 
	 * @param htmlGamesScanner
	 *          the new {@link HtmlGamesScanner} used to find games on the child
	 *          pages
	 */
	public void setHtmlGamesScanner(HtmlGamesScanner htmlGamesScanner) {
		i_htmlGamesScanner = htmlGamesScanner;
	}
}
