package update.tickets

import java.net.URI
import java.time.ZonedDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}

import cats.data.NonEmptyList
import dates.{DateParserFactory, PossiblyYearlessDateParser}
import html._
import logging.{RemoteLogging, RemoteStream}
import models.TicketType._
import models._
import monads.FE
import monads.FE.FutureEitherNel
import play.api.libs.ws.WSClient
import update.WsBody
import xml.NodeExtensions
import cats.instances.future._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.xml.{Elem, Node, Text}

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerImpl @javax.inject.Inject()(val rootUri: URI, ws: WSClient, dateParserFactory: DateParserFactory)(implicit val ec: ExecutionContext) extends TicketsGameScanner with RemoteLogging with WsBody with NodeExtensions {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] ={
    latestSeason match {
      case Some(ls) =>
        val ticketsUri = rootUri.resolve("/tickets/match-tickets")
        for {
          page <- bodyXml(ticketsUri)(ws.url(_).get())
          gameUpdateCommands <- createUpdateCommandsForAllTicketsPage(ls, page)
        } yield gameUpdateCommands
      case _ =>
        logger info "There are currently no games so tickets will not be searched for."
        FE(Future.successful(Seq.empty))
    }
  }

  def createUpdateCommandsForAllTicketsPage(latestSeason: Int, page: Elem)(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    val ticketPageUrls = for {
      a <- (page \\ "a").toList if a.hasClass("tickets-more-info")
      href <- a.attributes.asAttrMap.get("href").toList if href.contains("away-matches")
    } yield rootUri.resolve(new URI(href))
    val empty: FutureEitherNel[String, Seq[GameUpdateCommand]] = FE(Future.successful(Right(Seq.empty)))
    ticketPageUrls.foldLeft(empty) {(existingGameUpdateCommands, ticketPageUrl) =>
      for {
       eguc <- existingGameUpdateCommands
       nguc <- createUpdateCommandsForSinglePage(latestSeason, ticketPageUrl)
      } yield eguc ++ nguc
    }
  }

  def createUpdateCommandsForSinglePage(latestSeason: Int, uri: URI)
                                       (implicit remoteStream: RemoteStream): FutureEitherNel[String, List[GameUpdateCommand]] = {
    logger info s"Found tickets page $uri"
    for {
      page <- bodyXml(uri)(ws.url(_).get())
      ticketPageScanner <- FE(Right(TicketPageScanner(page, latestSeason)))
      when <- FE(ticketPageScanner.findWhen.toRight(NonEmptyList.of(s"Cannot find a date played at page $uri")))
    } yield ticketPageScanner.scanTicketDates(when).toList
  }

  case class TicketPageScanner(pageXml: Elem, latestSeason: Int)(implicit remoteStream: RemoteStream) {

    // look for <div class='matchDate'>Saturday 4 April 15:00</div>
    def findWhen: Option[ZonedDateTime] = {
      val zonedDateTimes = for {
        node <- pageXml \\ "div"
        attr <- node.attr("data-iso-match-date")
        zonedDateTime <- dateParserFactory.forSeason(latestSeason, "yyyy-MM-dd'T'HH:mm:ss").find(attr)
      } yield zonedDateTime
      zonedDateTimes.headOption
    }

    def scanTicketDates(zonedDateTime: ZonedDateTime): Seq[GameUpdateCommand] = {
      logger info s"Scanning for tickets for a game taking place at $zonedDateTime"
      def extractTexts(node: Node): Seq[String] = {
        node.child.flatMap {
          case text: Text => Some(text.data.trim).filterNot(_.isEmpty)
          case elem: Elem if elem.label == "script" => Seq.empty
          case child => extractTexts(child)
        }
      }
      val texts = extractTexts(pageXml).dropWhile(!_.contains("Accessibility")).map(_.trim)
      val datePlayedLocator = DatePlayedLocator(zonedDateTime)
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
                sellingDate <- dateParserFactory.forSeason(latestSeason, "ha, EEEE d MMMM").find(text) if sellingDate.toLocalDate != zonedDateTime.toLocalDate
              } yield {
                val sellingZonedDateTime = sellingDate.truncatedTo(ChronoUnit.MINUTES)
                logger info s"Found ticket type $ticketType on sale at $sellingZonedDateTime"
                val gameUpdateCommand = createGameUpdateCommand(datePlayedLocator, ticketType, sellingZonedDateTime)
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

    def createGameUpdateCommand(gameLocator: GameLocator, ticketType: TicketType, zonedDateTime: ZonedDateTime): GameUpdateCommand = {
      val updateCommandFactory: (GameLocator, ZonedDateTime) => GameUpdateCommand = ticketType match {
        case BondholderTicketType => BondHolderTicketsUpdateCommand.apply
        case PriorityPointTicketType => PriorityPointTicketsUpdateCommand.apply
        case SeasonTicketType => SeasonTicketsUpdateCommand.apply
        case AcademyTicketType => AcademyTicketsUpdateCommand.apply
        case GeneralSaleTicketType => GeneralSaleTicketsUpdateCommand.apply
      }
      updateCommandFactory(gameLocator, zonedDateTime)
    }
  }
}
