package controllers

import java.io.{PrintWriter, StringWriter}
import java.time.ZonedDateTime

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import cats.data.NonEmptyList
import dates.ZonedDateTimeFactory
import io.circe.{Decoder, Encoder}
import monads.FO.FutureOption
import play.api.libs.circe.Circe
import play.api.mvc.{AnyContent, ControllerComponents, Request, Result, AbstractController => PlayAbstractController}

import scala.concurrent.{ExecutionContext, Future, Promise}
import io.circe.syntax._
import logging.RemoteStream
import monads.FE.FutureEitherNel
import monads.FO

import scala.util.{Failure, Success}

/**
  * Created by alex on 15/07/17
  **/
class AbstractController(
                          override val controllerComponents: ControllerComponents,
                          val zonedDateTimeFactory: ZonedDateTimeFactory,
                          implicit val ec: ExecutionContext) extends PlayAbstractController(controllerComponents) with Circe {

  implicit val zonedDateTimeEncoder: Encoder[ZonedDateTime] = zonedDateTimeFactory.encoder
  implicit val zonedDateTimeDecoder: Decoder[ZonedDateTime] = zonedDateTimeFactory.decoder

  def fo[A](foa: FutureOption[A])(resultBuilder: A => Result)
              (implicit request: Request[AnyContent]): Future[Result] = {
    foa.value.map {
      case Some(a) => resultBuilder(a)
      case _ => NotFound
    }
  }

  def json[A, B](emt: FutureOption[A])(resultBuilder: A => B)
                (implicit request: Request[AnyContent], encoder: Encoder[B]): Future[Result] = {
    fo(emt)(t => Ok(resultBuilder(t).asJson))
  }


  def fe[A, B](fea: FutureEitherNel[String, A])(resultBuilder: A => Result)
              (implicit request: Request[AnyContent]): Future[Result] = {
    fea.value.map {
      case Right(a) => resultBuilder(a)
      case Left(errors) =>
        val errorObj = Map("errors" -> errors.toList)
        BadRequest(errorObj.asJson)
    }
  }

  def json[A, B](emt: FutureEitherNel[String, A])(resultBuilder: A => B)
                (implicit request: Request[AnyContent], encoder: Encoder[B]): Future[Result] = {
    fe(emt)(t => Ok(resultBuilder(t).asJson))
  }

  def chunked[A](messageProvider: RemoteStream => FutureEitherNel[String, A])(resultReporter: A => String)(implicit request: Request[AnyContent]): Result = {
    def peekMatValue[T, M](src: Source[T, M]): (Source[T, M], Future[M]) = {
      val p = Promise[M]
      val s = src.mapMaterializedValue { m =>
        p.trySuccess(m)
        m
      }
      (s, p.future)
    }
    val (queueSource, eventualQueue) = peekMatValue(Source.queue[String](128, OverflowStrategy.backpressure))
    eventualQueue.map { queue =>
      def offer(message: Any) = queue.offer(s"$message\n")
      val remoteStream: RemoteStream = (message: String) => offer(message)
      messageProvider(remoteStream).value.andThen {
        case Success(Right(result)) =>
          offer(resultReporter(result))
        case Success(Left(messages)) =>
          messages.foldLeft(Future.successful({})) { (acc, message) =>
            acc.map(_ => offer(message))
          }
        case Failure(e) =>
          val errorWriter = new StringWriter()
          val errorPrinter = new PrintWriter(errorWriter)
          errorPrinter.println(e.getMessage)
          e.printStackTrace(errorPrinter)
          offer(errorWriter)
          queue.complete()
      }.map(_ => queue.complete())
    }
    Ok.chunked(queueSource).as(TEXT)
  }
}
