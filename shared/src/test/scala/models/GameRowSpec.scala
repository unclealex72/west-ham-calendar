package models

import java.util.Date

import models.GameRow._
import models.TicketType.{AcademyTicketType, PriorityPointTicketType}
import org.specs2.mutable.Specification
import upickle.default._

/**
  * Created by alex on 15/02/16.
  */
class GameRowSpec extends Specification {

  val deserialisedGame: GameRow = GameRow(
    id = 728,
    at = new Date(1454779800000l),
    season = 2015,
    opponents = "Southampton",
    competition = Competition.PREM,
    location = Location.AWAY,
    geoLocation = Some(GeoLocation.SOUTHAMPTON),
    result = Some(GameResult(Score(1,0), Some(Score(2, 3)))),
    matchReport = Some("http://www.whufc.com/Fixtures/First-Team/Fixture-and-Results/Season-2015-2016/2016-February/Southampton-vs-West-Ham-United"),
    tickets = Map[TicketType, TicketingInformation](
      PriorityPointTicketType ->
        TicketingInformation(
          at = new Date(1452070800000l),
          links = Links[TicketingInformationRel]().withLink(TicketingInformationRel.FORM, "http://localhost:9000/prioritypoints/728")),
      AcademyTicketType ->
      TicketingInformation(at = new Date(1452502800000l), links = Links[TicketingInformationRel]())
    ),
    attended = Some(true),
    links = Links.withSelf("http://localhost:9000/game/728")
  )

  val serialisedGame =
    """
      |{
      |  "id": 728,
      |  "at": "2016-02-06T17:30:00Z",
      |  "season": 2015,
      |  "opponents": "Southampton",
      |  "competition": "PREM",
      |  "location": "AWAY",
      |  "tickets": {
      |    "prioritypoint": {
      |      "at": "2016-01-06T09:00:00Z",
      |      "links": [
      |        {
      |          "rel": "form",
      |          "href": "http://localhost:9000/prioritypoints/728"
      |        }
      |      ]
      |    },
      |    "academy": {
      |      "at": "2016-01-11T09:00:00Z",
      |      "links": []
      |    }
      |  },
      |  "links": [
      |    {
      |      "rel": "self",
      |      "href": "http://localhost:9000/game/728"
      |    }
      |  ],
      |  "geoLocation": "SOUTHAMPTON",
      |  "result": {
      |    "score": {
      |      "home": 1,
      |      "away": 0
      |    },
      |    "shootoutScore": {
      |      "home": 2,
      |      "away": 3
      |    }
      |  },
      |  "matchReport": "http://www.whufc.com/Fixtures/First-Team/Fixture-and-Results/Season-2015-2016/2016-February/Southampton-vs-West-Ham-United",
      |  "attended": true
      |}
    """.stripMargin.trim

  "Serialising a game" should {
    "correctly serialise it" in {
      write(deserialisedGame, 2) must be_===(serialisedGame)
    }
  }

  "Deserialising a game" should {
    "correctly deserialise it" in {
      val result = read[Either[String, GameRow]](serialisedGame)
      result must beRight(deserialisedGame)
    }
  }
}
