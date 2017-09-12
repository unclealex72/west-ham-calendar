package logging

import java.io.{PrintWriter, StringWriter}

import cats.data.NonEmptyList
import dao.FatalErrorDao
import dates.ZonedDateTimeFactory
import model.FatalError
import play.api.libs.ws.WSClient
import sms.SmsService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 28/02/16.
  */
class FatalImpl @javax.inject.Inject() (val ws: WSClient, val fatalErrorDao: FatalErrorDao, val zonedDateTimeFactory: ZonedDateTimeFactory, val smsService: SmsService, implicit val ec: ExecutionContext) extends Fatal with RemoteLogging {

  override def fail(errors: NonEmptyList[String], optionalException: Option[Throwable], optionalLinkBuilder: Option[FatalError => String])(implicit remoteStream: RemoteStream): Unit = {
    log(errors, optionalException)
    for {
      fatalError <- store(errors, optionalException)
      _ <- sms(fatalError, optionalLinkBuilder)
    } yield {}
  }

  def log(errors: NonEmptyList[String], optionalException: Option[Throwable])(implicit remoteStream: RemoteStream) = {
    optionalException match {
      case Some(exception) =>
        val reversed = errors.reverse
        val lastMessage = reversed.head
        val otherMessages = reversed.tail.reverse.toStream
        otherMessages.foreach { error =>
          logger.error(error)
        }
        logger.error(lastMessage, exception)
      case _ =>
        errors.toList.foreach(logger.error)
    }
  }

  def store(errors: NonEmptyList[String], optionalException: Option[Throwable]): Future[FatalError] = {
    val exceptionStr = optionalException.map { e =>
      val stringWriter = new StringWriter
      e.printStackTrace(new PrintWriter(stringWriter))
      stringWriter.toString
    }
    val message = (errors.toList ++ exceptionStr).mkString("\n")
    val fatalError = FatalError(0, zonedDateTimeFactory.now, message)
    fatalErrorDao.store(fatalError)
  }

  def sms(fatalError: FatalError, optionalLinkBuilder: Option[FatalError => String])(implicit remoteStream: RemoteStream): Future[Unit] = {
    val msg = Seq(s"Fatal error ${fatalError.id} has been logged.") ++ optionalLinkBuilder.map(builder => builder(fatalError))
    smsService.send(msg.mkString(" "))
  }
}
