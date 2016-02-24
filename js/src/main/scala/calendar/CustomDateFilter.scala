package calendar

import com.greencatsoft.angularjs.{FilterService, Filter, injectable}

import scala.scalajs.js

/**
  * Created by alex on 21/02/16.
  */
@injectable("customDate")
class CustomDateFilter(filterService: FilterService) extends Filter[js.Date] {

  override def filter(date: js.Date): String = null

  override def filter(date: js.Date, args: Seq[Any]): String = {
    val oFormat = args.headOption.filter(_.isInstanceOf[String]).map(_.asInstanceOf[String])
    val filteredResult = for {
      format <- oFormat
    } yield {
      val day = date.getDate()
      val optionalDaySuffix = day % 10 match {
        case 1 => Some("st")
        case 2 => Some("nd")
        case 3 => Some("rd")
        case _ => None
      }
      val daySuffix = optionalDaySuffix.filter(_ => day / 10 != 1).getOrElse("th")
      val alteredFormat = format.replace("dth", s"d'$daySuffix'").replace(":mm!", if (date.getMinutes() == 0) "" else ":mm")
      filterService("date").call(this, date, alteredFormat).toString.replace("AM", "am").replace("PM", "pm")
    }
    filteredResult.getOrElse("")
  }
}