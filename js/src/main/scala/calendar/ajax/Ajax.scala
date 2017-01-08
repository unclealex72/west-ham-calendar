package calendar.ajax

import org.scalajs.dom
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax.InputData
import upickle.default.{Reader => UpReader, _}

import scala.concurrent.Future
import scala.scalajs.js.JSON
import scalaz.Scalaz._
import scalaz._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by alex on 01/01/17
  **/
object Ajax {

  private type Url = String
  private type Timeout = Int
  private type Headers = Map[String, String]
  private type WithCredentials = Boolean
  private type ContentType = String

  def get[RESP](url: String)(implicit reader: UpReader[ValidationNel[String, RESP]]): Future[RESP] =
    connect[RESP](dom.ext.Ajax.get, url, reader)
  def put[RESP](url: String)(implicit reader: UpReader[ValidationNel[String, RESP]]): Future[RESP] =
    connect[RESP](dom.ext.Ajax.put, url, reader)

  private def connect[RESP]
    (method: (Url, InputData, Timeout, Headers, WithCredentials, ContentType) => Future[XMLHttpRequest], url: String, reader: UpReader[ValidationNel[String, RESP]]): Future[RESP] = {
      method(url, null, 1000, Map("Accepts" -> "application/json"), false, "json").flatMap { resp =>
        val response: String = JSON.stringify(resp.response)
        val parsedResponse: ValidationNel[String, RESP] = read(response)(reader)
        parsedResponse match {
          case Success(value) => Future.successful(value)
          case Failure(msgs) =>
            val msg = msgs.toList.mkString("\n")
            System.err.println(msg)
            Future.failed[RESP](new Exception(msg))
        }

    }
  }
}
