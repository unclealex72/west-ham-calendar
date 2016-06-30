package controllers

import dao.FatalErrorDao
import dates.DateTimeImplicits._
import dates.JodaDateTime
import logging.{Fatal, RemoteStream}
import model.FatalError
import models.{FatalErrorReport, FatalErrorReportRel, FatalErrorReports}
import play.api.i18n.MessagesApi
import play.api.mvc._
import security.Definitions._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Scalaz._
import scalaz._

class Errors @javax.inject.Inject() (val secret: SecretToken,
             val fatal: Fatal,
             val fatalErrorDao: FatalErrorDao,
             val messagesApi: MessagesApi,
             val env: DefaultEnvironment,
             val silhouette: DefaultSilhouette,
             val auth: Auth,
             implicit val ec: ExecutionContext) extends Secret with JsonResults with LinkFactories {

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

