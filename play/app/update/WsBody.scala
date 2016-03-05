package update

import java.net.URI

import monads.FE
import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}
import play.api.libs.ws.WSResponse
import upickle.default.{Reader => UReader, _}

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{Elem, XML}
import scalaz.Scalaz._
import scalaz._
/**
  * Created by alex on 04/02/16.
  */
trait WsBody {

  def bodyText(uri: URI)(responseBuilder: String => Future[WSResponse])(implicit ec: ExecutionContext): Future[\/[NonEmptyList[String], String]] = {
    responseBuilder(uri.toString).map { wsResponse =>
      val responseStatus = wsResponse.status
      if (responseStatus < 400) {
        wsResponse.body.right
      }
      else {
        NonEmptyList(s"Received $responseStatus ${wsResponse.statusText} from $uri").left
      }
    }
  }

  def bodyXml(uri: URI)(responseBuilder: String => Future[WSResponse])(implicit ec: ExecutionContext): Future[\/[NonEmptyList[String], Elem]] = FE {
    for {
      body <- FE <~ bodyText(uri)(responseBuilder)
    } yield {
      val cleaner = new HtmlCleaner()
      val rootNode = cleaner.clean(body)
      val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
      XML.loadString(page)
    }
  }

  def bodyJson[R](uri: URI)(responseBuilder: String => Future[WSResponse])(implicit reader: UReader[\/[NonEmptyList[String], R]], ec: ExecutionContext): Future[\/[NonEmptyList[String], R]] = FE {
    for {
      body <- FE <~ bodyText(uri)(responseBuilder)
      json <- FE <~ read[\/[NonEmptyList[String], R]](body)
    } yield json
  }

}
