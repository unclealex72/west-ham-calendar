package calendar.directives

import com.greencatsoft.angularjs.{ElementDirective, TemplatedDirective, injectable}

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcSidebar")
class SidebarDirective extends ElementDirective with TemplatedDirective with TemplateLocation {

  override val templateUrl = at("sidebar.html")
}