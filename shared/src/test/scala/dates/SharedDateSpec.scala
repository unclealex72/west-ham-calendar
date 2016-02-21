package dates

import org.specs2.mutable.Specification

/**
  * Created by alex on 20/02/16.
  */
class SharedDateSpec extends Specification {

  "A shared date with a 0 timezone offset" should {
    "deserialise if the format ends with a Z" in {
      SharedDate("1972-05-09T09:12:00Z").toEither must beRight(SharedDate(1972, 5, 9, 9, 12, 0, 0, 0))
    }
    "serialise it so that it ends with a Z" in {
      SharedDate(1972, 5, 9, 9, 12, 0, 0, 0).toString must be_===("1972-05-09T09:12:00Z")
    }
  }

  "A shared date with a positive timezone offset" should {
    "deserialise if the format ends with +hh:mm" in {
      SharedDate("1972-05-09T08:12:00+01:30").toEither must beRight(SharedDate(1972, 5, 9, 8, 12, 0, 1, 30))
    }
    "serialise it so that it ends with a +hh:mm" in {
      SharedDate(1972, 5, 9, 8, 12, 0, 1, 30).toString must be_===("1972-05-09T08:12:00+01:30")
    }
  }

  "A shared date with a negative timezone offset" should {
    "deserialise if the format ends with -hh:mm" in {
      SharedDate("1972-05-09T08:12:00-01:30").toEither must beRight(SharedDate(1972, 5, 9, 8, 12, 0, -1, 30))
    }
    "serialise it so that it ends with -hh:mm" in {
      SharedDate(1972, 5, 9, 8, 12, 0, -1, 30).toString must be_===("1972-05-09T08:12:00-01:30")
    }
  }
}
