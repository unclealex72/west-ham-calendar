package models

import json.JsonConverters
import upickle.Js

import scalaz.Scalaz._
import scalaz._

/**
  * Created by alex on 18/02/16.
  */
case class GameResult(score: Score, shootoutScore: Option[Score] = None) {
  val serialise: String = (Seq(score) ++ shootoutScore).map(s=>s"${s.home}-${s.away}").mkString(",")
  val format: String = score.format + shootoutScore.map(s => s" (${s.format})").getOrElse("")
}
case class Score(home: Int, away: Int) {
  val format: String = s"$home - $away"
}

object GameResult extends JsonConverters[GameResult] {

  private val regex = """^(\d+)-(\d+)(,(\d+)-(\d+))?$""".r("home", "away", "_", "homeShootout", "awayShootout")

  def apply(str: String): GameResult = {
    regex.findFirstMatchIn(str.replaceAll("""\s+""", "")).map { m =>
      def toScore(suffix: String) = Score(m.group("home" + suffix).toInt, m.group("away" + suffix).toInt)
      val result = toScore("")
      val shootoutResult = if (m.start(3) >= 0) Some(toScore("Shootout")) else None
      GameResult(result, shootoutResult)
    }.getOrElse(GameResult(Score(0, 0)))
  }

  private def deserialiseScore(value: Js.Value): ValidationNel[String, Score] = value.jsObj { fields =>
    val home = fields.mandatory("home", "Cannot find a home property for a Score")(_.jsNum).map(_.toInt)
    val away = fields.mandatory("away", "Cannot find an away property for a Score")(_.jsNum).map(_.toInt)
    (home |@| away)(Score.apply)
  }

  override def deserialise(value: Js.Value): ValidationNel[String, GameResult] = value.jsObj { fields =>
    val score = fields.mandatory("score", "Cannot find a score property for a GameResult")(deserialiseScore)
    val shootoutScore = fields.optional("shootoutScore")(deserialiseScore)
    (score |@| shootoutScore)(GameResult.apply)
  }

  private def serialiseScore(s: Score): Js.Value = Js.Obj("home" -> Js.Num(s.home), "away" -> Js.Num(s.away))

  override def serialise(g: GameResult): Js.Value = {
    val fields = Seq("score" -> serialiseScore(g.score)) ++ g.shootoutScore.map("shootoutScore" -> serialiseScore(_))
    Js.Obj(fields :_*)
  }
}

