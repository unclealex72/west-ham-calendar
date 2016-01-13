package model

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.squeryl.dsl.StatefulOneToMany
import pdf._
import dao.CalendarSchema._
/**
 * Created by alex on 15/02/15.
 */
case class PersistedPriorityPointsConfiguration(
                                               val id: Long,
                                               @Column("daytimetelephonenumber") val daytimeTelephoneNumber: String,
                                               @Column("mobilephonenumber") val mobilePhoneNumber: String,
                                               @Column("emailaddress") val emailAddress: String,
                                               @Column("address") val address: String,
                                               @Column("creditcardnumber") val creditCardNumber: String,
                                               @Column("creditcardexpirymonth") val creditCardExpiryMonth: Int,
                                               @Column("creditcardexpiryyear") val creditCardExpiryYear: Int,
                                               @Column("creditcardsecuritycode") val creditCardSecurityCode: Int,
                                               @Column("nameoncreditcard") val nameOnCreditCard: String
                                                 ) extends KeyedEntity[Long] {

  lazy val clients: StatefulOneToMany[PersistedClient] =
    priorityPointsConfigurationToClients.leftStateful(PersistedPriorityPointsConfiguration.this)
}

case class PersistedClient(
                          val id: Long,
                          @Column("name") val name: String,
                          @Column("referencenumber") val referenceNumber: Int,
                          @Column("clienttype") val clientType: Option[String],
                          @Column("prioritypointsconfigurationid") val priorityPointsConfigurationId: Long
                            ) extends KeyedEntity[Long]


sealed trait PersistedClientType extends PersistedClientType.Value

object PersistedClientType extends PersistableEnumeration[PersistedClientType] {
  case object OAP extends PersistedClientType {
    val persistableToken = "OAP"
  }
  OAP

  case object Junior extends PersistedClientType {
    val persistableToken = "JUNIOR"
  }
  Junior
}

object PersistedClient {

  val toClient: PersistedClient => Client = { p =>
    Client(p.name, p.referenceNumber, p.clientType.map(PersistedClientType.deserialise))
  }
}

object PersistedPriorityPointsConfiguration {

  val toPriorityPointsConfiguration: PersistedPriorityPointsConfiguration => PriorityPointsConfiguration = { p =>
    PriorityPointsConfiguration(
      p.clients.toList.sortBy(_.id).map(PersistedClient.toClient),
      ContactDetails(p.address, p.daytimeTelephoneNumber, p.mobilePhoneNumber, p.emailAddress),
      CreditCard(p.creditCardNumber, CreditCardDate(p.creditCardExpiryMonth, p.creditCardExpiryYear), p.creditCardSecurityCode, p.nameOnCreditCard)
    )
  }
}
