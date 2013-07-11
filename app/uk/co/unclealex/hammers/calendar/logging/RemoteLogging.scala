package uk.co.unclealex.hammers.calendar.logging

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

trait RemoteLogging {

  protected lazy val _logger: Logger = Logger(LoggerFactory getLogger getClass.getName)

  protected lazy val logger: RemoteLogger = new RemoteLogger(_logger)
}