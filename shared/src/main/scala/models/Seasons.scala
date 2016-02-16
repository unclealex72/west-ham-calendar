package models

import json.JsonCodecs
import upickle.Js
import Links._

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
}

object Seasons extends JsonCodecs {

  private def seasonToJson(season: Season): Js.Value = Js.Obj(
    "season" -> Js.Num(season.season),
    "links" -> linksToJson(season.links)
  )

  private def seasonsToJson(seasons: Seasons): Js.Value = Js.Obj("seasons" -> Js.Arr(seasons.seasons.map(seasonToJson) :_*))

  implicit val seasonsWriter: upickle.default.Writer[Seasons] = upickle.default.Writer[Seasons] { case value =>
    seasonsToJson(value)
  }

  private def jsonToSeason(value: Js.Value): Either[String, Season] = value.jsObj { fields =>
    for {
      season <- fields.mandatory("season", "Cannot find a season property for a Season")(_.jsNum).right
      links <- fields.optionalDefault("links")(jsonToLinks(SeasonRel))(Links[SeasonRel]()).right
    } yield Season(season.toInt, links)
  }

  private def jsonToSeasons(value: Js.Value): Either[String, Seasons] = value.jsObj { fields =>
    for {
      seasons <- fields.mandatory("seasons", "Cannot find a seasons property for a Seasons")(_.jsArr(jsonToSeason)).right
      links <- fields.optionalDefault("links")(jsonToLinks(SeasonsRel))(Links[SeasonsRel]()).right
    } yield Seasons(seasons, links)
  }

  implicit val seasonsReader: upickle.default.Reader[Either[String, Seasons]] =
    upickle.default.Reader[Either[String, Seasons]] { case value =>
    jsonToSeasons(value)
  }
}