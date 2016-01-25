package location

import java.net.URL
import javax.inject.{Inject, Named}

import argonaut._
import dao.GameDao
import geo.GeoLocation
import monads.FO
import scalaz._
import Scalaz._
import scala.concurrent._

/**
 * The default implementation of LocationService.
 * Created by alex on 12/04/15.
 */
class LocationServiceImpl @Inject() (
                                      val asyncHttpClient: AsyncHttpClient,
                                      val gameDao: GameDao,
                                      @Named("locationClientKey") val locationClientKey: String)(implicit ec: ExecutionContext) extends LocationService {

  override def location(gameId: Long): Future[Option[URL]] = {
    val urlT = for {
      game <- FO <~ gameDao.findById(gameId)
      geo <- FO <~ GeoLocation(game)
      url <- FO <~ generateUrl(geo)
    } yield url
    urlT.run
  }

  def generateUrl(geoLocation: GeoLocation): Future[Option[URL]] = {
    val futureOptionalBody = asyncHttpClient.get(
      "maps.googleapis.com",
      Seq("maps", "api", "place", "details", "json"),
      Map("placeid" -> geoLocation.placeId, "key" -> locationClientKey))
    val urlT = for {
      body <- FO <~ futureOptionalBody
      json <- FO <~ Parse.parseOption(body)
      locationField <- FO <~ (json.acursor --\ "result" --\ "url").focus
      location <- FO <~ locationField.string
    } yield new URL(location)
    urlT.run
  }
}
