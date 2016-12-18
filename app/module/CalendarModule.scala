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

import java.awt.Dimension
import java.net.URI

import cal.{CalendarFactory, CalendarFactoryImpl, CalendarWriter, IcalCalendarWriter}
import com.google.inject.{AbstractModule, Provides}
import controllers.SecretToken
import dao._
import dates.geo.{GeoLocationFactory, GeoLocationFactoryImpl}
import dates.{DateService, DateServiceImpl, NowService, SystemNowService}
import filters.{Filters, SSLFilter}
import location._
import logging.{Fatal, FatalImpl}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import pdf.{PdfBoxPriorityPointsPdfFactory, PdfPositioning, PriorityPointsPdfFactory}
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.http.HttpFilters
import security.models.daos.{CredentialsStorage, PlayCacheCredentialsStorage}
import security.{Authorised, RequireSSL}
import services.{GameRowFactory, GameRowFactoryImpl}
import sms.{ClickatellSmsService, SmsConfiguration, SmsService}
import sprites._
import update._
import update.fixtures.{FixturesGameScanner, FixturesGameScannerImpl}
import update.tickets.{TicketsGameScanner, TicketsGameScannerImpl}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
/**
  * @author alex
  *
  */
class CalendarModule() extends AbstractModule with ScalaModule {

  def configure() {

    // Persistence
    bind[DatabaseConfigFactory].to[PlayDatabaseConfigFactory]
    bind[NowService].toInstance(new SystemNowService())
    bind[GameDao].to[SlickGameDao]

    bind[GeoLocationFactory].to[GeoLocationFactoryImpl]
    // Dates
    bind[DateService].to[DateServiceImpl]
    bind[LastUpdated].to[PlayCacheLastUpdated]

    // Game harvesting and update services
    bind[URI].toInstance(new URI("http://www.whufc.com"))
    bind[FixturesGameScanner].to[FixturesGameScannerImpl]
    bind[TicketsGameScanner].to[TicketsGameScannerImpl]
    bind[MainUpdateService].to[MainUpdateServiceImpl]

    // Calendars
    bind[CalendarFactory].to[CalendarFactoryImpl]
    bind[CalendarWriter].to[IcalCalendarWriter]

    // Game Locations
    bind[LocationService].to[LocationServiceImpl]
    bind[LocationClientKey].toInstance(LocationClientKey("AIzaSyCnaYyFjEYYaKIQ6ZQ64Tx-xkKP2kArRzE"))

    //MVC
    bind[GameRowFactory].to[GameRowFactoryImpl]

    // PDF
    bind[PriorityPointsPdfFactory].to[PdfBoxPriorityPointsPdfFactory]
    bind[PriorityPointsConfigurationDao].to[SlickPriorityPointsConfigurationDao]

    // Logging
    bind[Fatal].to[FatalImpl]
    bind[FatalErrorDao].to[SlickFatalErrorDao]
    bind[SmsService].to[ClickatellSmsService]

    // Sprites
    bind[SpriteService].to[SpriteServiceImpl]

    bind[SpriteHolder].to[NoOpSpriteHolder]

    // filters.Filters
    //bind[SSLFilter].to[SSLFilter]

  }

  @Provides
  def provideCredentialsStorage(cache: CacheApi)(implicit ec: ExecutionContext): CredentialsStorage = {
    new PlayCacheCredentialsStorage(cache, Duration.Inf)
  }

  @Provides
  def provideLogoSizes(config: Configuration): LogoSizes = {
    def dimension(ty: String): Dimension = new Dimension(config.underlying.getInt(s"sprites.$ty.x"), config.underlying.getInt(s"sprites.$ty.y"))
    LogoSizes(dimension("teams"), dimension("competitions"))
  }

  @Provides
  def providePdfPositioning(config: Configuration): PdfPositioning = {
    config.underlying.as[PdfPositioning]("pdf")
  }

  @Provides
  def provideSmsConfiguration(config: Configuration): SmsConfiguration = {
    config.underlying.as[SmsConfiguration]("sms")
  }

  @Provides
  def provideSecretToken(config: Configuration): SecretToken = {
    SecretToken(config.underlying.getString("secret"))
  }

  @Provides
  def provideAuth(config: Configuration) = {
    Authorised(config.underlying.getString("valid-users.users").split(",").map(_.trim()))
  }

  @Provides
  def provideRequireSSL(config: Configuration) = {
    RequireSSL(config.underlying.getBooleanList("require-ssl").headOption.contains(true))
  }
}
