package location

import java.net.URL
import javax.inject.{Named, Inject}

import uk.co.unclealex.hammers.calendar.dao.Transactional
import uk.co.unclealex.hammers.calendar.geo.GeoLocation
import uk.co.unclealex.hammers.calendar.model.Location.AWAY

import scala.concurrent._
import argonaut._, Argonaut._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * The default implementation of LocationService.
 * Created by alex on 12/04/15.
 */
class LocationServiceImpl @Inject() (
                                      val asyncHttpClient: AsyncHttpClient,
                                      val tx: Transactional,
                                      @Named("locationClientKey") val locationClientKey: String) extends LocationService {

  override def location(gameId: Long): Future[Option[URL]] = {
    tx(_.findById(gameId)) filter (_.location == AWAY) flatMap (GeoLocation(_)) match {
      case Some(geoLocation) => {
        generateUrl(geoLocation)
      }
      case None => Promise.successful(None).future
    }
  }

  def generateUrl(geoLocation: GeoLocation): Future[Option[URL]] = {
    val futureOptionalBody = asyncHttpClient.get(
      "maps.googleapis.com",
      Seq("maps", "api", "place", "details", "json"),
      Map("placeid" -> geoLocation.placeId, "key" -> locationClientKey))
    futureOptionalBody.map { optionalBody =>
      for {
        body <- optionalBody
        json <- Parse.parseOption(body)
        locationField <- (json.acursor --\ "result" --\ "url").focus
        location <- locationField.string
      } yield new URL(location)
    }
  }
}
