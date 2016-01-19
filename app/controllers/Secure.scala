package controllers

import play.api.mvc.{AnyContent, Request}
import security.Definitions._

/**
  * A trait to make using Silhouette a bit easier by providing default type parameters and methods to get a user's name
  * and email from a Silhouette request.
  * Created by alex on 17/01/16.
  */
trait Secure extends Sil {

  case class EmailAndUsername(email: String, name: String)

  def emailAndUsername(implicit request: Request[_ <: AnyContent]): Option[EmailAndUsername] = request match {
    case securedRequest: SecuredRequest[UserType] => toEmailAndUsername(securedRequest.identity)
    case userAwareRequest: UserAwareRequest[UserType] => userAwareRequest.identity.flatMap(toEmailAndUsername)
    case _ => None
  }

  def toEmailAndUsername(user: UserType): Option[EmailAndUsername] = {
    for {
      email <- user.email
      name <- user.fullName
    } yield EmailAndUsername(email, name)
  }
}
