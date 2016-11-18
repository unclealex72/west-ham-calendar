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
import scala.xml.{Elem, Node, Text}
import scalaz._
import Scalaz._
import scala.language.postfixOps

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerImpl @javax.inject.Inject() (val rootUri: URI, ws: WSClient)(implicit val ec: ExecutionContext) extends TicketsGameScanner with RemoteLogging with WsBody with NodeExtensions {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = FE {
    latestSeason match {
      case Some(ls) =>
        val ticketsUri = rootUri.resolve("/tickets/match-tickets")
        for {
          page <- FE <~ bodyXml(ticketsUri)(ws.url(_).get())
          gameUpdateCommands <- FE <~ createUpdateCommandsForAllTicketsPage(ls, page)
        } yield gameUpdateCommands
      case _ =>
        logger info "There are currently no games so tickets will not be searched for."
        FE <~ Seq.empty
    }
  }

  def createUpdateCommandsForAllTicketsPage(latestSeason: Int, page: Elem)(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = {
    val ticketPageUrls = for {
      a <- (page \\ "a").toList if a.hasClass("tickets-more-info")
      href <- a.attributes.asAttrMap.get("href").toList if href.contains("away-matches")
    } yield rootUri.resolve(new URI(href))
    val empty: Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = Future.successful(Seq.empty.right)
    ticketPageUrls.foldLeft(empty) {(existingGameUpdateCommands, ticketPageUrl) =>
      FE {
        for {
         eguc <- FE <~ existingGameUpdateCommands
         nguc <- FE <~ createUpdateCommandsForSinglePage(latestSeason, ticketPageUrl)
        } yield eguc ++ nguc
      }
    }
  }

  def createUpdateCommandsForSinglePage(latestSeason: Int, uri: URI)(implicit remoteStream: RemoteStream): Future[\/[NonEmptyList[String], Seq[GameUpdateCommand]]] = FE {
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
      logger info s"Scanning for tickets for a game taking place at $dateTime"
      def extractTexts(node: Node): Seq[String] = {
        node.child.flatMap {
          case text: Text => Some(text.data.trim).filterNot(_.isEmpty)
          case elem: Elem if elem.label == "script" => Seq.empty
          case child => extractTexts(child)
        }
      }
      val texts = extractTexts(pageXml).dropWhile(!_.contains("Sale Dates"))
      val localDate = dateTime.toLocalDate
      val datePlayedLocator = DatePlayedLocator(localDate)
      case class State(
                        remainingTicketTypes: Set[TicketType] = TicketType.values.toSet,
                        maybeCurrentTicketType: Option[TicketType] = None,
                        gameUpdateCommands: Seq[GameUpdateCommand] = Seq.empty) {
        def parseLine(text: String): State = {
          val maybeNewTicketType = TicketType.within(text).toOption.filter(remainingTicketTypes.contains)
          maybeNewTicketType match {
            case Some(newTicketType) => State(remainingTicketTypes - newTicketType, Some(newTicketType), gameUpdateCommands)
            case _ =>
              val maybeNewState = for {
                ticketType <- maybeCurrentTicketType
                sellingDate <- PossiblyYearlessDateParser.forSeason(latestSeason)("d MMMM", "dd MMMM").find(text) if sellingDate.toLocalDate != localDate
              } yield {
                val sellingDateTime = sellingDate.withMillisOfDay(0).withHourOfDay(9)
                logger info s"Found ticket type $ticketType on sale at $sellingDateTime"
                val gameUpdateCommand = createGameUpdateCommand(datePlayedLocator, ticketType, sellingDateTime)
                State(
                  remainingTicketTypes = remainingTicketTypes - ticketType,
                  gameUpdateCommands = gameUpdateCommands :+ gameUpdateCommand)
              }
              maybeNewState.getOrElse(this)
          }
        }
      }
      val finalState = texts.foldLeft(State())((state, text) => state.parseLine(text))
      finalState.gameUpdateCommands
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
