package sprites
import org.joda.time.DateTime

import scala.concurrent.Future

/**
  * A sprite holder that does nothing.
  * Created by alex on 18/12/16.
  */
class NoOpSpriteHolder extends SpriteHolder {
  override def teams: Option[Sprite] = None

  override def competitions: Option[Sprite] = None

  override def lastUpdated: Option[DateTime] = None

  override def update: Future[Unit] = Future.successful({})
}
