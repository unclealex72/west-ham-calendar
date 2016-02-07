package logging

import java.io.PrintWriter
import java.io.StringWriter

/**
 * A trait that allows the remote logger to log to a remote stream.
 */

trait RemoteStream {
  
  /**
   * Log a message to a remote stream
   */
  def logToRemote(message: String): Unit
  
  /**
   * Log a message with an optional exception.
   */
  def log(message: String, optionalException: Option[Throwable] = None) : Unit = {
    logToRemote(message + '\n')
    optionalException foreach { t =>
      val w = new StringWriter
      val pw = new PrintWriter(w)
      t.printStackTrace(pw)
      pw.println()
      logToRemote(w.toString)
    }
  }
  
  
}