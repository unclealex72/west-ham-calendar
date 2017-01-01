package calendar

/**
  * Created by alex on 07/02/16.
  */

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object CalendarApp extends JSApp {

  override def main(): Unit = {
    import org.scalajs.dom
    dom.console.log("Hello world")
    dom.document.getElementById("root").innerHTML = "Hello World!"
  }
}
