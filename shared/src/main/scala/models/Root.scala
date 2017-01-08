package models

import json.JsonConverters
import upickle.Js
import Links._

import scalaz._
import Scalaz._
import scala.collection.SortedSet
/**
  * Created by alex on 16/02/16.
  */
case class Root(user: Option[String], seasons: Seq[Season], links: Links[RootRel]) {

  def alterAttendance(gameRow: GameRow, newAttendance: Boolean): Root = {
    this.copy(seasons = seasons.map(_.alterAttendance(gameRow, newAttendance)))
  }

  /**
    * Find the season with the given year or the latest season if none match.
    * @param maybeSeason
    * @return
    */
  def findOrLatestSeason(maybeSeason: Option[Int]): Option[Season] = {
    val selectedSeason: Option[Season] = maybeSeason.flatMap(s => seasons.find(season => season.season == s))
    selectedSeason.orElse(seasons.lastOption)
  }

}

sealed trait RootRel extends Rel
object RootRel extends RelEnum[RootRel] {
  val values = findValues

  object LOGIN extends Rel_("login") with RootRel
  object LOGOUT extends Rel_("logout") with RootRel
}

object Root extends JsonConverters[Root] {

  def serialise(entry: Root): Js.Value = {
    Js.Obj(entry.user.map(u => "user" -> Js.Str(u)).toSeq ++ Seq(
      "seasons" -> jsArr(Season.serialise)(entry.seasons),
      "links" -> linksToJson(entry.links)) :_*)
  }

  def deserialise(value: Js.Value): ValidationNel[String, Root] = value.jsObj("Entry") { fields =>
    (fields.optional("user")(_.jsStr) |@|
      fields.mandatory("seasons")(_.jsArr(Season.deserialise)) |@|
      fields.optionalDefault("links")(jsonToLinks(RootRel))(Links[RootRel]()))(Root.apply)
  }
}