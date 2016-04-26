package calendar.directives

import calendar.services.{AttendanceService, WatcherService}
import calendar.views.{GameView, JsTicketType}
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core.Scope
import org.scalajs.dom.Element

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

/**
  * Created by alex on 01/04/16.
  */
@injectable("hcGame")
class GameDirective(watcher: WatcherService, attendance: AttendanceService) extends ElementDirective with TemplatedDirective with IsolatedScope with TemplateLocation {

  override type ScopeType = GameDirectiveScope
  override val templateUrl = at("game.html")

  import ExtraScopeImplicits._

  bindings ++= Seq(
    "currentTicketType" := "",
    "game" := ""
  )

  override def link($scope: ScopeType, elems: Seq[Element], attrs: Attributes): Unit = {
    watcher.on($scope)(_.game.attended) { newAttended => oldAttended =>
      attendance.updateAttendance($scope.game) onSuccess {
        case Some(newAttendance) =>
          if ($scope.game.attended != newAttendance) {
            $scope.$apply {
              $scope.game.attended = newAttendance
            }
          }
        case None =>
      }
    }
  }


  // Add support for one way Angular bindings using :<
  object ExtraScopeImplicits {

    case class OneWayBinding(name: String, attribute: String = "") extends ScopeBinding("<")

    implicit class ExtraBindingBuilder(name: String) {
      def :<(attribute: String = ""): ScopeBinding = OneWayBinding(name, attribute)
    }
  }

}

@js.native
trait GameDirectiveScope extends Scope {
  var game: GameView = js.native
  var currentTicketType: JsTicketType = js.native
}