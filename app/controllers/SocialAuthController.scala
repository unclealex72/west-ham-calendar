package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers._
import dates.ZonedDateTimeFactory
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AnyContent, Call, ControllerComponents, Request}
import security.Definitions._
import security.models.services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * The social auth controller.
  *
  */
class SocialAuthController @Inject() (val silhouette: DefaultSilhouette,
                                      val auth: Auth,
                                      val userService: UserService,
                                      override val controllerComponents: ControllerComponents,
                                      override val zonedDateTimeFactory: ZonedDateTimeFactory,
                                      val authInfoRepository: AuthInfoRepository,
                                      val socialProviderRegistry: SocialProviderRegistry,
                                      override implicit val ec: ExecutionContext) extends AbstractController(controllerComponents, zonedDateTimeFactory, ec) with Secure with I18nSupport with Logger {

  private val index: Call = routes.Application.index()

  /**
    * Authenticates a user against a social provider.
    *
    * @param provider The ID of the provider to authenticate against.
    * @return The result to display.
    */
  def authenticate(provider: String) = Action.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(value, Redirect(index))
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect(index).flashing("error" -> Messages("could.not.authenticate"))
    }
  }

  /**
    * Handles the Sign Out action.
    *
    * @return The result to display.
    */
  def signOut = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val result = Redirect(index)
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))

    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

}