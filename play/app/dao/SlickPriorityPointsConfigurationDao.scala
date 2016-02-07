package dao



import pdf.Client._
import pdf._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.{GetResult => GR}

import scala.collection.SortedSet
import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by alex on 19/01/16.
  */
class SlickPriorityPointsConfigurationDao(val dbConfigFactory: DatabaseConfigFactory)(implicit ec: ExecutionContext) extends PriorityPointsConfigurationDao with Slick {

  import dbConfig.driver.api._

  /** Entity class storing rows of table Client
    *
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param name Database column name SqlType(varchar), Length(128,true)
    *  @param referenceNumber Database column referencenumber SqlType(int4)
    *  @param prioritypointsconfigurationid Database column prioritypointsconfigurationid SqlType(int8)
    *  @param clientType Database column clienttype SqlType(varchar), Length(128,true), Default(None) */
  case class ClientRow(id: Long, ordering: Long, name: String, referenceNumber: Int, prioritypointsconfigurationid: Long, clientType: Option[ClientType] = None)

  /** Table description of table client. Objects of this class serve as prototypes for rows in queries. */
  class TClient(_tableTag: Tag) extends Table[ClientRow](_tableTag, "client") {
    def * = (id, ordering, name, referenceNumber, priorityPointsConfigurationId, clientType) <> (ClientRow.tupled, ClientRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    //def ? = (Rep.Some(id), Rep.Some(ordering), Rep.Some(name), Rep.Some(referenceNumber), Rep.Some(priorityPointsConfigurationId), clientType).shaped.<>({ r=>; _1.map(_=> ClientRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column id SqlType(int8), PrimaryKey */
    val ordering: Rep[Long] = column[Long]("ordering")
    /** Database column name SqlType(varchar), Length(128,true) */
    val name: Rep[String] = column[String]("name", O.Length(128,varying=true))
    /** Database column referencenumber SqlType(int4) */
    val referenceNumber: Rep[Int] = column[Int]("referencenumber")
    /** Database column prioritypointsconfigurationid SqlType(int8) */
    val priorityPointsConfigurationId: Rep[Long] = column[Long]("prioritypointsconfigurationid")
    /** Database column clienttype SqlType(varchar), Length(128,true), Default(None) */
    val clientType: Rep[Option[ClientType]] = column[Option[ClientType]]("clienttype", O.Length(128,varying=true), O.Default(None))

    /** Foreign key referencing Prioritypointsconfiguration (database name clientfk1) */
    lazy val priorityPointsConfigurationFk = foreignKey("clientfk1", priorityPointsConfigurationId, priorityPointsConfigurations)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Client */
  lazy val clients = new TableQuery(tag => new TClient(tag))


  /** Entity class storing rows of table Prioritypointsconfiguration
    *
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param emailAddress Database column emailaddress SqlType(varchar), Length(128,true)
    *  @param daytimeTelephoneNumber Database column daytimetelephonenumber SqlType(varchar), Length(128,true)
    *  @param mobilePhoneNumber Database column mobilephonenumber SqlType(varchar), Length(128,true)
    *  @param address Database column address SqlType(varchar), Length(128,true)
    *  @param creditCardNumber Database column creditcardnumber SqlType(varchar), Length(128,true)
    *  @param creditCardSecurityCode Database column creditcardsecuritycode SqlType(int4)
    *  @param creditCardExpiryMonth Database column creditcardexpirymonth SqlType(int4)
    *  @param creditCardExpiryYear Database column creditcardexpiryyear SqlType(int4)
    *  @param nameOnCreditCard Database column nameoncreditcard SqlType(varchar), Length(128,true) */
  case class PriorityPointsConfigurationRow(
                                             id: Long,
                                             emailAddress: String,
                                             daytimeTelephoneNumber: String,
                                             mobilePhoneNumber: String,
                                             address: String,
                                             creditCardNumber: String,
                                             creditCardSecurityCode: Int,
                                             creditCardExpiryMonth: Int,
                                             creditCardExpiryYear: Int,
                                             nameOnCreditCard: String,
                                             signature: String)
  /** GetResult implicit for fetching PrioritypointsconfigurationRow objects using plain SQL queries */
  implicit def GetResultPrioritypointsconfigurationRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[PriorityPointsConfigurationRow] = GR{
    prs => import prs._
      PriorityPointsConfigurationRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table prioritypointsconfiguration. Objects of this class serve as prototypes for rows in queries. */
  class TPriorityPointsConfiguration(_tableTag: Tag) extends Table[PriorityPointsConfigurationRow](_tableTag, "prioritypointsconfiguration") {
    def * = (id, emailAddress, daytimeTelephoneNumber, mobilePhoneNumber, address, creditCardNumber, creditCardSecurityCode, creditCardExpiryMonth, creditCardExpiryYear, nameOnCreditCard, signature) <> (PriorityPointsConfigurationRow.tupled, PriorityPointsConfigurationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(emailAddress), Rep.Some(daytimeTelephoneNumber), Rep.Some(mobilePhoneNumber), Rep.Some(address), Rep.Some(creditCardNumber), Rep.Some(creditCardSecurityCode), Rep.Some(creditCardExpiryMonth), Rep.Some(creditCardExpiryYear), Rep.Some(nameOnCreditCard), Rep.Some(signature)).shaped.<>({ r=>import r._; _1.map(_=> PriorityPointsConfigurationRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column emailaddress SqlType(varchar), Length(128,true) */
    val emailAddress: Rep[String] = column[String]("emailaddress", O.Length(128,varying=true))
    /** Database column daytimetelephonenumber SqlType(varchar), Length(128,true) */
    val daytimeTelephoneNumber: Rep[String] = column[String]("daytimetelephonenumber", O.Length(128,varying=true))
    /** Database column mobilephonenumber SqlType(varchar), Length(128,true) */
    val mobilePhoneNumber: Rep[String] = column[String]("mobilephonenumber", O.Length(128,varying=true))
    /** Database column address SqlType(varchar), Length(128,true) */
    val address: Rep[String] = column[String]("address", O.Length(128,varying=true))
    /** Database column creditcardnumber SqlType(varchar), Length(128,true) */
    val creditCardNumber: Rep[String] = column[String]("creditcardnumber", O.Length(128,varying=true))
    /** Database column creditcardsecuritycode SqlType(int4) */
    val creditCardSecurityCode: Rep[Int] = column[Int]("creditcardsecuritycode")
    /** Database column creditcardexpirymonth SqlType(int4) */
    val creditCardExpiryMonth: Rep[Int] = column[Int]("creditcardexpirymonth")
    /** Database column creditcardexpiryyear SqlType(int4) */
    val creditCardExpiryYear: Rep[Int] = column[Int]("creditcardexpiryyear")
    /** Database column nameoncreditcard SqlType(varchar), Length(128,true) */
    val nameOnCreditCard: Rep[String] = column[String]("nameoncreditcard", O.Length(128,varying=true))
    val signature: Rep[String] = column[String]("signature", O.Length(128,varying=true))
  }
  /** Collection-like TableQuery object for table Prioritypointsconfiguration */
  lazy val priorityPointsConfigurations = new TableQuery(tag => new TPriorityPointsConfiguration(tag))

  /**
    * Get the configuration for priority point PDF forms.
    *
    * @return
    */
  override def get: Future[Option[PriorityPointsConfiguration]] = {
    val fConfigurations = dbConfig.db.run {
      val innerJoin = for {
        (c, pp) <- clients join priorityPointsConfigurations on (_.priorityPointsConfigurationId === _.id)
      } yield (c, pp)
      innerJoin.result
    }
    fConfigurations.map(toPriorityPointsConfiguration)
  }

  def toPriorityPointsConfiguration(rows: Seq[(ClientRow, PriorityPointsConfigurationRow)]): Option[PriorityPointsConfiguration] = {
    rows.foldLeft(Option.empty[PriorityPointsConfiguration]) { (oConf, rows) =>
      val (clientRow, priorityPointsConfigurationRow) = rows
      oConf match {
        case Some(conf) => Some(addClient(conf, clientRow))
        case _ => Some(createConfiguration(priorityPointsConfigurationRow, clientRow))
      }
    }
  }

  def createConfiguration(priorityPointsConfigurationRow: PriorityPointsConfigurationRow, clientRow: ClientRow): PriorityPointsConfiguration = {
    val contactDetails = ContactDetails(
      priorityPointsConfigurationRow.address,
      priorityPointsConfigurationRow.daytimeTelephoneNumber,
      priorityPointsConfigurationRow.mobilePhoneNumber,
      priorityPointsConfigurationRow.emailAddress)
    val creditCardDate = CreditCardDate(priorityPointsConfigurationRow.creditCardExpiryMonth, priorityPointsConfigurationRow.creditCardExpiryYear)
    val creditCard = CreditCard(
      priorityPointsConfigurationRow.creditCardNumber,
      creditCardDate,
      priorityPointsConfigurationRow.creditCardSecurityCode, priorityPointsConfigurationRow.nameOnCreditCard)

    PriorityPointsConfiguration(SortedSet(createClient(clientRow)), contactDetails, creditCard, priorityPointsConfigurationRow.signature)
  }

  def addClient(conf: PriorityPointsConfiguration, clientRow: ClientRow) = {
    conf.copy(clients = conf.clients + createClient(clientRow))
  }
  def createClient(clientRow: ClientRow): Client = {
    Client(clientRow.name, clientRow.referenceNumber, clientRow.clientType, clientRow.ordering)
  }
}
