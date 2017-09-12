package monads

import cats.data.{EitherT, NonEmptyList}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Monad compositions for Futures and Eithers.
  * Created by alex on 21/01/16.
  */
object FE {

  type FutureEither[L, R] = EitherT[Future, L, R]
  type FutureEitherNel[L, R] = EitherT[Future, NonEmptyList[L], R]

  def apply[L, R](v: Future[Either[L, R]]): FutureEither[L, R] = EitherT(v)

  def apply[L, R](v: Future[R])(implicit ec: ExecutionContext): FutureEither[L, R] = FE(v.map(Right(_)))

  def apply[L, R](v: Either[L, R]): FutureEither[L, R] = EitherT(Future.successful(v))

}