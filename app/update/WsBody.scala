package update

import java.net.URI

import cats.data.NonEmptyList
import io.circe.Decoder
import monads.FE
import monads.FE.FutureEitherNel
import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}
import play.api.libs.ws.WSResponse
import io.circe.parser._
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{Elem, XML}
import cats.instances.future._

/**
  * Created by alex on 04/02/16.
  */
trait WsBody {

  def bodyText(uri: URI)(responseBuilder: String => Future[WSResponse])(implicit ec: ExecutionContext): FutureEitherNel[String, String] = {
    FE(responseBuilder(uri.toString)).flatMap { wsResponse =>
      val responseStatus = wsResponse.status
      if (responseStatus < 400) {
        FE(Future.successful(Right(wsResponse.body)))
      }
      else {
        FE(Future.successful(Left(NonEmptyList.of(s"Received $responseStatus ${wsResponse.statusText} from $uri"))))
      }
    }
  }

  def bodyXml(uri: URI)(responseBuilder: String => Future[WSResponse])(implicit ec: ExecutionContext): FutureEitherNel[String, Elem] = {
    for {
      body <- bodyText(uri)(responseBuilder)
    } yield {
      cleanHtml(body)
    }
  }

  def cleanHtml(html: String): Elem = {
    val cleaner = new HtmlCleaner()
    val rootNode = cleaner.clean(html)
    val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
    XML.loadString(page)
  }

  def bodyJson[R](uri: URI)(responseBuilder: String => Future[WSResponse])(implicit ev: Decoder[R], ec: ExecutionContext): FutureEitherNel[String, R] = {
    for {
      body <- bodyText(uri)(responseBuilder)
      json <- FE(decodeAccumulating(body).leftMap(_.map(_.getMessage)).toEither)
    } yield json
  }

}
