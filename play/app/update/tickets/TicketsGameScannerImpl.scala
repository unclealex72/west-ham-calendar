package update.tickets

import java.net.URI

import dates.PossiblyYearlessDateParser
import html._
import logging.{RemoteLogging, RemoteStream}
import models._
import monads.FL
import org.joda.time.DateTime
import play.api.libs.ws.WSClient
import update.WsClientImplicits._

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{Elem, Node}
import scalaz.Scalaz._

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerImpl(val rootUri: URI, ws: WSClient)(implicit ec: ExecutionContext) extends TicketsGameScanner with RemoteLogging {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): Future[List[GameUpdateCommand]] = {
    latestSeason match {
      case Some(ls) =>
        val matchRegex = "(?:Home|Away)-Matches".r
        val ticketsUri = rootUri.resolve("/Tickets/Match-Tickets")
        FL {
          for {
            ticketsXml <- FL <~ ws.url(ticketsUri).get().map { response => List(response.cleanXml) }
            a <- FL <~ (ticketsXml \\ "a").toList
            href <- FL <~ a.attributes.asAttrMap.get("href").toList if matchRegex.findFirstMatchIn(href).isDefined
            updateCommands <- FL <~ createUpdateCommands(ls, rootUri.resolve(new URI(href)))
          } yield updateCommands
        }
      case _ =>
        logger info "There are currently no games so tickets will not be searched for."
        Future.successful(List.empty)
    }
  }

  def createUpdateCommands(latestSeason: Int, uri: URI)(implicit remoteStream: RemoteStream): Future[List[GameUpdateCommand]] = {
    logger info s"Found tickets page $uri"
    ws.url(uri).get().map { response =>
      val ticketPage = response.cleanXml
      val ticketPageScanner = TicketPageScanner(ticketPage, latestSeason)(remoteStream)
      ticketPageScanner.findWhen match {
        case Some(dateTime) =>
          logger info s"Found date $dateTime"
          ticketPageScanner.scanTicketDates(dateTime).toList
        case _ =>
          logger warn s"Cannot find a date played at page $uri"
          List.empty
      }
    }
  }

  case class TicketPageScanner(pageXml: Elem, latestSeason: Int)(implicit remoteStream: RemoteStream) {

    import NodeImplicits._

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
        ticketType <- logOnEmpty(TicketType(ticketTypeTd.trimmed), s"${ticketTypeTd.text} is not a valid ticket type")
        ticketSellingDate <- ticketDateParser.find(ticketSellingDateTd.trimmed)
      } yield {
        logger info s"Found ticket type $ticketType on sale on ${ticketSellingDateTd.text}"
        ticketType.toTicketsUpdateCommand(gameLocator, ticketSellingDate.withHourOfDay(9))
      }
    }

    object NodeImplicits {

      implicit class NodeHasAttribute(node: Node) {
        def hasClass(value: String) = hasAttr("class", value)

        def hasAttr(name: String, value: String): Boolean = node.attributes.asAttrMap.get(name).contains(value)

        def trimmed: String = node.text.trim
      }
    }
  }
}
