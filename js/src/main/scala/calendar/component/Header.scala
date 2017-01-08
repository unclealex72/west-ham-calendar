package calendar.component

import calendar.{GamesPage, Page}
import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactElement, ReactEventAliases}
import models.{Root, RootRel}

/**
  * Created by alex on 04/01/17
  **/
object Header {

  private val linkTexts: Map[RootRel, String] = Map(RootRel.LOGIN -> "Login", RootRel.LOGOUT -> "Logout")

  // shorthand for styles
  case class Props(
                    ctl: RouterCtl[Page],
                    proxy: ModelProxy[Root],
                    year: Int)

  private class Backend($: BackendScope[Props, Unit]) extends ReactEventAliases {

    def onSeasonChange(props: Props)(e: ReactEventI): Callback = {
      props.ctl.set(GamesPage(Integer.parseInt(e.target.value)))
    }

    def render(props: Props) = {
      val root = props.proxy.value
      val seasons = props.proxy.value.seasons
      <.div(
        <.select(
          ^.onChange ==> onSeasonChange(props),
          ^.value := props.year,
          seasons.map(season =>
            <.option(season.season)
          )
        ),
        <.span(s"User = ${root.user.getOrElse("Nobody")}"),
        for {
          href <- root.links.render(linkTexts)
        } yield {
          props.proxy.connect(_ => href).apply(proxy => Link(proxy))
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("Header")
    .renderBackend[Backend].build

  def apply(
             ctl: RouterCtl[Page],
             proxy: ModelProxy[Root],
             season: Int): ReactElement =
    component(Props(ctl, proxy, season))
}
