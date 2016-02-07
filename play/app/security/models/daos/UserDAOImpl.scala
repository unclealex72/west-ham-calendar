package security.models.daos

import java.util.UUID


import com.mohiva.play.silhouette.api.LoginInfo
import security.models.User

import scala.concurrent.ExecutionContext

/**
 * Give access to the user object.
 */
class UserDAOImpl(credentialsStorage: CredentialsStorage)(implicit ec: ExecutionContext) extends UserDAO {

  implicit val loginInfoKeySerialiser: LoginInfo => String = loginInfo => s"userLoginInfo:${loginInfo.providerID}:${loginInfo.providerKey}"
  implicit val uuidKeySerialiser: UUID => String = uuid => s"uuid:$uuid"

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = credentialsStorage.get(loginInfo)

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = credentialsStorage.get(userID)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    for {
      _ <- credentialsStorage.set(user.userID, user)
      _ <- credentialsStorage.set(user.loginInfo, user)
    } yield user
  }
}