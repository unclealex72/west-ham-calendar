package update.fixtures

import java.net.URI

import org.joda.time.DateTime
import dates.PossiblyYearlessDateParser
import html._
import logging.RemoteStream
import model.Location.{AWAY, HOME}
import model.{Competition, GameKey, Location}

import scalaz.{Failure, Success, Validation}

/**
 * Created by alex on 08/03/15.
 */
case class FixturesResponse(fixtures: List[Fixture])

case class Fixture(
  matchDate: String,
  competitionName: String,
  homeTeam: String,
  awayTeam: String,
  venue: String,
  homeTeamScore: String,
  awayTeamScore: String,
  homeShootoutScore: String,
  awayShootoutScore: String,
  link: Option[String],
  home: Boolean) {

  def toGameUpdateCommands(rootUrl: URI, season: Int)
                          (implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val (opponents, location) = if (home) (awayTeam, HOME) else (homeTeam, AWAY)
    for {
      competition <- Competition.apply(competitionName).toList
      matchDate <- PossiblyYearlessDateParser.forSeason(season)("d MMMM HH:mm", "dd MMMM HH:mm").logFailures.find(matchDate).toList
      gameUpdateCommand <- toGameCommands(rootUrl, opponents, location, competition, matchDate, season)
    } yield gameUpdateCommand
  }

  def toGameCommands(rootUrl: URI, opponents: String, location: Location, competition: Competition, matchDate: DateTime, season: Int)(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val locator: GameLocator = GameKeyLocator(GameKey(competition, location, opponents, season))
    val datePlayedUpdateCommand = Some(DatePlayedUpdateCommand(locator, matchDate))
    val isEmptyOrNull: String => Option[String] = str => Option(str).filterNot(_.isEmpty)
    val resultUpdateCommand = for {
      homeScore <- isEmptyOrNull(homeTeamScore)
      awayScore <- isEmptyOrNull(awayTeamScore)
    } yield {
      val score = s"$homeScore - $awayScore"
      val penalties = for {
        homePenalties <- isEmptyOrNull(homeShootoutScore)
        awayPenalties <- isEmptyOrNull(awayShootoutScore)
      } yield s" ($homePenalties - $awayPenalties)"
      val result = score + penalties.getOrElse("")
      ResultUpdateCommand(locator, result)
    }
    val matchReportUpdateCommand = link.map { link =>
      val fullLink = rootUrl.resolve(link)
      MatchReportUpdateCommand(locator, fullLink.toString)
    }
    List(datePlayedUpdateCommand, resultUpdateCommand, matchReportUpdateCommand).flatten
  }
}

object FixturesResponse {

  import argonaut._
  import Argonaut._

  implicit def FixturesResponseDecoder: DecodeJson[FixturesResponse] = jdecode1L(FixturesResponse.apply)("resultList")
  implicit def FixtureDecoder: DecodeJson[Fixture] = jdecode11L(Fixture.apply)(
    "MatchDate", "CompetitionName", "HomeTeamName", "AwayTeamName", "VenueName",
    "HomeTeamScore", "AwayTeamScore", "HomeShootOutScore", "AwayShootOutScore", "MatchCenterPath", "IsHomeSide")
}