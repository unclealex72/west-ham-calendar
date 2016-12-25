package calendar.services

import com.greencatsoft.angularjs.core.{HttpResult, HttpService, Promise}
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import upickle.default.{Reader => UPReader, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.{Function1, JSON}
import scalaz.Scalaz._
import scalaz._

/**
  * Created by alex on 17/02/16.
  */
@injectable("ajax")
class AjaxService(http: HttpService) extends Service {

  def get[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.get[HttpResult])(url)
  def put[E](url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = connect(http.put[HttpResult])(url)

  private def connect[E](promiseFactory: String => Promise[HttpResult])(url: String)(implicit reader: UPReader[ValidationNel[String, E]]): Future[E] = {
    val p = scala.concurrent.Promise[HttpResult]
    val successCallback = new Function1[HttpResult, HttpResult] {
      override def apply(r: HttpResult): HttpResult = {
        p.success(r)
        r
      }
    }
    val errorCallback = new Function1[HttpResult, Unit] {
      override def apply(r: HttpResult): Unit = {
        val message = s"${r.status}: ${r.statusText}"
        System.err.println(message)
        p.failure(new Exception(message))
      }
    }
    promiseFactory(url).`then`(successCallback, errorCallback)
    p.future.flatMap { HttpResult =>
      val jsonHttpResult = JSON.stringify(HttpResult.data)
      val parseHttpResult = read[ValidationNel[String, E]](jsonHttpResult)
      parseHttpResult match {
        case Success(obj) => Future.successful(obj)
        case Failure(msgs) =>
          val msg = msgs.toList.mkString("\n")
          System.err.println(msg)
          Future.failed[E](new Exception(msg))
      }
    }
  }
}

@injectable("ajax")
class AjaxServiceFactory(http: HttpService) extends Factory[AjaxService] {

  override def apply(): AjaxService = new AjaxService(http)
}