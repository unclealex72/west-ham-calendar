package controllers

import java.time.{LocalDate, ZonedDateTime}
import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.UserAwareRequest
import dao.GameDao
import dates.ZonedDateTimeExtensions._
import dates.ZonedDateTimeFactory
import models.EntryRel.{LOGIN, LOGOUT}
import models.GameRow._
import models.{EntryTemplate, SeasonTemplate, _}
import monads.FO
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContent, ControllerComponents}
import security.Definitions._
import services.GameRowFactory

import scala.collection.SortedSet
import scala.concurrent.ExecutionContext

class Application @Inject() (val gameDao: GameDao,
                             val gameRowFactory: GameRowFactory,
                             val secret: SecretToken,
                             override val messagesApi: MessagesApi,
                             override val controllerComponents: ControllerComponents,
                             val silhouette: DefaultSilhouette,
                             val auth: Auth,
                             override val zonedDateTimeFactory: ZonedDateTimeFactory,
                             override implicit val ec: ExecutionContext) extends AbstractController(
  controllerComponents, zonedDateTimeFactory, ec) with Secure with LinkFactories {

  def index = silhouette.UserAwareAction {
    Ok(views.html.index())
  }

  def game(id: Long) = silhouette.UserAwareAction.async { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    json(gameDao.findById(id)) { game =>
      val includeAttended = request.identity.isDefined
      gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended))(game)
    }
  }

  def entry() = silhouette.UserAwareAction.async { implicit request: UserAwareRequest[DefaultEnv, AnyContent] =>
    json(FO(gameDao.getAll)) { games =>
      val includeAttended = request.identity.isDefined
      val gamesBySeason = games.groupBy(_.season)
      val seasons = gamesBySeason.map {
        case (year, gamesForSeason) =>
          val gamesByMonth = gamesForSeason.groupBy { game =>
            game.at.map { dt => (dt.getMonth, dt.getYear) }
          }.toSeq.flatMap(monthGame => monthGame._1.map(monthYear => (monthYear._1, monthYear._2, monthGame._2)))
          val months = gamesByMonth.map { mygs =>
            val (month, year, games) = mygs
            val gameRows = games.map(gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended)))
            val firstDayInMonth = LocalDate.of(year, month, 1).atStartOfDay(zonedDateTimeFactory.zoneId)
            MonthTemplate(firstDayInMonth, SortedSet.empty[GameRow[ZonedDateTime]] ++ gameRows)
          }
          SeasonTemplate(year, SortedSet.empty[MonthTemplate[ZonedDateTime]] ++ months)
      }

      val fullName: Option[String] = request.identity.flatMap(_.fullName)
      val links = Links.
        withLink(LOGOUT.asInstanceOf[EntryRel], routes.SocialAuthController.signOut().absoluteURL()).
        withLink(LOGIN, routes.SocialAuthController.authenticate("google").absoluteURL()).
        withSelf(routes.Application.entry().absoluteURL())
      EntryTemplate(fullName, SortedSet.empty[SeasonTemplate[ZonedDateTime]] ++ seasons, links)
    }
  }
}