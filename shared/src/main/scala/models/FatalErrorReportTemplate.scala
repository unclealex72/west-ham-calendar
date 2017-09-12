package models

import io.circe.{Decoder, Encoder}

import scala.collection.immutable

/**
  * Created by alex on 05/03/16.
  */
case class FatalErrorReportTemplate[D](id: Long, at: D, links: Links[FatalErrorReportRel])

sealed trait FatalErrorReportRel extends Rel
object FatalErrorReportRel extends RelEnum[FatalErrorReportRel] {
  val values: immutable.IndexedSeq[FatalErrorReportRel] = findValues

  object MESSAGE extends Rel_("message") with FatalErrorReportRel
}

case class FatalErrorReports[D](fatalErrorReports: Seq[FatalErrorReportTemplate[D]])

object FatalErrorReportTemplate {

  implicit def fatalErrorReportEncoder[D](implicit ev: Encoder[D]): Encoder[FatalErrorReportTemplate[D]] = {
    Encoder.forProduct3("id", "at", "links")(f => (f.id, f.at, f.links))
  }

  implicit def fatalErrorReportDecoder[D](implicit ev: Decoder[D]): Decoder[FatalErrorReportTemplate[D]] = {
    Decoder.forProduct3("id", "at", "links")(FatalErrorReportTemplate.apply)
  }
}

object FatalErrorReports {
  implicit def fatalErrorReportsEncoder[D](implicit ev: Encoder[D]): Encoder[FatalErrorReports[D]] = {
    Encoder.forProduct1("fatalErrorReports")(_.fatalErrorReports)
  }

  implicit def fatalErrorReportsDecoder[D](implicit ev: Decoder[D]): Decoder[FatalErrorReports[D]] = {
    Decoder.forProduct1("fatalErrorReports")(FatalErrorReports.apply)
  }
}