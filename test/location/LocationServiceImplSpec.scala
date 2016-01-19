package location

import java.net.URL

import dao.{GameDao, Transactional}
import model.Competition.PREM
import model.Location.AWAY
import model.{Game, GameKey}
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
        Promise.successful(Some("""{ "result": { "url": "http://myurl" } }""")).future
      val game = new Game(GameKey(PREM, AWAY, "Swansea City", 2015))
      val gameDao = mock[GameDao]
      gameDao.findById(555l) returns Some(game)
      val tx = new Transactional {
        override def tx[T](block: (GameDao) => T): T = block(gameDao)
      }
      val locationService = new LocationServiceImpl(asyncHttpClient, tx, "client")
      val geoFuture = locationService.location(555l)
      geoFuture must beEqualTo(Some(new URL("http://myurl"))).await
    }
  }
}
