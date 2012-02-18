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
import java.util.Set;

import org.cdmckay.coffeedom.Content;
import org.cdmckay.coffeedom.Document;
import org.cdmckay.coffeedom.Element;
import org.cdmckay.coffeedom.filter.Filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author alex
 *
 */
public abstract class ElementLinkHarvester implements LinkHarvester {

	private final String i_elementName;
	
	public ElementLinkHarvester(String elementName) {
		super();
		i_elementName = elementName;
	}

	@Override
	public List<URI> harvestLinks(final URI pageUri, Document document) throws IOException {
		final Set<URI> links = Sets.newLinkedHashSet();
		final String elementName = getElementName();
		Filter filter = new Filter() {
			@Override
			public boolean matches(Object object) {
				if (object instanceof Element && elementName.equals(((Element) object).getName())) {
					URI linkUri = checkForLink(pageUri, (Element) object);
					if (linkUri != null) {
						links.add(linkUri);
					}
				}
				return false;
			}
		};
		for (@SuppressWarnings("unused") Content content : document.getDescendants(filter)) {
			// Do nothing
		}
		return Lists.newArrayList(links);
	}

	protected abstract URI checkForLink(URI uri, Element element);
	
	public String getElementName() {
		return i_elementName;
	}

}
