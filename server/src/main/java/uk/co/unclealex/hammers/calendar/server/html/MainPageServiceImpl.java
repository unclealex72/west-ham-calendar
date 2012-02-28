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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.htmlcleaner.TagNode;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link MainPageService}.
 * 
 * @author alex
 * 
 */
public class MainPageServiceImpl implements MainPageService {

	private static final Logger log = LoggerFactory.getLogger(MainPageServiceImpl.class);

	/**
	 * The URI for the main web page.
	 */
	private final URI i_mainPageUri;

	/**
	 * The URI for the tickets web page.
	 */
	private URI i_ticketsUri;

	/**
	 * The URI for the fixtures web page.
	 */
	private URI i_fixturesUri;

	/**
	 * The {@link HtmlPageLoader} used to load the main page.
	 */
	private HtmlPageLoader i_htmlPageLoader;

	public MainPageServiceImpl(String mainPageUrl) throws URISyntaxException {
		super();
		i_mainPageUri = new URI(mainPageUrl);
	}

	/**
	 * Initialise this service by finding the tickets and fixtures links.
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void initialise() throws IOException {
		URL mainPageUrl = getMainPageUri().toURL();
		initialise(mainPageUrl);
	}

	/**
	 * Initialise this service by finding the tickets and fixtures links.
	 * 
	 * @param The
	 *          URL of the main page.
	 * @throws IOException
	 */
	public void initialise(URL mainPageUrl) throws IOException {
		TagNode mainPage = getHtmlPageLoader().loadPage(mainPageUrl);
		TagNodeFilter filter = new TagNodeFilter() {

			@Override
			public boolean apply(TagNode tagNode) {
				return "script".equals(tagNode.getName());
			}
		};
		boolean linksFound = false;
		for (Iterator<TagNode> iter = filter.list(mainPage).iterator(); !linksFound && iter.hasNext();) {
			linksFound |= searchForLinks(iter.next());
		}
		if (!linksFound) {
			throw new IOException("Cannot find both the fixtures and tickets list from the main page " + getMainPageUri());
		}
	}

	/**
	 * Search for and populate the ticket and fixtures link within a javascript
	 * script element.
	 * 
	 * @param scriptNode
	 *          The {@link TagNode} of the javascript element.
	 * @return True if the links were found and populated, false otherwise.
	 */
	protected boolean searchForLinks(TagNode scriptNode) {
		Context cx = Context.enter();
		try {
			final Scriptable scope = cx.initStandardObjects();
			String script = TagNodeUtils.textOf(scriptNode).toString().replace('\n', ' ');
			try {
				cx.evaluateString(scope, script, "<cmd>", 1, null);
			}
			catch (Throwable t) {
				// Ignore.
			}
			ObjectSearcher searcher = new ObjectSearcher() {
				@Override
				public boolean search(Object obj) {
					if (obj instanceof NativeArray) {
						return searchForLinks((NativeArray) obj, scope);
					}
					else {
						return false;
					}
				}

				@Override
				public Object get(String id) {
					return scope.get(id, scope);
				}

				@Override
				public Object get(int id) {
					return scope.get(id, scope);
				}
			};
			return searcher.search(scope.getIds());
		}
		finally {
			Context.exit();
		}
	}

	/**
	 * An {@link ObjectSearcher} is an abstract class that can be used to search
	 * for links in a javascript array.
	 * 
	 * @author alex
	 * 
	 */
	abstract class ObjectSearcher {

		/**
		 * Search a javascript variable context for links.
		 * 
		 * @param ids
		 *          The ids of each javascript variable.
		 * @return True if a link is found, false otherwise.
		 */
		public boolean search(Object[] ids) {
			boolean found = false;
			for (int idx = 0; !found && idx < ids.length; idx++) {
				Object id = ids[idx];
				Object obj = (id instanceof Number) ? get(((Number) id).intValue()) : get(id.toString());
				found |= search(obj);
			}
			return found;
		}

		/**
		 * Search an instance of a javascript variable.
		 * 
		 * @param obj
		 *          The javascript variable to search.
		 * @return True if whatever was being searched for was found, false
		 *         otherwise.
		 */
		public abstract boolean search(Object obj);

		/**
		 * Get a javascript sub-variable by its interger id.
		 * 
		 * @param id
		 *          The id of the sub-variable.
		 * @return The value of the sub-variable.
		 */
		public abstract Object get(int id);

		/**
		 * Get a javascript sub-variable by its string id.
		 * 
		 * @param id
		 *          The id of the sub-variable.
		 * @return The value of the sub-variable.
		 */
		public abstract Object get(String id);
	}

	/**
	 * Search for links in a javascript array.
	 * @param array The array to search.
	 * @param scope The original scope.
	 * @return True if the links are found, false otherwise.
	 */
	protected boolean searchForLinks(final NativeArray array, final Scriptable scope) {
		ObjectSearcher searcher = new ObjectSearcher() {
			@Override
			public boolean search(Object obj) {
				if (obj instanceof NativeArray) {
					return searchForLinks((NativeArray) obj, scope);
				}
				else if (obj instanceof Scriptable) {
					Scriptable s = (Scriptable) obj;
					if (s.has("name", scope) && s.has("uri", scope)) {
						URI uri = getMainPageUri().resolve(s.get("uri", scope).toString());
						Object name = s.get("name", scope);
						if ("Fixtures &amp; Results".equals(name)) {
							log.info("Found fixtures link: " + uri);
							setFixturesUri(uri);
						}
						else if ("Ticket News".equals(name)) {
							log.info("Found tickets link: " + uri);
							setTicketsUri(uri);
						}
					}
				}
				return getFixturesUri() != null && getTicketsUri() != null;
			}

			@Override
			public Object get(String id) {
				return array.get(id);
			}

			@Override
			public Object get(int id) {
				return array.get(id);
			}
		};
		return searcher.search(array.getIds());
	}

	public URI getTicketsUri() {
		return i_ticketsUri;
	}

	public void setTicketsUri(URI ticketsUri) {
		i_ticketsUri = ticketsUri;
	}

	public URI getFixturesUri() {
		return i_fixturesUri;
	}

	public void setFixturesUri(URI fixturesUri) {
		i_fixturesUri = fixturesUri;
	}

	public HtmlPageLoader getHtmlPageLoader() {
		return i_htmlPageLoader;
	}

	public void setHtmlPageLoader(HtmlPageLoader htmlPageLoader) {
		i_htmlPageLoader = htmlPageLoader;
	}

	public URI getMainPageUri() {
		return i_mainPageUri;
	}

}
