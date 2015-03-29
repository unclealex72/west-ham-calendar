package uk.co.unclealex.hammers.calendar.update

import uk.co.unclealex.hammers.calendar.html.GameUpdateCommand
import uk.co.unclealex.hammers.calendar.logging.RemoteStream

/**
 * The common interface for services that scan the web for details on West Ham games. Such details can either
 * be when games are played, the result, what games are televised or when tickets are available.
 * @author alex
 *
 */
trait GameScanner {

  def scan(implicit remoteStream: RemoteStream): List[GameUpdateCommand]
}
