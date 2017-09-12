package logging

import com.typesafe.scalalogging.StrictLogging

/**
 * A trait used for tests so that an implicit remote stream can be injected.
 */
class SimpleRemoteStream extends RemoteStream with StrictLogging {
    def logToRemote(message: String): Unit = {}
}