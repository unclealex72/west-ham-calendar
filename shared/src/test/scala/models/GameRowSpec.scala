package models

import java.time.{ZonedDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.{TemporalAccessor, TemporalQuery}

import dates.DateLike
import models.TicketType.{AcademyTicketType, PriorityPointTicketType}
import org.specs2.mutable.Specification

import scala.util.Try
import cats.syntax.either._
import models.GameRowRel.ATTEND

import io.circe.syntax._
import io.circe.parser.decode

/**
  * Created by alex on 15/02/16.
  */
class GameRowSpec extends Specification {

  import GameRowSpec._
  import GameRowSpec.ZonedDateTimeDateLike._
  import dates.DateLike._

  val deserialisedGame: GameRow[ZonedDateTime] = GameRow[ZonedDateTime](
    id = 728,
    at = "2016-02-06T17:30:00Z",
    season = 2015,
    opponents = "Southampton",
    competition = Competition.PREM,
    location = Location.AWAY,
    maybeResult = Some(GameResult(Score(1,0), Some(Score(2, 3)))),
    tickets = Map[TicketType, ZonedDateTime](
      PriorityPointTicketType -> "2016-02-06T09:00:00Z",
      AcademyTicketType -> "2016-02-11T09:00:00Z"
    ),
    maybeAttended = Some(true),
    links = Links.withSelf("http://localhost:9000/game/728").withLink(ATTEND, "http://localhost:9000/game/728/attend")
  )

  val serialisedGame: String =
    """
      |{
      |  "id" : 728,
      |  "at" : "2016-02-06T17:30:00Z",
      |  "season" : 2015,
      |  "opponents" : "Southampton",
      |  "competition" : "PREM",
      |  "location" : "AWAY",
      |  "result" : {
      |    "score" : {
      |      "home" : 1,
      |      "away" : 0
      |    },
      |    "shootoutScore" : {
      |      "home" : 2,
      |      "away" : 3
      |    }
      |  },
      |  "tickets" : {
      |    "prioritypoint" : "2016-02-06T09:00:00Z",
      |    "academy" : "2016-02-11T09:00:00Z"
      |  },
      |  "attended" : true,
      |  "links" : {
      |    "links" : [
      |      {
      |        "rel" : "attend",
      |        "href" : "http://localhost:9000/game/728/attend"
      |      },
      |      {
      |        "rel" : "_self",
      |        "href" : "http://localhost:9000/game/728"
      |      }
      |    ]
      |  }
      |}
      """.stripMargin.trim

  "Serialising a game" should {
    "correctly serialise it" in {
      deserialisedGame.asJson.toString() must be_===(serialisedGame)
    }
  }

  "Deserialising a game" should {
    "correctly deserialise it" in {
      val result = decode[GameRow[ZonedDateTime]](serialisedGame)
      result must beRight(deserialisedGame)
    }
  }
}

object GameRowSpec {

  implicit object ZonedDateTimeDateLike extends DateLike[ZonedDateTime] {

    override def serialise(d: ZonedDateTime): String = formatter.format(d)
    override def deserialise(s: String): Either[String, ZonedDateTime] = {
      Try(parse(s)).toEither.leftMap(_.getMessage)
    }
    override def lt(d1: ZonedDateTime, d2: ZonedDateTime): Boolean = d1.isBefore(d2)

    override def format(d: ZonedDateTime, fmt: String): String = fmt
  }

  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault())
  private def parse(s: String): ZonedDateTime = {
    val qry = new TemporalQuery[ZonedDateTime] {
      override def queryFrom(temporal: TemporalAccessor): ZonedDateTime = ZonedDateTime.from(temporal)
    }
    formatter.parse(s, qry)
  }


  implicit def stringToDate(str: String): ZonedDateTime = parse(str)
}