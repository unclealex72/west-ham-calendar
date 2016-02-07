package update.fixtures

import java.net.URI

import argonaut._
import dates.NowService
import html.GameUpdateCommand
import logging.{RemoteLogging, RemoteStream}
import play.api.libs.ws.WSClient
import update.WsClientImplicits._
import update.fixtures.FixturesRequest._
import update.fixtures.FixturesResponse._

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
        postJ(fixturesRequest)
      fResponse.flatMap { response =>
        def fail(str: String): Future[List[GameUpdateCommand]] = {
          logger error str
          Future.successful(List.empty)
        }
        def success(fixturesResponse: FixturesResponse): Future[List[GameUpdateCommand]] = {
          val gameUpdateCommands = fixturesResponse.fixtures.flatMap(_.toGameUpdateCommands(rootUri, yearToSearch))
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
        Parse.decodeValidation[FixturesResponse](response.body).fold(fail, success)
      }
    }
  }
}
