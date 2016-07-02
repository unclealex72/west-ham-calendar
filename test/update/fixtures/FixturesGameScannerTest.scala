package update.fixtures

import java.io.PrintWriter
import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dates._
import html._
import logging.SimpleRemoteStream
import model.GameKey
import models.Competition.{FACP, LGCP, PREM}
import models.Location.{AWAY, HOME}
import models.{GameResult, Score}
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Handler, Request, Server, ServerConnector}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import org.specs2.specification._
import play.api.libs.ws.ahc.AhcWSClient
import upickle.default._
import util.Materialisers

import scala.io.Source
import scalaz._

/**
 * Created by alex on 28/03/15.
 */
class FixturesGameScannerTest extends Specification with DisjunctionMatchers with StrictLogging with Materialisers {


  "The fixtures game scanner" should {
    "find all the known games" in new ServerContext {

      val nowService: NowService = NowService(December(25, 2015))
      implicit val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
      val wsClient = new AhcWSClient(new DefaultAsyncHttpClientConfig.Builder().build())
      val fixturesGameScanner = new FixturesGameScannerImpl(new URI(s"http://localhost:$port"), wsClient)
      val gameCommands = fixturesGameScanner.scan(Some(2014))(remoteStream)
      gameCommands must be_\/-(containTheSameElementsAs(Seq[GameUpdateCommand](
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2014)), August(16,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur", 2014)), GameResult(Score(0, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Tottenham-Hotspur"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2014)),August(23,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace", 2014)), GameResult(Score(1, 3))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/Crystal-Palace-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United",2014)),August(26,2014) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United", 2014)), GameResult(Score(1, 1), Some(Score(4, 5)))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Sheffield-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2014)),August(30,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton", 2014)), GameResult(Score(1, 3))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-August/West-Ham-United-vs-Southampton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2014)),September(15,2014) at (20,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City", 2014)), GameResult(Score(2, 2))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/Hull-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2014)),September(20,2014) at (17,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool", 2014)), GameResult(Score(3, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/West-Ham-United-vs-Liverpool"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2014)),September(27,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United", 2014)), GameResult(Score(2, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-September/Manchester-United-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers",2014)),October(5,2014) at (16,15)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers", 2014)), GameResult(Score(2, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/West-Ham-United-vs-Queens-Park-Rangers"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2014)),October(18,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley", 2014)), GameResult(Score(1, 3))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/Burnley-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2014)),October(25,2014) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City", 2014)), GameResult(Score(2, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-October/West-Ham-United-vs-Manchester-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2014)),November(1,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City", 2014)), GameResult(Score(2, 2))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/Stoke-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa",2014)),November(8,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa", 2014)), GameResult(Score(0, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/West-Ham-United-vs-Aston-Villa"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2014)),November(22,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton", 2014)), GameResult(Score(2, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/Everton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United",2014)),November(29,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United", 2014)), GameResult(Score(1, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-November/West-Ham-United-vs-Newcastle-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2014)),December(2,2014) at (20,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion", 2014)), GameResult(Score(1, 2))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Bromwich-Albion-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2014)),December(7,2014) at (13,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City", 2014)), GameResult(Score(3, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Swansea-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2014)),December(13,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland", 2014)), GameResult(Score(1, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/Sunderland-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2014)),December(20,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City", 2014)), GameResult(Score(2, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Leicester-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2014)),December(26,2014) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea", 2014)), GameResult(Score(2, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/Chelsea-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2014)),December(28,2014) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal", 2014)), GameResult(Score(1, 2))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2014-December/West-Ham-United-vs-Arsenal"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2014)),January(1,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion", 2014)), GameResult(Score(1, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-West-Bromwich-Albion"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton",2014)),January(6,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton", 2014)), GameResult(Score(1, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Everton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2014)),January(10,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City", 2014)), GameResult(Score(1, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Swansea-City-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton",2014)),January(13,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton", 2014)), GameResult(Score(2, 2), Some(Score(9, 8)))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-Everton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2014)),January(18,2015) at (13,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City", 2014)), GameResult(Score(3, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/West-Ham-United-vs-Hull-City"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City",2014)),January(25,2015) at (14,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City", 2014)), GameResult(Score(0, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Bristol-City-vs-West-Ham-United-(25)"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2014)),January(31,2015) at (15,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool", 2014)), GameResult(Score(2, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-January/Liverpool-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2014)),February(8,2015) at (16,15)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United", 2014)), GameResult(Score(1, 1))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Ham-United-vs-Manchester-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2014)),February(11,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton", 2014)), GameResult(Score(0, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/Southampton-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion",2014)),February(14,2015) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion", 2014)), GameResult(Score(4, 0))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Bromwich-Albion-vs-West-Ham-United-(23)"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2014)),February(22,2015) at (12,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur", 2014)), GameResult(Score(2, 2))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/Tottenham-Hotspur-vs-West-Ham-United"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2014)),February(28,2015) at (12,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace", 2014)), GameResult(Score(1, 3))),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-February/West-Ham-United-vs-Crystal-Palace"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2014)),March(4,2015) at (19,45)),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea", 2014)), GameResult(Score(0, 1))),
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
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United", 2014)), s"http://localhost:$port/Fixtures/First-Team/Fixture-and-Results/Season-2014-2015/2015-May/Newcastle-United-vs-West-Ham-United"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2014)),s"http://localhost:$port/getmedia/dfd00424-f72c-419c-903b-a42b25ba199a/Spurs.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2014)),s"http://localhost:$port/getmedia/ce5a326a-86b8-450c-a434-c64d2df74e6e/CrystalPalace.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United",2014)),"http://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/teams/sheffieldunited/sheffield-united.png"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(LGCP,HOME,"Sheffield United",2014)),"http://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/capital%20one%20cup/capital-one-cup.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2014)),s"http://localhost:$port/getmedia/a71481ba-d617-415f-9318-24272fee7601/Southampton.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2014)),s"http://localhost:$port/getmedia/ea1356cf-2e61-4c67-82f6-b3d45e9f23db/HullCity.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2014)),s"http://localhost:$port/getmedia/740988e5-28e4-4b9d-a3a0-2964ade44bcd/Liverpool.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2014)),s"http://localhost:$port/getmedia/19122221-f633-46e5-8d22-be533b78c82b/ManUtd.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers",2014)),s"http://localhost:$port/getmedia/1440373c-6c87-4a09-bcbc-218aafafab96/QPR.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Queens Park Rangers",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2014)),s"http://localhost:$port/getmedia/bf67c796-d5ae-4107-8818-633cb0a9a960/Burnley.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2014)),s"http://localhost:$port/getmedia/1552b7db-e1f6-4d63-b4d4-8e8d082a742b/Mancity.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2014)),s"http://localhost:$port/getmedia/1d93a243-25a4-4a66-819d-81e84332a2d6/Stroke.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa",2014)),s"http://localhost:$port/getmedia/8c7c7b55-999a-46b4-964e-84244fd3b10a/AstonVilla.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Aston Villa",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2014)),s"http://localhost:$port/getmedia/96d0cf41-e12d-4326-9e4d-0a86d46d5880/Everton.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United",2014)),s"http://localhost:$port/getmedia/9d36c5ee-3963-43c6-9daa-54d3b230c99e/Newcastle.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Newcastle United",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/5f57c5df-c1d6-49ee-a69d-48d1123f57f5/WestBrom.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2014)),s"http://localhost:$port/getmedia/7123a3c0-a769-44da-ac6e-97455e7867de/Swansea.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2014)),s"http://localhost:$port/getmedia/cb6f973a-7e8c-4d82-9a13-031f72e091a6/Sunderland.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2014)),s"http://localhost:$port/getmedia/f6019326-2bb7-4310-bacd-4478c912adff/Leicester.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2014)),s"http://localhost:$port/getmedia/ad4847f6-e87c-437a-b0db-04c5271b19f1/Chelsea.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2014)),s"http://localhost:$port/getmedia/73c6d8c1-28f6-4a05-ae61-2a1a230111a8/Arsenal.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/5f57c5df-c1d6-49ee-a69d-48d1123f57f5/WestBrom.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton",2014)),s"http://localhost:$port/getmedia/96d0cf41-e12d-4326-9e4d-0a86d46d5880/Everton.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Everton",2014)),s"http://localhost:$port/getmedia/211656a4-def6-434b-882f-5bb644b96037/TheFAClub_logo.png.aspx"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2014)),s"http://localhost:$port/getmedia/7123a3c0-a769-44da-ac6e-97455e7867de/Swansea.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton",2014)),s"http://localhost:$port/getmedia/96d0cf41-e12d-4326-9e4d-0a86d46d5880/Everton.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FACP,HOME,"Everton",2014)),s"http://localhost:$port/getmedia/211656a4-def6-434b-882f-5bb644b96037/TheFAClub_logo.png.aspx"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2014)),s"http://localhost:$port/getmedia/ea1356cf-2e61-4c67-82f6-b3d45e9f23db/HullCity.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City",2014)),"http://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/teams/bristol-city/bristol-city.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"Bristol City",2014)),s"http://localhost:$port/getmedia/211656a4-def6-434b-882f-5bb644b96037/TheFAClub_logo.png.aspx"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2014)),s"http://localhost:$port/getmedia/740988e5-28e4-4b9d-a3a0-2964ade44bcd/Liverpool.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2014)),s"http://localhost:$port/getmedia/19122221-f633-46e5-8d22-be533b78c82b/ManUtd.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2014)),s"http://localhost:$port/getmedia/a71481ba-d617-415f-9318-24272fee7601/Southampton.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/5f57c5df-c1d6-49ee-a69d-48d1123f57f5/WestBrom.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FACP,AWAY,"West Bromwich Albion",2014)),s"http://localhost:$port/getmedia/211656a4-def6-434b-882f-5bb644b96037/TheFAClub_logo.png.aspx"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2014)),s"http://localhost:$port/getmedia/dfd00424-f72c-419c-903b-a42b25ba199a/Spurs.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2014)),s"http://localhost:$port/getmedia/ce5a326a-86b8-450c-a434-c64d2df74e6e/CrystalPalace.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2014)),s"http://localhost:$port/getmedia/ad4847f6-e87c-437a-b0db-04c5271b19f1/Chelsea.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2014)),s"http://localhost:$port/getmedia/73c6d8c1-28f6-4a05-ae61-2a1a230111a8/Arsenal.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2014)),s"http://localhost:$port/getmedia/cb6f973a-7e8c-4d82-9a13-031f72e091a6/Sunderland.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2014)),s"http://localhost:$port/getmedia/f6019326-2bb7-4310-bacd-4478c912adff/Leicester.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2014)),s"http://localhost:$port/getmedia/1d93a243-25a4-4a66-819d-81e84332a2d6/Stroke.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2014)),s"http://localhost:$port/getmedia/1552b7db-e1f6-4d63-b4d4-8e8d082a742b/Mancity.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Queens Park Rangers",2014)),s"http://localhost:$port/getmedia/1440373c-6c87-4a09-bcbc-218aafafab96/QPR.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Queens Park Rangers",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Queens Park Rangers",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2014)),s"http://localhost:$port/getmedia/bf67c796-d5ae-4107-8818-633cb0a9a960/Burnley.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Aston Villa",2014)),s"http://localhost:$port/getmedia/8c7c7b55-999a-46b4-964e-84244fd3b10a/AstonVilla.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Aston Villa",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Aston Villa",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2014)),s"http://localhost:$port/getmedia/96d0cf41-e12d-4326-9e4d-0a86d46d5880/Everton.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United",2014)),s"http://localhost:$port/getmedia/9d36c5ee-3963-43c6-9daa-54d3b230c99e/Newcastle.png.aspx"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United",2014)),s"http://localhost:$port/getmedia/939f0956-418a-4b86-be9e-6a3908ad7dc7/WestHam.png.aspx"),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Newcastle United",2014)),"https://az719727.vo.msecnd.net/cmsstorage/whu/media/whu/barclays%20premier%20league/barclays-premier-league.png")
      ))).await
    }
  }

  trait ServerContext extends After with SimpleRemoteStream {

    val (server, port): (Server, Int) = {
      val server = new Server(0)
      val handler: Handler = new AbstractHandler {
        override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
          def loadResource(path: String): Unit = {
            Option(classOf[ServerContext].getClassLoader.getResourceAsStream(path)) match {
              case Some(in) =>
                logger.info(s"Loading $path")
                val jsonResponse = Source.fromInputStream(in).mkString
                val writer: PrintWriter = response.getWriter
                writer.println(jsonResponse)
                writer.close()
              case _ =>
                logger.error(s"Cound not find a resource at $path")
                response.sendError(HttpStatus.NOT_FOUND_404)
            }
          }
          if ("POST" == request.getMethod) {
            val body = Source.fromInputStream(request.getInputStream).mkString
            logger.info(s"Received body $body")
            read[ValidationNel[String, FixturesRequest]](body) match {
              case Success(fixturesRequest) =>
                val season = fixturesRequest.season
                val path = s"html${request.getPathInfo}.$season.json"
                loadResource(path)
              case Failure(msgs) =>
                logger.error(s"Could not parse $body")
                msgs.foreach(msg => logger.error(msg))
                response.sendError(HttpStatus.BAD_REQUEST_400)
            }
          }
          else if ("GET" == request.getMethod && "/Fixtures/First-Team/Fixture-and-Results" == request.getPathInfo) {
            val path = "html/fixtures-and-results.html"
            loadResource(path)
          }
          else {
            response.sendError(HttpStatus.NOT_FOUND_404)
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
