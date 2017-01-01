package controllers

import model.{FatalError, Game}
import models.GameRowRel._
import models.TicketType.PriorityPointTicketType
import models._
import play.api.mvc.{AnyContent, Request}

/**
 * Created by alex on 13/02/15.
 */
trait LinkFactories extends Secure with Secret with FutureResults {

  def ticketLinksFactory(implicit request: Request[_ <: AnyContent]): Game => TicketType => Links[TicketingInformationRel] = game => ticketType => {
    secureLinks(game, Links[TicketingInformationRel]()) { links =>
      ticketType match {
        case _ => links
      }
    }
  }

  def gameRowLinksFactory(includeUpdates: Boolean)(implicit request: Request[_ <: AnyContent]): Game => Links[GameRowRel] = game => {
    val links = Links
      .withSelf[GameRowRel](controllers.routes.Application.game(game.id).absoluteURL())
      .withLink(GameRowRel.LOCATION, controllers.routes.Location.location(game.id).absoluteURL())
      .withLink(GameRowRel.MATCH_REPORT, game.matchReport)
      .withLink(GameRowRel.HOME_LOGO, game.homeTeamImageLink)
      .withLink(GameRowRel.AWAY_LOGO, game.awayTeamImageLink)
      .withLink(GameRowRel.COMPETITION_LOGO, game.competitionImageLink)
    if (includeUpdates) {
        links.withLink(UNATTEND, controllers.routes.Update.unattend(game.id).absoluteURL())
             .withLink(ATTEND, controllers.routes.Update.attend(game.id).absoluteURL())
    }
    else {
      links
    }
  }

  def fatalErrorReportLinksFactory(implicit request: Request[_ <: AnyContent]): FatalError => Links[FatalErrorReportRel] = fatalError => {
    Links.withLink(FatalErrorReportRel.MESSAGE, routes.Errors.message(secret.token, fatalError.id).absoluteURL())
  }

  def secureLinks[R <: Rel](game: Game, links: Links[R])(f: Links[R] => Links[R])(implicit request: Request[_ <: AnyContent]): Links[R] = {
    emailAndUsername.foldLeft(links) { (newLinks, _) => f(newLinks) }
  }
}
