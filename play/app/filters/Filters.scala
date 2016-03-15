package filters

/**
  * Created by alex on 15/03/16.
  */
import play.api.http.HttpFilters

class Filters(ssl: SSLFilter) extends HttpFilters {

  val filters = Seq(ssl)
}