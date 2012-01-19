/**
 * Copyright 2010 Alex Jones
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
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JdomFixtureTableExtractorService implements FixtureTableExtractorService<Document> {

	@Override
	public Document extractFixtureTable(String content) throws IOException {
		Pattern pattern = Pattern.compile("<table[^>]+class=(?:\"fixtureList\"|'fixtureList').+?</table>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find(0)) {
			// Remove non-breaking spaces
			String matchedContent = matcher.group().replace("&nbsp;", "");
			// Remove image tags as they may be non-terminating
			matchedContent = matchedContent.replaceAll("<img.+?>", "").replaceAll("</img>", "");
			// Fix any non-escaped ampersands
			matchedContent = matchedContent.replaceAll("&\\s", "&amp; ");
			matchedContent = matchedContent.replaceAll("&$", "&amp;");
			try {
				return new SAXBuilder(false).build(new StringReader(matchedContent));
			}
			catch (JDOMException e) {
				throw new IOException(e);
			}
		}
		else {
			return null;
		}
	}

}
