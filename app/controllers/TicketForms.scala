package controllers

import models.{PriorityPointTicketType, TicketType}
import play.api.mvc.Request
import securesocial.core.RequestWithUser
import uk.co.unclealex.hammers.calendar.model.Game

/**
 * Created by alex on 13/02/15.
 */
trait TicketForms extends Secure {

  def ticketFormUrlFactory(implicit request: Request[_ <: Any]): TicketType => Option[Game => String] = {
    emailAndName match {
      case Some(_) => {
        Map(PriorityPointTicketType.asInstanceOf[TicketType] -> { (game: Game) =>
          routes.PriorityPointsPdf.priorityPoints(game.id).absoluteURL()
        }).get
      }
      case None => {
        (ticketType: TicketType) => None
      }
    }
  }

}
