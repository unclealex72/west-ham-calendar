package update

import java.net.URI

import argonaut.Argonaut._
import argonaut.EncodeJson
import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.Future
import scala.xml.{Elem, XML}

/**
  * Created by alex on 04/02/16.
  */
object WsClientImplicits {

  implicit class WsClientExtensions(ws: WSClient) {
    def url(uri: URI): WSRequest = ws.url(uri.toString)
  }

  implicit class WsRequestExtensions(wsRequest: WSRequest) {
    def postJ[T](body: T)(implicit e: EncodeJson[T]): Future[WSResponse] =
      wsRequest.post(body.asJson.nospaces)
  }

  implicit class WsResponseExtension(wSResponse: WSResponse) {
    def cleanXml: Elem = {
      val cleaner = new HtmlCleaner()
      val rootNode = cleaner.clean(wSResponse.body)
      val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
      XML.loadString(page)

    }
  }

}
