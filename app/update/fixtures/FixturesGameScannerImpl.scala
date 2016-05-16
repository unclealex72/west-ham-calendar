package update.fixtures

import java.net.URI

import dates.{PossiblyYearlessDateParser, NowService}
import html._
import logging.{Fatal, RemoteLogging, RemoteStream}
import model.GameKey
import models.{GameResult, Score, Location, Competition}
import models.Location.{AWAY, HOME}
import monads.FE
import org.joda.time.DateTime
import play.api.libs.ws.WSClient
import update.WsBody
import upickle.default._
import FixturesRequest._
import FixturesResponse._
import xml.NodeExtensions
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Elem
import scala.xml.{Node => XmlNode}
import scalaz._
import Scalaz._
/**
 * Created by alex on 08/03/15.
 */
class FixturesGameScannerImpl @javax.inject.Inject() (rootUri: URI, ws: WSClient, implicit val ec: ExecutionContext) extends FixturesGameScanner with RemoteLogging with WsBody with NodeExtensions {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], List[GameUpdateCommand]]] = {
    val fixturesUri = rootUri.resolve("/Fixtures/First-Team/Fixture-and-Results")
    FE {
      for {
        page <- FE <~ bodyXml(fixturesUri)(uri => ws.url(uri).get())
        seasons <- FE <~ listSeasons(page)
        gameUpdateCommands <- FE <~ downloadFixtures(seasons)
      } yield gameUpdateCommands.toList
    }
  }

  def listSeasons(page: Elem)(implicit remoteStream: RemoteStream): \/[NonEmptyList[String], Seq[FixturesSeason]] = {
    def parseSeasons(div: XmlNode): \/[NonEmptyList[String], Seq[FixturesSeason]] = {
      val empty: Disjunction[NonEmptyList[String], Seq[FixturesSeason]] = List.empty.right
      (div \\ "option").foldLeft(empty) { (seasons, option) =>
        option.attribute("value").toSeq.flatten[XmlNode].headOption.map(_.text.trim) match {
          case Some(season) => for {
            ss <- seasons
            i <- season.parseInt.disjunction.leftMap(_ => NonEmptyList(s"Cannot parse $season as a season"))
          } yield ss :+ FixturesSeason(i)
          case None => seasons
        }
      }
    }
    val possiblyEmptySeasons = for {
      div <- (page \\ "div").find(_.hasId("seasonList")).toRightDisjunction(NonEmptyList("Cannot find a seasonList wrapper"))
      seasons <- parseSeasons(div)
    } yield seasons
    possiblyEmptySeasons.ensure(NonEmptyList("Cannot find any seasons on the fixtures page"))(_.nonEmpty)
  }

  def downloadFixtures(seasons: Seq[FixturesSeason])(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = {
    val empty: Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = Future.successful(Seq.empty.right)
    seasons.foldLeft(empty){ (gameUpdateCommands, season) =>
      FE {
        for {
          existingGameUpdateCommands <- FE <~ gameUpdateCommands
          newGameUpdateCommands <- FE <~ downloadFixturesForSeason(season)
        } yield existingGameUpdateCommands ++ newGameUpdateCommands
      }
    }
  }

  def downloadFixturesForSeason(season: FixturesSeason)(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = {
    val fixturesRequest = FixturesRequest(season.yearFromHtml)
    val fixturesUri: URI = rootUri.resolve("/API/Fixture/Fixtures-and-Result-Listing-API.aspx")
    FE {
      for {
        fixturesResponse <- FE <~ bodyJson[FixturesResponse](fixturesUri) {
          ws.url(_).
            withQueryString("t" -> Math.random.toString).
            withHeaders("Content-Type" -> "application/x-www-form-encoded").
            post(write(fixturesRequest))
        }.map(_.ensure(NonEmptyList(s"Did not get a valid fixtures response for season ${season.actualYear}"))(_.isSuccess))

      } yield fixturesResponse.fixtures.flatMap(fx => toGameUpdateCommands(fx, rootUri, season.actualYear)).toList
    }
  }

  def toGameUpdateCommands(fixture: Fixture, rootUrl: URI, season: Int)
                          (implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val (opponents, location) = if (fixture.home) (fixture.awayTeam, HOME) else (fixture.homeTeam, AWAY)
    for {
      competition <- logOnEmpty(Competition.apply(fixture.competitionName)).toList
      matchDate <- PossiblyYearlessDateParser.forSeason(season)("d MMMM HH:mm", "dd MMMM HH:mm").logFailures.find(fixture.matchDate).toList
      gameUpdateCommand <- toGameCommands(fixture, rootUrl, opponents, location, competition, matchDate, season)
    } yield {
      logger.info(s"Found update $gameUpdateCommand")
      gameUpdateCommand
    }
  }

  def toGameCommands(fixture: Fixture, rootUrl: URI, opponents: String, location: Location, competition: Competition, matchDate: DateTime, season: Int)(implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val locator: GameLocator = GameKeyLocator(GameKey(competition, location, opponents, season))
    val datePlayedUpdateCommand = Some(DatePlayedUpdateCommand(locator, matchDate))
    def isNeitherEmptyNorNull(str: String): Option[String] = Option(str.trim).filterNot(_.isEmpty)
    def isDigits(str: String): Option[Int] = isNeitherEmptyNorNull(str).filter { s =>
      s.matches("""\d+""")
    }.map(_.toInt)
    val resultUpdateCommand = for {
      homeScore <- isDigits(fixture.homeTeamScore)
      awayScore <- isDigits(fixture.awayTeamScore)
    } yield {
      val score = Score(homeScore.toInt, awayScore.toInt)
      val penalties = for {
        homePenalties <- isDigits(fixture.homeShootoutScore)
        awayPenalties <- isDigits(fixture.awayShootoutScore)
      } yield Score(homePenalties, awayPenalties)
      val result = GameResult(score, penalties)
      ResultUpdateCommand(locator, result)
    }
    val matchReportUpdateCommand = fixture.link.map { link =>
      val fullLink = rootUrl.resolve(link)
      MatchReportUpdateCommand(locator, fullLink.toString)
    }
    val uriCleaners: Seq[String => String] = Seq(
      _.replace("~", ""),
      _.replace(" ", "%20"),
      str => {
        val queryParams = str.indexOf('?')
        if (queryParams < 0) str else str.substring(0, queryParams)
      }
    )
    val logoUpdateCommands: Seq[Option[GameUpdateCommand]] = Seq(
      fixture.homeTeamLogo -> HomeTeamImageLinkCommand.curried(locator),
      fixture.awayTeamLogo -> AwayTeamImageLinkCommand.curried(locator),
      fixture.competitionLogo -> CompetitionImageLinkCommand.curried(locator)).map {
      case (value, gameUpdater) =>
        for {
          logo <- isNeitherEmptyNorNull(value)
        } yield {
          val sanitised = uriCleaners.foldLeft(logo)((str, f) => f(str))
          val absoluteUrl = rootUrl.resolve(sanitised).toASCIIString
          gameUpdater(absoluteUrl)
        }
    }
    (Seq(datePlayedUpdateCommand, resultUpdateCommand, matchReportUpdateCommand) ++ logoUpdateCommands).flatten
  }

  // A case class to hold seasons from the top level fixture page. For some reason these are sometimes single figure numbers
  // that need 2000 to be added to them.
  case class FixturesSeason(yearFromHtml: Int, actualYear: Int)

  object FixturesSeason {
    def apply(yearFromHtml: Int): FixturesSeason =
      FixturesSeason(yearFromHtml, if (yearFromHtml < 2000) yearFromHtml + 2000 else yearFromHtml)
  }
}
