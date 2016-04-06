package security

import com.mohiva.play.silhouette.api.{Authorization, Env, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import security.models.User

/**
  * Shortcuts for Silhouette types so the identity and authenticator types are all in the same place.
  */
object Definitions {

  type UserType = User
  type AuthenticatorType = CookieAuthenticator
  type Auth = Authorization[UserType, AuthenticatorType]
  trait DefaultEnv extends Env {
    type I = UserType
    type A = AuthenticatorType
  }
  type DefaultEnvironment = Environment[DefaultEnv]
  type DefaultSilhouette = Silhouette[DefaultEnv]

}