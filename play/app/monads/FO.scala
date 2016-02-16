package monads

import scala.concurrent.Future
import scalaz._
import scalaz.syntax.monad._

/**
  * Monad compositions for Futures and Options.
  * Created by alex on 21/01/16.
  */
trait FO {

  type Result[A] = OptionT[Future, A]

    def <~[A](v: Future[Option[A]]): Result[A] = OptionT(v)

    def <~[A](v: Option[A]): Result[A] = OptionT(Future.successful(v))

    def <~[A](v: A)(implicit ev: Applicative[Result]): Result[A] = v.point[Result]

}

object FO extends FO {

  def apply[A](b: Result[A]): Future[Option[A]] = b.run
}