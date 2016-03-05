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

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerImpl(val rootUri: URI, ws: WSClient)(implicit ec: ExecutionContext) extends TicketsGameScanner with RemoteLogging with WsBody with NodeExtensions {

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
      val ticketDateParser = PossiblyYearlessDateParser.forSeason(latestSeason)("d MMMM", "dd MMMM").logFailures
      val gameLocator = DatePlayedLocator(dateTime)
      for {
        table <- pageXml \\ "table" if table.hasClass("saleDateTable")
        tr <- table \\ "tr"
        ticketTypeTd <- (tr \\ "td").toSeq if ticketTypeTd.hasClass("left")
        ticketSellingDateTd <- (tr \\ "td").toSeq if ticketSellingDateTd.hasClass("right")
        ticketType <- logOnEmpty(TicketType(ticketTypeTd.trimmed))
        ticketSellingDate <- ticketDateParser.find(ticketSellingDateTd.trimmed)
      } yield {
        logger info s"Found ticket type $ticketType on sale on ${ticketSellingDateTd.text}"
        val updateCommandFactory: (GameLocator, DateTime) => GameUpdateCommand = ticketType match {
          case BondholderTicketType => BondHolderTicketsUpdateCommand.apply
          case PriorityPointTicketType => PriorityPointTicketsUpdateCommand.apply
          case SeasonTicketType => SeasonTicketsUpdateCommand.apply
          case AcademyTicketType => AcademyTicketsUpdateCommand.apply
          case GeneralSaleTicketType => GeneralSaleTicketsUpdateCommand.apply
        }
        updateCommandFactory(gameLocator, ticketSellingDate.withHourOfDay(9))
      }
    }
  }
}
