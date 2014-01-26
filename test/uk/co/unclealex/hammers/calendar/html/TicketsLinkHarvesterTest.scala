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

  "Harvesting the tickets links in 2013" should {
    2013 expects (
      "http://www.whufc.com/articles/20130601/v-chelsea-a_2236896_3215399",
      "http://www.whufc.com/articles/20130601/v-swansea-city-h_2236896_3215417",
      "http://www.whufc.com/articles/20130601/v-aston-villa-a_2236896_3215425",
      "http://www.whufc.com/articles/20130601/v-norwich-city-h_2236896_3215426",
      "http://www.whufc.com/articles/20130601/v-southampton-h_2236896_3215430",
      "http://www.whufc.com/articles/20130601/v-everton-a_2236896_3215433",
      "http://www.whufc.com/articles/20130601/v-hull-city-h_2236896_3215437",
      "http://www.whufc.com/articles/20130601/v-manchester-united-h_2236896_3215448",
      "http://www.whufc.com/articles/20130601/v-liverpool-h_2236896_3215451",
      "http://www.whufc.com/articles/20130601/v-tottenham-hotspur-h_2236896_3215475")
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
        harvester.harvestLinks(new URI("http://www.whufc.com/page/TicketNews/0,,12562,00.html"), tagNode)
      s"contain the correct links" in {
        actualLinks must be equalTo (List(expectedLinks: _*) map ((uri: String) => new URI(uri)))
      }
    }
  }

}
