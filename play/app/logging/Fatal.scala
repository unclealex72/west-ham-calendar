package logging

/**
  * A service for logging fatal errors.
  * Created by alex on 28/02/16.
  */
trait Fatal {
  def fail(errors: Seq[String])(implicit remoteStream: RemoteStream): Unit
}
