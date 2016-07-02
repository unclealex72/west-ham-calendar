package util

import akka.actor.Cancellable
import akka.stream.{ClosedShape, Graph, Materializer}

import scala.concurrent.duration.FiniteDuration

/**
  * A trait to supply a no action materializer for testing.
  * Created by alex on 02/07/16.
  */
trait Materialisers {

  implicit val materializer: Materializer = NoMaterializer
}

object NoMaterializer extends Materializer {
  def withNamePrefix(name: String) = throw new UnsupportedOperationException("NoMaterializer cannot be named")
  implicit def executionContext = throw new UnsupportedOperationException("NoMaterializer does not have an execution context")
  def materialize[Mat](runnable: Graph[ClosedShape, Mat]) =
    throw new UnsupportedOperationException("No materializer was provided, probably when attempting to extract a response body, but that body is a streamed body and so requires a materializer to extract it.")
  override def scheduleOnce(delay: FiniteDuration, task: Runnable): Cancellable =
    throw new UnsupportedOperationException("NoMaterializer can't schedule tasks")
  override def schedulePeriodically(initialDelay: FiniteDuration, interval: FiniteDuration, task: Runnable): Cancellable =
    throw new UnsupportedOperationException("NoMaterializer can't schedule tasks")
}

