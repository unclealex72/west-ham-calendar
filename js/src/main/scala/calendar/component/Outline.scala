package calendar.component

import java.util.Date

import calendar.Page
import dates.{SharedDate, SharedDay}
import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement}
import models.Root

import scala.scalajs.js

/**
  * Created by alex on 04/01/17
  **/
object Outline {
  // shorthand for styles
  case class Props(
                    ctl: RouterCtl[Page],
                    proxy: ModelProxy[Root],
                    maybeSeason: Option[Int],
                    maybeMonth: Option[Int])

  private class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      val today = SharedDay(new Date())
      val root = props.proxy.value
      val maybeHtml = for {
        season <- root.findOrLatestSeason(props.maybeSeason)
        month <- season.findOrFirstMonth(props.maybeMonth, today)
      } yield {
        <.div(
          props.proxy.connect(root => root).apply(proxy => Header(props.ctl, proxy, season.season)),
          props.proxy.connect(root => season.months).apply(proxy => NavBar(props.ctl, season.season, proxy)),
          <.p(s"Month = ${month.date.month}"),
          <.p(s"Today = $today"),
          <.ul(
            month.games.map { game =>
              props.proxy.connect(_ => game).apply(proxy => Game(proxy))
            }
          )
        )
      }
      maybeHtml.getOrElse(<.div())
    }
  }

  private val component = ReactComponentB[Props]("Outline")
    .renderBackend[Backend].build

  def apply(
             ctl: RouterCtl[Page],
             proxy: ModelProxy[Root],
             maybeSeason: Option[Int],
             maybeMonth: Option[Int]): ReactElement =
    component(Props(ctl, proxy, maybeSeason, maybeMonth))
}
