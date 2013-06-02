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
import java.net.URL;

import javax.inject.Provider;

import junit.framework.Assert;

import org.junit.Test;

/**
 * The Class MainPageServiceImplTest.
 * 
 * @author alex
 */
public class MainPageServiceImplTest {

  /**
   * Test.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  @Test
  public void test() throws IOException, URISyntaxException {
    final URL url = getClass().getClassLoader().getResource("html/home.html");
    final Provider<MainPageService> provider = new MainPageServiceProvider(url.toURI(), new HtmlPageLoaderImpl());
    final MainPageService mainPageService = provider.get();
    Assert.assertEquals(
        "The wrong tickets uri was found.",
        new URI("file:/page/TicketNews/0,,12562,00.html"),
        mainPageService.ticketsUri());
    Assert.assertEquals(
        "The wrong fixtures uri was found.",
        new URI("file:/page/FixturesResults/0,,12562,00.html"),
        mainPageService.fixturesUri());
  }

}
