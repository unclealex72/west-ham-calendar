package models

import json.JsonConverters
import upickle.Js
import Links._

import scala.collection.SortedSet
import scalaz._
import Scalaz._
import scala.math.{Ordering => SOrdering}
/**
  * Javascript for Seasons
  * Created by alex on 16/02/16.
  */
case class Season(season: Int, months: SortedSet[Month])

object Season extends JsonConverters[Season] {
  implicit val ordering: SOrdering[Season] = SOrdering.by(s => s.season)

  def serialise(season: Season): Js.Value = Js.Obj(
    "season" -> Js.Num(season.season),
    "months" -> jsSorted(Month.serialise)(season.months)
  )

  def deserialise(value: Js.Value): ValidationNel[String, Season] = value.jsObj("Season") { fields =>
    val season = fields.mandatory("season")(_.jsInt)
    val months = fields.mandatory("months")(_.jsSorted(Month.deserialise))
    (season |@| months)(Season.apply)
  }
}