package dao



import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by alex on 30/01/16.
  */
trait DatabaseConfigFactory {

  def apply: DatabaseConfig[JdbcProfile]
}

class PlayDatabaseConfigFactory(dbConfigProvider: DatabaseConfigProvider) extends DatabaseConfigFactory {

  def apply = dbConfigProvider.get[JdbcProfile]
}