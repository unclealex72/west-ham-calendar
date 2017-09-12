package dates

import java.time.{Instant, ZoneId, ZonedDateTime}

import cats.data.NonEmptyList
import io.circe.{Decoder, Encoder}

/**
  * Created by alex on 10/07/17
  **/
trait ZonedDateTimeFactory {

  def now: ZonedDateTime

  def fromInstant(instant: Instant): ZonedDateTime

  val timeZoneName: String = "Europe/London"

  val zoneId: ZoneId = ZoneId.of(timeZoneName)

  def parse(str: String): Either[NonEmptyList[String], ZonedDateTime]

  val encoder: Encoder[ZonedDateTime]

  val decoder: Decoder[ZonedDateTime]
}
