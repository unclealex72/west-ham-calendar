package update

import java.net.URI

import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}
import play.api.libs.json.{Json, JsValue}
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

  implicit class WsResponseExtension(wSResponse: WSResponse) {
    def cleanXml: Elem = {
      val cleaner = new HtmlCleaner()
      val rootNode = cleaner.clean(wSResponse.body)
      val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
      XML.loadString(page)

    }
  }

}
