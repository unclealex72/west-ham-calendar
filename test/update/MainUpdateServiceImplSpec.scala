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
package update

import java.net.URI

import dao.GameDao
import dates.{September, TimeOfDay, ZonedDateTimeFactory, ZonedDateTimeFactoryImpl}
import html.{AttendenceUpdateCommand, DatePlayedLocator, DatePlayedUpdateCommand, GameKeyLocator, GameUpdateCommand, SeasonTicketsUpdateCommand}
import logging.{RemoteStream, SimpleRemoteStream}
import models.{Competition, Location}
import Competition._
import Location._
import model.{Game, GameKey}
import java.time.{Clock, ZoneId, ZonedDateTime}

import monads.FE.FutureEitherNel
import monads.{FE, FO}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import update.fixtures.FixturesGameScanner
import update.tickets.TicketsGameScanner

import scala.concurrent.{ExecutionContext, Future}
import cats.instances.future._

/**
 * @author alex
 *
 */
class MainUpdateServiceImplSpec extends Specification with DisjunctionMatchers with Mockito {

  sequential

  val TICKETS_URL = new URI("http://tickets")
  val FIXTURES_URL = new URI("http://fixtures")
  val SOUTHAMPTON = GameKey(FACP, HOME, "Southampton", 2014)
  val LIVERPOOL = GameKey(FACP, HOME, "Liverpool", 2014)
  val LATEST_SEASON: Option[Int] = Some(2015)

  def updates(gameUpdateCommands: GameUpdateCommand*)(implicit ec: ExecutionContext): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    FE(Future.successful(gameUpdateCommands))
  }

  "Adding a completely new game" should {
    "create and update a new game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      s.gameDao.getAll returns Future.successful(List.empty[Game])
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at(15, 0)))
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      val expectedStoredGame = Game.gameKey(SOUTHAMPTON).copy(at = Some(September(5, 2013) at (15, 0)))
      s.gameDao.store(expectedStoredGame) returns Future.successful(expectedStoredGame)
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there was one(s.gameDao).store(expectedStoredGame)
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with new information" should {
    "update the game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val existingStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0))
      )
      s.gameDao.getAll returns Future.successful(List(existingStoredGame))
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(AttendenceUpdateCommand(SOUTHAMPTON, 34966))
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      val expectedStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0)),
        attendance = Some(34966)
      )
      s.gameDao.store(expectedStoredGame) returns Future.successful(expectedStoredGame)
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there was one(s.gameDao).store(expectedStoredGame)
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with no extra information" should {
    "not update the game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val existingStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0)),
        attendance = Some(34966)
      )
      s.gameDao.getAll returns Future.successful(List(existingStoredGame))
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        AttendenceUpdateCommand(SOUTHAMPTON, 34966))
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there were no(s.gameDao).store(any[Game]())
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with new ticket information" should {
    "update the game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val existingStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0))
      )
      s.gameDao.getAll returns Future.successful(List(existingStoredGame))
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      val expectedStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0)),
        seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      )
      s.gameDao.store(expectedStoredGame) returns Future.successful(expectedStoredGame)
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there was one(s.gameDao).store(expectedStoredGame)
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Updating an existing game with no new ticket information" should {
    "not update the game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val existingStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0)),
        seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      )
      s.gameDao.getAll returns Future.successful(List(existingStoredGame))
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Creating a new game and also updating its ticket information" should {
    "create and update the game" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      s.gameDao.getAll returns Future.successful(List.empty)
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        DatePlayedUpdateCommand(SOUTHAMPTON, September(5, 2013) at(15, 0)))
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      val firstStoredGame = Game.gameKey(SOUTHAMPTON).copy(at = Some(September(5, 2013) at (15, 0)))
      val secondStoredGame = Game.gameKey(SOUTHAMPTON).copy(
        at = Some(September(5, 2013) at (15, 0)),
        seasonTicketsAvailable = Some(September(3, 2013) at (9, 0))
      )
      List(firstStoredGame, secondStoredGame) foreach { game =>
        s.gameDao.store(game) returns Future.successful(game)
      }
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(1).await
      there was one(s.gameDao).store(firstStoredGame)
      there was one(s.gameDao).store(secondStoredGame)
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Tickets for a non-existing game" should {
    "be ignored" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.gameDao.getAll returns Future.successful(List.empty)
      s.fixturesGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates()
      s.ticketsGameScanner.scan(LATEST_SEASON)(s.remoteStream) returns updates(
        SeasonTicketsUpdateCommand(DatePlayedLocator(September(5, 2013) at(15, 0)), September(3, 2013) at(9, 0)))
      s.mainUpdateService.processDatabaseUpdates(s.remoteStream).value must beRight(0).await
      there were no(s.gameDao).store(any[Game]())
      there was one(s.lastUpdated).at(s.now)
    }
  }

  "Attending a game" should {
    "persist its attended flag to true" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val unattendedGame = Game.gameKey(SOUTHAMPTON)
      val attendedGame = Game.gameKey(SOUTHAMPTON).copy(attended = true)

      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.gameDao.findById(1l) returns FO(Some(unattendedGame))
      s.gameDao.store(attendedGame) returns Future.successful(attendedGame)
      val changedGame = s.mainUpdateService.attendGame(1l).value
      changedGame.map(_.map(_.attended)) must beSome(true).await
      there was one(s.gameDao).store(attendedGame)
    }
  }

  "Unattending a game" should {
    "persist its attended flag to false" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val unattendedGame = Game.gameKey(SOUTHAMPTON).copy(attended = false)
      val attendedGame = Game.gameKey(SOUTHAMPTON).copy(attended = true)

      s.gameDao.getLatestSeason returns FO(LATEST_SEASON)
      s.gameDao.findById(1l) returns FO(Some(attendedGame))
      s.gameDao.store(unattendedGame) returns Future.successful(unattendedGame)
      val changedGame = s.mainUpdateService.unattendGame(1l).value
      changedGame.map(_.map(_.attended)) must beSome(false).await
    }
  }

  "Attending all home games in a season" should {
    "persist all home games attended flag to true" in { implicit ee: ExecutionEnv =>
      val s = new Services()
      implicit val zonedDateTimeFactory: ZonedDateTimeFactory = s.zonedDateTimeFactory
      val homeGames2013 = List(SOUTHAMPTON, LIVERPOOL)
      val unattendedGames = homeGames2013.map(Game.gameKey)
      val attendedGames = homeGames2013.map(Game.gameKey(_).copy(attended = true))

      s.gameDao.getAllForSeasonAndLocation(2013, HOME) returns Future.successful(unattendedGames)
      attendedGames foreach (attendedGame => s.gameDao.store(attendedGame) returns Future.successful(attendedGame))
      s.mainUpdateService.attendAllHomeGamesForSeason(2013).map(_.map(_.attended)) must be_==(List(true, true)).await
    }
  }

  // Game locator implicits

  implicit val zonedDateTimeToDatePlayedLocator: TimeOfDay => DatePlayedLocator = DatePlayedLocator(_)

  implicit val gameKeyToGameKeyLocator: GameKey => GameKeyLocator = GameKeyLocator
  /**
   * A matcher that checks that two games are equal.
   */
  implicit val gameMatcher: Game => (Game => Boolean) = { g1 =>
    g2 =>
      g1.gameKey == g2.gameKey &&
        g1.attended == g2.attended &&
        g1.attendance == g2.attendance &&
        g1.academyMembersAvailable == g2.academyMembersAvailable &&
        g1.bondholdersAvailable == g2.bondholdersAvailable &&
        g1.generalSaleAvailable == g2.generalSaleAvailable &&
        g1.at == g2.at &&
        g1.priorityPointAvailable == g2.priorityPointAvailable &&
        g1.seasonTicketsAvailable == g2.seasonTicketsAvailable &&
        g1.id == g2.id &&
        g1.location == g2.location &&
        g1.matchReport == g2.matchReport &&
        g1.result == g2.result &&
        g1.televisionChannel == g2.televisionChannel
  }

  /**
   * A class that holds all the mocked services
   */
  class Services(implicit ee: ExecutionEnv) {
    private val zoneId = ZoneId.of("Europe/London")
    implicit val zonedDateTimeFactory: ZonedDateTimeFactory = new ZonedDateTimeFactoryImpl()
    val now: ZonedDateTime = zonedDateTimeFactory.now
    val gameDao: GameDao = mock[GameDao]
    val lastUpdated: LastUpdated = mock[LastUpdated]
    val ticketsGameScanner: TicketsGameScanner = mock[TicketsGameScanner]
    val fixturesGameScanner: FixturesGameScanner = mock[FixturesGameScanner]
    val mainUpdateService = new MainUpdateServiceImpl(gameDao, fixturesGameScanner, ticketsGameScanner, lastUpdated)
    val remoteStream: RemoteStream = new SimpleRemoteStream()
  }
}

