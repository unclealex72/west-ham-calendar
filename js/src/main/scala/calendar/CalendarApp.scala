package calendar

/**
  * Created by alex on 07/02/16.
  */

import calendar.ajax.Ajax
import calendar.circuit.CalendarCircuit
import calendar.component.Outline
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router.{BaseUrl, Redirect, Resolution, Router, RouterConfigDsl, RouterCtl}
import japgolly.scalajs.react.vdom.ReactTagOf
import japgolly.scalajs.react.vdom.prefix_<^._
import models._
import org.scalajs.dom.html.Div

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.ExecutionContext.Implicits.global

@JSExport
object CalendarApp extends JSApp {

  override def main(): Unit = {
    import org.scalajs.dom
    dom.console.log("Hello world")

    Ajax.get[Root]("/entry").foreach { root =>
      val circuit = new CalendarCircuit(root)

      def outlineFactory(maybeSeason: Option[Int], maybeMonth: Option[Int])(ctl: RouterCtl[Page]) =
        circuit.connect(root => root).apply(proxy => Outline(ctl, proxy, maybeSeason, maybeMonth))

      def layout(c: RouterCtl[Page], r: Resolution[Page]): ReactTagOf[Div] = {
        <.div(r.render())
      }

      val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
        import dsl._
        import dsl.{ root => _root }

        val emptyRule = staticRoute(_root, GamesPage.default) ~> renderR(outlineFactory(None, None))
        val seasonMonthRoute = dynamicRouteCT(("#season" / int.option ~ ( "/month" / int).option).caseClass[GamesPage])
        val seasonMonthRule = seasonMonthRoute ~> dynRenderR {
          case (GamesPage(season, month), ctl) => outlineFactory(season, month)(ctl)
        }
        (emptyRule | seasonMonthRule).notFound(redirectToPage(GamesPage.default)(Redirect.Replace))
      }.renderWith(layout)

      val router = Router(BaseUrl.until_#, routerConfig)
      ReactDOM.render(router(), dom.document.getElementById("root"))
    }
  }

}

sealed trait Page
case class GamesPage(season: Option[Int], month: Option[Int]) extends Page
object GamesPage {
  val default: GamesPage = GamesPage(None, None)

  def apply(season: Int, month: Int): GamesPage = GamesPage(Some(season), Some(month))
  def apply(season: Int): GamesPage = GamesPage(Some(season), None)
}