package location

import dispatch._

/**
 * Created by alex on 12/04/15.
 */
trait AsyncHttpClient {

  def get(host: String, path: Seq[String], queryParams: Map[String, String]): Future[Option[String]]
}
