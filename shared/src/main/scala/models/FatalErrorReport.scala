package models

import dates.SharedDate
import json.JsonConverters
import upickle.Js

import scalaz._
import Scalaz._

/**
  * Created by alex on 05/03/16.
  */
case class FatalErrorReport(id: Long, at: SharedDate, links: Links[FatalErrorReportRel])

sealed trait FatalErrorReportRel extends Rel
object FatalErrorReportRel extends RelEnum[FatalErrorReportRel] {
  val values = findValues

  object MESSAGE extends Rel_("message") with FatalErrorReportRel
}

case class FatalErrorReports(fatalErrorReports: Seq[FatalErrorReport])

object FatalErrorReport extends JsonConverters[FatalErrorReport] {
  override def serialise(fre: FatalErrorReport): Js.Value = Js.Obj(
    "id" -> Js.Num(fre.id), "at" -> dateToJson(fre.at), "links" -> Links.linksToJson(fre.links)
  )

  override def deserialise(value: Js.Value): ValidationNel[String, FatalErrorReport] = value.jsObj("FatalErrorReport") { fields =>
    val id = fields.mandatory("id")(_.jsLong)
    val at = fields.mandatory("at")(_.jsDate)
    val links = fields.mandatory("links")(Links.jsonToLinks(FatalErrorReportRel))
    (id |@| at |@| links)(FatalErrorReport.apply)
  }
}