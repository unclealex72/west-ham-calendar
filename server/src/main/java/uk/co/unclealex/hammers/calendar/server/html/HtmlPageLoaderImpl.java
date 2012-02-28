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
import java.net.URL;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * The default implementation of {@link HtmlPageLoader} that cleans a page using
 * HtmlCleaner.
 * 
 * @author alex
 * 
 */
public class HtmlPageLoaderImpl implements HtmlPageLoader {

	/**
	 * The {@link HtmlCleaner} used to create a clean HTML page.
	 */
	private final HtmlCleaner i_htmlCleaner = new HtmlCleaner();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagNode loadPage(URL url) throws IOException {
		HtmlCleaner htmlCleaner = getHtmlCleaner();
		return htmlCleaner.clean(url);
	}

	public HtmlCleaner getHtmlCleaner() {
		return i_htmlCleaner;
	}

}
