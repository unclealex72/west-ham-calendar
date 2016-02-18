package update.fixtures

import json.JsonConverters
import upickle.Js
import scalaz._
import Scalaz._
/**
 * Created by alex on 08/03/15.
 */
case class FixturesResponse(fixtures: Seq[Fixture])

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

object FixturesResponse extends JsonConverters[FixturesResponse] {

  def jsonToFixture(value: Js.Value): ValidationNel[String, Fixture] = value.jsObj { fields =>
    val matchDate = fields.mandatory("MatchDate", "Cannot find a MatchDate property for a Fixture")(_.jsStr)
    val competitionName = fields.mandatory("CompetitionName", "Cannot find a CompetitionName property for a Fixture")(_.jsStr)
    val homeTeam = fields.mandatory("HomeTeamName", "Cannot find a HomeTeamName property for a Fixture")(_.jsStr)
    val awayTeam = fields.mandatory("AwayTeamName", "Cannot find an AwayTeamName property for a Fixture")(_.jsStr)
    val venue = fields.mandatory("VenueName", "Cannot find a VenueName property for a Fixture")(_.jsStr)
    val homeTeamScore = fields.mandatory("HomeTeamScore", "Cannot find a HomeTeamScore property for a Fixture")(_.jsStr)
    val awayTeamScore = fields.mandatory("AwayTeamScore", "Cannot find an AwayTeamScore property for a Fixture")(_.jsStr)
    val homeShootoutScore = fields.mandatory("HomeShootOutScore", "Cannot find a HomeShootOutScore property for a Fixture")(_.jsStr)
    val awayShootoutScore = fields.mandatory("AwayShootOutScore", "Cannot find an AwayShootOutScore property for a Fixture")(_.jsStr)
    val homeTeamLogo = fields.mandatory("HomeTeamLogo", "Cannot find a HomeTeamLogo property for a Fixture")(_.jsStr)
    val awayTeamLogo = fields.mandatory("AwayTeamLogo", "Cannot find an AwayTeamLogo property for a Fixture")(_.jsStr)
    val competitionLogo = fields.mandatory("CompetitionLogo", "Cannot find a CompetitionLogo property for a Fixture")(_.jsStr)
    val link = fields.optional("MatchCenterPath")(_.jsStr)
    val home = fields.mandatory("IsHomeSide", "Cannot find an IsHomeSide property for a Fixture")(_.jsBool)
    val left = (matchDate |@| competitionName |@| homeTeam |@| awayTeam |@| venue |@| homeTeamScore |@| awayTeamScore).tupled
    val right = (homeShootoutScore |@| awayShootoutScore |@| homeTeamLogo |@| awayTeamLogo |@| competitionLogo |@| link |@| home).tupled
    (left |@| right)((_, _)).map { case (l, r) =>
      Fixture(l._1, l._2, l._3, l._4, l._5, l._6, l._7, r._1, r._2, r._3, r._4, r._5, r._6, r._7)
    }
  }

  def deserialise(value: Js.Value): ValidationNel[String, FixturesResponse] = value.jsObj { fields =>
    fields.mandatory("resultList", "Cannot find a resultList property for a FixturesResponse")(_.jsArr(jsonToFixture)).map(FixturesResponse(_))
  }

  def serialise(fixturesResponse: FixturesResponse): Js.Value = { Js.Null }
}