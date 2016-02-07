package logging

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

trait RemoteLogging {

  protected lazy val _logger: Logger = Logger(LoggerFactory getLogger getClass.getName)

  protected lazy val logger: RemoteLogger = new RemoteLogger(_logger)

  def logOnEmpty[E](o: Option[E], msg: String)(implicit remoteStream: RemoteStream): Option[E] = {
    if (o.isEmpty) {
      logger warn msg
    }
    o
  }
}