package update.fixtures

import json.JsonCodecs
import upickle.Js

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

object FixturesResponse extends JsonCodecs {

  private def jsonToFixture(value: Js.Value): Either[String, Fixture] = value.jsObj { fields =>
    for {
      matchDate <- fields.mandatory("MatchDate", "Cannot find a MatchDate property for a Fixture")(_.jsStr).right
      competitionName <- fields.mandatory("CompetitionName", "Cannot find a CompetitionName property for a Fixture")(_.jsStr).right
      homeTeam <- fields.mandatory("HomeTeamName", "Cannot find a HomeTeamName property for a Fixture")(_.jsStr).right
      awayTeam <- fields.mandatory("AwayTeamName", "Cannot find an AwayTeamName property for a Fixture")(_.jsStr).right
      venue <- fields.mandatory("VenueName", "Cannot find a VenueName property for a Fixture")(_.jsStr).right
      homeTeamScore <- fields.mandatory("HomeTeamScore", "Cannot find a HomeTeamScore property for a Fixture")(_.jsStr).right
      awayTeamScore <- fields.mandatory("AwayTeamScore", "Cannot find an AwayTeamScore property for a Fixture")(_.jsStr).right
      homeShootoutScore <- fields.mandatory("HomeShootOutScore", "Cannot find a HomeShootOutScore property for a Fixture")(_.jsStr).right
      awayShootoutScore <- fields.mandatory("AwayShootOutScore", "Cannot find an AwayShootOutScore property for a Fixture")(_.jsStr).right
      homeTeamLogo <- fields.mandatory("HomeTeamLogo", "Cannot find a HomeTeamLogo property for a Fixture")(_.jsStr).right
      awayTeamLogo <- fields.mandatory("AwayTeamLogo", "Cannot find an AwayTeamLogo property for a Fixture")(_.jsStr).right
      competitionLogo <- fields.mandatory("CompetitionLogo", "Cannot find a CompetitionLogo property for a Fixture")(_.jsStr).right
      link <- fields.optional("MatchCenterPath")(_.jsStr).right
      home <- fields.mandatory("IsHomeSide", "Cannot find an IsHomeSide property for a Fixture")(_.jsBool).right
    } yield {
      Fixture(
        matchDate, competitionName, homeTeam, awayTeam, venue, homeTeamScore, awayTeamScore, homeShootoutScore, awayShootoutScore,
        homeTeamLogo, awayTeamLogo, competitionLogo, link, home)
    }
  }

  private def jsonToFixturesResponse(value: Js.Value): Either[String, FixturesResponse] = value.jsObj { fields =>
    for {
      fixtures <- fields.mandatory("resultList", "Cannot find a resultList property for a FixturesResponse")(_.jsArr(jsonToFixture)).right
    } yield FixturesResponse(fixtures)
  }

  implicit val fixturesResponse2Reader: upickle.default.Reader[Either[String, FixturesResponse]] =
    upickle.default.Reader[Either[String, FixturesResponse]] { case js =>
      jsonToFixturesResponse(js)
    }
}