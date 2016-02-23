package monads

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import scalaz.syntax.monad._
/**
  * Monad compositions for Futures and Options.
  * Created by alex on 21/01/16.
  */
trait FL {

  type Result[A] = ListT[Future, A]

  def <~[A](v: Future[List[A]]): Result[A] = ListT(v)

  def <~?[A](v: Future[Option[A]])(implicit ec: ExecutionContext): Result[A] = ListT(v.map(_.toList))

  def <~?[A](v: Option[A])(implicit ec: ExecutionContext): Result[A] = ListT(Future.successful(v.toList))

  def <~~[A](v: Future[Seq[A]])(implicit ec: ExecutionContext): Result[A] = ListT(v.map(_.toList))

  def <~~[A](v: Seq[A])(implicit ec: ExecutionContext): Result[A] = ListT(Future.successful(v.toList))

  def <~[A](v: Future[A])(implicit ec: ExecutionContext): Result[A] = ListT(v.map(List(_)))

  def <~[A](v: List[A]): Result[A] = ListT(Future.successful(v))

  def <~[A](v: A)(implicit ev: Applicative[Result]): Result[A] = v.point[Result]

}

object FL extends FL {

  def apply[A](b: Result[A]): Future[List[A]] = b.run

  def unit[A](b: Result[A])(implicit ec: ExecutionContext): Future[Unit] = b.run.map(_ => ())

}