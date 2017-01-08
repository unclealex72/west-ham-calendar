package dates

import java.util.Date

import scalaz.Ordering._
import scalaz.Scalaz._
import scalaz._
import scala.math.{Ordering => SOrdering}

/**
  * A class to allow dates to be shared between Javascript and Scala
  * Created by alex on 20/02/16.
  */

case class SharedDate(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int, offsetHours: Int, offsetMinutes: Int) {

  override def toString: String = {
    val offset = Ordering.fromInt(offsetHours * 60 + offsetMinutes) match {
      case EQ => "Z"
      case LT => f"-${-offsetHours}%02d:$offsetMinutes%02d"
      case GT => f"+$offsetHours%02d:$offsetMinutes%02d"
    }
    f"$year%04d-$month%02d-$day%02dT$hours%02d:$minutes%02d:$seconds%02d" + offset
  }

  def toSharedDay: SharedDay = SharedDay(year, month, day)
}

case class SharedDay(year: Int, month: Int, day: Int)
object SharedDay {
  def apply(date: Date): SharedDay = {
    //noinspection ScalaDeprecation
    SharedDay(date.getYear + 1900, date.getMonth + 1, date.getDate)
  }
}

object SharedDate {

  // Only order as far as the day - that's close enough for football match purposes.
  implicit val ordering: SOrdering[SharedDate] = SOrdering.by( sd => (sd.year, sd.month, sd.day))

  private object Private {
    trait TimezoneParser {
      def parse(str: String): Option[(Int, String)]
    }

    object ZuluTimezoneParser extends TimezoneParser {
      override def parse(str: String): Option[(Int, String)] =
        if (str.endsWith("Z")) Some((0, str.substring(0, str.length - 1))) else None
    }
    
    case class RegexParser(sign: String, multiplier: Int) extends TimezoneParser {
      private val r = s"(.+)$sign([0-9]{2}):([0-9]{2})".r
      
      override def parse(str: String): Option[(Int, String)] = str match {
        case r(dateTime, hours, minutes) => Some((hours.toInt * 60 + minutes.toInt) * multiplier, dateTime)
        case _ => None
      }
    }

    val timezoneParsers: Stream[TimezoneParser] = Stream(ZuluTimezoneParser, RegexParser("""\+""", 1), RegexParser("-", -1))

    def parseTimeZone(str: String): \/[String, (Int, String)] = {
      timezoneParsers.flatMap { timezoneParser => timezoneParser.parse(str) }.headOption match {
        case Some(tz) => tz.right[String]
        case _ => s"Cannot parse timezone in string $str".left[(Int, String)]
      }
    }
    
    //2016-02-20T13:14:10+00:00

    private val dateTimeRegex = "([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})".r
    
    def parseDateTime(timezoneOffset: Int, str: String): \/[String, SharedDate] = str match {
      case dateTimeRegex(year, month, day, hours, minutes, seconds) =>
        val (y, mo, d, h, mi, s) = (year.toInt, month.toInt, day.toInt, hours.toInt, minutes.toInt, seconds.toInt)
        SharedDate(y, mo, d, h, mi, s, timezoneOffset / 60, Math.abs(timezoneOffset % 60)).right[String]
      case _ => s"Cannot parse date and time in string $str".left[SharedDate]
    }
    
    def parse(fmt: String): \/[String, SharedDate] = {
      for {
        timezoneOffset <- parseTimeZone(fmt)
        sharedDate <- parseDateTime(timezoneOffset._1, timezoneOffset._2)
      } yield sharedDate
    }
  }

  def apply(fmt: String): ValidationNel[String, SharedDate] = Private.parse(fmt).validationNel[String]
}