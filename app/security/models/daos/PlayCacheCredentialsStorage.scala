package security.models.daos



import play.api.cache.CacheApi

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * Created by alex on 19/01/16.
  */
class PlayCacheCredentialsStorage(cache: CacheApi, duration: Duration)(implicit ec: ExecutionContext) extends CredentialsStorage {

  override def get[A, B](key: A)(implicit keySerialiser: (A) => String): Future[Option[B]] = Future.successful {
    cache.get(keySerialiser(key))
  }

  override def set[A, B](key: A, value: B)(implicit keySerialiser: (A) => String): Future[Unit] = Future.successful {
    cache.set(keySerialiser(key), value, duration)
  }

  override def remove[A](key: A)(implicit keySerialiser: (A) => String): Future[Unit] = Future.successful {
    cache.remove(keySerialiser(key))
  }
}
