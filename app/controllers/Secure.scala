package controllers

import com.mohiva.play.silhouette.api.actions.{SecuredActionBuilder, SecuredRequest, UserAwareRequest}
import play.api.mvc.{AnyContent, Controller, Request}
import security.Definitions
import security.Definitions._

import scala.concurrent.Future

/**
  * A trait to make using Silhouette a bit easier by providing default type parameters and methods to get a user's name
  * and email from a Silhouette request.
  * Created by alex on 17/01/16.
  */
trait Secure extends Controller {

  val silhouette: DefaultSilhouette
  val auth: Auth

  case class EmailAndUsername(email: String, name: String)

  def emailAndUsername(implicit request: Request[_ <: AnyContent]): Option[EmailAndUsername] = request match {
    case securedRequest: SecuredRequest[DefaultEnv @unchecked, _] => toEmailAndUsername(securedRequest.identity)
    case userAwareRequest: UserAwareRequest[DefaultEnv @unchecked, _] => userAwareRequest.identity.flatMap(toEmailAndUsername)
    case _ => None
  }

  def toEmailAndUsername(user: Definitions.UserType): Option[EmailAndUsername] = {
    for {
      email <- user.email
      name <- user.fullName
    } yield EmailAndUsername(email, name)
  }

  //def SecuredAction: SecuredActionBuilder[DefaultEnv] = silhouette.SecuredAction(auth)

  def SecuredAction: SecuredActionBuilder[DefaultEnv] = silhouette.SecuredAction(auth)

}
