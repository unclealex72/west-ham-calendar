package calendar.services

import com.greencatsoft.angularjs.core.{HttpPromise, HttpService}
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import upickle.default.{Reader => UPReader, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON
import scalaz.Scalaz._
import scalaz._
/**
  * Created by alex on 17/02/16.
  */
@injectable("ajax")
class AjaxService(http: HttpService) extends Service {

  def get[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.get[js.Any])(url)
  def put[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.put[js.Any])(url)

  private def connect[E](promiseFactory: String => HttpPromise[js.Any])(url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] =
    promiseFactory(url).map { response =>
      val parseResponse = read[ValidationNel[String, E]](JSON.stringify(response))
      parseResponse.valueOr { msgs =>
        val msg = msgs.toList.mkString("\n")
        System.err.println(msg)
        throw new Exception(msg)
      }
    }
}

@injectable("ajax")
class AjaxServiceFactory(http: HttpService) extends Factory[AjaxService] {

  override def apply(): AjaxService = new AjaxService(http)
}