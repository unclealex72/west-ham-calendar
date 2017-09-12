package controllers

import java.time.ZonedDateTime

import dao.FatalErrorDao
import dates.ZonedDateTimeFactory
import logging.Fatal
import model.FatalError
import models.{FatalErrorReportTemplate, FatalErrorReports}
import models.FatalErrorReports._
import monads.FE
import monads.FE.FutureEitherNel
import play.api.i18n.MessagesApi
import play.api.mvc.ControllerComponents
import security.Definitions._
import cats.instances.future._
import scala.concurrent.ExecutionContext

class Errors @javax.inject.Inject() (val secret: SecretToken,
             val fatal: Fatal,
             val fatalErrorDao: FatalErrorDao,
             override val messagesApi: MessagesApi,
             override val controllerComponents: ControllerComponents,
             val env: DefaultEnvironment,
             val silhouette: DefaultSilhouette, override val zonedDateTimeFactory: ZonedDateTimeFactory,
             val auth: Auth, override implicit val ec: ExecutionContext) extends AbstractController(controllerComponents, zonedDateTimeFactory, ec) with Secret with LinkFactories {

  def message(secretPayload: String, id: Long) = Secret(secretPayload) {
    Action.async { implicit request =>
      fo(fatalErrorDao.findById(id)) { fatalError =>
        Ok(fatalError.message)
      }
    }
  }

  def fatalErrors(secretPayload: String) = Secret(secretPayload) {
    Action.async { implicit request =>
      val maybeSinceStr = request.getQueryString("since")
      val evFatalErrors: FutureEitherNel[String, List[FatalError]] = maybeSinceStr match {
        case Some(sinceStr) =>
          zonedDateTimeFactory.parse(sinceStr) match {
            case Right(since) => FE(fatalErrorDao.since(since))
            case Left(errors) => FE(Left(errors))
          }
        case None => FE(fatalErrorDao.getAll)
      }
      json(evFatalErrors) { fatalErrors =>
        val fatalErrorReports = fatalErrors.map { fatalError =>
          FatalErrorReportTemplate(fatalError.id, fatalError.at, fatalErrorReportLinksFactory.apply(fatalError))
        }
        FatalErrorReports[ZonedDateTime](fatalErrorReports)
      }
    }
  }
}

