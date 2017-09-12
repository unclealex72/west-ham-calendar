package location

import java.net.URL

import dao.GameDao
import dates.geo.GeoLocationFactory
import models.GeoLocation
import monads.FO
import monads.FO.FutureOption
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import cats.instances.future._
import scala.concurrent._

/**
 * The default implementation of LocationService.
 * Created by alex on 12/04/15.
 */
class LocationServiceImpl @javax.inject.Inject() (
                           val wsClient: WSClient,
                           val gameDao: GameDao,
                           val geoLocationFactory: GeoLocationFactory,
                           val locationClientKey: LocationClientKey, implicit val ec: ExecutionContext) extends LocationService {

  override def location(gameId: Long): FutureOption[URL] = {
    for {
      game <- gameDao.findById(gameId)
      geo <- FO(geoLocationFactory.forGame(game))
      url <- generateUrl(geo)
    } yield url
  }

  def generateUrl(geoLocation: GeoLocation): FutureOption[URL] = {
    val fResponse: Future[WSResponse] =
      wsClient.url("https://maps.googleapis.com/maps/api/place/details/json").withQueryString(
        "placeid" -> geoLocation.placeId, "key" -> locationClientKey.value).get()
    val obj: JsValue => Option[JsObject] = {
      case jsObject: JsObject => Some(jsObject)
      case _ => None
    }
    val str: JsValue => Option[String] = {
      case jsString: JsString => Some(jsString.value)
      case _ => None
    }

    for {
      response <- FO(fResponse)
      json <- FO(obj(Json.parse(response.body)))
      resultField <- FO(Some(json.value("result")))
      resultObj <- FO(obj(resultField))
      locationField <- FO(Some(resultObj.value("url")))
      location <- FO(Some(locationField))
    } yield new URL(location.toString())
  }
}
