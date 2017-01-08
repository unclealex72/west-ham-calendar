package calendar.component

import calendar.circuit.AlterAttendance
import diode.react.ModelProxy
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactElement, ReactEventAliases}
import models.GameRow

/**
  * Created by alex on 04/01/17
  **/
object Game {
  // shorthand for styles
  case class Props(proxy: ModelProxy[GameRow])

  private class Backend($: BackendScope[Props, Unit]) extends ReactEventAliases {

    def render(props: Props) = {
      val game = props.proxy.value
      <.li(
        s"${game.at} - ${game.opponents}",
        game.attended.map { attended =>
          <.input(
            ^.`type` := "checkbox",
            ^.checked := attended,
            ^.onChange ==> {
              (e: ReactEventI) => Callback.log(s"${game.opponents} -> ${e.target.checked}") >> props.proxy.dispatchCB(AlterAttendance(game, e.target.checked))
            }
          )
        },
        game.attended.map { attended =>
          <.span(s"$attended")
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("Game")
    .renderBackend[Backend].build

  def apply(proxy: ModelProxy[GameRow]): ReactElement =
    component(Props(proxy))
}
