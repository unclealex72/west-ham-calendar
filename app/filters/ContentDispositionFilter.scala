package filters

import javax.inject.Inject

import akka.stream.Materializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 13/05/16.
  */
class ContentDispositionFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with StrictLogging {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      val originalHeaders: Map[String, String] = result.header.headers
      if (originalHeaders.get("Content-Disposition").isDefined) {
        logger.warn("Content Disposition Header set.")
      }
      val headers = originalHeaders.filterKeys(_ != "Content-Disposition")
      result.withHeaders(headers.toSeq :_*)
    }
  }
}
