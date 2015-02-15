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

import java.net.URI

import com.typesafe.config.ConfigFactory
import com.tzavellas.sse.guice.ScalaModule
import controllers.Authorised
import json.ConfigurationReader
import pdf.{PdfBoxPriorityPointsPdfFactory, PdfPositioning, PriorityPointsConfiguration, PriorityPointsPdfFactory}
import securesocial.core.Authorization
import services.{GameRowFactory, GameRowFactoryImpl}
import uk.co.unclealex.hammers.calendar.cal.{CalendarFactory, CalendarFactoryImpl, CalendarWriter, IcalCalendarWriter}
import uk.co.unclealex.hammers.calendar.dao.{SquerylPriorityPointsConfigurationDao, PriorityPointsConfigurationDao, SquerylGameDao, Transactional}
import uk.co.unclealex.hammers.calendar.dates.{DateService, DateServiceImpl, NowService, SystemNowService}
import uk.co.unclealex.hammers.calendar.html.{HtmlGamesScanner, HtmlPageLoader, HtmlPageLoaderImpl, LinkHarvester, MainPageService, MainPageServiceProvider, SeasonHtmlGamesScanner, TicketsLinkHarvester}
import uk.co.unclealex.hammers.calendar.update.{LastUpdated, MainUpdateService, MainUpdateServiceImpl, PlayCacheLastUpdated, TicketsHtmlGamesScannerFactory, TicketsHtmlGamesScannerFactoryImpl}

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
    bind[LastUpdated].to[PlayCacheLastUpdated]
    
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
    bind[Authorization].toInstance(Authorised(validUsers.split(",").map(_.trim())))

    // PDF
    bind[PriorityPointsPdfFactory].to[PdfBoxPriorityPointsPdfFactory]
    bind[PdfPositioning].toInstance(ConfigurationReader[PdfPositioning]("pdf-positioning.json"))
    bind[PriorityPointsConfigurationDao].to[SquerylPriorityPointsConfigurationDao]
  }

}