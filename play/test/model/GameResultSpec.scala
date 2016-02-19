package model

import models.{Score, GameResult}
import org.specs2.mutable.Specification

/**
  * Created by alex on 18/02/16.
  */
class GameResultSpec extends Specification {

  "Serialising a game without penalties" should {
    "serialise in the form of home-away" in {
      GameResult(Score(2, 3), None).serialise must be_===("2-3")
    }
  }

  "Serialising a game with penalties" should {
    "serialise in the form of home-away,home-away" in {
      GameResult(Score(2, 3), Some(Score(5, 1))).serialise must be_===("2-3,5-1")
    }
  }

  "Deserialising a string of the form home-away,home-away" should {
    "create a result with penalties" in {
      GameResult.apply("2-3,5-1") must be_===(GameResult(Score(2, 3), Some(Score(5, 1))))
    }
  }

  "Deserialising a string of the form home-away" should {
    "create a result without penalties" in {
      GameResult.apply("2-3") must be_===(GameResult(Score(2, 3)))
    }
  }
}
