package controllers

import model.Game
import models.TicketType.PriorityPointTicketType
import models._
import play.api.mvc.{AnyContent, Request}

/**
 * Created by alex on 13/02/15.
 */
trait LinkFactories extends Secure {

  def ticketLinksFactory(implicit request: Request[_ <: AnyContent]): Game => TicketType => Links[TicketingInformationRel] = game => ticketType => {
    secureLinks(game, Links[TicketingInformationRel]()) { links =>
      ticketType match {
        case PriorityPointTicketType =>
          val url = controllers.routes.PriorityPointsPdf.priorityPoints(game.id).absoluteURL()
          links.withLink(TicketingInformationRel.Form, url)
        case _ => links
      }
    }
  }

  def gameRowLinksFactory(implicit request: Request[_ <: AnyContent]): Game => Links[GameRowRel] = game => {
    Links().withSelf(controllers.routes.Application.game(game.id).absoluteURL())
  }

  def secureLinks[R <: Rel](game: Game, links: Links[R])(f: Links[R] => Links[R])(implicit request: Request[_ <: AnyContent]): Links[R] = {
    emailAndUsername.foldLeft(links) { (newLinks, _) => f(newLinks) }
  }
}
