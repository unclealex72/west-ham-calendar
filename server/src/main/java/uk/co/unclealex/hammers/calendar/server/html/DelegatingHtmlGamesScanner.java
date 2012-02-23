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
import java.util.SortedSet;

import org.htmlcleaner.TagNode;

import com.google.common.collect.Sets;

/**
 * A base class for {@link HtmlGamesScanner}s that read a page and then delegates scanning to a set of other pages
 * that are links off of the original page.
 * @author alex
 *
 */
public class DelegatingHtmlGamesScanner extends TagNodeBasedHtmlGamesScanner {

	private LinkHarvester i_linkHarvester;
	private HtmlGamesScanner i_htmlGamesScanner;
	
	@Override SortedSet<GameUpdateCommand> scan(URI uri, TagNode tagNode) throws IOException {
		SortedSet<GameUpdateCommand> gameUpdateCommands = Sets.newTreeSet();
		List<URI> links = getLinkHarvester().harvestLinks(uri, tagNode);
		for (URI link : links) {
			gameUpdateCommands.addAll(getHtmlGamesScanner().scan(link));
		}
		return gameUpdateCommands;
	}

	public LinkHarvester getLinkHarvester() {
		return i_linkHarvester;
	}

	public void setLinkHarvester(LinkHarvester linkHarvester) {
		i_linkHarvester = linkHarvester;
	}

	public HtmlGamesScanner getHtmlGamesScanner() {
		return i_htmlGamesScanner;
	}

	public void setHtmlGamesScanner(HtmlGamesScanner htmlGamesScanner) {
		i_htmlGamesScanner = htmlGamesScanner;
	}
}
