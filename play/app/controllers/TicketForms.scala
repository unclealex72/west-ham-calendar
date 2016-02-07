package controllers

import model.Game
import models.{PriorityPointTicketType, TicketType}
import play.api.mvc.{AnyContent, Request}

/**
 * Created by alex on 13/02/15.
 */
trait TicketForms extends Secure {

  def ticketFormUrlFactory(implicit request: Request[_ <: AnyContent]): TicketType => Game => Option[String] = ticketType => game => {
    emailAndUsername.map(_ => controllers.routes.PriorityPointsPdf.priorityPoints(game.id).absoluteURL()).
      filter(_ => ticketType == PriorityPointTicketType)
  }

}
