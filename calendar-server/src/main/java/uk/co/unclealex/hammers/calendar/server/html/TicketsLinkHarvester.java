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

import java.net.URI;

import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;


/**
 * A link harvester that harvests links for ticket selling dates.
 * @author alex
 *
 */
public class TicketsLinkHarvester extends ElementLinkHarvester {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(TicketsLinkHarvester.class);
	
	
	/**
	 * Instantiates a new tickets link harvester.
	 */
	public TicketsLinkHarvester() {
		super("a");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected URI checkForLink(URI uri, TagNode tagNode) {
		String href = Strings.nullToEmpty(tagNode.getAttributeByName("href"));
		if (href.matches("/articles/[0-9]+/(h|a)-v-.+")) {
			URI linkUri = uri.resolve(href);
			log.info("Found tickets link " + linkUri);
			return linkUri;
		}
		else {
			return null;
		}
	}
}
