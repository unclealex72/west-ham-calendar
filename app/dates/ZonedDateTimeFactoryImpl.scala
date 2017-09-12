package dates
import java.time.format.DateTimeFormatter
import java.time.{Clock, Instant, ZonedDateTime}

import cats.data.NonEmptyList
import io.circe.{Decoder, Encoder}

import scala.util.Try
import cats.syntax.either._

/**
  * Created by alex on 12/07/17
  **/
class ZonedDateTimeFactoryImpl extends ZonedDateTimeFactory {

  val clock: Clock = Clock.systemUTC()

  override def now: ZonedDateTime = fromInstant(clock.instant())

  override def fromInstant(instant: Instant): ZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)

  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zoneId)

  val encoder: Encoder[ZonedDateTime] = Encoder.encodeString.contramap(_.format(formatter))
  val decoder: Decoder[ZonedDateTime] = Decoder.decodeString.emap(parseDate)

  private def parseDate(str: String): Either[String, ZonedDateTime] =
    Try(ZonedDateTime.parse(str, formatter)).toEither.leftMap(_.getMessage)

  def parse(str: String): Either[NonEmptyList[String], ZonedDateTime] =
    parseDate(str).leftMap(e => NonEmptyList.of(e))
}
