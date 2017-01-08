package calendar.component

import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement, ReactEventAliases}
import models.Href

/**
  * Created by alex on 04/01/17
  **/
object Link {

  // shorthand for styles
  case class Props(proxy: ModelProxy[Href])

  private class Backend($: BackendScope[Props, Unit]) extends ReactEventAliases {

    def render(props: Props) = {
      val href = props.proxy.value
      <.a(href.text, ^.href := href.url)
    }
  }

  private val component = ReactComponentB[Props]("Link")
    .renderBackend[Backend].build

  def apply(proxy: ModelProxy[Href]): ReactElement =
    component(Props(proxy))
}
