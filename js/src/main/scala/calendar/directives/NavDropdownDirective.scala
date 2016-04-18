package calendar.directives

import com.greencatsoft.angularjs._
import org.querki.jquery._
import org.scalajs.dom.Element

import scala.language.implicitConversions
import scala.scalajs.js

/**
  * A small directive for materialize css nav dropdowns.
  * Created by alex on 01/04/16.
  */
@injectable("hcNavDropdown")
class NavDropdownDirective extends AttributeDirective {

  import JQueryWithDropdown._

  override def link(scope: ScopeType, elems: Seq[Element], attrs: Attributes): Unit = {
    elems.foreach { elem =>
      $(elem).dropdown()
    }
  }
}


//noinspection NotImplementedCode
@js.native
trait JQueryDropdown extends JQuery {
  def dropdown(): this.type = ???
}

object JQueryWithDropdown {
  implicit def jq2dropdown(jq: JQuery): JQueryDropdown =
    jq.asInstanceOf[JQueryDropdown]
}