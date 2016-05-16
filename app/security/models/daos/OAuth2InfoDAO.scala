package security.models.daos



import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
 * The DAO to store the OAuth2 information.
 *
 * Note: Not thread safe, demo only.
 */
class OAuth2InfoDAO @Inject() (val credentialsStorage: CredentialsStorage, implicit val ec: ExecutionContext) extends AuthInfoRepository {

  implicit val loginInfoKeySerialiser: LoginInfo => String = loginInfo => s"oauth2LoginInfo:${loginInfo.providerID}:${loginInfo.providerKey}"


  override def add[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] = for {
    _ <- credentialsStorage.set(loginInfo, authInfo)
  } yield authInfo


  override def update[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] = add(loginInfo, authInfo)

  override def remove[T <: AuthInfo](loginInfo: LoginInfo)(implicit tag: ClassTag[T]): Future[Unit] = {
    credentialsStorage.remove(loginInfo)
  }

  override def save[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  override def find[T <: AuthInfo](loginInfo: LoginInfo)(implicit tag: ClassTag[T]): Future[Option[T]] = credentialsStorage.get(loginInfo)
}