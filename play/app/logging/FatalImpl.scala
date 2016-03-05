package logging

import java.io.{StringWriter, PrintWriter}

import dao.FatalErrorDao
import dates.NowService
import model.FatalError
import play.api.libs.ws.WSClient
import sms.SmsService

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

/**
  * Created by alex on 28/02/16.
  */
class FatalImpl(val ws: WSClient, val fatalErrorDao: FatalErrorDao, val nowService: NowService, val smsService: SmsService)(implicit ec: ExecutionContext) extends Fatal with RemoteLogging {

  override def fail(errors: NonEmptyList[String], optionalException: Option[Throwable])(implicit remoteStream: RemoteStream): Unit = {
    log(errors, optionalException)
    for {
      id <- store(errors, optionalException)
      _ <- sms(id)
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
        errors.foreach(logger.error)
    }
  }

  def store(errors: NonEmptyList[String], optionalException: Option[Throwable]): Future[Long] = {
    val exceptionStr = optionalException.map { e =>
      val stringWriter = new StringWriter
      e.printStackTrace(new PrintWriter(stringWriter))
      stringWriter.toString
    }
    val message = (errors.toList ++ exceptionStr).mkString("\n")
    val fatalError = FatalError(0, nowService.now, message)
    fatalErrorDao.store(fatalError).map(_.id)
  }

  def sms(id: Long)(implicit remoteStream: RemoteStream): Future[Unit] = {
    smsService.send(s"Fatal error $id has been logged.")
  }
}
