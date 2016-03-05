package monads

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._

/**
  * Monad compositions for Futures and Scalaz Eithers.
  * Created by alex on 21/01/16.
  */
trait FE[L] {

  type Result[R] = EitherT[Future, L, R]

  def <~[R](v: Future[\/[L, R]]): Result[R] = EitherT(v)

  def <~[R](v: Future[R])(implicit ec: ExecutionContext): Result[R] = <~(v.map(_.right))

  def <~[R](v: \/[L, R]): Result[R] = EitherT(Future.successful(v))

  def <~[R](v: R)(implicit ev: Applicative[Result]): Result[R] = v.point[Result]

}

object FE extends FE[NonEmptyList[String]] {

  def apply[R](r: Result[R]): Future[\/[NonEmptyList[String], R]] = r.run

}