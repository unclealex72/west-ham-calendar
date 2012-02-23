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
 * @author alex
 * 
 */
public class MainPageServiceImpl implements MainPageService {

	private static final Logger log = LoggerFactory.getLogger(MainPageServiceImpl.class);
	
	private final URI i_mainPageUri;
	private URI i_ticketsUri;
	private URI i_fixturesUri;

	private HtmlPageLoader i_htmlPageLoader;

	public MainPageServiceImpl(String mainPageUrl) throws URISyntaxException {
		super();
		i_mainPageUri = new URI(mainPageUrl);
	}

	@PostConstruct
	public void initialise() throws IOException {
		URL mainPageUrl = getMainPageUri().toURL();
		initialise(mainPageUrl);
	}

	public void initialise(URL mainPageUrl) throws IOException {
		TagNode mainPage = getHtmlPageLoader().loadPage(mainPageUrl);
		TagNodeFilter filter = new TagNodeFilter() {
			
			@Override
			public boolean apply(TagNode tagNode) {
				return "script".equals(tagNode.getName());
			}
		};
		boolean linksFound = false;
		for (Iterator<TagNode> iter = filter.list(mainPage).iterator(); !linksFound
				&& iter.hasNext();) {
			linksFound |= searchForLinks(iter.next());
		}
		if (!linksFound) {
			throw new IOException("Cannot find both the fixtures and tickets list from the main page " + getMainPageUri());
		}
	}

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
	
	abstract class ObjectSearcher {
		
		public boolean search(Object[] ids) {
			boolean found = false;
			for (int idx = 0; !found && idx < ids.length; idx++) {
				Object id = ids[idx];
				Object obj = (id instanceof Number)?get(((Number) id).intValue()):get(id.toString());
					found |= search(obj);
				}
			return found;
			}

		public abstract boolean search(Object obj);
		public abstract Object get(int id);
		public abstract Object get(String id);
	}

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
