package update.fixtures

import json.{JsonDeserialisers, JsonConverters}
import upickle.Js
import scalaz._
import Scalaz._
/**
 * Created by alex on 08/03/15.
 */
case class FixturesResponse(fixtures: Seq[Fixture], isSuccess: Boolean)

case class Fixture(
  matchDate: String,
  competitionName: String,
  homeTeam: String,
  awayTeam: String,
  venue: String,
  homeTeamScore: String,
  awayTeamScore: String,
  homeShootoutScore: String,
  awayShootoutScore: String,
  homeTeamLogo: String,
  awayTeamLogo: String,
  competitionLogo: String,
  link: Option[String],
  home: Boolean) {

}

object FixturesResponse extends JsonDeserialisers[FixturesResponse] {

  def jsonToFixture(value: Js.Value): ValidationNel[String, Fixture] = value.jsObj("Fixture") { fields =>
    val matchDate = fields.mandatory("MatchDate")(_.jsStr)
    val competitionName = fields.mandatory("CompetitionName")(_.jsStr)
    val homeTeam = fields.mandatory("HomeTeamName")(_.jsStr)
    val awayTeam = fields.mandatory("AwayTeamName")(_.jsStr)
    val venue = fields.mandatory("VenueName")(_.jsStr)
    val homeTeamScore = fields.mandatory("HomeTeamScore")(_.jsStr)
    val awayTeamScore = fields.mandatory("AwayTeamScore")(_.jsStr)
    val homeShootoutScore = fields.mandatory("HomeShootOutScore")(_.jsStr)
    val awayShootoutScore = fields.mandatory("AwayShootOutScore")(_.jsStr)
    val homeTeamLogo = fields.mandatory("HomeTeamLogo")(_.jsStr)
    val awayTeamLogo = fields.mandatory("AwayTeamLogo")(_.jsStr)
    val competitionLogo = fields.mandatory("CompetitionLogo")(_.jsStr)
    val link = fields.optional("MatchCenterPath")(_.jsStr)
    val home = fields.mandatory("IsHomeSide")(_.jsBool)
    val left = (matchDate |@| competitionName |@| homeTeam |@| awayTeam |@| venue |@| homeTeamScore |@| awayTeamScore).tupled
    val right = (homeShootoutScore |@| awayShootoutScore |@| homeTeamLogo |@| awayTeamLogo |@| competitionLogo |@| link |@| home).tupled
    (left |@| right)((_, _)).map { case (l, r) =>
      Fixture(l._1, l._2, l._3, l._4, l._5, l._6, l._7, r._1, r._2, r._3, r._4, r._5, r._6, r._7)
    }
  }

  def deserialise(value: Js.Value): ValidationNel[String, FixturesResponse] = value.jsObj("FixturesResponse") { fields =>
    val fixtures = fields.mandatory("resultList")(_.jsArr(jsonToFixture))
    val isSuccess = fields.optionalDefault("isSuccess")(_.jsBool)(false)
    (fixtures |@| isSuccess)(FixturesResponse(_, _))
  }
}