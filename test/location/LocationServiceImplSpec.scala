package location

import java.net.URL

import org.scalamock.specs2.MockFactory
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import dao.{Transactional, GameDao}
import model.Competition.PREM
import model.{GameKey, Game}
import model.Location.AWAY
import scala.concurrent._

/**
 * Created by alex on 12/04/15.
 */
class LocationServiceImplSpec extends Specification with MockFactory {

  "The location service" should {
    "be able to extract the geolocation URL from google" in {
      val asyncHttpClient = mock[AsyncHttpClient]
      (asyncHttpClient.get _) expects ("maps.googleapis.com",
        Seq("maps", "api", "place", "details", "json"),
        Map("placeid" -> "ChIJhbU1kmj1bkgRkB1I94UJXh4", "key" -> "client")) returning (
        Promise.successful(Some("""{ "result": { "url": "http://myurl" } }""")).future)
      val game = new Game(GameKey(PREM, AWAY, "Swansea City", 2015))
      val gameDao = mock[GameDao]
      (gameDao.findById _) expects (555l) returning Some(game)
      val tx = new Transactional {
        override def tx[T](block: (GameDao) => T): T = block(gameDao)
      }
      val locationService = new LocationServiceImpl(asyncHttpClient, tx, "client")
      val geoFuture = locationService.location(555l)
      geoFuture must beEqualTo(Some(new URL("http://myurl"))).await
    }
  }
}
