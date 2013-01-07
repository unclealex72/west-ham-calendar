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

import org.hamcrest.Matchers;
import org.htmlcleaner.TagNode;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * The Class TicketsLinkHarvesterTest.
 * 
 * @author alex
 */
public class TicketsLinkHarvesterTest {

  @Test
  public void test2011() throws IOException, URISyntaxException {
    testTickets(
        2011,
        "http://www.whufc.com/articles/20110601/a-v-peterborough-united_2236896_2480754",
        "http://www.whufc.com/articles/20110601/h-v-southampton_2236896_2479769",
        "http://www.whufc.com/articles/20110601/a-v-blackpool_2236896_2480761",
        "http://www.whufc.com/articles/20110601/h-v-crystal-palace_2236896_2480447",
        "http://www.whufc.com/articles/20110601/h-v-watford_2236896_2479773",
        "http://www.whufc.com/articles/20110601/h-v-doncaster-rovers_2236896_2480528",
        "http://www.whufc.com/articles/20110601/h-v-middlesbrough_2236896_2479825",
        "http://www.whufc.com/articles/20110601/a-v-burnley_2236896_2480811");
  }

  @Test
  public void test2012() throws IOException, URISyntaxException {
    testTickets(
        2012,
        "http://www.whufc.com/articles/20120601/h-v-aston-villa_2236896_2836514",
        "http://www.whufc.com/articles/20120601/a-v-swansea-city_2236896_2840504",
        "http://www.whufc.com/articles/20120601/h-v-crewe-alexandra_2236896_2885073",
        "http://www.whufc.com/articles/20120601/h-v-fulham_2236896_2836518",
        "http://www.whufc.com/articles/20120601/a-v-norwich-city_2236896_2873919",
        "http://www.whufc.com/articles/20120601/h-v-sunderland_2236896_2862415",
        "http://www.whufc.com/articles/20120601/a-v-queens-park-rangers_2236896_2874349",
        "http://www.whufc.com/articles/20120601/h-v-arsenal_2236896_2862432",
        "http://www.whufc.com/articles/20120601/h-v-southampton_2236896_2867091",
        "http://www.whufc.com/articles/20120601/h-v-manchester-city_2236896_2867078",
        "http://www.whufc.com/articles/20120601/h-v-stoke-city_2236896_2867074");
  }

  /**
   * Test tickets.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  public void testTickets(int year, String... expectedLinks) throws IOException, URISyntaxException {
    TicketsLinkHarvester harvester = new TicketsLinkHarvester();
    URL url = getClass().getClassLoader().getResource(Joiner.on('/').join("html", "tickets", year, "tickets.html"));
    TagNode tagNode = new HtmlPageLoaderImpl().loadPage(url);
    Function<URI, String> f = new Function<URI, String>() {
      @Override
      public String apply(URI uri) {
        return uri.toString();
      }
    };
    Iterable<String> actualLinks =
        Iterables.transform(
            harvester.harvestLinks(new URI("http://www.whufc.com/page/TicketNews/0,,12562,00.html"), tagNode),
            f);
    Assert.assertThat("The wrong ticket links were returned.", actualLinks, Matchers.containsInAnyOrder(expectedLinks));
  }

}
