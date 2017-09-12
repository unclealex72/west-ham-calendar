package dao

import java.sql.Timestamp
import java.time.{Instant, ZonedDateTime}

import dates.ZonedDateTimeFactory
import models.{Competition, GameResult, Location}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Implicits for Slick custom column types
  * Created by alex on 19/01/16.
  */
class Slick(dbConfigFactory: DatabaseConfigFactory, zonedDateTimeFactory: ZonedDateTimeFactory) {

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigFactory.apply

  import dbConfig.profile.api._

  implicit val zonedDateTimeColumnType: BaseColumnType[ZonedDateTime] = MappedColumnType.base[ZonedDateTime, Timestamp](
    dt => new Timestamp(dt.toInstant.toEpochMilli),
    ts => zonedDateTimeFactory.fromInstant(Instant.ofEpochMilli(ts.getTime))
  )

  implicit val competitionColumnType: BaseColumnType[Competition] =
    MappedColumnType.base[Competition, String](_.entryName, Competition.withName)

  implicit val locationColumnType: BaseColumnType[Location] =
    MappedColumnType.base[Location, String](_.entryName, Location.withName)

  implicit val gameResultColumnType: BaseColumnType[GameResult] =
    MappedColumnType.base[GameResult, String](_.serialise, GameResult.apply)
}
