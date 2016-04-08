package calendar.directives

import com.greencatsoft.angularjs.{ElementDirective, IsolatedScope, TemplatedDirective, injectable}

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcMonth")
class MonthDirective extends ElementDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override val templateUrl = at("month.html")

  bindings ++= Seq(
    "month" := "",
    "search" := ""
  )

}