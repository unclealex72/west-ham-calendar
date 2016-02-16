package update.fixtures

import java.net.URI

import dates.{PossiblyYearlessDateParser, NowService}
import html._
import logging.{RemoteLogging, RemoteStream}
import model.GameKey
import models.{Location, Competition}
import models.Location.{AWAY, HOME}
import org.joda.time.DateTime
import play.api.libs.ws.WSClient
import update.WsClientImplicits._
import upickle.default._
import FixturesRequest._
import FixturesResponse._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by alex on 08/03/15.
 */
class FixturesGameScannerImpl(rootUri: URI, nowService: NowService, ws: WSClient)(implicit ec: ExecutionContext) extends FixturesGameScanner with RemoteLogging {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): Future[List[GameUpdateCommand]] = {
    val currentYear = nowService.now.getYear
    val seasonsDownloader = SeasonsDownloader(currentYear)(remoteStream)
    seasonsDownloader.downloadFixtures(currentYear)
  }

  case class SeasonsDownloader(val currentYear: Int)(implicit val remoteStream: RemoteStream) {

    def downloadFixtures(yearToSearch: Int): Future[List[GameUpdateCommand]] = {
      val fixturesRequest = FixturesRequest(yearToSearch)
      val fResponse = ws.url(rootUri.resolve("/API/Fixture/Fixtures-and-Result-Listing-API.aspx")).
        withQueryString("t" -> Math.random.toString).
        withHeaders("Content-Type" -> "application/x-www-form-encoded").
        post(write(fixturesRequest))
      fResponse.flatMap { response =>
        def fail(str: String): Future[List[GameUpdateCommand]] = {
          logger error str
          Future.successful(List.empty)
        }
        def success(fixturesResponse: FixturesResponse): Future[List[GameUpdateCommand]] = {
          val gameUpdateCommands = fixturesResponse.fixtures.flatMap(fx => toGameUpdateCommands(fx, rootUri, yearToSearch)).toList
          if (gameUpdateCommands.isEmpty && currentYear == yearToSearch) {
            downloadFixtures(yearToSearch - 1)
          } else if (gameUpdateCommands.isEmpty) {
            Future.successful(gameUpdateCommands)
          }
          else {
            downloadFixtures(yearToSearch - 1).map {
              gameUpdateCommands ::: _
            }
          }
        }
        read[Either[String, FixturesResponse]](response.body).fold(fail, success)
      }
    }
  }

  def toGameUpdateCommands(fixture: Fixture, rootUrl: URI, season: Int)
                          (implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val (opponents, location) = if (fixture.home) (fixture.awayTeam, HOME) else (fixture.homeTeam, AWAY)
    for {
      competition <- logOnEmpty(Competition.apply(fixture.competitionName)).toList
      matchDate <- PossiblyYearlessDateParser.forSeason(season)("d MMMM HH:mm", "dd MMMM HH:mm").logFailures.find(fixture.matchDate).toList
      gameUpdateCommand <- toGameCommands(fixture, rootUrl, opponents, location, competition, matchDate, season)
    } yield gameUpdateCommand
  }

  def toGameCommands(fixture: Fixture, rootUrl: URI, opponents: String, location: Location, competition: Competition, matchDate: DateTime, season: Int)(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val locator: GameLocator = GameKeyLocator(GameKey(competition, location, opponents, season))
    val datePlayedUpdateCommand = Some(DatePlayedUpdateCommand(locator, matchDate))
    val isEmptyOrNull: String => Option[String] = str => Option(str).filterNot(_.isEmpty)
    val resultUpdateCommand = for {
      homeScore <- isEmptyOrNull(fixture.homeTeamScore)
      awayScore <- isEmptyOrNull(fixture.awayTeamScore)
    } yield {
      val score = s"$homeScore - $awayScore"
      val penalties = for {
        homePenalties <- isEmptyOrNull(fixture.homeShootoutScore)
        awayPenalties <- isEmptyOrNull(fixture.awayShootoutScore)
      } yield s" ($homePenalties - $awayPenalties)"
      val result = score + penalties.getOrElse("")
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
          logo <- isEmptyOrNull(value)
        } yield {
          val sanitised = uriCleaners.foldLeft(logo)((str, f) => f(str))
          val absoluteUrl = rootUrl.resolve(sanitised).toASCIIString
          gameUpdater(absoluteUrl)
        }
    }
    (List(datePlayedUpdateCommand, resultUpdateCommand, matchReportUpdateCommand) ++ logoUpdateCommands).flatten
  }

}
