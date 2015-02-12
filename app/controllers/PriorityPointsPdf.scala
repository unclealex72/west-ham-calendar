package controllers

import javax.inject.Inject

import pdf.PriorityPointsPdfFactory
import play.api.libs.iteratee.Enumerator
import play.mvc.Controller
import securesocial.core.Authorization
import uk.co.unclealex.hammers.calendar.dao.Transactional
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by alex on 12/02/15.
 */
class PriorityPointsPdf @Inject() (
                                    /**
                                     * The authorization object used to check a user is authorised.
                                     */
                                    val authorization: Authorization,
                                    /**
                                     * The transactional object used to get games and seasons.
                                     */
                                    tx: Transactional,
                                    /**
                                     * The game row factory used to get game row models.
                                     */
                                    priorityPointsPdfFactory: PriorityPointsPdfFactory) extends Controller with Secure {

  def priorityPoints(gameId: Long) = SecuredAction(authorization) { implicit request =>
    tx { gameDao =>
      gameDao.findById(gameId).filter(_.location.isAway) match {
        case Some(game) => {
          Ok.chunked {
            Enumerator.outputStream {
              out =>
                priorityPointsPdfFactory.generate(game.opponents, game.competition.isLeague, out)}}.
            as("application/pdf")
        }
        case None => NotFound
      }
    }
  }
}
