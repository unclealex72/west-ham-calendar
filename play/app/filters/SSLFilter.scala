package filters

/**
  * X-Forwarded-Proto
  * Created by alex on 15/03/16.
  */
import play.api.mvc._
import security.RequireSSL

import scala.concurrent.Future

/**
  * A filter to make Play aware of the X-Forwarded-Proto header.
  */
class SSLFilter(requireSSL: RequireSSL) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    val isSecure = requestHeader.secure || requireSSL() || requestHeader.headers.get("X-Forwarded-Proto").contains("https")
    val wrappedRequestHeader = new RequestHeader {
      override def secure: Boolean = isSecure
      override def uri: String = requestHeader.uri
      override def remoteAddress: String = requestHeader.remoteAddress
      override def queryString: Map[String, Seq[String]] = requestHeader.queryString
      override def method: String = requestHeader.method
      override def headers: Headers = requestHeader.headers
      override def path: String = requestHeader.path
      override def version: String = requestHeader.version
      override def tags: Map[String, String] = requestHeader.tags
      override def id: Long = requestHeader.id
    }
    nextFilter(wrappedRequestHeader)
  }
}