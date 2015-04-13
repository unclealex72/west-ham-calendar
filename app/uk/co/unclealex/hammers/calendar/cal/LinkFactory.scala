package uk.co.unclealex.hammers.calendar.cal

import java.net.URI

import uk.co.unclealex.hammers.calendar.model.Location

/**
 * A trait that can create links to this server.
 * Created by alex on 13/04/15.
 */
trait LinkFactory {

  /**
   * Create a link to the location page for a game.
   * @param gameId
   * @return
   */
  def locationLink(gameId: Long, location: Location): Option[URI]
}
