package models

import io.circe.{Decoder, Encoder}

import scala.math.Ordering

/**
  * Created by alex on 20/02/16.
  */
case class MonthTemplate[D](date: D, games: Set[GameRow[D]])

object MonthTemplate {

  implicit def monthOrdering[D](implicit dateOrdering: Ordering[D]): Ordering[MonthTemplate[D]] = Ordering.by(_.date)

  implicit def monthEncoder[D](implicit ev: Encoder[D]): Encoder[MonthTemplate[D]] =
    Encoder.forProduct2("date", "games")(m => (m.date, m.games))

  implicit def monthDecoder[D](implicit ev: Decoder[D]): Decoder[MonthTemplate[D]] =
    Decoder.forProduct2("date", "games")(MonthTemplate.apply[D])
}