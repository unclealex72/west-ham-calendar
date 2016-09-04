package update.tickets

import java.net.URI

import dates.PossiblyYearlessDateParser
import html._
import logging.{RemoteLogging, RemoteStream}
import models.TicketType._
import models._
import monads.{FE, FL}
import org.joda.time.DateTime
import play.api.libs.ws.WSClient
import update.WsBody
import xml.NodeExtensions

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{Elem, Node}
import scalaz._
import Scalaz._
import scala.collection.SortedMap
import scala.language.postfixOps

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerImpl @javax.inject.Inject() (val rootUri: URI, ws: WSClient)(implicit val ec: ExecutionContext) extends TicketsGameScanner with RemoteLogging with WsBody with NodeExtensions {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], List[GameUpdateCommand]]] = FE {
    latestSeason match {
      case Some(ls) =>
        val ticketsUri = rootUri.resolve("/Tickets/Match-Tickets")
        for {
          page <- FE <~ bodyXml(ticketsUri)(ws.url(_).get())
          gameUpdateCommands <- FE <~ createUpdateCommandsForAllTicketsPage(ls, page)
        } yield gameUpdateCommands
      case _ =>
        logger info "There are currently no games so tickets will not be searched for."
        FE <~ List.empty
    }
  }

  def createUpdateCommandsForAllTicketsPage(latestSeason: Int, page: Elem)(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], List[GameUpdateCommand]]] = {
    val matchRegex = "(?:Home|Away)-Matches".r
    val ticketPageUrls = for {
      a <- (page \\ "a").toList
      href <- a.attributes.asAttrMap.get("href").toList if matchRegex.findFirstMatchIn(href).isDefined
    } yield rootUri.resolve(new URI(href))
    val empty: Future[\/[NonEmptyList[String], List[GameUpdateCommand]]] = Future.successful(List.empty.right)
    ticketPageUrls.foldLeft(empty) {(existingGameUpdateCommands, ticketPageUrl) =>
      FE {
        for {
         eguc <- FE <~ existingGameUpdateCommands
         nguc <- FE <~ createUpdateCommandsForSinglePage(latestSeason, ticketPageUrl)
        } yield eguc ++ nguc
      }
    }
  }

  def createUpdateCommandsForSinglePage(latestSeason: Int, uri: URI)(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], List[GameUpdateCommand]]] = FE {
    logger info s"Found tickets page $uri"
    for {
      page <- FE <~ bodyXml(uri)(ws.url(_).get())
      ticketPageScanner <- FE <~ TicketPageScanner(page, latestSeason)(remoteStream)
      when <- FE <~ ticketPageScanner.findWhen.toRightDisjunction(NonEmptyList(s"Cannot find a date played at page $uri"))
    } yield ticketPageScanner.scanTicketDates(when).toList
  }

  case class TicketPageScanner(pageXml: Elem, latestSeason: Int)(implicit remoteStream: RemoteStream) {

    // look for <div class='matchDate'>Saturday 4 April 15:00</div>
    def findWhen: Option[DateTime] = {
      val dateTimes = for {
        node <- pageXml \\ "div" if node.hasClass("matchDate")
        dateTime <- PossiblyYearlessDateParser.forSeason(latestSeason)("d MMMM HH:mm", "dd MMMM HH:mm").find(node.text)
      } yield dateTime
      dateTimes.headOption
    }

    def scanTicketDates(dateTime: DateTime): Seq[GameUpdateCommand] = {
      for {
        htmlContent <- pageXml \\ "div" if htmlContent.hasClass("htmlContent")
        subTitle <- htmlContent \\ "div" if subTitle.hasClass("subTitle") && subTitle.text == "Match Event Information"
        gameUpdateCommand <- generateGameUpdateCommands(htmlContent.nonEmptyChildren, dateTime)
      } yield gameUpdateCommand
    }

    def generateGameUpdateCommands(divChildren: Seq[Node], dateTime: DateTime): Seq[GameUpdateCommand] = {
      val gameLocator = DatePlayedLocator(dateTime)
      case class State(currentTicketType: Option[TicketType] = None, gameUpdateCommands: Map[TicketType, GameUpdateCommand] = Map.empty) {
        def withTicketType(ticketType: TicketType) =
          this.copy(currentTicketType = Some(ticketType).filterNot(gameUpdateCommands.get(_).isDefined))
        def withSellingDateTime(dt: DateTime) =
          State(None, gameUpdateCommands ++ currentTicketType.map { tt =>
            logger info s"Found ticket type $tt on sale on $dt"
            tt -> createGameUpdateCommand(gameLocator, tt, dt)
          })
      }
      val finalState = divChildren.map(_.text).foldLeft(State()) { (state, text) =>
        TicketType(text) match {
          case Right(tt) => state.withTicketType(tt)
          case _ =>
            PossiblyYearlessDateParser.forSeason(latestSeason)("d MMMM", "dd MMMM").find(text) match {
              case Some(dt) =>
                state.withSellingDateTime(dt.withMillisOfDay(0).withHourOfDay(9))
              case _ => state
            }
        }
      }
      finalState.gameUpdateCommands.values.toSeq
    }

    def createGameUpdateCommand(gameLocator: GameLocator, ticketType: TicketType, dateTime: DateTime): GameUpdateCommand = {
      val updateCommandFactory: (GameLocator, DateTime) => GameUpdateCommand = ticketType match {
        case BondholderTicketType => BondHolderTicketsUpdateCommand.apply
        case PriorityPointTicketType => PriorityPointTicketsUpdateCommand.apply
        case SeasonTicketType => SeasonTicketsUpdateCommand.apply
        case AcademyTicketType => AcademyTicketsUpdateCommand.apply
        case GeneralSaleTicketType => GeneralSaleTicketsUpdateCommand.apply
      }
      updateCommandFactory(gameLocator, dateTime)
    }
  }
}
