package calendar

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by alex on 17/02/16.
  */
@injectable("ajax")
class AjaxService(http: HttpService) extends Service {

  def get[E: Reader](url: String): Future[E] = http.get[js.Any](url).map(response => read[E](JSON.stringify(response)))

}

@injectable("ajax")
class AjaxServiceFactory(http: HttpService) extends Factory[AjaxService] {

  override def apply(): AjaxService = new AjaxService(http)
}