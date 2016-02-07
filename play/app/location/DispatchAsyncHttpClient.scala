package location



import dispatch._, Defaults._

/**
 * The Dispatch flavour of AsyncHttpClient.
 * Created by alex on 12/04/15.
 */
class DispatchAsyncHttpClient extends AsyncHttpClient {

  def get(remoteHost: String, path: Seq[String], queryParams: Map[String, String]): Future[Option[String]] = {
    val hostWithPath = path.foldLeft(host(remoteHost).secure) { (url, partialPath) =>
      url / partialPath
    }
    Http(hostWithPath <<? queryParams OK as.String).option
  }
}
