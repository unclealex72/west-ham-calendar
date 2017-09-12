package logging

import cats.data.NonEmptyList
import model.FatalError

/**
  * A service for logging fatal errors.
  * Created by alex on 28/02/16.
  */
trait Fatal {
  def fail(errors: NonEmptyList[String], optionalException: Option[Throwable], optionalLinkBuilder: Option[FatalError => String])(implicit remoteStream: RemoteStream): Unit

  def fail(error: String, optionalException: Option[Throwable], optionalLinkBuilder: Option[FatalError => String])(implicit remoteStream: RemoteStream): Unit =
    fail(NonEmptyList.of(error), optionalException, optionalLinkBuilder)

  def fail(errors: NonEmptyList[String], exception: Throwable)(implicit remoteStream: RemoteStream): Unit = fail(errors, Some(exception), None)

  def fail(error: String, exception: Throwable)(implicit remoteStream: RemoteStream): Unit = fail(error, Some(exception), None)

  def fail(errors: NonEmptyList[String])(implicit remoteStream: RemoteStream): Unit = fail(errors, None, None)

  def fail(error: String)(implicit remoteStream: RemoteStream): Unit = fail(error, None, None)

  def fail(errors: NonEmptyList[String], exception: Throwable, linkBuilder: FatalError => String)(implicit remoteStream: RemoteStream): Unit =
    fail(errors, Some(exception), Some(linkBuilder))

  def fail(error: String, exception: Throwable, linkBuilder: FatalError => String)(implicit remoteStream: RemoteStream): Unit =
    fail(error, Some(exception), Some(linkBuilder))

  def fail(errors: NonEmptyList[String], linkBuilder: FatalError => String)(implicit remoteStream: RemoteStream): Unit =
    fail(errors, None, Some(linkBuilder))

  def fail(error: String, linkBuilder: FatalError => String)(implicit remoteStream: RemoteStream): Unit =
    fail(error, None, Some(linkBuilder))
}
