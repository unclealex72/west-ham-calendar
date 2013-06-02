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

package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException
import java.net.URI

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

import org.specs2.mutable.Specification

/**
 * The Class TicketsLinkHarvesterTest.
 *
 * @author alex
 */
class TicketsLinkHarvesterTest extends Specification {

  "Harvesting the tickets links in 2011" should {
    2011 expects (
      "http://www.whufc.com/articles/20110601/a-v-peterborough-united_2236896_2480754",
      "http://www.whufc.com/articles/20110601/h-v-southampton_2236896_2479769",
      "http://www.whufc.com/articles/20110601/a-v-blackpool_2236896_2480761",
      "http://www.whufc.com/articles/20110601/h-v-crystal-palace_2236896_2480447",
      "http://www.whufc.com/articles/20110601/h-v-watford_2236896_2479773",
      "http://www.whufc.com/articles/20110601/h-v-doncaster-rovers_2236896_2480528",
      "http://www.whufc.com/articles/20110601/h-v-middlesbrough_2236896_2479825",
      "http://www.whufc.com/articles/20110601/a-v-burnley_2236896_2480811")
  }

  "Harvesting the tickets links in 2012" should {
    2012 expects (
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
      "http://www.whufc.com/articles/20120601/h-v-stoke-city_2236896_2867074")
  }

  /**
   * The actual testing code.
   */
  implicit class SeasonImplicits(season: Int) {
    def expects(expectedLinks: String*) = {
      val harvester = new TicketsLinkHarvester();
      val url = getClass.getClassLoader.getResource(s"html/tickets/$season/tickets.html")
      val tagNode = new HtmlPageLoaderImpl().loadPage(url);
      val actualLinks =
        harvester.harvestLinks(new URI("http://www.whufc.com/page/TicketNews/0,,12562,00.html"), tagNode).asScala.toList
      s"contain the correct links" in {
        actualLinks must be equalTo (List(expectedLinks: _*) map ((uri: String) => new URI(uri)))
      }
    }
  }

}
