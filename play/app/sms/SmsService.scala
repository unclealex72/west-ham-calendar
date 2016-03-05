package sms

import logging.RemoteStream

import scala.concurrent.Future

/**
  * Send an SMS message.
  * Created by alex on 01/03/16.
  */
trait SmsService {

  def send(message: String)(implicit remoteStream: RemoteStream): Future[Unit]
}
