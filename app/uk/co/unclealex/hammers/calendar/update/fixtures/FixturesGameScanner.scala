package uk.co.unclealex.hammers.calendar.update.fixtures

import java.net.URI
import javax.inject.Inject

import argonaut.Argonaut._
import argonaut._
import uk.co.unclealex.hammers.calendar.update.fixtures.FixturesRequest._
import uk.co.unclealex.hammers.calendar.update.fixtures.FixturesResponse._
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import uk.co.unclealex.hammers.calendar.dates.NowService
import uk.co.unclealex.hammers.calendar.html.GameUpdateCommand
import uk.co.unclealex.hammers.calendar.logging.{RemoteLogging, RemoteStream}
import uk.co.unclealex.hammers.calendar.update.GameScanner

import scala.io.Source

/**
 * Created by alex on 08/03/15.
 */
class FixturesGameScanner @Inject() (rootUri: URI, nowService: NowService) extends GameScanner with RemoteLogging {

  override def scan(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val httpClient = HttpClients.createDefault()
    try {
      val currentYear = nowService.now.getYear
      val seasonsDownloader = SeasonsDownloader(currentYear, httpClient)(remoteStream)
      seasonsDownloader.downloadFixtures(currentYear)
    }
    finally {
      httpClient.close
    }
  }

  case class SeasonsDownloader(val currentYear: Int, val httpClient: HttpClient)(implicit val remoteStream: RemoteStream) {

    def downloadFixtures(yearToSearch: Int): List[GameUpdateCommand] = {
      val uri = new URIBuilder(rootUri).setPath("/API/Fixture/Fixtures-and-Result-Listing-API.aspx").addParameter("t", Math.random.toString).build
      val post = new HttpPost(uri)
      post.setHeader("Content-Type", "application/x-www-form-encoded")
      val fixturesRequest = FixturesRequest(yearToSearch)
      val requestEntity = EntityBuilder.create().setText(fixturesRequest.asJson.nospaces).build
      post.setEntity(requestEntity)
      val httpResponse = httpClient.execute(post)
      val responseEntity = httpResponse.getEntity
      try {
        val responseText = Source.fromInputStream(responseEntity.getContent).getLines().mkString("\n")
        def fail(str: String): List[GameUpdateCommand] = {
          logger error str
          List.empty
        }
        def success(fixturesResponse: FixturesResponse): List[GameUpdateCommand] = {
          val gameUpdateCommands = fixturesResponse.fixtures.flatMap(_.toGameUpdateCommands(rootUri, yearToSearch))
          if (gameUpdateCommands.isEmpty && currentYear == yearToSearch) {
            downloadFixtures(yearToSearch - 1)
          } else if (gameUpdateCommands.isEmpty) {
            gameUpdateCommands
          }
          else {
            gameUpdateCommands ::: downloadFixtures(yearToSearch - 1)
          }
        }
        Parse.decodeValidation[FixturesResponse](responseText).fold(fail, success)
      }
      finally {
        EntityUtils.consume(responseEntity)
      }
    }
  }
}
