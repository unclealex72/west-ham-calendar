package monads

import cats.data.OptionT

import scala.concurrent.{ExecutionContext, Future}

/**
  * Monad compositions for Futures and Options.
  * Created by alex on 21/01/16.
  */
object FO {

  type FutureOption[A] = OptionT[Future, A]

  def apply[A](v: Future[Option[A]]): FutureOption[A] = OptionT(v)

  def apply[A](v: Future[A])(implicit ec: ExecutionContext): FutureOption[A] = FO(v.map(Some(_)))

  def apply[A](v: Option[A]): FutureOption[A] = OptionT(Future.successful(v))

}
