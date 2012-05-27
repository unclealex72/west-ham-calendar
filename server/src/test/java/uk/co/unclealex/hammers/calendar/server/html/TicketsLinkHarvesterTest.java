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
import java.net.URISyntaxException;
import java.net.URL;

import org.htmlcleaner.TagNode;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;


/**
 * The Class TicketsLinkHarvesterTest.
 * 
 * @author alex
 */
public class TicketsLinkHarvesterTest {

	/**
	 * Test tickets.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void testTickets() throws IOException, URISyntaxException {
		TicketsLinkHarvester harvester = new TicketsLinkHarvester();
		URL url = getClass().getClassLoader().getResource("html/tickets.html");
		TagNode tagNode = new HtmlPageLoaderImpl().loadPage(url);
		URI[] actualLinks = Iterables.toArray(harvester.harvestLinks(new URI("http://www.whufc.com/page/TicketNews/0,,12562,00.html"), tagNode), URI.class);
		URI[] expectedLinks = new URI[] { new URI("http://www.whufc.com/articles/20110601/a-v-peterborough-united_2236896_2480754"),
				new URI("http://www.whufc.com/articles/20110601/h-v-southampton_2236896_2479769"),
				new URI("http://www.whufc.com/articles/20110601/a-v-blackpool_2236896_2480761"),
				new URI("http://www.whufc.com/articles/20110601/h-v-crystal-palace_2236896_2480447"),
				new URI("http://www.whufc.com/articles/20110601/h-v-watford_2236896_2479773"),
				new URI("http://www.whufc.com/articles/20110601/h-v-doncaster-rovers_2236896_2480528"),
				new URI("http://www.whufc.com/articles/20110601/h-v-middlesbrough_2236896_2479825"),
				new URI("http://www.whufc.com/articles/20110601/a-v-burnley_2236896_2480811") };
		Assert.assertArrayEquals("The wrong ticket links were returned.", expectedLinks, actualLinks);
	}

}
