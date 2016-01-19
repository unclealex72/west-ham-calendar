package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.User
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import pdf.{Client, PriorityPointsPdfFactory}
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Enumerator
import dao.Transactional
import play.api.libs.concurrent.Execution.Implicits._
import security.Definitions._

/**
 * Created by alex on 12/02/15.
 */
case class PriorityPointsPdf @Inject() (
                                    /**
                                     * The authorization object used to check a user is authorised.
                                     */
                                    val authorization: Auth,
                                    /**
                                     * The transactional object used to get games and seasons.
                                     */
                                    tx: Transactional,
                                    /**
                                     * The game row factory used to get game row models.
                                     */
                                    priorityPointsPdfFactory: PriorityPointsPdfFactory,
                                    messagesApi: MessagesApi, env: Env) extends Secure {

  def priorityPoints(gameId: Long) = SecuredAction(authorization) { implicit request =>
    tx { gameDao =>
      gameDao.findById(gameId).filter(_.location.isAway) match {
        case Some(game) => {
          val optionalClientNames = request.queryString.map(kv => (kv._1.toLowerCase, kv._2)).get("name")
          val clientFilter: Client => Boolean = optionalClientNames match {
            case Some(clientNames) => client => clientNames.exists { clientName =>
              client.name.toLowerCase.startsWith(clientName.toLowerCase)
            }
            case None => _ => true
          }
          Ok.chunked {
            Enumerator.outputStream {
              out =>
                priorityPointsPdfFactory.generate(game.opponents, game.competition.isLeague, clientFilter, out)}}.
            as("application/pdf")
        }
        case None => NotFound
      }
    }
  }
}
