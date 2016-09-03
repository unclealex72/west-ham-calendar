package update.tickets

import java.io.PrintWriter
import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dates._
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

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerTest extends Specification with DisjunctionMatchers with StrictLogging with Mockito with SimpleRemoteStream with Materialisers {


  "The tickets game scanner" should {
    "find all the known tickets" in new ServerContext {
      implicit val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
      val wsClient = new AhcWSClient(new DefaultAsyncHttpClientConfig.Builder().build())

      val ticketsGameScanner = new TicketsGameScannerImpl(new URI(s"http://localhost:$port"), wsClient)
      val gameUpdateCommands = ticketsGameScanner.scan(Some(2016))
      val watford = DatePlayedLocator(September(10, 2016) at 3 pm)
      val westbrom = DatePlayedLocator(September(17, 2016) at 3 pm)
      val accrington = DatePlayedLocator(September(21, 2016) at (19, 45))
      def format(gameUpdateCommands: Seq[GameUpdateCommand]): Seq[String] = {
        def locatorFormatter(gameLocator: GameLocator): String = {
          if (gameLocator == westbrom) "West_Brom" else
          if (gameLocator == accrington) "Accrington" else
          if (gameLocator == watford) "Watford" else
            "Unknown"
        }
        gameUpdateCommands.map { gameUpdateCommand =>
          s"${locatorFormatter(gameUpdateCommand.gameLocator)}:${gameUpdateCommand.name}@${gameUpdateCommand.value}"
        }
      }
      gameUpdateCommands.map(_.map(format(_))) must be_\/-(containTheSameElementsAs(format(Seq[GameUpdateCommand](

        AcademyTicketsUpdateCommand(accrington, September(6, 2016) at 9 am),
        GeneralSaleTicketsUpdateCommand(accrington, September(8, 2016) at 9 am),

        BondHolderTicketsUpdateCommand(westbrom, September(3, 2016) at 9 am),
        PriorityPointTicketsUpdateCommand(westbrom, September(5, 2016) at 9 am),
        SeasonTicketsUpdateCommand(westbrom, September(6, 2016) at 9 am),
        AcademyTicketsUpdateCommand(westbrom, September(7, 2016) at 9 am),
        GeneralSaleTicketsUpdateCommand(westbrom, September(8, 2016) at 9 am))))).awaitFor(10.seconds)
    }
  } 
  
  
  trait ServerContext extends After with SimpleRemoteStream {

    val (server, port): (Server, Int) = {
      val server = new Server(0)
      val handler: Handler = new AbstractHandler {
        override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
          if ("GET" == request.getMethod) {
            val pathInfo = request.getPathInfo
            val path = if (pathInfo.startsWith("/html")) s"${request.getPathInfo}.html".substring(1) else s"html${request.getPathInfo}.html"
            Option(classOf[ServerContext].getClassLoader.getResourceAsStream(path)) match {
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

    override def after: Any = {
      server.stop()
    }
  }
}
