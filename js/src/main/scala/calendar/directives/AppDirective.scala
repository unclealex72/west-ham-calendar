package calendar.directives

import com.greencatsoft.angularjs.{ElementDirective, TemplatedDirective, injectable}

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcApp")
class AppDirective extends ElementDirective with TemplatedDirective with TemplateLocation {

  override val templateUrl = at("app.html")
}