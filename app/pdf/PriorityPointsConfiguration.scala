package pdf

import argonaut.Argonaut._
import argonaut._
import model.PersistableEnumeration

import scala.collection.SortedSet

/**
 * Created by alex on 09/02/15.
 */

case class PriorityPointsConfiguration(clients: SortedSet[Client], contactDetails: ContactDetails, creditCard: CreditCard, signature: String)
case class Client(name: String, referenceNumber: Int, clientType: Option[ClientType], ordering: Long)

object Client {

  implicit val clientOrdering: Ordering[Client] = Ordering.by { c =>
    (c.ordering, c.referenceNumber, c.name, c.clientType.map(_.persistableToken).getOrElse(""))
  }
}

case class ContactDetails(address: String, daytimeTelephoneNumber: String, mobilePhoneNumber: String, emailAddress: String)
case class CreditCard(number: String, expiry: CreditCardDate, securityCode: Int, nameOnCard: String)
case class CreditCardDate(month: Int, year: Int)

sealed trait ClientType extends ClientType.Value

object ClientType extends PersistableEnumeration[ClientType] {
  case object OAP extends ClientType {
    val persistableToken = "OAP"
  }
  OAP

  case object Junior extends ClientType {
    val persistableToken = "JUNIOR"
  }
  Junior
}

object PriorityPointsConfiguration {

  import ClientType._

  // JSON codecs
  private val clientTypeMappings = Seq(OAP -> "oap", Junior -> "junior")
  implicit def clientTypeEncoder: EncodeJson[ClientType] =
    EncodeJson((ct: ClientType) => jString(clientTypeMappings.find(_._1 == ct).get._2))
  implicit def clientTypeDecoder: DecodeJson[ClientType] =
    DecodeJson { c =>
      c.as[String].flatMap { token =>
        val optClientType = clientTypeMappings.find(_._2 == token).map(_._1)
        val optionalResult: Option[DecodeResult[ClientType]] = optClientType.map(DecodeResult.ok)
        optionalResult.getOrElse(DecodeResult.fail(s"Unknown client type $token", c.history))
      }
    }

  implicit def clientCodec = casecodec4(Client.apply, Client.unapply)("name", "referenceNumber", "clientType", "ordering")
  implicit def contactDetailsCodec =
    casecodec4(ContactDetails.apply, ContactDetails.unapply)("address", "daytimeTelephoneNumber", "mobilePhoneNumber", "emailAddress")
  implicit def creditCardDateCodec = casecodec2(CreditCardDate.apply, CreditCardDate.unapply)("month", "year")
  implicit def creditCardCodec =
    casecodec4(CreditCard.apply, CreditCard.unapply)("number", "expiry", "securityCode", "nameOnCard")
  implicit def priorityPointsConfigurationCodec =
    casecodec4(PriorityPointsConfiguration.apply, PriorityPointsConfiguration.unapply)("clients", "contactDetails", "creditCard", "signature")
}