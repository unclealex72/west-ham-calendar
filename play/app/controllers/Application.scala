package controllers

import dao.GameDao
import models.EntryRel.{LOGOUT, LOGIN, SEASONS}
import models.GameRow._
import models.SeasonRel.GAMES
import models._
import play.api.i18n.MessagesApi
import play.api.mvc._
import scaldi.{Injectable, Injector}
import security.Definitions._
import services.GameRowFactory
import upickle.default._
import models.Entry._
import models.Seasons._

import scala.concurrent.ExecutionContext

class Application(implicit injector: Injector) extends Secure with LinkFactories with JsonResults with Injectable {

  implicit val authorization: Auth = inject[Auth]
  val gameDao: GameDao = inject[GameDao]
  val gameRowFactory: GameRowFactory = inject[GameRowFactory]
  val messagesApi: MessagesApi = inject[MessagesApi]
  val env: Env = inject[Env]
  implicit val ec: ExecutionContext = inject[ExecutionContext]

  /**
   * Redirect to the  homepage.
   */
  def index = Action {
    Ok(views.html.index())
  }

  /**
    * Redirect to the  homepage.
    */
  def proto = Action {
    Ok(views.html.proto())
  }

  def games(season: Int) = UserAwareAction.async { implicit request =>
    jsonF(gameDao.getAllForSeason(season)) { games =>
      val includeAttended = request.identity.isDefined
      Map("games" -> games.map(gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended), ticketLinksFactory)))
    }
  }

  def game(id: Long) = UserAwareAction.async { implicit request =>
    jsonFo(gameDao.findById(id)) { game =>
      val includeAttended = request.identity.isDefined
      gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended), ticketLinksFactory)(game)
    }
  }

  def matchReport(gameId: Long) = Action.async {
    FutureResult.foo(gameDao.findById(gameId)) { game =>
      game.matchReport.map { matchReport => TemporaryRedirect(matchReport) }
    }
  }

  def entry() = UserAwareAction { implicit request =>
    json[Entry] {
      val fullName: Option[String] = request.identity.flatMap(_.fullName)
      val (authRel, authLink): (EntryRel, String) = if (fullName.isDefined) {
        (LOGOUT, routes.SocialAuthController.signOut().absoluteURL())
      }
      else {
        (LOGIN, routes.SocialAuthController.authenticate("google").absoluteURL())
      }
      val links = Links.
        withLink(SEASONS.asInstanceOf[EntryRel], routes.Application.seasons().absoluteURL()).
        withSelf(routes.Application.entry().absoluteURL()).
        withLink(authRel, authLink)
      Entry(fullName, links)
    }
  }

  def seasons() = UserAwareAction.async { implicit request =>
    jsonF(gameDao.getAllSeasons) { years =>
      val seasons = years.toSeq.map { year =>
        val seasonLinks = Links.withLink(GAMES.asInstanceOf[SeasonRel], routes.Application.games(year).absoluteURL())
        Season(year, seasonLinks)
      }
      Seasons(seasons, Links.withSelf(routes.Application.seasons().absoluteURL()))
    }
  }
}