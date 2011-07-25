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
package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import uk.co.unclealex.hammers.calendar.SpringTest;
import uk.co.unclealex.hammers.calendar.server.html.UrlExtractorService;
import uk.co.unclealex.hammers.calendar.server.html.UrlExtractorServiceImpl;

public class UrlExtractorServiceTest extends SpringTest {

	public void testGetDefaultFixtureListUrl() throws IOException {
		URL u = getUrlExtractorService().getDefaultFixtureListUrl(getTestResourceUrl());
		assertEquals(
				"The wrong default fixture list was returned", 
				makeUrl("/test.html"), 
				u);
	}

	public void testGetFixtureListUrls() throws IOException {
		Map<Integer, URL> actualUrlsByYear = getUrlExtractorService().getFixtureListUrls(getTestResourceUrl());
		Map<Integer, URL> expectedUrlsByYear = new HashMap<Integer, URL>();
		
		for (int year : new int[] { 2001, 2002 }) {
			expectedUrlsByYear.put(year, makeUrl(Integer.toString(year) + ".html"));
		}
		assertEquals(
				"The wrong urls for each season were returned", 
				expectedUrlsByYear, 
				actualUrlsByYear);
	}
	
	public UrlExtractorService getUrlExtractorService() {
		return autowire(new UrlExtractorServiceImpl());
	}
}
