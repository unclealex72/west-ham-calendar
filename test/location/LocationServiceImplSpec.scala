package location

import java.net.URL

import dao.GameDao
import dates.SystemNowService
import model.Competition.PREM
import model.Game
import model.Location.AWAY
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent._

/**
 * Created by alex on 12/04/15.
 */
class LocationServiceImplSpec extends Specification with Mockito {

  "The location service" should {
    "be able to extract the geolocation URL from google" in { implicit ee: ExecutionEnv =>
      val asyncHttpClient = mock[AsyncHttpClient]
      asyncHttpClient.get("maps.googleapis.com",
        Seq("maps", "api", "place", "details", "json"),
        Map("placeid" -> "ChIJhbU1kmj1bkgRkB1I94UJXh4", "key" -> "client")) returns
        Future.successful(Some("""{ "result": { "url": "http://myurl" } }"""))
      val game = Game.gameKey(PREM, AWAY, "Swansea City", 2015)(new SystemNowService())
      val gameDao = mock[GameDao]
      gameDao.findById(555l) returns Future.successful(Some(game))
      val locationService = new LocationServiceImpl(asyncHttpClient, gameDao, LocationClientKey("client"))
      val geoFuture = locationService.location(555l)
      geoFuture must beEqualTo(Some(new URL("http://myurl"))).await
    }
  }
}
