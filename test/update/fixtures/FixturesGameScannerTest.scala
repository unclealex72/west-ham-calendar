package update.fixtures

import java.io.PrintWriter
import java.net.URI
import java.time.ZonedDateTime
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import cats.data
import com.typesafe.scalalogging.StrictLogging
import dates._
import html._
import logging.SimpleRemoteStream
import model.GameKey
import models.Competition._
import models.Location.{AWAY, HOME}
import models.{GameResult, Score}
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Handler, Request, Server, ServerConnector}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import org.specs2.specification._
import play.api.libs.ws.ahc.{AhcWSClient, StandaloneAhcWSClient}
import util.Materialisers

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source
import scalaz.Scalaz._
import scalaz._
import logging.RemoteStream
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient

/**
 * Created by alex on 28/03/15.
 */
class FixturesGameScannerTest extends Specification with DisjunctionMatchers with StrictLogging with Materialisers {

  implicit val remoteStream: RemoteStream = new SimpleRemoteStream()

  "The fixtures game scanner" should {
    "find all the known games" in new ServerContext {

      implicit val ee: ExecutionEnv = ExecutionEnv.fromGlobalExecutionContext
      val wsClient = new AhcWSClient(new StandaloneAhcWSClient(new DefaultAsyncHttpClient()))
      implicit val zonedDateTimeFactory = new ZonedDateTimeFactoryImpl()
      val dateParserFactory = new DateParserFactoryImpl()
      val fixturesGameScanner = new FixturesGameScannerImpl(new URI(s"http://localhost:$port"), wsClient, dateParserFactory)
      val maybeGameCommands: Either[data.NonEmptyList[String], Seq[GameUpdateCommand]] =
        Await.result(fixturesGameScanner.scan(Some(2014))(remoteStream).value, 10.minutes)

      val expectedGameUpdateCommands: Seq[GameUpdateCommand] = {
        var maybeGameKey: Option[GameKey] = None
        var gameUpdateCommands: Seq[GameUpdateCommand] = Seq.empty
        def add[V](arg: V, f: (GameLocator, V) => GameUpdateCommand) = {
          gameUpdateCommands ++= maybeGameKey.map(gameKey => f(GameKeyLocator(gameKey), arg))
        }

        def addUrl(url: String, f: (GameLocator, String) => GameUpdateCommand) = {
          val fullUrl = s"http://localhost:$port$url"
          add(fullUrl, f)
        }

        def game(gameKey: GameKey) = maybeGameKey = Some(gameKey)

        def datePlayed(zonedDateTime: ZonedDateTime) = add(zonedDateTime, DatePlayedUpdateCommand)
        def competition(url: String) = addUrl(url, CompetitionImageLinkCommand)
        def result(result: GameResult) = add(result, ResultUpdateCommand)
        def homeTeam(url: String) = addUrl(url, HomeTeamImageLinkCommand)
        def awayTeam(url: String) = addUrl(url, AwayTeamImageLinkCommand)
        def report(url: String) = addUrl(url, MatchReportUpdateCommand)

        game(GameKey(FRIENDLY,AWAY,"Carolina RailHawks",2016))
        datePlayed(July(13,2016) at (1,0))
        result(GameResult(Score(2,2),None))
        homeTeam("/sites/default/files/logos/teams/carolinarailhawks.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/carolina-railhawks-vs-west-ham-united")
        game(GameKey(PREM,AWAY,"Watford",2016))
        datePlayed(February(25,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/watford.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Bournemouth",2016))
        datePlayed(March(11,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/afc-bournemouth-icon.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Manchester United",2016))
        datePlayed(November(27,2016) at (16,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/ManUtd.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(EUROPA,AWAY,"NK Domzale",2016))
        datePlayed(July(28,2016) at (19,45))
        competition("/sites/default/files/competition/2016-08/europaleague.png")
        result(GameResult(Score(2,1),None))
        homeTeam("/sites/default/files/logos/teams/domzale_0.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/nk-domzale-vs-west-ham-united")
        game(GameKey(FRIENDLY,AWAY,"FC Slovacko",2016))
        datePlayed(July(19,2016) at (17,0))
        result(GameResult(Score(2,2),None))
        homeTeam("/sites/default/files/logos/teams/slovacko_1.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/fc-slovacko-vs-west-ham-united")
        game(GameKey(PREM,AWAY,"Stoke City",2016))
        datePlayed(April(29,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Stroke.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Manchester City",2016))
        datePlayed(August(28,2016) at (16,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(3,1),None))
        homeTeam("/sites/default/files/logos/teams/mancitylogo.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/manchester-city-vs-west-ham-united")
        game(GameKey(EUROPA,AWAY,"Astra Giurgiu",2016))
        datePlayed(August(18,2016) at (19,15))
        competition("/sites/default/files/competition/2016-08/europaleague.png")
        result(GameResult(Score(1,1),None))
        homeTeam("/sites/default/files/logos/teams/astra.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/astra-giurgiu-vs-west-ham-united")
        game(GameKey(PREM,AWAY,"Crystal Palace",2016))
        datePlayed(October(15,2016) at (17,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(0,1),None))
        homeTeam("/sites/default/files/logos/teams/CrystalPalace.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/crystal-palace-vs-west-ham-united")
        game(GameKey(PREM,HOME,"Crystal Palace",2016))
        datePlayed(January(14,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/CrystalPalace.png")
        game(GameKey(PREM,AWAY,"Southampton",2016))
        datePlayed(February(4,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Southampton.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(EUROPA,HOME,"NK Domzale",2016))
        datePlayed(August(4,2016) at (19,45))
        competition("/sites/default/files/competition/2016-08/europaleague.png")
        result(GameResult(Score(3,0),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/domzale_0.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-nk-domzale")
        game(GameKey(PREM,HOME,"Chelsea",2016))
        datePlayed(March(4,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Chelsea_0.png")
        game(GameKey(PREM,AWAY,"Liverpool",2016))
        datePlayed(December(11,2016) at (16,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Liverpool.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Burnley",2016))
        datePlayed(May(21,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Burnley.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(FRIENDLY,AWAY,"Seattle Sounders",2016))
        datePlayed(July(6,2016) at (3,30))
        result(GameResult(Score(3,0),None))
        homeTeam("/sites/default/files/logos/teams/seattlesounders.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/seattle-sounders-vs-west-ham-united")
        game(GameKey(EUROPA,HOME,"Astra Giurgiu",2016))
        datePlayed(August(25,2016) at (19,45))
        competition("/sites/default/files/competition/2016-08/europaleague.png")
        result(GameResult(Score(0,1),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/astra.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-astra-giurgiu")
        game(GameKey(PREM,HOME,"West Bromwich Albion",2016))
        datePlayed(February(11,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/WestBrom.png")
        game(GameKey(FRIENDLY,AWAY,"Rubin Kazan",2016))
        datePlayed(July(20,2016) at (17,0))
        result(GameResult(Score(3,0),None))
        homeTeam("/sites/default/files/logos/teams/rubin-kazan200.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/rubin-kazan-vs-west-ham-united")
        game(GameKey(PREM,HOME,"Burnley",2016))
        datePlayed(December(14,2016) at (19,45))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Burnley.png")
        game(GameKey(PREM,HOME,"Swansea City",2016))
        datePlayed(April(8,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Swansea.png")
        game(GameKey(PREM,AWAY,"Arsenal",2016))
        datePlayed(April(4,2017) at (19,45))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Arsenal_0.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,HOME,"Arsenal",2016))
        datePlayed(December(3,2016) at (17,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Arsenal_0.png")
        game(GameKey(PREM,AWAY,"Hull City",2016))
        datePlayed(April(1,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/HullCity.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,HOME,"Middlesbrough",2016))
        datePlayed(October(1,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(1,1),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/middlesbrough.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-middlesbrough")
        game(GameKey(PREM,HOME,"Hull City",2016))
        datePlayed(December(17,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/HullCity.png")
        game(GameKey(LGCP,AWAY,"Manchester United",2016))
        datePlayed(November(30,2016) at (20,0))
        competition("/sites/default/files/competition/2016-09/eflcup.png")
        homeTeam("/sites/default/files/logos/teams/ManUtd.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Everton",2016))
        datePlayed(October(30,2016) at (13,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(2,0),None))
        homeTeam("/sites/default/files/logos/teams/Everton.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/everton-vs-west-ham-united")
        game(GameKey(PREM,HOME,"Stoke City",2016))
        datePlayed(November(5,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(1,1),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Stroke.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-stoke-city-0")
        game(GameKey(PREM,HOME,"Everton",2016))
        datePlayed(April(22,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Everton.png")
        game(GameKey(PREM,AWAY,"Sunderland",2016))
        datePlayed(April(15,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/sunderland-afc-logo.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,HOME,"Watford",2016))
        datePlayed(September(10,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(2,4),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/watford.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-2-4-watford")
        game(GameKey(LGCP,HOME,"Chelsea",2016))
        datePlayed(October(26,2016) at (19,45))
        competition("/sites/default/files/competition/2016-09/eflcup.png")
        result(GameResult(Score(2,1),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Chelsea_0.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-chelsea")
        game(GameKey(PREM,HOME,"Manchester United",2016))
        datePlayed(January(2,2017) at (17,15))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/ManUtd.png")
        game(GameKey(PREM,AWAY,"Leicester City",2016))
        datePlayed(December(31,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/leicester.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,HOME,"Southampton",2016))
        datePlayed(September(25,2016) at (16,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(0,3),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Southampton.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-southampton")
        game(GameKey(PREM,HOME,"Bournemouth",2016))
        datePlayed(August(21,2016) at (16,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(1,0),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/afc-bournemouth-icon.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-bournemouth")
        game(GameKey(PREM,AWAY,"Middlesbrough",2016))
        datePlayed(January(21,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/middlesbrough.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Swansea City",2016))
        datePlayed(December(26,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Swansea.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(FRIENDLY,HOME,"Juventus",2016))
        datePlayed(August(7,2016) at (13,0))
        competition("/sites/default/files/competition/2016-07/betway_cup.png")
        result(GameResult(Score(2,3),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/juventus_0.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-juventus")
        game(GameKey(PREM,AWAY,"West Bromwich Albion",2016))
        datePlayed(September(17,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(4,2),None))
        homeTeam("/sites/default/files/logos/teams/WestBrom.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-bromwich-albion-vs-west-ham-united")
        game(GameKey(PREM,HOME,"Liverpool",2016))
        datePlayed(May(13,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Liverpool.png")
        game(GameKey(FRIENDLY,AWAY,"Karlsruher SC",2016))
        datePlayed(July(23,2016) at (16,0))
        result(GameResult(Score(0,3),None))
        homeTeam("/sites/default/files/logos/teams/ksc_1.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/karlsruher-sc-vs-west-ham-united")
        game(GameKey(PREM,HOME,"Manchester City",2016))
        datePlayed(February(1,2017) at (19,45))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/mancitylogo.png")
        game(GameKey(PREM,AWAY,"Tottenham Hotspur",2016))
        datePlayed(November(19,2016) at (17,30))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/Spurs.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        game(GameKey(PREM,AWAY,"Chelsea",2016))
        datePlayed(August(15,2016) at (20,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(2,1),None))
        homeTeam("/sites/default/files/logos/teams/Chelsea_0.png")
        awayTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/chelsea-vs-west-ham-united-1")
        game(GameKey(PREM,HOME,"Sunderland",2016))
        datePlayed(October(22,2016) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        result(GameResult(Score(1,0),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/sunderland-afc-logo.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-sunderland")
        game(GameKey(PREM,HOME,"Leicester City",2016))
        datePlayed(March(18,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/leicester.png")
        game(GameKey(PREM,HOME,"Tottenham Hotspur",2016))
        datePlayed(May(6,2017) at (15,0))
        competition("/sites/default/files/competition/2016-07/premier-league.png")
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/Spurs.png")
        game(GameKey(LGCP,HOME,"Accrington Stanley",2016))
        datePlayed(September(21,2016) at (19,45))
        competition("/sites/default/files/competition/2016-09/eflcup.png")
        result(GameResult(Score(1,0),None))
        homeTeam("/sites/default/files/logos/teams/whu_newbadge.png")
        awayTeam("/sites/default/files/logos/teams/accringtonstanley.png")
        report("/fixtures/first-team/fixtures-and-results/season-20162017/west-ham-united-vs-accrington-stanley")

        gameUpdateCommands
      }
      maybeGameCommands must beRight { gameCommands: Seq[GameUpdateCommand] =>
        gameCommands must containTheSameElementsAs(expectedGameUpdateCommands)
      }
    }
  }

  trait ServerContext extends After {

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
