package controllers



import dao.{PriorityPointsConfigurationDao, GameDao}
import monads.FO
import pdf.{Client, PriorityPointsPdfFactory}
import play.api.i18n.MessagesApi
import play.api.libs.iteratee.Enumerator
import scaldi.{Injectable, Injector}
import security.Definitions._

import scala.concurrent.ExecutionContext
import scalaz._
import Scalaz._
/**
 * Created by alex on 12/02/15.
 */
case class PriorityPointsPdf(implicit injector: Injector) extends Secure with Injectable {

  val authorization: Auth = inject[Auth]
  val gameDao: GameDao = inject[GameDao]
  val priorityPointsPdfFactory: PriorityPointsPdfFactory = inject[PriorityPointsPdfFactory]
  val priorityPointsConfigurationDao: PriorityPointsConfigurationDao = inject[PriorityPointsConfigurationDao]
  val messagesApi: MessagesApi = inject[MessagesApi]
  val env: Env = inject[Env]
  implicit val ec: ExecutionContext = inject[ExecutionContext]

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
