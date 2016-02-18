package calendar

import com.greencatsoft.angularjs.core.{Location, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import models.{Seasons, Entry}
import models.EntryRel.SEASONS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

/**
  * Created by alex on 17/02/16.
  */
@JSExport
@injectable("calendarCtrl")
class CalendarCtrl(scope: CalendarScope, ajax: AjaxService) extends AbstractController[CalendarScope](scope){

  for {
    entry <- ajax.get[Entry]("/entry")
    seasons <- ajax.get[Seasons](entry.links.required(SEASONS))
  } yield {
    seasons.seasons.foreach { season => println(season.season) }
  }

}

@js.native
trait CalendarScope extends Scope {

  var seasons: Seq[Int] = js.native
}
