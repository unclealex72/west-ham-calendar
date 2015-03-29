package uk.co.unclealex.hammers.calendar.log

import com.typesafe.scalalogging.slf4j.StrictLogging
import uk.co.unclealex.hammers.calendar.logging.RemoteStream

/**
 * A trait used for tests so that an implicit remote stream can be injected.
 */
trait SimpleRemoteStream extends StrictLogging {

  /**
   * An implicit remote stream that does nothing.
   */
  implicit val remoteStream = new RemoteStream() {
    def logToRemote(message: String) = {}
  }
}