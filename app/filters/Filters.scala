package filters

/**
  * Created by alex on 15/03/16.
  */
import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.csrf.CSRFFilter
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject() (
                          ssl: SSLFilter,
                          csrfFilter: CSRFFilter,
                          securityHeadersFilter: SecurityHeadersFilter,
                          gzipFilter: GzipFilter,
                          contentDispositionFilter: ContentDispositionFilter) extends HttpFilters {

  val filters = Seq.empty //Seq(ssl, csrfFilter, securityHeadersFilter, gzipFilter)
}

