package logging

import scalaz.NonEmptyList

/**
  * A service for logging fatal errors.
  * Created by alex on 28/02/16.
  */
trait Fatal {
  def fail(errors: NonEmptyList[String], optionalException: Option[Throwable])(implicit remoteStream: RemoteStream): Unit

  def fail(error: String, optionalException: Option[Throwable])(implicit remoteStream: RemoteStream): Unit =
    fail(NonEmptyList(error), optionalException)

  def fail(errors: NonEmptyList[String], exception: Throwable)(implicit remoteStream: RemoteStream): Unit = fail(errors, Some(exception))

  def fail(error: String, exception: Throwable)(implicit remoteStream: RemoteStream): Unit = fail(error, Some(exception))

  def fail(errors: NonEmptyList[String])(implicit remoteStream: RemoteStream): Unit = fail(errors, None)

  def fail(error: String)(implicit remoteStream: RemoteStream): Unit = fail(error, None)
}
