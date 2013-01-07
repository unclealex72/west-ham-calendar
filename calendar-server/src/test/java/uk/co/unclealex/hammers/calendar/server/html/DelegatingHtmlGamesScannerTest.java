/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.htmlcleaner.TagNode;
import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.dates.DateServiceImpl;
import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * The Class DelegatingHtmlGamesScannerTest.
 * 
 * @author alex
 */
public class DelegatingHtmlGamesScannerTest {

	/**
	 * Test.
	 * 
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws URISyntaxException, IOException {
		DelegatingHtmlGamesScanner delegatingHtmlGamesScanner = new DelegatingHtmlGamesScanner();
		delegatingHtmlGamesScanner.setHtmlPageLoader(new HtmlPageLoaderImpl());
		delegatingHtmlGamesScanner.setDateService(new DateServiceImpl());
		HtmlGamesScanner htmlGamesScanner = new HtmlGamesScanner() {
			int season = 2012;
			@Override
			public SortedSet<GameUpdateCommand> scan(URI uri) throws IOException {
				GameUpdateCommand gameUpdateCommand = GameUpdateCommand.matchReport(
						GameLocator.gameKeyLocator(new GameKey(Competition.FACP, Location.HOME, "Them", season++)), uri.toString());
				return Sets.newTreeSet(Collections.singleton(gameUpdateCommand));
			}
		};
		delegatingHtmlGamesScanner.setHtmlGamesScanner(htmlGamesScanner);
		LinkHarvester linkHarvester = new LinkHarvester() {

			@Override
			public List<URI> harvestLinks(URI pageUri, TagNode tagNode) throws IOException {
				final List<URI> links = Lists.newArrayList();
				new TagNodeWalker(tagNode) {
					@Override
					public void execute(TagNode tagNode) {
						String href = tagNode.getAttributeByName("href");
						if (href != null) {
							links.add(URI.create(href));
						}
					}
				};
				return links;
			}
		};
		delegatingHtmlGamesScanner.setLinkHarvester(linkHarvester);
		URI uri = getClass().getClassLoader().getResource("delegate.xml").toURI();
		SortedSet<GameUpdateCommand> actualGameUpdateCommands = delegatingHtmlGamesScanner.scan(uri);
		Function<String, GameUpdateCommand> expectedGameUpdateCommandFunction = new Function<String, GameUpdateCommand>() {
			int season = 2012;
			@Override
			public GameUpdateCommand apply(String uri) {
				return GameUpdateCommand.matchReport(
						GameLocator.gameKeyLocator(new GameKey(Competition.FACP, Location.HOME, "Them", season++)), uri);
			}
		};
		Iterable<GameUpdateCommand> expectedGameUpdateCommands = Iterables.transform(
				Arrays.asList("1.html", "2.html", "3.html", "4.html"), expectedGameUpdateCommandFunction);
		Assert.assertArrayEquals(Iterables.toArray(expectedGameUpdateCommands, GameUpdateCommand.class),
				Iterables.toArray(actualGameUpdateCommands, GameUpdateCommand.class));
	}
}
