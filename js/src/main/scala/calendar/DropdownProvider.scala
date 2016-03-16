package calendar

import com.greencatsoft.angularjs.{Factory, injectable}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * Created by alex on 16/03/16.
  */
@injectable("dropdownProvider")
class DropdownProvider {

  def apply(text: Any): DropdownBuilder = new DropdownBuilder(text)
}

@injectable("dropdownProvider")
class DropdownProviderFactory extends Factory[DropdownProvider] {
  override def apply() = new DropdownProvider
}

class DropdownBuilder(var text: Any) {

  def click(callback: () => Unit): Dropdown = {
    val jsClick: js.Function0[Unit] = callback
    new Dropdown(text.toString, click = Some(jsClick).orUndefined)
  }
  def href(link: String): Dropdown = new Dropdown(text.toString, href = Some(link).orUndefined)
}

@ScalaJSDefined
class Dropdown(
                var text: String,
                var click: js.UndefOr[js.Function0[Unit]] = js.undefined,
                var href: js.UndefOr[String] = js.undefined) extends js.Object