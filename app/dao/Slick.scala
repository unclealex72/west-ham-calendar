package dao

import java.sql.Timestamp

import dates.JodaDateTime
import models.{Competition, GameResult, Location}
import org.joda.time.DateTime
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Implicits for Slick custom column types
  * Created by alex on 19/01/16.
  */
trait Slick {

  val dbConfigFactory: DatabaseConfigFactory

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigFactory.apply

  import dbConfig.driver.api._

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    { dt => new Timestamp(dt.toDate.getTime) },
    { ts => JodaDateTime(ts) }
  )

  implicit val competitionColumnType = MappedColumnType.base[Competition, String](_.entryName, Competition.withName)

  implicit val locationColumnType = MappedColumnType.base[Location, String](_.entryName, Location.withName)

  implicit val gameResultColumnType = MappedColumnType.base[GameResult, String](_.serialise, GameResult.apply)
}
