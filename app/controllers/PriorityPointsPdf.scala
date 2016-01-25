package controllers

import javax.inject.Inject

import dao.{PriorityPointsConfigurationDao, GameDao}
import monads.FO
import pdf.{Client, PriorityPointsPdfFactory}
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Enumerator
import security.Definitions._

import scala.concurrent.ExecutionContext
import scalaz._
import Scalaz._
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
                                    gameDao: GameDao,
                                    /**
                                     * The game row factory used to get game row models.
                                     */
                                    priorityPointsPdfFactory: PriorityPointsPdfFactory,
                                    priorityPointsConfigurationDao: PriorityPointsConfigurationDao,
                                    messagesApi: MessagesApi, env: Env)(implicit ec: ExecutionContext) extends Secure {

  def priorityPoints(gameId: Long) = SecuredAction(authorization).async { implicit request =>
    val pp = for {
      priorityPointsConfiguration <- FO <~ priorityPointsConfigurationDao.get
      game <- FO <~ gameDao.findById(gameId)
    } yield {
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
            priorityPointsPdfFactory.generate(priorityPointsConfiguration, game.opponents, game.competition.isLeague, clientFilter, out)
        }}.
        as("application/pdf")
    }
    pp.getOrElse(NotFound)
  }
}
