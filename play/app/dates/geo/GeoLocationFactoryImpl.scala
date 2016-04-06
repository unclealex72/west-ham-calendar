package dates.geo

import javax.inject.Inject

import com.rockymadden.stringmetric.similarity.JaroWinklerMetric
import model.Game
import models.GeoLocation._
import models.{GeoLocation, Location}

/**
  * Created by alex on 14/02/16.
  */
class GeoLocationFactoryImpl @Inject() extends GeoLocationFactory {

  def lookFor(team: String): Option[GeoLocation] = {
    val lowerCaseTeam = team.toLowerCase
    val largestDifference = { (geolocation: GeoLocation) =>
      val differences = geolocation.team :: geolocation.alternativeNames.toList flatMap {
        (teamName: String) => JaroWinklerMetric.compare(lowerCaseTeam, teamName.toLowerCase)
      }
      differences match {
        case Nil => None
        case _ => Some(differences.max)
      }
    }
    val largestDifferences: GeoLocation => Traversable[(GeoLocation, Double)] = { (geoLocation: GeoLocation) =>
      largestDifference(geoLocation) map (difference => geoLocation -> difference)
    }
    values.flatMap(largestDifferences).toList match {
      case Nil => None
      case ld => Some(ld.maxBy(_._2)._1)
    }
  }

  /**
    * Get a geographic location from a team.
    */
  def forTeam(team: String): Option[GeoLocation] = {
    val lc = team.toLowerCase
    GeoLocation.lowerCaseNamesToValuesMap.get(lc).orElse(lookFor(lc))
  }

  /**
    * Get a geographic location for a game.
    */
  def forGame(game: Game): Option[GeoLocation] = game.location match {
    case Location.HOME => Some(WEST_HAM)
    case Location.AWAY => forTeam(game.opponents)
  }

}
