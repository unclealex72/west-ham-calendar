package sms

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}
import io.circe.{Decoder, Encoder, Printer}
import io.circe.parser._
import logging.{RemoteLogging, RemoteStream}
import play.api.libs.ws.{DefaultBodyWritables, WSClient}

import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._
import cats.syntax.either._
import io.circe.syntax._
import play.api.http.ContentTypes
import play.libs.ws.{BodyWritable, WSBody}

/**
  * Created by alex on 01/03/16.
  */
class ClickatellSmsService @javax.inject.Inject() (
                                                    ws: WSClient,
                                                    smsConfiguration: SmsConfiguration,
                                                    implicit val ec: ExecutionContext) extends
  SmsService with RemoteLogging with DefaultBodyWritables {

  override def send(message: String)(implicit remoteStream: RemoteStream): Future[Unit] = {
    val request = ClickatellRequest(s"[Hammers Calendar] $message", smsConfiguration.phoneNumbers)
    val body = request.asJson.pretty(Printer.noSpaces)
    ws.url("https://api.clickatell.com/rest/message").withHttpHeaders(
      "X-Version" -> "1",
    "Content-Type" -> "application/json",
    "Authorization" -> s"Bearer ${smsConfiguration.authenticationToken}",
    "Accept" -> "application/json").post(body).map { wsResponse =>
      if (wsResponse.status >= 400) {
        logger.error("Clickatell request errored: " + wsResponse.body)
      }
      else {
        val response: Either[NonEmptyList[String], Unit] = for {
          clickatellResponse <- decode[ClickatellResponse](wsResponse.body).leftMap(e => NonEmptyList.of(e.getMessage))
          failedAttempts <- findFailedAttempts(clickatellResponse)
        } yield {
          failedAttempts
        }
        response match {
          case Left(phoneNumbers) => phoneNumbers.toList.foreach {
            phoneNumber => logger.error(s"Could not send a message to $phoneNumber")
          }
          case Right(_) =>
            logger.info("All messages were sent successfully")
        }
      }
    }
  }

  def findFailedAttempts(clickatellResponse: ClickatellResponse): Either[NonEmptyList[String], Unit] = {
    val empty: ValidatedNel[String, Unit] = Valid({})
    val validatedFailedAttempts = clickatellResponse.data.messages.foldLeft(empty){ (acc, message) =>
      val failureMessage: ValidatedNel[String, Unit] = if (message.accepted) {
        Valid({})
      }
      else {
        Invalid(NonEmptyList.of(s"Sending to ${message.to} failed"))
      }
      (acc |@| failureMessage).map((_, _) => Unit)
    }
    validatedFailedAttempts.toEither
  }
}

/**
  * JSON requests to the clickatell REST API.
 *
  * @param text
  * @param to
  */
case class ClickatellRequest(text: String, to: Seq[String])

object ClickatellRequest {

  implicit val clickatellRequestEncoder: Encoder[ClickatellRequest] =
    Encoder.forProduct2("text", "to")(cr => (cr.text, cr.to))

}

/**
  * JSON responses from the clickatell REST API.
  * {"data":{"message":[{"accepted":true,"to":"+447753588969","apiMessageId":"52e0ee60c944a1e6c5c3071b802ede17"}]}}
  * @param data
  */
case class ClickatellResponse(data: ClickatellData)
case class ClickatellData(messages: Seq[ClickatellMessage])
case class ClickatellMessage(accepted: Boolean, to: String, apiMessageId: Option[String])

object ClickatellResponse {

  implicit val clickatellMessageDecoder: Decoder[ClickatellMessage] =
    Decoder.forProduct3("accepted", "to", "apiMessageId")(ClickatellMessage.apply)
  implicit val clickatellDataDecoder: Decoder[ClickatellData] =
    Decoder.forProduct1("message")(ClickatellData.apply)
  implicit val clickatellResponseDecoder: Decoder[ClickatellResponse] =
    Decoder.forProduct1("data")(ClickatellResponse.apply)

}