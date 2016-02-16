package location

import java.net.URL

import dao.GameDao
import dates.geo.GeoLocationFactory
import models.GeoLocation
import monads.FO
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

import scala.concurrent._
import scalaz.Scalaz._
import scalaz._

/**
 * The default implementation of LocationService.
 * Created by alex on 12/04/15.
 */
class LocationServiceImpl(
                                      val asyncHttpClient: AsyncHttpClient,
                                      val gameDao: GameDao,
                                      val geoLocationFactory: GeoLocationFactory,
                                      val locationClientKey: LocationClientKey)(implicit ec: ExecutionContext) extends LocationService {

  override def location(gameId: Long): Future[Option[URL]] = {
    val urlT = for {
      game <- FO <~ gameDao.findById(gameId)
      geo <- FO <~ geoLocationFactory.forGame(game)
      url <- FO <~ generateUrl(geo)
    } yield url
    urlT.run
  }

  def generateUrl(geoLocation: GeoLocation): Future[Option[URL]] = {
    val futureOptionalBody = asyncHttpClient.get(
      "maps.googleapis.com",
      Seq("maps", "api", "place", "details", "json"),
      Map("placeid" -> geoLocation.placeId, "key" -> locationClientKey.value))
    val obj: JsValue => Option[JsObject] = {
      case jsObject: JsObject => Some(jsObject)
      case _ => None
    }
    val str: JsValue => Option[String] = {
      case jsString: JsString => Some(jsString.value)
      case _ => None
    }

    FO {
      for {
        body <- FO <~ futureOptionalBody
        json <- FO <~ obj(Json.parse(body))
        resultField <- FO <~ json.value("result")
        resultObj <- FO <~ obj(resultField)
        locationField <- FO <~ resultObj.value("url")
        location <- FO <~ str(locationField)
      } yield new URL(location)
    }
  }
}
