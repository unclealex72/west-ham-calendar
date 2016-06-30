package controllers

import dao.{GameDao, PriorityPointsConfigurationDao}
import monads.FO
import pdf.{Client, PriorityPointsPdfFactory}
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Action
import security.Definitions._

import scalaz._
import Scalaz._
import scala.concurrent.ExecutionContext
/**
 * Created by alex on 12/02/15.
 */
class PriorityPointsPdf @javax.inject.Inject() (val gameDao: GameDao,
                                                val priorityPointsPdfFactory: PriorityPointsPdfFactory,
                                                val priorityPointsConfigurationDao: PriorityPointsConfigurationDao,
                                                val messagesApi: MessagesApi,
                                                val silhouette: DefaultSilhouette,
                                                val auth: Auth,
                                                implicit val ec: ExecutionContext) extends Secure {

  def priorityPoints(gameId: Long) = SecuredAction.async { implicit request =>
    val pp = FO {
      for {
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
          as("application/pdf").withHeaders("Content-Disposition" -> s"attachment; filename=${game.opponents}-${game.competition.name}.pdf".toLowerCase)
      }
    }
    pp.map { _.getOrElse(NotFound) }
  }
}
