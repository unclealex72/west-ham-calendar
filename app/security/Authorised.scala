package security

import com.typesafe.scalalogging.slf4j.StrictLogging
import play.api.i18n.Messages
import play.api.mvc.Request
import security.Definitions._

import scala.concurrent.Future

/**
 * An Authorization object that allows users with known email addresses to be authorised.
 * @author alex
 *
 */
case class Authorised(
  /**
   * The email addresses that are allowed to login.
   */
  emailAddresses: Seq[String]) extends Auth with StrictLogging {


  override def isAuthorized[B](
    identity: UserType,
    authenticator: AuthenticatorType)(implicit request: Request[B]): Future[Boolean] = Future.successful {
      identity.email match {
        case Some(email) =>
          val authorized = emailAddresses contains email
          if (!authorized) {
            logger warn s"Unauthorised user $email has attempted to log in."
          }
          authorized
        case None => false
      }
  }
}
