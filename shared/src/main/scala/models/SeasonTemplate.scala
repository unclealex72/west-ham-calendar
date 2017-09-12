package models

import io.circe.{Decoder, Encoder}

import scala.math.Ordering
/**
  * Javascript for Seasons
  * Created by alex on 16/02/16.
  */
case class SeasonTemplate[D](season: Int, months: Set[MonthTemplate[D]])

object SeasonTemplate {

  implicit def seasonOrdering[D]: Ordering[SeasonTemplate[D]] = Ordering.by(s => s.season)

  implicit def seasonEncoder[D](implicit ev: Encoder[D]): Encoder[SeasonTemplate[D]] =
    Encoder.forProduct2("season", "months")(s => (s.season, s.months))

  implicit def seasonDecoder[D](implicit ev: Decoder[D]): Decoder[SeasonTemplate[D]] =
    Decoder.forProduct2("season", "months")(SeasonTemplate.apply)
}