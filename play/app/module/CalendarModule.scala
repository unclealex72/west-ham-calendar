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
  * http://www.apache.org/licenses/LICENSE-2.0
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

import java.lang.Boolean
import java.net.URI

import cal.{CalendarFactory, CalendarFactoryImpl, CalendarWriter, IcalCalendarWriter}
import com.typesafe.config.ConfigFactory
import controllers.SecretToken
import dao._
import dates.geo.{GeoLocationFactory, GeoLocationFactoryImpl}
import dates.{DateService, DateServiceImpl, NowService, SystemNowService}
import filters.{Filters, SSLFilter}
import location._
import logging.{Fatal, FatalImpl}
import pdf.{PdfBoxPriorityPointsPdfFactory, PdfPositioning, PriorityPointsPdfFactory}
import play.api.cache.CacheApi
import play.api.http.HttpFilters
import scaldi.Module
import security.{RequireSSL, Authorised}
import security.Definitions.Auth
import security.models.daos.{CredentialsStorage, PlayCacheCredentialsStorage}
import services.{GameRowFactory, GameRowFactoryImpl}
import sms.{SmsConfiguration, ClickatellSmsService, ClickatellData, SmsService}
import update._
import update.fixtures.{FixturesGameScanner, FixturesGameScannerImpl}
import update.tickets.{TicketsGameScanner, TicketsGameScannerImpl}
import net.ceedubs.ficus.Ficus._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import scala.collection.JavaConversions._
/**
  * @author alex
  *
  */
class CalendarModule extends Module {

  /**
    * The configuration object supplied with this application.
    */
  val config = ConfigFactory.load

  // Persistence
  bind[DatabaseConfigFactory] toNonLazy injected[PlayDatabaseConfigFactory]
  bind[NowService] toNonLazy new SystemNowService()
  bind[GameDao] toNonLazy injected[SlickGameDao]

  bind[GeoLocationFactory] toNonLazy injected[GeoLocationFactoryImpl]
  // Dates
  bind[DateService] toNonLazy injected[DateServiceImpl]
  bind[LastUpdated] toNonLazy injected[PlayCacheLastUpdated]

  // Game harvesting and update services
  bind[URI] toNonLazy new URI("http://www.whufc.com")
  bind[FixturesGameScanner] toNonLazy injected[FixturesGameScannerImpl]
  bind[TicketsGameScanner] toNonLazy injected[TicketsGameScannerImpl]
  bind[MainUpdateService] toNonLazy injected[MainUpdateServiceImpl]

  // Calendars
  bind[CalendarFactory] toNonLazy injected[CalendarFactoryImpl]
  bind[CalendarWriter] toNonLazy injected[IcalCalendarWriter]

  // Game Locations
  bind[LocationService] toNonLazy injected[LocationServiceImpl]
  bind[LocationClientKey] toNonLazy LocationClientKey("AIzaSyCnaYyFjEYYaKIQ6ZQ64Tx-xkKP2kArRzE")

  //MVC
  bind[GameRowFactory] toNonLazy injected[GameRowFactoryImpl]
  bind[SecretToken] toNonLazy SecretToken(config.getString("secret"))

  // PDF
  bind[PriorityPointsPdfFactory] toNonLazy injected[PdfBoxPriorityPointsPdfFactory]
  bind[PdfPositioning] toNonLazy config.as[PdfPositioning]("pdf")
  bind[PriorityPointsConfigurationDao] toNonLazy injected[SlickPriorityPointsConfigurationDao]

  // Logging
  bind[Fatal] toNonLazy injected[FatalImpl]
  bind[FatalErrorDao] toNonLazy injected[SlickFatalErrorDao]
  bind[SmsService] toNonLazy injected[ClickatellSmsService]
  bind[SmsConfiguration] toNonLazy config.as[SmsConfiguration]("sms")

  // filters.Filters
  bind[HttpFilters] toNonLazy injected[Filters]
  bind[SSLFilter] toNonLazy injected[SSLFilter]

  // Security
  bind[Auth] toNonLazy {
    Authorised(config.getString("valid-users.users").split(",").map(_.trim()))
  }
  bind[RequireSSL] toNonLazy {
    RequireSSL(config.getBooleanList("require-ssl").headOption.contains(true))
  }

  bind[CredentialsStorage] toNonLazy new PlayCacheCredentialsStorage(inject[CacheApi], 15.minutes)(inject[ExecutionContext])

}