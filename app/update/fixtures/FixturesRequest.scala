package update.fixtures

/**
 * Created by alex on 08/03/15.
 */
case class FixturesRequest(season: Int, competitionId: String = "all")

object FixturesRequest {

  import argonaut._, Argonaut._

  implicit def FixturesRequestCodecJson: CodecJson[FixturesRequest] =
    casecodec2(FixturesRequest.apply, FixturesRequest.unapply)("matchSeason", "competitionID")
}
