package models

import json.JsonConverters
import upickle.Js

import scala.collection.SortedSet
import scala.language.implicitConversions
import scala.math.{Ordering => SOrdering}
import scalaz.Scalaz._
import scalaz.{ValidationNel, _}
/**
  * Created by alex on 20/02/16.
  */
case class Months(months : SortedSet[Month])

case class Month(month: Int, year: Int, games: SortedSet[GameRow])

object Months extends JsonConverters[Months] {

  def jsonToMonth(value: Js.Value): ValidationNel[String, Month] = value.jsObj("Month") { fields =>
    val month = fields.mandatory("month")(_.jsInt)
    val year = fields.mandatory("year")(_.jsInt)
    val games = fields.mandatory("games")(_.jsSorted(GameRow.deserialise))
    (month |@| year |@| games)(Month.apply)
  }

  def monthToJson(m: Month): Js.Value = Js.Obj (
    "month" -> Js.Num(m.month),
    "year" -> Js.Num(m.year),
    "games" -> jsSorted(GameRow.serialise)(m.games)
  )

  override def deserialise(value: Js.Value): ValidationNel[String, Months] = value.jsObj("Months") { fields =>
    fields.mandatory("months")(_.jsSorted(jsonToMonth)).map(Months.apply)
  }

  override def serialise(m: Months): Js.Value = Js.Obj("months" -> jsSorted(monthToJson)(m.months))

  implicit def unwrap(m: Months): SortedSet[Month] = m.months
}

object Month {

  implicit val ordering: SOrdering[Month] = SOrdering.by((m: Month) => (m.year, m.month))

}