package security

import com.mohiva.play.silhouette.api.{Authorization, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import security.models.User

/**
  * Shortcuts for Silhouette types so the identity and authenticator types are all in the same place.
  */
object Definitions {

  type UserType = User
  type AuthenticatorType = CookieAuthenticator
  type Auth = Authorization[UserType, AuthenticatorType]
  type Env = Environment[UserType, AuthenticatorType]
  type Sil = Silhouette[UserType, AuthenticatorType]

}