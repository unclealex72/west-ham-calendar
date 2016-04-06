package filters

/**
  * Created by alex on 15/03/16.
  */
import javax.inject.Inject

import play.api.http.HttpFilters

class Filters @Inject() (ssl: SSLFilter) extends HttpFilters {

  val filters = Seq(ssl)
}