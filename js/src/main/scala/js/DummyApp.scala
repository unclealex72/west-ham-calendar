package js

/**
  * Created by alex on 07/02/16.
  */

import models.Competition
import upickle.default._

import scala.scalajs.js.JSApp

object DummyApp extends JSApp {
  def main(): Unit = {
    val competition: Competition = Competition.FACP
    println(write(competition))
    println(read[Competition](""""FACP"""").isLeague)
  }
}