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
case class Seasons(seasons: SortedSet[Season], links: Links[SeasonsRel])

sealed trait SeasonsRel extends Rel
object SeasonsRel extends RelEnum[SeasonsRel] {
  val values = findValues
}

case class Season(season: Int, links: Links[SeasonRel])

object Season {
  implicit val ordering: SOrdering[Season] = SOrdering.by(s => s.season)
}

sealed trait SeasonRel extends Rel
object SeasonRel extends RelEnum[SeasonRel] {
  val values = findValues

  object MONTHS extends Rel_("months") with SeasonRel
}

object Seasons extends JsonConverters[Seasons] {

  private def seasonToJson(season: Season): Js.Value = Js.Obj(
    "season" -> Js.Num(season.season),
    "links" -> linksToJson(season.links)
  )

  def serialise(seasons: Seasons): Js.Value = Js.Obj("seasons" -> jsSorted(seasonToJson)(seasons.seasons))

  private def jsonToSeason(value: Js.Value): ValidationNel[String, Season] = value.jsObj("Season") { fields =>
    val season = fields.mandatory("season")(_.jsInt)
    val links = fields.optionalDefault("links")(jsonToLinks(SeasonRel))(Links[SeasonRel]())
    (season |@| links)(Season.apply)
  }

  def deserialise(value: Js.Value): ValidationNel[String, Seasons] = value.jsObj("Seasons") { fields =>
    val seasons = fields.mandatory("seasons")(_.jsSorted(jsonToSeason))
    val links = fields.optionalDefault("links")(jsonToLinks(SeasonsRel))(Links[SeasonsRel]())
    (seasons |@| links)(Seasons.apply)
  }

  implicit def unwrap(s: Seasons): SortedSet[Season] = s.seasons
}