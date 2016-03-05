package sms

import json.{JsonDeserialisers, JsonSerialisers, JsonConverters}
import logging.{RemoteStream, RemoteLogging}
import play.api.libs.ws.WSClient
import upickle.Js
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

/**
  * Created by alex on 01/03/16.
  */
class ClickatellSmsService(ws: WSClient, smsConfiguration: SmsConfiguration)(implicit ec: ExecutionContext) extends SmsService with RemoteLogging {

  import ClickatellResponse._

  override def send(message: String)(implicit remoteStream: RemoteStream): Future[Unit] = {
    val request = ClickatellRequest(s"[Hammers Calendar] $message", smsConfiguration.phoneNumbers)
    ws.url("https://api.clickatell.com/rest/message").withHeaders(
      "X-Version" -> "1",
    "Content-Type" -> "application/json",
    "Authorization" -> s"Bearer ${smsConfiguration.authenticationToken}",
    "Accept" -> "application/json").post(write(request)).map { wsResponse =>
      if (wsResponse.status >= 400) {
        logger.error("Clickatell request errored: " + wsResponse.body)
      }
      else {
        val response = for {
          clickatellResponse <- read[ValidationNel[String, ClickatellResponse]](wsResponse.body).disjunction
          failedAttempts <- findFailedAttempts(clickatellResponse).disjunction
        } yield {
          failedAttempts
        }
        response match {
          case -\/(phoneNumbers) => phoneNumbers.foreach {
            phoneNumber => logger.error(s"Could not send a message to $phoneNumber")
          }
          case \/-(_) =>
            logger.info("All messages were sent successfully")
        }
      }
    }
  }

  def findFailedAttempts(clickatellResponse: ClickatellResponse): ValidationNel[String, Unit] = {
    val failedMessages =
      clickatellResponse.data.messages.filterNot(_.accepted).map(message => s"Sending to ${message.to} failed").toList
    failedMessages match {
      case h :: t => NonEmptyList(h, t :_*).failure[Unit]
      case Nil => ().successNel[String]
    }
  }
}

/**
  * JSON requests to the clickatell REST API.
 *
  * @param text
  * @param to
  */
case class ClickatellRequest(text: String, to: Seq[String])

object ClickatellRequest extends JsonSerialisers[ClickatellRequest] {

  override def serialise(c: ClickatellRequest): Js.Value = Js.Obj("text" -> Js.Str(c.text), "to" -> Js.Arr(c.to.map(Js.Str) :_*))

}

/**
  * JSON responses from the clickatell REST API.
 *
  * @param data
  */
case class ClickatellResponse(data: ClickatellData)
case class ClickatellData(messages: Seq[ClickatellMessage])
case class ClickatellMessage(accepted: Boolean, to: String, apiMessageId: Option[String])

object ClickatellResponse extends JsonDeserialisers[ClickatellResponse] {

  def jsonToMessage(value: Js.Value): ValidationNel[String, ClickatellMessage] = value.jsObj("ClickatellMessage") { fields =>
    val accepted = fields.mandatory("accepted")(_.jsBool)
    val to = fields.mandatory("to")(_.jsStr)
    val apiMessageId = fields.optional("apiMessageId")(_.jsStr)
    (accepted |@| to |@| apiMessageId)(ClickatellMessage.apply)
  }

  def jsonToData(value: Js.Value): ValidationNel[String, ClickatellData] = value.jsObj("ClickatellData") { fields =>
    val messages = fields.mandatory("message")(_.jsArr(jsonToMessage))
    messages.map(ClickatellData)
  }

  def jsonToResponse(value: Js.Value): ValidationNel[String, ClickatellResponse] = value.jsObj("ClickatellResponse") { fields =>
    val data = fields.mandatory("data")(jsonToData)
    data.map(ClickatellResponse(_))
  }

  override def deserialise(value: Js.Value): ValidationNel[String, ClickatellResponse] = jsonToResponse(value)
}