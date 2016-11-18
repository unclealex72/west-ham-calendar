package update.fixtures

import java.io.PrintWriter
import java.net.URI
import java.text.SimpleDateFormat
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dates._
import html._
import logging.SimpleRemoteStream
import model.GameKey
import models.Competition._
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
import util.Materialisers

import scala.concurrent.Await
import scala.io.Source
import scalaz.Scalaz._
import scalaz._
import scala.concurrent.duration._

/**
 * Created by alex on 28/03/15.
 */
class FixturesGameScannerTest extends Specification with DisjunctionMatchers with StrictLogging with Materialisers {


  "The fixtures game scanner" should {
    "find all the known games" in new ServerContext {

      val nowService: NowService = NowService(December(25, 2016))
      implicit val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
      val wsClient = new AhcWSClient(new DefaultAsyncHttpClientConfig.Builder().build())
      val fixturesGameScanner = new FixturesGameScannerImpl(new URI(s"http://localhost:$port"), wsClient)
      val maybeGameCommands = Await.result(fixturesGameScanner.scan(Some(2014))(remoteStream), 10.minutes)
      maybeGameCommands must be_\/-(containTheSameElementsAs(Seq[GameUpdateCommand](
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016)), July(6,2016) at (3,30)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016)), GameResult(Score(3,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016)), s"http://localhost:$port/sites/default/files/logos/teams/seattlesounders.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/seattle-sounders-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016)), July(13,2016) at (1,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016)), GameResult(Score(2,2),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016)), s"http://localhost:$port/sites/default/files/logos/teams/carolinarailhawks.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/carolina-railhawks-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016)), July(19,2016) at (17,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016)), GameResult(Score(2,2),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016)), s"http://localhost:$port/sites/default/files/logos/teams/slovacko_1.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/fc-slovacko-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016)), July(20,2016) at (17,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016)), GameResult(Score(3,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016)), s"http://localhost:$port/sites/default/files/logos/teams/rubin-kazan200.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/rubin-kazan-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016)), July(23,2016) at (16,0)),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016)), GameResult(Score(0,3),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016)), s"http://localhost:$port/sites/default/files/logos/teams/ksc_1.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/karlsruher-sc-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), July(28,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/competition/2016-08/europaleague.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), GameResult(Score(2,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/logos/teams/domzale_0.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"NK Domzale",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/nk-domzale-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), August(4,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/competition/2016-08/europaleague.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), GameResult(Score(3,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), s"http://localhost:$port/sites/default/files/logos/teams/domzale_0.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"NK Domzale",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-nk-domzale"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), August(7,2016) at (13,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/betway_cup.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), GameResult(Score(2,3),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), s"http://localhost:$port/sites/default/files/logos/teams/juventus_0.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Juventus",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-juventus"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), August(15,2016) at (20,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), GameResult(Score(2,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Chelsea_0.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Chelsea",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/chelsea-vs-west-ham-united-1"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), August(18,2016) at (19,15)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/competition/2016-08/europaleague.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), GameResult(Score(1,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/logos/teams/astra.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/astra-giurgiu-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), August(21,2016) at (16,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), GameResult(Score(1,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/logos/teams/afc-bournemouth-icon.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Bournemouth",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-bournemouth"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), August(25,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/competition/2016-08/europaleague.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), GameResult(Score(0,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), s"http://localhost:$port/sites/default/files/logos/teams/astra.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(EUROPA,HOME,"Astra Giurgiu",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-astra-giurgiu"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), August(28,2016) at (16,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), GameResult(Score(3,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/mancitylogo.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester City",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/manchester-city-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), September(10,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), GameResult(Score(2,4),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), s"http://localhost:$port/sites/default/files/logos/teams/watford.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Watford",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-2-4-watford"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), September(17,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), GameResult(Score(4,2),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/logos/teams/WestBrom.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"West Bromwich Albion",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-bromwich-albion-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), September(21,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), s"http://localhost:$port/sites/default/files/competition/2016-09/eflcup.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), GameResult(Score(1,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/accringtonstanley.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Accrington Stanley",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-accrington-stanley"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), September(25,2016) at (16,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), GameResult(Score(0,3),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Southampton.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Southampton",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-southampton"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), October(1,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), GameResult(Score(1,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/logos/teams/middlesbrough.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Middlesbrough",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-middlesbrough"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), October(15,2016) at (17,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), GameResult(Score(0,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/logos/teams/CrystalPalace.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Crystal Palace",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/crystal-palace-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), October(22,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), GameResult(Score(1,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/logos/teams/sunderland-afc-logo.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Sunderland",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-sunderland"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), October(26,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/competition/2016-09/eflcup.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), GameResult(Score(2,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Chelsea_0.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,HOME,"Chelsea",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-chelsea"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), October(30,2016) at (13,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), GameResult(Score(2,0),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Everton.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Everton",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/everton-vs-west-ham-united"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), November(5,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        ResultUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), GameResult(Score(1,1),None)),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Stroke.png"),
        MatchReportUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Stoke City",2016)), s"http://localhost:$port/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-stoke-city-0"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2016)), November(19,2016) at (17,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Spurs.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2016)), November(27,2016) at (16,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/ManUtd.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Manchester United",2016)), November(30,2016) at (20,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/competition/2016-09/eflcup.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/ManUtd.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(FRIENDLY,AWAY,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2016)), December(3,2016) at (17,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Arsenal_0.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2016)), December(11,2016) at (16,30)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Liverpool.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2016)), December(14,2016) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Burnley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Burnley.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2016)), December(17,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Hull City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/HullCity.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2016)), December(26,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Swansea.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2016)), December(31,2016) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/leicester.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2016)), January(2,2017) at (17,15)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester United",2016)), s"http://localhost:$port/sites/default/files/logos/teams/ManUtd.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2016)), January(14,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Crystal Palace",2016)), s"http://localhost:$port/sites/default/files/logos/teams/CrystalPalace.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Middlesbrough",2016)), January(21,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/logos/teams/middlesbrough.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Middlesbrough",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2016)), February(1,2017) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Manchester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/mancitylogo.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2016)), February(4,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Southampton.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Southampton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2016)), February(11,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"West Bromwich Albion",2016)), s"http://localhost:$port/sites/default/files/logos/teams/WestBrom.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Watford",2016)), February(25,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Watford",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Watford",2016)), s"http://localhost:$port/sites/default/files/logos/teams/watford.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Watford",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2016)), March(4,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Chelsea",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Chelsea_0.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Bournemouth",2016)), March(11,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/logos/teams/afc-bournemouth-icon.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Bournemouth",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2016)), March(18,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Leicester City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/leicester.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2016)), April(1,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/HullCity.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Hull City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2016)), April(4,2017) at (19,45)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Arsenal_0.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Arsenal",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2016)), April(8,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Swansea City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Swansea.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2016)), April(15,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/logos/teams/sunderland-afc-logo.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Sunderland",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2016)), April(22,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Everton",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Everton.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2016)), April(29,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Stroke.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Stoke City",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2016)), May(6,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Tottenham Hotspur",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Spurs.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2016)), May(13,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,HOME,"Liverpool",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Liverpool.png"),
        DatePlayedUpdateCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2016)), May(21,2017) at (15,0)),
        CompetitionImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2016)), s"http://localhost:$port/sites/default/files/competition/2016-07/premier-league.png"),
        HomeTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/Burnley.png"),
        AwayTeamImageLinkCommand(GameKeyLocator(GameKey(PREM,AWAY,"Burnley",2016)), s"http://localhost:$port/sites/default/files/logos/teams/whu_newbadge.png")
      )))
    }
  }

  trait ServerContext extends After with SimpleRemoteStream {

    val (server, port): (Server, Int) = {
      val server = new Server(0)
      val handler: Handler = new AbstractHandler {
        override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
          def loadResource(path: String): Unit = {
            Option(classOf[ServerContext].getClassLoader.getResourceAsStream(s"fixtures/$path")) match {
              case Some(in) =>
                logger.info(s"Loading $path")
                val responseBody = Source.fromInputStream(in).mkString
                val writer: PrintWriter = response.getWriter
                writer.println(responseBody)
                writer.close()
              case _ =>
                logger.error(s"Cound not find a resource at $path")
                response.sendError(HttpStatus.NOT_FOUND_404)
            }
          }
          (request.getMethod, request.getPathInfo) match {
            case ("GET", "/fixtures/first-team/fixtures-and-results") =>
              loadResource("fixtures-and-results.html")
            case ("POST", "/views/ajax") =>
              val maybePage = for {
                pageStr <- Option(request.getParameter("page")).toRightDisjunction("Cannot find parameter 'page'")
                page <- pageStr.parseInt.disjunction.leftMap(_.getMessage)
              } yield page
              maybePage match {
                case \/-(page) =>
                  loadResource(s"$page.json")
                case -\/(message) => response.sendError(HttpStatus.BAD_REQUEST_400, message)
              }
            case _ =>
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
