package calendar

import com.greencatsoft.angularjs.{Service, Factory, injectable}
import models.GameRow

import scala.concurrent.Future

/**
  * Created by alex on 22/02/16.
  */
@injectable("attendanceService")
class AttendanceService(ajax: AjaxService) extends Service {

  def alterAttendance(attendanceUrl: String): Future[GameRow] = ajax.put[GameRow](attendanceUrl)
}

@injectable("attendanceService")
class AttendanceServiceFactory(ajaxService: AjaxService) extends Factory[AttendanceService] {
  override def apply() = new AttendanceService(ajaxService)
}