package update.fixtures

import upickle.Js

/**
 * Created by alex on 08/03/15.
 */
case class FixturesRequest(season: Int, competitionId: String = "all")

object FixturesRequest {

  implicit val fixturesRequest2Writer: upickle.default.Writer[FixturesRequest] = upickle.default.Writer[FixturesRequest] { fixturesRequest =>
    Js.Obj("season" -> Js.Num(fixturesRequest.season), "competitionId" -> Js.Str(fixturesRequest.competitionId))
  }
}
