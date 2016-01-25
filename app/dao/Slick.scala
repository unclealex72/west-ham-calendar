package dao

import java.sql.Timestamp

import dates.JodaDateTime
import model.{Competition, Location}
import org.joda.time.DateTime
import pdf.ClientType
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Implicits for Slick custom column types
  * Created by alex on 19/01/16.
  */
trait Slick {

  val dbConfigProvider: DatabaseConfigProvider

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    { dt => new Timestamp(dt.toDate.getTime) },
    { ts => JodaDateTime(ts) }
  )

  implicit val competitionColumnType = MappedColumnType.base[Competition, String](
    { comp => Competition.serialise(comp) },
    { token => Competition.deserialise(token) }
  )

  implicit val locationColumnType = MappedColumnType.base[Location, String](
    { loc => Location.serialise(loc) },
    { token => Location.deserialise(token) }
  )

  implicit val clientTypeColumnType = MappedColumnType.base[ClientType, String](
    { ct => ClientType.serialise(ct) },
    { token => ClientType.deserialise(token) }
  )
}
