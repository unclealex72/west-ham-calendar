package calendar.directives

import calendar.views.MonthView
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core.Scope
import org.scalajs.dom.Element

import scala.scalajs.js
import org.querki.jquery._

import scala.scalajs.js.{Any, Dictionary, Function2, JSON}

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcNav")
class NavDirective extends ElementDirective with AttributeDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override type ScopeType = NavDirectiveScope

  override val templateUrl = at("nav.html")

  bindings ++= Seq(
    "months" := "",
    "search" := "",
    "authenticationLink" := "",
    "user" := ""
  )

  override def link($scope: ScopeType, elems: Seq[Element], attrs: Attributes): Unit = {
    val scrollToMonth = new Function2[String, MonthView, js.Any] {
      override def apply(topNavId: String, month: MonthView): js.Any = {
        val navbarHeight = $(s"#$topNavId").height()
        val monthTop = $(s"#${month.id}").offset().top
        $("html, body").animate(Dictionary[js.Any]("scrollTop" -> (monthTop - navbarHeight)), "fast")
        false
      }
    }
    $scope.scrollToMonth = scrollToMonth
  }
}

@js.native
trait NavDirectiveScope extends Scope {

  var scrollToMonth: js.Function
}