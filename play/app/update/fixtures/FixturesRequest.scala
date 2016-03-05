package update.fixtures

import json.JsonConverters
import upickle.Js

import scalaz.Scalaz._
import scalaz._

/**
 * Created by alex on 08/03/15.
 */
case class FixturesRequest(season: Int, competitionId: String = "all")

object FixturesRequest extends JsonConverters[FixturesRequest] {

  override def serialise(fr: FixturesRequest): Js.Value =
    Js.Obj("matchSeason" -> Js.Num(fr.season), "competitionID" -> Js.Str(fr.competitionId))

  override def deserialise(value: Js.Value): ValidationNel[String, FixturesRequest] = value.jsObj("FixturesRequest") { fields =>
    val season = fields.mandatory("matchSeason")(_.jsInt)
    val competitionId = fields.mandatory("competitionID")(_.jsStr)
    (season |@| competitionId)(FixturesRequest.apply)
  }
}
