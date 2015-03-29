package uk.co.unclealex.hammers.calendar.logging

import com.typesafe.scalalogging.slf4j.Logger


class RemoteLogger(logger: Logger) {

  def info(message: String)(implicit remoteStream: RemoteStream) = {
    logger info message
    remoteStream log(message, None)
  }
  
  def warn(message: String)(implicit remoteStream: RemoteStream) = {
    logger warn message
    remoteStream log(message, None)
  }
  
  def warn(message: String, e: Throwable)(implicit remoteStream: RemoteStream) = {
    logger warn (message, e)
    remoteStream log(message, Some(e))
  }

  def error(message: String)(implicit remoteStream: RemoteStream) = {
    logger error message
    remoteStream log(message, None)
  }

  def error(message: String, e: Throwable)(implicit remoteStream: RemoteStream) = {
    logger error (message, e)
    remoteStream log(message, Some(e))
  }

  def debug(message: String)(implicit remoteStream: RemoteStream) = {
    logger debug message
    remoteStream log(message, None)
  }
}