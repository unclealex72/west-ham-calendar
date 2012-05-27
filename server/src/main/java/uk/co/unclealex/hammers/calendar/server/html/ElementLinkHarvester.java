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
import java.util.Set;

import org.htmlcleaner.TagNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * An {@link ElementLinkHarvester} is a {@link LinkHarvester} that searches a page for HTML elements with
 * a given name and then processes it's inner text.
 * @author alex
 * 
 */
public abstract class ElementLinkHarvester implements LinkHarvester {

	/**
	 * The name of the element to look for.
	 */
	private final String i_elementName;

	/**
	 * Instantiates a new element link harvester.
	 * 
	 * @param elementName
	 *          the element name
	 */
	public ElementLinkHarvester(String elementName) {
		super();
		i_elementName = elementName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<URI> harvestLinks(final URI pageUri, TagNode tagNode) throws IOException {
		final Set<URI> links = Sets.newLinkedHashSet();
		final String elementName = getElementName();
		new TagNodeWalker(tagNode) {
			
			@Override
			public void execute(TagNode tagNode) {
				if (elementName.equals(tagNode.getName())) {
					URI linkUri = checkForLink(pageUri, tagNode);
					if (linkUri != null) {
						links.add(linkUri);
					}
				}
			}
		};
		return Lists.newArrayList(links);
	}

	/**
	 * Once a suitable element has been found check to see if a link can be found.
	 * @param uri The URI of the page being harvested.
	 * @param tagNode The {@link TagNode} that had the correct element name.
	 * @return A new child link, or null if none was found.
	 */
	protected abstract URI checkForLink(URI uri, TagNode tagNode);

	/**
	 * Gets the name of the element to look for.
	 * 
	 * @return the name of the element to look for
	 */
	public String getElementName() {
		return i_elementName;
	}

}
