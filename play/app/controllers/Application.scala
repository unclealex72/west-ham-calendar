package controllers

import dao.GameDao
import json.JsonResults
import models.GameRow._
import models.Globals
import play.api.i18n.MessagesApi
import play.api.mvc._
import scaldi.{Injectable, Injector}
import security.Definitions._
import services.GameRowFactory
import upickle.default._

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
    Ok(views.html.proto())
  }

  /**
   * Get the global information required: all seasons, all ticket types and the email of the logged in user if present.
   */
  def constants = UserAwareAction.async { implicit request =>
    val futureGlobals = gameDao.getAllSeasons.map { seasons =>
      val username = emailAndUsername.map(_.name)
      Globals(seasons.toList, username)
    }
    futureGlobals.map { globals =>
      Ok(views.js.constants(globals)).withHeaders(
        PRAGMA -> "no-cache", CACHE_CONTROL -> "no-cache, no-store, must-revalidate", EXPIRES -> "0")
    }
  }

  def games(season: Int) = UserAwareAction.async { implicit request =>
    val includeAttended = request.identity.isDefined
    gameDao.getAllForSeason(season).map { games =>
      json {
        Map("games" -> games.map(gameRowFactory.toRow(includeAttended, gameRowLinksFactory, ticketLinksFactory)))
      }
    }
  }

  def game(id: Long) = UserAwareAction.async { implicit request =>
    gameDao.findById(id).map {
      case Some(game) =>
        val includeAttended = request.identity.isDefined
        json {
            gameRowFactory.toRow(includeAttended, gameRowLinksFactory, ticketLinksFactory)(game)
        }
      case _ => NotFound
    }
  }

  def entry() = play.mvc.Results.TODO

  def seasons() = play.mvc.Results.TODO
}