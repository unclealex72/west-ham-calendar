package calendar.services

import com.greencatsoft.angularjs.core.{HttpPromise, HttpService, Promise}
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import upickle.default.{Reader => UPReader, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.{Function1, JSON}
import scalaz.Scalaz._
import scalaz._
import scala.scalajs.js.Dynamic.{ global => g }

/**
  * Created by alex on 17/02/16.
  */
@injectable("ajax")
class AjaxService(http: HttpService) extends Service {

  def get[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.get[Response])(url)
  def put[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.put[Response])(url)

  private def connect[E](promiseFactory: String => Promise[Response])(url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = {
    val p = scala.concurrent.Promise[Response]
    promiseFactory(url).`then`(new Function1[Response, Response] {
      override def apply(v: Response): Response = {
        p.success(v)
        v
      }
    })
    p.future.map { response =>
      val jsonResponse = JSON.stringify(response.data)
      val parseResponse = read[ValidationNel[String, E]](jsonResponse)
      parseResponse.valueOr { msgs =>
        val msg = msgs.toList.mkString("\n")
        System.err.println(msg)
        throw new Exception(msg)
      }
    }
  }
}

trait Response extends js.Object {
  def data: js.Object = js.native
}

@injectable("ajax")
class AjaxServiceFactory(http: HttpService) extends Factory[AjaxService] {

  override def apply(): AjaxService = new AjaxService(http)
}