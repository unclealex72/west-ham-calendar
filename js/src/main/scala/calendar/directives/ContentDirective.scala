package calendar.directives

import com.greencatsoft.angularjs._

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcContent")
class ContentDirective extends ElementDirective with TemplatedDirective with UseParentScope with TemplateLocation {

  override val templateUrl = at("content.html")
}