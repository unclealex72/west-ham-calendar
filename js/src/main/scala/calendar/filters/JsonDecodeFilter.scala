package calendar.filters

import com.greencatsoft.angularjs.{Filter, FilterService, injectable}

import scala.scalajs.js
import scala.scalajs.js.JSON

/**
  * Created by alex on 21/02/16.
  */
@injectable("jsonDecode")
class JsonDecodeFilter(filterService: FilterService) extends Filter[js.UndefOr[String]] {

  override def filter(oValue: js.UndefOr[String]): String = {
    oValue.map { value =>
      JSON.parse(value).toString
    }.getOrElse("")
  }

}