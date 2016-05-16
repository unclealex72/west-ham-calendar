package pdf

import enumeratum.EnumEntry.Uppercase
import enumeratum._

import scala.collection.SortedSet

/**
 * Created by alex on 09/02/15.
 */

case class PriorityPointsConfiguration(clients: SortedSet[Client], contactDetails: ContactDetails, creditCard: CreditCard, signature: String)
case class Client(name: String, referenceNumber: Int, clientType: Option[ClientType], ordering: Long)

object Client {

  implicit val clientOrdering: Ordering[Client] = Ordering.by { _.ordering }
}

case class ContactDetails(address: String, daytimeTelephoneNumber: String, mobilePhoneNumber: String, emailAddress: String)
case class CreditCard(number: String, expiry: CreditCardDate, securityCode: Int, nameOnCard: String)
case class CreditCardDate(month: Int, year: Int)

sealed trait ClientType extends EnumEntry with Uppercase

object ClientType extends Enum[ClientType] {

  val values = findValues

  case object OAP extends ClientType
  case object Junior extends ClientType
}