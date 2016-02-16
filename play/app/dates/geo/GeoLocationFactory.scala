package dates.geo

import model.Game
import models.GeoLocation

/**
  * Created by alex on 14/02/16.
  */
trait GeoLocationFactory {

  def forTeam(team: String): Option[GeoLocation]

  /**
    * Get a geographic location for a game.
    */
  def forGame(game: Game): Option[GeoLocation]
}
