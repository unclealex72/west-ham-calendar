package controllers

import model.Game
import models.TicketType.PriorityPointTicketType
import models.{Links, TicketType}
import play.api.mvc.{AnyContent, Request}

/**
 * Created by alex on 13/02/15.
 */
trait LinkFactories extends Secure {

  def ticketLinksFactory(implicit request: Request[_ <: AnyContent]): Game => TicketType => Links = game => ticketType => {
    secureLinks(game, Links()) { links =>
      ticketType match {
        case PriorityPointTicketType =>
          val url = controllers.routes.PriorityPointsPdf.priorityPoints(game.id).absoluteURL()
          links.withLink("form", url)
        case _ => links
      }
    }
  }

  def gameRowLinksFactory(implicit request: Request[_ <: AnyContent]): Game => Links = game => {
    Links().withSelf(controllers.routes.Application.game(game.id).absoluteURL())
  }

  def secureLinks(game: Game, links: Links)(f: Links => Links)(implicit request: Request[_ <: AnyContent]): Links = {
    f(links) //emailAndUsername.foldLeft(links) { (newLinks, _) => f(newLinks) }
  }
}
