package models

import dates.SharedDay
import json.JsonConverters
import upickle.Js

import scala.math.{Ordering => SOrdering}
import scalaz.Scalaz._
import scalaz._
/**
  * Javascript for Seasons
  * Created by alex on 16/02/16.
  */
case class Season(season: Int, latest: Boolean, months: Seq[Month]) {

  def alterAttendance(gameRow: GameRow, newAttendance: Boolean): Season = {
    this.copy(months = months.map(_.alterAttendance(gameRow, newAttendance)))
  }

  /**
    * Find the given month or the first month if it cannot be found.
    * @param month
    * @return
    */
  def findOrFirstMonth(maybeMonth: Option[Int], now: SharedDay): Option[Month] = {
    // First look for a month if one was provided
    val maybeSelectedMonth = for {
      month <- maybeMonth
      selectedMonth <- months.find(mth => mth.date.month == month)
    } yield {
      selectedMonth
    }
    val maybeSelectedMonthOrCurrentMonth = maybeSelectedMonth.orElse {
      // No month found. Find the current month.
      months.find { mth =>
        val date = mth.date
        date.month == now.month && date.year == now.year
      }
    }
    // Fallback to the first month if all else fails.
    maybeSelectedMonthOrCurrentMonth.orElse(months.headOption)
  }

}

object Season extends JsonConverters[Season] {
  implicit val ordering: SOrdering[Season] = SOrdering.by(s => s.season)

  def serialise(season: Season): Js.Value = Js.Obj(
    "season" -> Js.Num(season.season),
    "latest" -> jsBool(season.latest),
    "months" -> jsArr(Month.serialise)(season.months)
  )

  def deserialise(value: Js.Value): ValidationNel[String, Season] = value.jsObj("Season") { fields =>
    val season = fields.mandatory("season")(_.jsInt)
    val latest = fields.mandatory("latest")(_.jsBool)
    val months = fields.mandatory("months")(_.jsArr(Month.deserialise))
    (season |@| latest |@| months)(Season.apply)
  }
}