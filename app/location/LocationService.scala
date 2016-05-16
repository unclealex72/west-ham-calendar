package location

import java.net.URL

import scala.concurrent.Future

/**
 * A trait that can get the URL for a game's google location
 * Created by alex on 12/04/15.
 */
trait LocationService {

  def location(gameId: Long): Future[Option[URL]]
}
