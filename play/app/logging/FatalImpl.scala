package logging

/**
  * Created by alex on 28/02/16.
  */
class FatalImpl extends Fatal with RemoteLogging {

  override def fail(errors: Seq[String])(implicit remoteStream: RemoteStream): Unit = {
    errors.foreach { error =>
      logger.error(error)
    }
  }
}
