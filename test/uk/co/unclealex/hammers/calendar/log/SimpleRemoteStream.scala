package uk.co.unclealex.hammers.calendar.log

import uk.co.unclealex.hammers.calendar.logging.RemoteStream

/**
 * A trait used for tests so that an implicit remote stream can be injected.
 */
trait SimpleRemoteStream {

  /**
   * An implicit remote stream that does nothing.
   */
  implicit val remoteStream = new RemoteStream() {
    def logToRemote(message: String) = {}
  }
}