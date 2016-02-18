package models

import json.JsonConverters
import upickle.Js
import Links._

import scalaz._
import Scalaz._
/**
  * Javascript for Seasons
  * Created by alex on 16/02/16.
  */
case class Seasons(seasons: Seq[Season], links: Links[SeasonsRel])

sealed trait SeasonsRel extends Rel
object SeasonsRel extends RelEnum[SeasonsRel] {
  val values = findValues
}

case class Season(season: Int, links: Links[SeasonRel])

sealed trait SeasonRel extends Rel
object SeasonRel extends RelEnum[SeasonRel] {
  val values = findValues

  object GAMES extends Rel_("games") with SeasonRel
}

object Seasons extends JsonConverters[Seasons] {

  private def seasonToJson(season: Season): Js.Value = Js.Obj(
    "season" -> Js.Num(season.season),
    "links" -> linksToJson(season.links)
  )

  def serialise(seasons: Seasons): Js.Value = Js.Obj("seasons" -> Js.Arr(seasons.seasons.map(seasonToJson) :_*))

  private def jsonToSeason(value: Js.Value): ValidationNel[String, Season] = value.jsObj { fields =>
    val season = fields.mandatory("season", "Cannot find a season property for a Season")(_.jsNum).map(_.toInt)
    val links = fields.optionalDefault("links")(jsonToLinks(SeasonRel))(Links[SeasonRel]())
    (season |@| links)(Season.apply)
  }

  def deserialise(value: Js.Value): ValidationNel[String, Seasons] = value.jsObj { fields =>
    val seasons = fields.mandatory("seasons", "Cannot find a seasons property for a Seasons")(_.jsArr(jsonToSeason))
    val links = fields.optionalDefault("links")(jsonToLinks(SeasonsRel))(Links[SeasonsRel]())
    (seasons |@| links)(Seasons.apply)
  }

}