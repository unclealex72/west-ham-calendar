package update.tickets

import scalaz._
import Scalaz._
import java.io.PrintWriter
import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dates._
import enumeratum.EnumEntry
import html._
import logging.SimpleRemoteStream
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Handler, Request, Server, ServerConnector}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification._
import play.api.libs.ws.ahc.AhcWSClient
import util.Materialisers

import scala.concurrent.duration._
import scala.io.Source
import enumeratum.Enum
import org.joda.time.DateTime
import org.specs2.specification.core.Fragment

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerTest extends Specification with DisjunctionMatchers with StrictLogging with Mockito with SimpleRemoteStream with Materialisers {

  val (server, port): (Server, Int) = {
    val server = new Server(0)
    val handler: Handler = new AbstractHandler {
      override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
        if ("GET" == request.getMethod) {
          val pathInfo = request.getPathInfo
          val path = if (pathInfo.startsWith("/html")) s"${request.getPathInfo}.html".substring(1) else s"html${request.getPathInfo}.html"
          Option(classOf[TicketsGameScannerTest].getClassLoader.getResourceAsStream(path)) match {
            case Some(in) =>
              logger.info(s"Loading $path")
              val htmlResponse = Source.fromInputStream(in).mkString
              val writer: PrintWriter = response.getWriter
              writer.println(htmlResponse)
              writer.close()
            case _ =>
              logger.error(s"Cound not find a resource at $path")
              response.sendError(HttpStatus.NOT_FOUND_404)
          }
        }
        else {
          response.sendError(HttpStatus.METHOD_NOT_ALLOWED_405)
        }
      }
    }
    server.setHandler(handler)
    server.start()
    (server, server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort)
  }

  val wsClient = new AhcWSClient(new DefaultAsyncHttpClientConfig.Builder().build())

  val ticketsGameScanner = new TicketsGameScannerImpl(new URI(s"http://localhost:$port"), wsClient)
  val gameUpdateCommandsValidation: \/[NonEmptyList[String], Seq[GameUpdateCommand]] = Await.result(ticketsGameScanner.scan(Some(2016)), 10.minutes)

  server.stop()

  "The tickets game scanner" should {
    Fragment.foreach(GameAndTickets.values) { gameAndTickets =>
      s"find all the tickets for ${gameAndTickets.entryName}" in {
        val expectedGameUpdateCommands: Seq[GameUpdateCommand] = gameAndTickets.gameUpdateCommands
        val actualGameUpdateComamndsValidation = gameUpdateCommandsValidation.map(_.filter(gud => gameAndTickets.gameLocator == gud.gameLocator))

        def format(gameUpdateCommands: Seq[GameUpdateCommand]): Seq[String] = {
          def locatorFormatter(gameLocator: GameLocator): String = {
            GameAndTickets.values.find(gat => gat.gameLocator == gameLocator).map(_.entryName).getOrElse("Unknown")
          }
          gameUpdateCommands.map { gameUpdateCommand =>
            s"${locatorFormatter(gameUpdateCommand.gameLocator)}:${gameUpdateCommand.name}@${gameUpdateCommand.value}"
          }
        }
        actualGameUpdateComamndsValidation.map(format) must be_\/-(containTheSameElementsAs(format(expectedGameUpdateCommands)))
      }
    }
  }
}

sealed trait GameAndTickets extends EnumEntry {
  val gameLocator: GameLocator
  val gameUpdateCommands: Seq[GameUpdateCommand]
}

object GameAndTickets extends Enum[GameAndTickets] {
  val values = findValues

  abstract class GameAndTicketsImpl(override val entryName: String, dateTimePlayed: DateTime, gameUpdateCommandsFactories: (GameLocator => GameUpdateCommand)*) extends GameAndTickets {

    override val gameLocator = DatePlayedLocator(dateTimePlayed)
    override val gameUpdateCommands = gameUpdateCommandsFactories.map(_(gameLocator))
  }

  object MANURE_LEAGUE extends GameAndTicketsImpl(
    "ManUre_League",
    November(27, 2016) at (16,30),
    BondHolderTicketsUpdateCommand(_, November(5, 2016) at 9 am),
    PriorityPointTicketsUpdateCommand(_, November(7, 2016) at 9 am),
    AcademyTicketsUpdateCommand(_, November(9, 2016) at 9 am),
    GeneralSaleTicketsUpdateCommand(_, November(10, 2016) at 9 am)
  )

  object MANURE_EFL extends GameAndTicketsImpl(
    "ManUre_EFL",
    November(30, 2016) at 8 pm,
    BondHolderTicketsUpdateCommand(_, November(9, 2016) at 9 am),
    PriorityPointTicketsUpdateCommand(_, November(10, 2016) at 9 am),
    AcademyTicketsUpdateCommand(_, November(12, 2016) at 9 am),
    GeneralSaleTicketsUpdateCommand(_, November(14, 2016) at 9 am)
  )

  object LIVERPOOL extends GameAndTicketsImpl(
    "Liverpool",
    December(11, 2016) at (16, 30),
    BondHolderTicketsUpdateCommand(_, November(16, 2016) at 9 am),
    PriorityPointTicketsUpdateCommand(_, November(17, 2016) at 9 am),
    AcademyTicketsUpdateCommand(_, November(19, 2016) at 9 am),
    GeneralSaleTicketsUpdateCommand(_, November(21, 2016) at 9 am)
  )

  object SWANSEA extends GameAndTicketsImpl(
    "Swansea",
    December(26, 2016) at 4 pm,
    BondHolderTicketsUpdateCommand(_, November(23, 2016) at 9 am),
    PriorityPointTicketsUpdateCommand(_, November(23, 2016) at 5 pm),
    AcademyTicketsUpdateCommand(_, November(26, 2016) at 9 am),
    GeneralSaleTicketsUpdateCommand(_, November(28, 2016) at 9 am)
  )

  object LEICESTER extends GameAndTicketsImpl(
    "Leicester",
    December(31, 2016) at 3 pm,
    BondHolderTicketsUpdateCommand(_, November(30, 2016) at 9 am),
    PriorityPointTicketsUpdateCommand(_, December(1, 2016) at 9 am),
    AcademyTicketsUpdateCommand(_, December(3, 2016) at 9 am),
    GeneralSaleTicketsUpdateCommand(_, December(5, 2016) at 9 am)
  )

}

