package dao

import javax.inject.Inject

import model.FatalError
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 19/01/16.
  */
class SlickFatalErrorDao @Inject() (val dbConfigFactory: DatabaseConfigFactory, implicit val ec: ExecutionContext) extends FatalErrorDao with Slick {

  import dbConfig.driver.api._

  val create = fatalErrors.schema.create
  val drop = fatalErrors.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TFatalError(_tableTag: Tag) extends Table[FatalError](_tableTag, "fatal_error") {
    def * = (
      id,
      at,
      message) <> (FatalError.tupled, FatalError.unapply)

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    /** Database column at SqlType(timestamp), Default(None) */
    val at: Rep[DateTime] = column[DateTime]("at")
    /** Database column tvchannel SqlType(varchar), Length(128,true), Default(None) */
    val message: Rep[String] = column[String]("message", O.Length(65536,varying=true))
    /** Database column report SqlType(text), Default(None) */
  }

  /** Collection-like TableQuery object for table FatalErrors */
  lazy val fatalErrors = new TableQuery(tag => new TFatalError(tag))



  /**
    * Find all the errors since a given date.
    *
    * @param since
    * @return
    */
  override def since(since: DateTime): Future[List[FatalError]] = dbConfig.db.run {
    fatalErrors.filter(_.at >= since).sortBy(_.at).result
  }.map(_.toList)

  /**
    * Update a fatal error
    */
  override def store(fatalError: FatalError): Future[FatalError] = dbConfig.db.run {
    (fatalErrors returning fatalErrors).insertOrUpdate(fatalError)
  }.map(_.getOrElse(fatalError))


  /**
    * Find a fatal error by its ID.
    */
  override def findById(id: Long): Future[Option[FatalError]] = dbConfig.db.run {
    fatalErrors.filter(_.id === id).result.headOption
  }

  /**
    * Get all known fatal errors.
    */
  override def getAll: Future[List[FatalError]] = dbConfig.db.run {
    fatalErrors.sortBy(_.at).result
  }.map(_.toList)
}
