package models

import dates.SharedDate
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
case class Month(date: SharedDate, games: Seq[GameRow]) {

  def alterAttendance(gameRow: GameRow, newAttendance: Boolean): Month = {
    games.zipWithIndex.find(gi => gi._1.id == gameRow.id).map(_._2) match {
      case Some(idx) => this.copy(games = games.updated(idx, gameRow.updateAttendance(newAttendance)))
      case _ => this
    }
  }
}

object Month extends JsonConverters[Month] {

  implicit val ordering: SOrdering[Month] = SOrdering.by((m: Month) => (m.date.year, m.date.month))

  def deserialise(value: Js.Value): ValidationNel[String, Month] = value.jsObj("Month") { fields =>
    val date = fields.mandatory("date")(_.jsDate)
    val games = fields.mandatory("games")(_.jsArr(GameRow.deserialise))
    (date |@| games)(Month.apply)
  }

  def serialise(m: Month): Js.Value = Js.Obj (
    "date" -> dateToJson(m.date),
    "games" -> jsArr(GameRow.serialise)(m.games)
  )
}