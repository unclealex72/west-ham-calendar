/**
 * Copyright 2013 Alex Jones
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
package module

import com.typesafe.config.ConfigFactory
import com.tzavellas.sse.guice.ScalaModule
import uk.co.unclealex.hammers.calendar.dao.GameDao
import uk.co.unclealex.hammers.calendar.dao.SquerylGameDao
import uk.co.unclealex.hammers.calendar.dao.Transactional
import uk.co.unclealex.hammers.calendar.html.HtmlGamesScanner
import uk.co.unclealex.hammers.calendar.html.TicketsHtmlSingleGameScanner
import uk.co.unclealex.hammers.calendar.html.SeasonHtmlGamesScanner
import uk.co.unclealex.hammers.calendar.html.HtmlPageLoader
import uk.co.unclealex.hammers.calendar.html.HtmlPageLoaderImpl
import uk.co.unclealex.hammers.calendar.dates.DateServiceImpl
import uk.co.unclealex.hammers.calendar.dates.DateService
import uk.co.unclealex.hammers.calendar.html.MainPageServiceProvider
import uk.co.unclealex.hammers.calendar.html.MainPageService
import uk.co.unclealex.hammers.calendar.update.MainUpdateService
import uk.co.unclealex.hammers.calendar.update.MainUpdateServiceImpl
import java.net.URI
import uk.co.unclealex.hammers.calendar.update.TicketsHtmlGamesScannerFactory
import uk.co.unclealex.hammers.calendar.update.TicketsHtmlGamesScannerFactoryImpl
import uk.co.unclealex.hammers.calendar.cal.CalendarFactory
import uk.co.unclealex.hammers.calendar.cal.CalendarFactoryImpl
import uk.co.unclealex.hammers.calendar.cal.CalendarWriter
import uk.co.unclealex.hammers.calendar.cal.IcalCalendarWriter
import uk.co.unclealex.hammers.calendar.dates.NowService
import uk.co.unclealex.hammers.calendar.dates.SystemNowService
import com.google.inject.Provides
import services.GameRowFactory
import services.GameRowFactory
import services.GameRowFactory
import services.GameRowFactory
import services.GameRowFactoryImpl
import securesocial.core.Authorization
import controllers.Authorised
import controllers.Authorised
import uk.co.unclealex.hammers.calendar.html.LinkHarvester
import uk.co.unclealex.hammers.calendar.html.TicketsLinkHarvester

/**
 * @author alex
 *
 */
class CalendarModule extends ScalaModule {

  /**
   * The configuration object supplied with this application.
   */
  val config = ConfigFactory.load()

  override def configure {
    // Persistence
    bind[NowService].to[SystemNowService]
    bind[Transactional].to[SquerylGameDao]

    // Dates
    bind[DateService].to[DateServiceImpl]

    // Game harvesting and update services
    bind[TicketsHtmlGamesScannerFactory].to[TicketsHtmlGamesScannerFactoryImpl]
    bind[LinkHarvester].to[TicketsLinkHarvester]
    bind[HtmlGamesScanner].to[SeasonHtmlGamesScanner]
    bind[URI].annotatedWithName("mainPage").toInstance(new URI("http://www.whufc.com/page/Home/0,,12562,00.html"))
    bind[HtmlPageLoader].to[HtmlPageLoaderImpl]
    bind[MainPageService].toProvider(classOf[MainPageServiceProvider])
    bind[MainUpdateService].to[MainUpdateServiceImpl]
    
    // Calendars
    bind[CalendarFactory].to[CalendarFactoryImpl]
    bind[CalendarWriter].to[IcalCalendarWriter]

    //MVC
    bind[GameRowFactory].to[GameRowFactoryImpl]
    bind[String].annotatedWithName("secret").toInstance(config.getString("secret"))
    
    //Authorisation
    val validUsers = ((path: String) => if (config.hasPath(path)) config.getString(path) else "")("valid-users.users")
    bind[Authorization].toInstance(Authorised(validUsers.split(",").map(_.trim())));
  }

}