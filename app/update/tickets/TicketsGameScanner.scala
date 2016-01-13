package update.tickets

import java.net.URI
import javax.inject.Inject

import models._
import org.apache.http.client.utils.URIBuilder
import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}
import org.joda.time.DateTime
import dao.Transactional
import dates.PossiblyYearlessDateParser
import html._
import logging.{RemoteLogging, RemoteStream}
import update.GameScanner

import scala.xml.{NodeSeq, Elem, Node, XML}

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScanner @Inject() (val rootUri: URI, val tx: Transactional) extends GameScanner with RemoteLogging {

  override def scan(implicit remoteStream: RemoteStream): List[GameUpdateCommand] = {
    val latestSeason = tx { gameDao => gameDao.getLatestSeason }
    latestSeason match {
      case Some(latestSeason) => {
        val ticketsUri = new URIBuilder(rootUri).setPath("/Tickets/Match-Tickets").build()
        val ticketsXml = loadPage(ticketsUri)
        val matchRegex = "(?:Home|Away)-Matches".r
        for {
          a <- (ticketsXml \\ "a").toList
          href <- a.attributes.asAttrMap.get("href").toList if matchRegex.findFirstMatchIn(href).isDefined
          updateCommands <- createUpdateCommands(latestSeason, rootUri.resolve(new URI(href)))
        } yield updateCommands
      }
      case _ => {
        logger info "There are currently no games so tickets will not be searched for."
        List.empty
      }
    }
  }

  def createUpdateCommands(latestSeason: Int, uri: URI)(implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    logger info s"Found tickets page $uri"
    val ticketPage = loadPage(uri)
    val ticketPageScanner = TicketPageScanner(ticketPage, latestSeason)(remoteStream)
    ticketPageScanner.findWhen match {
      case Some(dateTime) => {
        logger info s"Found date $dateTime"
        ticketPageScanner.scanTicketDates(dateTime)
      }
      case _ => {
        logger warn s"Cannot find a date played at page $uri"
        Seq.empty
      }
    }
  }

  case class TicketPageScanner(pageXml: Elem, latestSeason: Int)(implicit remoteStream: RemoteStream) {

    object NodeImplicits {
      implicit class NodeHasAttribute(node: Node) {
        def hasAttr(name: String, value: String): Boolean = node.attributes.asAttrMap.get(name) == Some(value)
        def hasClass(value: String) = hasAttr("class", value)
        def trimmed: String = node.text.trim
      }
    }

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
  }

  def loadPage(uri: URI): Elem = {
    val cleaner = new HtmlCleaner()
    val rootNode = cleaner.clean(uri.toURL)
    val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
    XML.loadString(page)
  }
}
