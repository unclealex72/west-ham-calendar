package update.fixtures

import java.io.{PrintWriter, StringWriter}
import java.net.URI

import cats.data.NonEmptyList
import dates.DateParserFactory
import html._
import logging.{RemoteLogging, RemoteStream}
import model.GameKey
import models.Location.{AWAY, HOME}
import models.{Competition, GameResult, Score}
import monads.FE
import monads.FE.FutureEitherNel
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import update.WsBody
import xml.NodeExtensions
import cats.instances.future._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.xml.{Elem, NodeSeq, Node => XmlNode}
/**
 * Created by alex on 08/03/15.
 */
class FixturesGameScannerImpl @javax.inject.Inject()(rootUri: URI, ws: WSClient, dateParserFactory: DateParserFactory)(implicit val ec: ExecutionContext) extends FixturesGameScanner with RemoteLogging with WsBody with NodeExtensions {

  override def scan(latestSeason: Option[Int])(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    val fixturesUri = rootUri.resolve("/fixtures/first-team/fixtures-and-results")
    for {
      page <- bodyXml(fixturesUri)(uri => ws.url(uri).get())
      seasons <- listSeasons(page)
      gameUpdateCommands <- downloadFixtures(seasons)
    } yield gameUpdateCommands
  }

  def listSeasons(page: Elem)(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[FixturesSeason]] = {
    def parseSeasons(select: XmlNode): Seq[FixturesSeason] = {
      for {
        option <- select \\ "option"
        id <- option.attribute("value").toSeq.flatten[XmlNode].headOption.map(_.text.trim)
        season <- Try(option.text.trim().take(4).toInt).toOption
      } yield {
        FixturesSeason(id, season)
      }
    }
    val possiblyEmptySeasons = for {
      div <- (page \\ "select").find(_.hasAttr("name", "field_competition_season")).toRight(NonEmptyList.of("Cannot find a select with name field_competition_season"))
      seasons <- Right(parseSeasons(div))
    } yield seasons
    FE(possiblyEmptySeasons.filterOrElse(_.nonEmpty, NonEmptyList.of("Cannot find any seasons on the fixtures page")))
  }

  def downloadFixtures(seasons: Seq[FixturesSeason])(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    val empty: FutureEitherNel[String, Seq[GameUpdateCommand]] = FE(Future.successful(Seq.empty))
    seasons.foldLeft(empty){ (gameUpdateCommands, season) =>
      for {
        existingGameUpdateCommands <- gameUpdateCommands
        newGameUpdateCommands <- downloadFixturesForSeason(season)
      } yield existingGameUpdateCommands ++ newGameUpdateCommands
    }
  }

  def downloadFixturesForSeason(season: FixturesSeason)(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    downloadFixturesForSeason(season, 0, Seq.empty)
  }

  def downloadFixturesForSeason(season: FixturesSeason, page: Int, previousGameUpdateCommands: Seq[GameUpdateCommand])(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    val form: Map[String, Any] = Map(
        "view_name" -> "fixtures_view",
        "view_display_id" -> "first_team_fixtures_view",
        "view_args" -> "",
        "view_path" -> "/fixtures/first-team/fixtures-and-results",
        "view_base_path" -> "fixtures/first-team/fixtures-and-results",
        "view_dom_id" -> "908ad4727a4796c59f5fa187e39dbda3b8cb834b0e6dbebec694fdc61e248813",
        "pager_element" -> 0,
        "field_competition" -> "All",
        "field_competition_season" -> season.id,
        "" -> "Apply",
        "page" -> page,
        "_drupal_ajax" -> 1,
        "ajax_page_state[theme]" -> "westham",
        "ajax_page_state[theme_token]" -> "",
        "ajax_page_state[libraries]" -> "ajax_loader/ajax_loader.throbber,bootstrap/popover,bootstrap/tooltip,core/html5shiv,google_analytics/google_analytics,popup_message/popup_message_style,system/base,views/views.ajax,views/views.module,views_infinite_scroll/views-infinite-scroll,westham/bootstrap-scripts,westham/global-styling")
      
    val eventualResponse: Future[WSResponse] = ws.url(rootUri.resolve("/views/ajax").toString).
      withQueryStringParameters("_wrapper_format" -> "drupal_ajax").
      withHttpHeaders("accept" -> "application/json",
        "accept-encoding" -> "gzip, deflate",
        "Content-Type" -> "application/x-www-form-urlencoded").post(form.mapValues(v => Seq(v.toString)))
    val feResponse: FutureEitherNel[String, WSResponse] = FE(eventualResponse)
    feResponse.flatMap(toGameUpdateCommands(season.year)).flatMap { newGameUpdateCommands =>
      if (newGameUpdateCommands.diff(previousGameUpdateCommands).isEmpty) {
        FE(Right(previousGameUpdateCommands))
      }
      else {
        downloadFixturesForSeason(season, page + 1, previousGameUpdateCommands ++ newGameUpdateCommands)
      }
    }
  }

  def toGameUpdateCommands(season: Int)(response: WSResponse)(implicit remoteStream: RemoteStream): FutureEitherNel[String, Seq[GameUpdateCommand]] = {
    if (response.status != 200) {
      FE(Left(NonEmptyList.of(response.statusText)))
    }
    else {
      try {
        val fullJson = Json.parse(response.body)
        val dataElements: Seq[JsValue] = fullJson \\ "data"
        val gameUpdateCommands = for {
          dataElement <- dataElements
          data <- dataElement.asOpt[String].toSeq
          gameUpdateCommand <- parseHtml(season, data)
        } yield gameUpdateCommand
        gameUpdateCommands.foreach { gameUpdateCommand =>
          logger debug s"Found game update command $gameUpdateCommand"
        }
        FE(Right(gameUpdateCommands))
      }
      catch {
        case t: Throwable =>
          val sw = new StringWriter
          t.printStackTrace(new PrintWriter(sw))
          FE(Left(NonEmptyList.of(t.toString)))
      }
    }
  }

  def parseHtml(season: Int, data: String)(implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val unescapedHtml: String = "<html><head/><body>" + StringContext.treatEscapes(data).replace("\\n", " ") + "</body></html>"
    val fauxHtml = cleanHtml(unescapedHtml)
    for {
      div <- fauxHtml \\~ ("div", "matchItem")
      gameUpdateCommand <- parseRow(season, div)
    } yield {
      gameUpdateCommand
    }
  }

  case class CompetitionInfo(competition: Competition, maybeImageUrl: Option[String])

  def parseRow(season: Int, div: XmlNode)(implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val datePlayedParser = dateParserFactory.forSeason(season, "d MMMM H:mm", "dd MMMM H:mm", "d MMMM HH:mm", "dd MMMM HH:mm")
    val gameKeysAndMaybeCompetitionImageUrls: Seq[(GameKey, Option[String])] = for {
      homeTeamNameDiv <- div \\~ ("div", "homeTeam")
      awayTeamNameDiv <- div \\~ ("div", "awayTeam")
      venueNameDiv <- div \\~ ("div", "venueName")
      competitionDiv <- div \\~ ("div", "center-image")
    } yield {
      val homeTeam = homeTeamNameDiv.trimmed
      val awayTeam = awayTeamNameDiv.trimmed
      val competitionInfo = (competitionDiv \\ "img").headOption.flatMap(_.attr("src")) match {
        case Some(src) =>
          CompetitionInfo(Competition(src), Some(src))
        case None =>
          val competitionToken = competitionDiv.trimmed
          CompetitionInfo(Competition(competitionToken), None)
      }
      val isHome = "West Ham United" == homeTeam
      (GameKey(competitionInfo.competition, if (isHome) HOME else AWAY, if(isHome) awayTeam else homeTeam, season), competitionInfo.maybeImageUrl)
    }
    gameKeysAndMaybeCompetitionImageUrls.flatMap { gameKeyAndMaybeCompetitionImageUrl =>
      val (gameKey, maybeCompetitionImageUrl) = gameKeyAndMaybeCompetitionImageUrl
      val gameLocator = GameKeyLocator(gameKey)
      // Date played
      val datePlayedUpdateCommands: Seq[GameUpdateCommand] = for {
        datePlayedDiv <- div \\~ ("div", "date")
        datePlayed <- datePlayedParser.find(datePlayedDiv.trimmed)
      } yield {
        DatePlayedUpdateCommand(gameLocator, datePlayed)
      }
      val competitionImageGameUpdateCommands: Seq[GameUpdateCommand] = maybeCompetitionImageUrl.toSeq.map { url =>
        CompetitionImageLinkCommand(gameLocator, rootUri.resolve(url).toString)
      }
      val resultUpdateCommands: Seq[GameUpdateCommand] = parseScore(div \\~ ("div", "versus")).map { score =>
         GameResult(score, parseScore(div \\~ ("div", "versus") \~ ("div", "shoot")))
      }.map(ResultUpdateCommand(gameLocator, _)).toSeq
      val matchReportGameUpdateCommands: Seq[GameUpdateCommand] = for {
        a <- div \\ "a" if a.trimmed == "Match Centre"
        src <- a.attr("href")
      } yield {
        MatchReportUpdateCommand(gameLocator, rootUri.resolve(src).toString)
      }
      def parseTeamImage(clazz: String)(builder: String => GameUpdateCommand): Seq[GameUpdateCommand] = for {
        teamDiv <- div \\ "div" if teamDiv.hasClass(clazz)
        img <- teamDiv \\ "img"
        src <- img.attr("src")
      } yield {
        builder(rootUri.resolve(src).toString)
      }
      val homeImageLinkUpdateCommands: Seq[GameUpdateCommand] = parseTeamImage("homeTeam")(HomeTeamImageLinkCommand(gameLocator, _))
      val awayImageLinkUpdateCommands: Seq[GameUpdateCommand] = parseTeamImage("awayTeam")(AwayTeamImageLinkCommand(gameLocator, _))
      datePlayedUpdateCommands ++ competitionImageGameUpdateCommands ++ resultUpdateCommands ++
        homeImageLinkUpdateCommands ++ awayImageLinkUpdateCommands ++ matchReportGameUpdateCommands
    }
  }

  def parseScore(div: NodeSeq): Option[Score] = {
    def parse(span: XmlNode): Option[Int] = {
      Try(span.text.filter(ch => Character.isDigit(ch)).toInt).toOption
    }
    val scores = for {
      homeSpan <- div \~ ("span", "home")
      awaySpan <- div \~ ("span", "away")
      homeScore <- parse(homeSpan)
      awayScore <- parse(awaySpan)
    } yield {
      Score(homeScore, awayScore)
    }
    scores.headOption
  }

  /*
  def toGameUpdateCommands(fixture: Fixture, rootUrl: URI, season: Int)
                          (implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val (opponents, location) = if (fixture.home) (fixture.awayTeam, HOME) else (fixture.homeTeam, AWAY)
    for {
      competition <- logOnEmpty(Competition.apply(fixture.competitionName)).toList
      matchDate <- PossiblyYearlessDateParser.forSeason(season)("d MMMM HH:mm", "dd MMMM HH:mm").logFailures.find(fixture.matchDate).toList
      gameUpdateCommand <- toGameCommands(fixture, rootUrl, opponents, location, competition, matchDate, season)
    } yield {
      logger.info(s"Found update $gameUpdateCommand")
      gameUpdateCommand
    }
  }

  def toGameCommands(fixture: Fixture, rootUrl: URI, opponents: String, location: Location, competition: Competition, matchDate: ZonedDateTime, season: Int)(implicit remoteStream: RemoteStream): Seq[GameUpdateCommand] = {
    val locator: GameLocator = GameKeyLocator(GameKey(competition, location, opponents, season))
    val datePlayedUpdateCommand = Some(DatePlayedUpdateCommand(locator, matchDate))
    def isNeitherEmptyNorNull(str: String): Option[String] = Option(str.trim).filterNot(_.isEmpty)
    def isDigits(str: String): Option[Int] = isNeitherEmptyNorNull(str).filter { s =>
      s.matches("""\d+""")
    }.map(_.toInt)
    val resultUpdateCommand = for {
      homeScore <- isDigits(fixture.homeTeamScore)
      awayScore <- isDigits(fixture.awayTeamScore)
    } yield {
      val score = Score(homeScore.toInt, awayScore.toInt)
      val penalties = for {
        homePenalties <- isDigits(fixture.homeShootoutScore)
        awayPenalties <- isDigits(fixture.awayShootoutScore)
      } yield Score(homePenalties, awayPenalties)
      val result = GameResult(score, penalties)
      ResultUpdateCommand(locator, result)
    }
    val matchReportUpdateCommand = fixture.link.map { link =>
      val fullLink = rootUrl.resolve(link)
      MatchReportUpdateCommand(locator, fullLink.toString)
    }
    val uriCleaners: Seq[String => String] = Seq(
      _.replace("~", ""),
      _.replace(" ", "%20"),
      str => {
        val queryParams = str.indexOf('?')
        if (queryParams < 0) str else str.substring(0, queryParams)
      }
    )
    val logoUpdateCommands: Seq[Option[GameUpdateCommand]] = Seq(
      fixture.homeTeamLogo -> HomeTeamImageLinkCommand.curried(locator),
      fixture.awayTeamLogo -> AwayTeamImageLinkCommand.curried(locator),
      fixture.competitionLogo -> CompetitionImageLinkCommand.curried(locator)).map {
      case (value, gameUpdater) =>
        for {
          logo <- isNeitherEmptyNorNull(value)
        } yield {
          val sanitised = uriCleaners.foldLeft(logo)((str, f) => f(str))
          val absoluteUrl = rootUrl.resolve(sanitised).toASCIIString
          gameUpdater(absoluteUrl)
        }
    }
    (Seq(datePlayedUpdateCommand, resultUpdateCommand, matchReportUpdateCommand) ++ logoUpdateCommands).flatten
  }
  */
  case class FixturesSeason(id: String, year: Int)
}
