package filters

/**
  * X-Forwarded-Proto
  * Created by alex on 15/03/16.
  */
import java.security.cert.X509Certificate
import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc._
import security.RequireSSL

import scala.concurrent.Future

/**
  * A filter to make Play aware of the X-Forwarded-Proto header.
  */
class SSLFilter @Inject() (requireSSL: RequireSSL, implicit val mat: Materializer) extends Filter {

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
      override def clientCertificateChain: Option[Seq[X509Certificate]] = requestHeader.clientCertificateChain
    }
    nextFilter(wrappedRequestHeader)
  }
}