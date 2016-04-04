package calendar.directives

import com.greencatsoft.angularjs.{ElementDirective, IsolatedScope, TemplatedDirective, injectable}

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcTeamLogo")
class TeamLogoDirective extends ElementDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override val templateUrl = at("team-logo.html")

  bindings ++= Seq(
    "team" := ""
  )
}