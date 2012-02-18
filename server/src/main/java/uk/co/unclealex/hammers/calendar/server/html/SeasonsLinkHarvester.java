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

import java.net.URI;

import org.cdmckay.coffeedom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Find all the links for every season's fixtures.
 * @author alex
 *
 */
public class SeasonsLinkHarvester extends ElementLinkHarvester {

	private static final Logger log = LoggerFactory.getLogger(SeasonsLinkHarvester.class);
	
	public SeasonsLinkHarvester() {
		super("option");
	}

	@Override
	protected URI checkForLink(URI uri, Element element) {
		String value = Strings.nullToEmpty(element.getAttributeValue("value"));
		String optionText = element.getTextNormalize();
		if (optionText.matches(".*[0-9]+/[0-9]+.*")) {
			URI linkUri = uri.resolve(value);
			log.info("Found link " + linkUri + " for " + optionText);
			return linkUri;
		}
		else {
			return null;
		}
	}
}
