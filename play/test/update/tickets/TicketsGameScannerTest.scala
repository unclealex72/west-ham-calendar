package update.tickets

import java.io.PrintWriter
import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.ning.http.client.AsyncHttpClientConfig
import com.typesafe.scalalogging.slf4j.StrictLogging
import dates.{April, February, March}
import html._
import logging.SimpleRemoteStream
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Handler, Request, Server, ServerConnector}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification._
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Future
import scala.io.Source

/**
 * Created by alex on 28/03/15.
 */
class TicketsGameScannerTest extends Specification with StrictLogging with Mockito with SimpleRemoteStream {


  "The tickets game scanner" should {
    "find all the known tickets" in new ServerContext {
      implicit val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
      val wsClient = new NingWSClient(new AsyncHttpClientConfig.Builder().build())

      val ticketsGameScanner = new TicketsGameScannerImpl(new URI(s"http://localhost:$port"), wsClient)
      val gameUpdateCommands: Future[List[GameUpdateCommand]] = ticketsGameScanner.scan(Some(2014))
      val arsenal = DatePlayedLocator(March(14, 2015) at 3 pm)
      val sunderland = DatePlayedLocator(March(21, 2015) at (17, 30))
      val leicester = DatePlayedLocator(April(4, 2015) at 3 pm)
      var stoke = DatePlayedLocator(April(11, 2015) at 3 pm)
      gameUpdateCommands must containTheSameElementsAs(Seq[GameUpdateCommand](
        BondHolderTicketsUpdateCommand(arsenal, February(12, 2015) at 9 am),
        PriorityPointTicketsUpdateCommand(arsenal, February(11, 2015) at 9 am),
        SeasonTicketsUpdateCommand(arsenal, February(14, 2015) at 9 am),
        AcademyTicketsUpdateCommand(arsenal, February(16, 2015) at 9 am),
        GeneralSaleTicketsUpdateCommand(arsenal, February(17, 2015) at 9 am),

        SeasonTicketsUpdateCommand(sunderland, February(9, 2015) at 9 am),
        AcademyTicketsUpdateCommand(sunderland, February(3, 2015) at 9 am),
        GeneralSaleTicketsUpdateCommand(sunderland, February(10, 2015) at 9 am),

        BondHolderTicketsUpdateCommand(leicester, February(26, 2015) at 9 am),
        PriorityPointTicketsUpdateCommand(leicester, February(26, 2015) at 9 am),
        SeasonTicketsUpdateCommand(leicester, February(28, 2015) at 9 am),
        AcademyTicketsUpdateCommand(leicester, March(2, 2015) at 9 am),
        GeneralSaleTicketsUpdateCommand(leicester, March(3, 2015) at 9 am),

        SeasonTicketsUpdateCommand(stoke, March(2, 2015) at 9 am),
        AcademyTicketsUpdateCommand(stoke, February(24, 2015) at 9 am),
        GeneralSaleTicketsUpdateCommand(stoke, March(3, 2015) at 9 am))).await
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
