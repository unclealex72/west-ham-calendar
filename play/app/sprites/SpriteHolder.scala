package sprites

import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

/**
  * A trait holds the current state of CSS sprites.
  * Created by alex on 03/04/16.
  */
trait SpriteHolder {

  def teams: Option[Sprite]

  def competitions: Option[Sprite]

  def lastUpdated: Option[DateTime]

  def update(implicit ec: ExecutionContext): Future[Unit]
}
