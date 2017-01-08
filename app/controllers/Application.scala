package controllers

import javax.inject.Inject

import dao.GameDao
import dates.SharedDate
import models.RootRel.{LOGIN, LOGOUT}
import models.GameRow._
import models._
import play.api.i18n.MessagesApi
import play.api.mvc._
import security.Definitions._
import services.GameRowFactory
import upickle.default._
import models.Root
import models.Season

import scala.collection.SortedSet
import scala.concurrent.ExecutionContext

class Application @Inject() (val gameDao: GameDao,
                             val gameRowFactory: GameRowFactory,
                             val secret: SecretToken,
                             val messagesApi: MessagesApi,
                             val silhouette: DefaultSilhouette,
                             val auth: Auth,
                             implicit val ec: ExecutionContext) extends Secure with LinkFactories with JsonResults {

  def index = silhouette.UserAwareAction {
    Ok(views.html.index())
  }

  def game(id: Long) = silhouette.UserAwareAction.async { implicit request =>
    jsonFo(gameDao.findById(id)) { game =>
      val includeAttended = request.identity.isDefined
      gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended), ticketLinksFactory)(game)
    }
  }

  def entry() = silhouette.UserAwareAction.async { implicit request =>
    jsonF(gameDao.getAll) { games =>
      val includeAttended = request.identity.isDefined
      val gamesBySeason = games.groupBy(_.season)
      val maybeLatestSeason = if (gamesBySeason.isEmpty) None else Some(gamesBySeason.keys.max)
      val seasons = for {
        latestSeason <- maybeLatestSeason.toSeq
        (season, gamesForSeason) <- gamesBySeason.toSeq
      } yield {
        val gamesByMonth = gamesForSeason.groupBy { game =>
          game.at.map { dt => (dt.getMonthOfYear, dt.getYear) }
        }.toSeq.flatMap(monthGame => monthGame._1.map(monthYear => (monthYear._1, monthYear._2, monthGame._2)))
        val months = gamesByMonth.map { mygs =>
          val (month, year, games) = mygs
          val gameRows = games.map(gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended), ticketLinksFactory))
          val firstDayInMonth = SharedDate(year, month, 2, 0, 0, 0, 0, 0)
          Month(firstDayInMonth, gameRows.sorted)
        }
        Season(season, season == latestSeason, months.sorted)
      }

      val fullName: Option[String] = request.identity.flatMap(_.fullName)
      val authLinks: Map[RootRel, Call] = fullName match {
        case Some(_) => Map(LOGOUT -> routes.SocialAuthController.signOut())
        case _ => Map(LOGIN -> routes.SocialAuthController.authenticate("google"))
      }
      val links = Links.
        withLinks(authLinks.mapValues(_.absoluteURL())).
        withSelf(routes.Application.entry().absoluteURL())
      Root(fullName, seasons.toSeq.sorted, links)
    }
  }
}