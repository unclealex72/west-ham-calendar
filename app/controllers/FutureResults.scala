package controllers

import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 19/02/16.
  */
trait FutureResults extends Results {

  implicit val ec: ExecutionContext

  class FutureResultBuilder[B](fb: B => Result) {
    def foo[A](fa: Future[Option[A]])(f: A => Option[B]): Future[Result] = fa.map { a =>
      a.flatMap(f) match {
        case Some(b) => fb(b)
        case _ => NotFound
      }
    }

    def fo[A](fa: Future[Option[A]])(f: A => B): Future[Result] = foo(fa)(a => Some(f(a)))

    def f[A](fa: Future[A])(f: A => B): Future[Result] = fo(fa.map(Some(_)))(f)

  }

  object FutureResult extends FutureResultBuilder[Result](identity)

  def resultFooBuilder[A, B](fb: B => Result)(fa: Future[Option[A]])(f: A => Option[B]): Future[Result] =
    new FutureResultBuilder[B](fb).foo[A](fa)(f)

  def resultFoBuilder[A, B](fb: B => Result)(fa: Future[Option[A]])(f: A => B): Future[Result] =
    new FutureResultBuilder[B](fb).fo[A](fa)(f)

  def resultFBuilder[A, B](fb: B => Result)(fa: Future[A])(f: A => B): Future[Result] =
    new FutureResultBuilder[B](fb).f[A](fa)(f)

}
