package dates

import io.circe.{Decoder, Encoder}

/**
  * A typeclass around dates that allow dates to be handled in the same way in the JVM and in JS.
  **/
trait DateLike[D] {

  /**
    * Serialise this date.
    * @param d The date to serialise.
    * @return An ISO 8601 representation of this date.
    */
  def serialise(d: D): String

  /**
    * Try to deserialise a date.
    * @param s An string representation of this date.
    * @return Left if the string is not in ISO 8601 date format, or the date the string represents otherwise.
    */
  def deserialise(s: String): Either[String, D]

  /**
    * Compare two dates.
    * @param d1 The first date.
    * @param d2 The second date.
    * @return True if d1 < d2, false otherwise.
    */
  def lt(d1: D, d2: D): Boolean

  /**
    * Format a date.
    * @param d The date to format.
    * @param fmt The format string to use.
    * @return The date formatted using the format string.
    */
  def format(d: D, fmt: String): String
}

/**
  * Implicits for DateLike.
  */
object DateLike {

  implicit def dateLikeEncoder[D](implicit ev: DateLike[D]): Encoder[D] =
    Encoder.encodeString.contramap(d => ev.serialise(d))

  implicit def dateLikeDecoder[D](implicit ev: DateLike[D]): Decoder[D] =
    Decoder.decodeString.emap(d => ev.deserialise(d))

  implicit def dateLikeOrdering[D](implicit ev: DateLike[D]): Ordering[D] = Ordering.fromLessThan(ev.lt)
}