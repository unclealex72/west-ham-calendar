package calendar.services

import calendar.views.GameView
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import models.GameRow

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by alex on 22/02/16.
  */
@injectable("attendanceService")
class AttendanceService(ajax: AjaxService) extends Service {

  def updateAttendance(gameView: GameView): Future[Option[Boolean]] = {
    val optionalUrl = (if (gameView.attended) gameView.attendUrl else gameView.unattendUrl).toOption
    optionalUrl match {
      case Some(url) => for {
        gameRow <- ajax.put[GameRow](url)
      } yield gameRow.attended
      case _ => Future.successful(None)
    }
  }
}

@injectable("attendanceService")
class AttendanceServiceFactory(ajaxService: AjaxService) extends Factory[AttendanceService] {
  override def apply() = new AttendanceService(ajaxService)
}