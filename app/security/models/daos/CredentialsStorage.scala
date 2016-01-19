package security.models.daos

import scala.concurrent.Future

/**
  * A trait that will store credentials for a certain amount of time.
  * Created by alex on 19/01/16.
  */
trait CredentialsStorage {

  def get[A, B](key: A)(implicit keySerialiser: A => String): Future[Option[B]]

  def set[A, B](key: A, value: B)(implicit keySerialiser: A => String): Future[Unit]

  def remove[A](key: A)(implicit keySerialiser: A => String): Future[Unit]
}
