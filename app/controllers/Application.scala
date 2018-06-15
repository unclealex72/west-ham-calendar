package controllers

import javax.inject.Inject
import dao.GameDao
import dates.SharedDate
import model.Game
import models.EntryRel.{LOGIN, LOGOUT}
import models.GameRow._
import models._
import play.api.i18n.MessagesApi
import play.api.mvc._
import org.joda.time.format.DateTimeFormat
import security.Definitions._
import services.GameRowFactory
import models.Entry
import models.Season
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.SortedSet
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

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
      val seasons = gamesBySeason.map {
        case (year, gamesForSeason) =>
          val gamesByMonth = gamesForSeason.groupBy { game =>
            game.at.map { dt => (dt.getMonthOfYear, dt.getYear) }
          }.toSeq.flatMap(monthGame => monthGame._1.map(monthYear => (monthYear._1, monthYear._2, monthGame._2)))
          val months = gamesByMonth.map { mygs =>
            val (month, year, games) = mygs
            val gameRows = games.map(gameRowFactory.toRow(includeAttended, gameRowLinksFactory(includeAttended), ticketLinksFactory))
            val firstDayInMonth = SharedDate(year, month, 2, 0, 0, 0, 0, 0)
            Month(firstDayInMonth, SortedSet.empty[GameRow] ++ gameRows)
          }
          Season(year, SortedSet.empty[Month] ++ months)
      }

      val fullName: Option[String] = request.identity.flatMap(_.fullName)
      val links = Links.
        withLink(LOGOUT.asInstanceOf[EntryRel], routes.SocialAuthController.signOut().absoluteURL()).
        withLink(LOGIN, routes.SocialAuthController.authenticate("google").absoluteURL()).
        withSelf(routes.Application.entry().absoluteURL())
      Entry(fullName, SortedSet.empty[Season] ++ seasons, links)
    }
  }

  def list() = Action.async { implicit request =>
    Try(request.queryString.getOrElse("season", Seq.empty).headOption.map(Integer.parseInt)) match {
      case Success(maybeSeason) =>
        val eventualMaybeSeason: Future[Option[Int]] = maybeSeason match {
          case Some(season) => Future.successful(Some(season))
          case None => gameDao.getLatestSeason
        }
        eventualMaybeSeason.flatMap {
          case Some(season) =>
            val eventualGames: Future[List[Game]] = gameDao.getAllForSeason(season)
            eventualGames.map {
              case Nil => NotFound("")
              case games =>
                def formatter(pattern: String): DateTime => String = {
                  val dateTimeFormat = DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.forID("Europe/London"))
                  dt => {
                    dateTimeFormat.print(dt.toLocalDateTime)
                  }
                }
                Ok(views.html.list(games, season, formatter("EEEE dd MMM yyyy"), formatter("HH:mm")))
            }
          case None => Future.successful(NotFound(""))
        }
      case Failure(e) => Future.successful(BadRequest(e.getMessage))
    }
  }
}