package models

import io.circe.{Decoder, Encoder}

/**
  * Created by alex on 18/02/16.
  */
case class GameResult(score: Score, maybeShootoutScore: Option[Score] = None) {
  val serialise: String = (Seq(score) ++ maybeShootoutScore).map(s=>s"${s.home}-${s.away}").mkString(",")
  val format: String = score.format + maybeShootoutScore.map(s => s" (${s.format})").getOrElse("")
}
case class Score(home: Int, away: Int) {
  val format: String = s"$home - $away"
}

object GameResult {

  private val regex = """^(\d+)-(\d+)(,(\d+)-(\d+))?$""".r("home", "away", "_", "homeShootout", "awayShootout")

  def apply(str: String): GameResult = {
    regex.findFirstMatchIn(str.replaceAll("""\s+""", "")).map { m =>
      def toScore(suffix: String) = Score(m.group("home" + suffix).toInt, m.group("away" + suffix).toInt)
      val result = toScore("")
      val shootoutResult = if (m.start(3) >= 0) Some(toScore("Shootout")) else None
      GameResult(result, shootoutResult)
    }.getOrElse(GameResult(Score(0, 0)))
  }

  implicit val gameResultEncoder: Encoder[GameResult] = Encoder.forProduct2("score", "shootoutScore")(f => (f.score, f.maybeShootoutScore))
  implicit val gameResultDecoder: Decoder[GameResult] = Decoder.forProduct2("score", "shootoutScore")(GameResult.apply)

}

object Score {
  implicit val scoreEncoder: Encoder[Score] = Encoder.forProduct2("home", "away")(f => (f.home, f.away))
  implicit val scoreDecoder: Decoder[Score] = Decoder.forProduct2("home", "away")(Score.apply)
}

