package controllers

import dao.FatalErrorDao
import dates.{JodaDateTime, DateTimeImplicits, SharedDate}
import logging.{Fatal, RemoteStream}
import model.FatalError
import models.{FatalErrorReportRel, FatalErrorReport, FatalErrorReports}
import play.api.i18n.MessagesApi
import play.api.mvc._
import scaldi.{Injectable, Injector}
import security.Definitions._

import scala.concurrent.{Future, ExecutionContext}
import DateTimeImplicits._
import scalaz._
import Scalaz._

class Errors(implicit injector: Injector) extends Secret with JsonResults with LinkFactories with Injectable {

  val secret: SecretToken = inject[SecretToken]
  val fatal: Fatal = inject[Fatal]
  val fatalErrorDao: FatalErrorDao = inject[FatalErrorDao]
  val messagesApi: MessagesApi = inject[MessagesApi]
  val env: Env = inject[Env]
  implicit val ec: ExecutionContext = inject[ExecutionContext]

  def quickFail = {
    Action { implicit request =>
      implicit val remoteStream: RemoteStream = new RemoteStream {
        override def logToRemote(message: String): Unit = println(message)
      }
      val linkBuilder: FatalError => String = fe => fatalErrorReportLinksFactory(request)(fe).required(FatalErrorReportRel.MESSAGE)
      fatal.fail("Poo", linkBuilder)
      Ok
    }
  }

  def message(secretPayload: String, id: Long) = Secret(secretPayload) {
    Action.async { implicit request =>
      FutureResult.fo(fatalErrorDao.findById(id)) { fatalError =>
        Ok(fatalError.message)
      }
    }
  }

  def fatalErrors(secretPayload: String) = Secret(secretPayload) {
    Action.async { implicit request =>
      def errors(fErrors: Future[List[FatalError]]) = jsonF(fErrors) { errors =>
        val fatalErrorReports = errors.map { fatalError =>
          FatalErrorReport(fatalError.id, fatalError.at, fatalErrorReportLinksFactory.apply(fatalError))
        }
        FatalErrorReports(fatalErrorReports)
      }
      request.getQueryString("since").map(JodaDateTime(_)) match {
        case None => errors(fatalErrorDao.getAll)
        case Some(Success(since)) => errors(fatalErrorDao.since(since))
        case Some(Failure(msgs)) => Future.successful(BadRequest(msgs.toList.mkString("\n")))
      }
    }
  }
}

