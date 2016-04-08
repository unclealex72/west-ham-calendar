package calendar.directives

import com.greencatsoft.angularjs._

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcNav")
class NavDirective extends ElementDirective with AttributeDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override val templateUrl = at("nav.html")

  bindings ++= Seq(
    "months" := "",
    "search" := "",
    "authenticationLink" := "",
    "user" := ""
  )

}