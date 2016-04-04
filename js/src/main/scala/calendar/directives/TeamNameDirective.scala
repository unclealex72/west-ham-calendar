package calendar.directives

import com.greencatsoft.angularjs._

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcTeamName")
class TeamNameDirective extends ElementDirective with AttributeDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override val templateUrl = at("team-name.html")

  bindings ++= Seq(
    "team" := ""
  )
}