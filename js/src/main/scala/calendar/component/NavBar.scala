package calendar.component

import java.util.Date

import calendar.{GamesPage, Page}
import dates.SharedDay
import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement}
import models.{Month, Root}

/**
  * Created by alex on 04/01/17
  **/
object NavBar {

  private val monthNames: Map[Int, String] = Map(
    1 -> "January",
    2 -> "February",
    3 -> "March",
    4 -> "April",
    5 -> "May",
    6 -> "June",
    7 -> "July",
    8 -> "August",
    9 -> "September",
    10 -> "October",
    11 -> "November",
    12 -> "December")

  // shorthand for styles
  case class Props(
                    ctl: RouterCtl[Page],
                    season: Int,
                    proxy: ModelProxy[Seq[Month]])

  private class Backend($: BackendScope[Props, Unit]) {

    def render(props: Props) = {
      val months = props.proxy.value
      <.ol(
        for {
          month <- months
          monthName <- monthNames.get(month.date.month)
        } yield {
          <.li(
            props.ctl.link(GamesPage(props.season, month.date.month))(monthName)
          )
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("NavBar")
    .renderBackend[Backend].build

  def apply(
             ctl: RouterCtl[Page],
             season: Int,
             proxy: ModelProxy[Seq[Month]]): ReactElement =
    component(Props(ctl, season, proxy))
}
