package update.fixtures

import java.io.PrintWriter
import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import argonaut._
import com.typesafe.scalalogging.slf4j.StrictLogging
import dates._
import html._
import logging.SimpleRemoteStream
import model.Competition.{FACP, LGCP, PREM}
import model.GameKey
import model.Location.{AWAY, HOME}
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Handler, Request, Server, ServerConnector}
import org.specs2.mutable.Specification
import org.specs2.specification._
import update.fixtures.FixturesRequest._

import scala.io.Source

/**
 * Created by alex on 28/03/15.
 */
class FixturesGameScannerTest extends Specification with StrictLogging {


  "The fixtures game scanner" should {
    "find all the known games" in new ServerContext {

      val nowService: NowService = NowService(December(25, 2015))

      val fixturesGameScanner = new FixturesGameScanner(new URI(s"http://localhost:$port"), nowService)
      val gameCommands = fixturesGameScanner.scan(Some(2014))(remoteStream)
      gameCommands must containTheSameElementsAs(Seq[GameUpdateCommand](DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2014)), August(16,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur", 2014)), "0 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Tottenham-Hotspur"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2014)),August(23,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace", 2014)), "1 - 3"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/Crystal-Palace-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United",2014)),August(26,2014) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United", 2014)), "1 - 1 (4 - 5)"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Sheffield-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2014)),August(30,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton", 2014)), "1 - 3"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Southampton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2014)),September(15,2014) at (20,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City", 2014)), "2 - 2"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/Hull-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2014)),September(20,2014) at (17,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool", 2014)), "3 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/West-Ham-United-vs-Liverpool"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2014)),September(27,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United", 2014)), "2 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/Manchester-United-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers",2014)),October(5,2014) at (16,15)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers", 2014)), "2 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/West-Ham-United-vs-Queens-Park-Rangers"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2014)),October(18,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley", 2014)), "1 - 3"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/Burnley-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2014)),October(25,2014) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City", 2014)), "2 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/West-Ham-United-vs-Manchester-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2014)),November(1,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City", 2014)), "2 - 2"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/Stoke-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa",2014)),November(8,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa", 2014)), "0 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/West-Ham-United-vs-Aston-Villa"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2014)),November(22,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton", 2014)), "2 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/Everton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United",2014)),November(29,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United", 2014)), "1 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/West-Ham-United-vs-Newcastle-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2014)),December(2,2014) at (20,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion", 2014)), "1 - 2"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Bromwich-Albion-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2014)),December(7,2014) at (13,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City", 2014)), "3 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Swansea-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2014)),December(13,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland", 2014)), "1 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/Sunderland-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2014)),December(20,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City", 2014)), "2 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Leicester-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2014)),December(26,2014) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea", 2014)), "2 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/Chelsea-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2014)),December(28,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal", 2014)), "1 - 2"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Arsenal"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2014)),January(1,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion", 2014)), "1 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-West-Bromwich-Albion"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton",2014)),January(6,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton", 2014)), "1 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Everton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2014)),January(10,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City", 2014)), "1 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Swansea-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton",2014)),January(13,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton", 2014)), "2 - 2 (9 - 8)"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-Everton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2014)),January(18,2015) at (13,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City", 2014)), "3 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-Hull-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City",2014)),January(25,2015) at (14,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City", 2014)), "0 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Bristol-City-vs-West-Ham-United-(25)"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2014)),January(31,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool", 2014)), "2 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Liverpool-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2014)),February(8,2015) at (16,15)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United", 2014)), "1 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Ham-United-vs-Manchester-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2014)),February(11,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton", 2014)), "0 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/Southampton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion",2014)),February(14,2015) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion", 2014)), "4 - 0"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Bromwich-Albion-vs-West-Ham-United-(23)"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2014)),February(22,2015) at (12,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur", 2014)), "2 - 2"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/Tottenham-Hotspur-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2014)),February(28,2015) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace", 2014)), "1 - 3"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Ham-United-vs-Crystal-Palace"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2014)),March(4,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea", 2014)), "0 - 1"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-March/West-Ham-United-vs-Chelsea"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2014)),March(14,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-March/Arsenal-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2014)),March(21,2015) at (17,30)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-March/West-Ham-United-vs-Sunderland"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2014)),April(4,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-April/Leicester-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2014)),April(11,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-April/West-Ham-United-vs-Stoke-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2014)),April(19,2015) at (13,30)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-April/Manchester-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Queens Park Rangers",2014)),April(25,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Queens Park Rangers", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-April/Queens-Park-Rangers-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2014)),May(2,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-May/West-Ham-United-vs-Burnley"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Aston Villa",2014)),May(9,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Aston Villa", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-May/Aston-Villa-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2014)),May(16,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-May/West-Ham-United-vs-Everton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United",2014)),May(24,2015) at (15,0)),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-May/Newcastle-United-vs-West-Ham-United")
      ))
    }
  }

  trait ServerContext extends After with SimpleRemoteStream {

    val (server, port): (Server, Int) = {
      val server = new Server(0)
      val handler: Handler = new AbstractHandler {
        override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
          if ("POST" == request.getMethod) {
            val body = Source.fromInputStream(request.getInputStream).mkString
            Parse.decodeOption[FixturesRequest](body) match {
              case Some(fixturesRequest) => {
                val season = fixturesRequest.season
                val path = s"html${request.getPathInfo}.${season}.json"
                Option(classOf[ServerContext].getClassLoader.getResourceAsStream(path)) match {
                  case Some(in) => {
                    logger.info(s"Loading $path")
                    val jsonResponse = Source.fromInputStream(in).mkString
                    val writer: PrintWriter = response.getWriter
                    writer.println(jsonResponse)
                    writer.close()
                  }
                  case _ => {
                    logger.error(s"Cound not find a resource at $path")
                    response.sendError(HttpStatus.NOT_FOUND_404)
                  }
                }

              }
              case _ => {
                response.sendError(HttpStatus.BAD_REQUEST_400)
              }
            }
          }
          else {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED_405)
          }
        }
      }
      server.setHandler(handler)
      server.start()
      (server, server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort())
    }

    override def after: Any = {
      server.stop()
    }
  }
}
