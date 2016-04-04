package dao

import dates.NowService
import model.Game
import models.{GameResult, Competition, Location}
import org.joda.time.DateTime
import search.{AttendedSearchOption, GameOrTicketSearchOption, LocationSearchOption, SearchOption}

import scala.collection.SortedSet
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alex on 19/01/16.
  */
class SlickGameDao(val dbConfigFactory: DatabaseConfigFactory)(implicit ec: ExecutionContext) extends GameDao with Slick {

  import dbConfig.driver.api._

  val create = games.schema.create
  val drop = games.schema.drop

  /** Table description of table game. Objects of this class serve as prototypes for rows in queries. */
  class TGame(_tableTag: Tag) extends Table[Game](_tableTag, "game") {
    def * = (
      id,
      location,
      season,
      opponents,
      competition,
      at,
      attended,
      result,
      attendence,
      report,
      tvchannel,
      academymembers,
      bondholders,
      prioritypoint,
      seasontickets,
      generalsale,
      hometeamimageurl,
      awayteamimageurl,
      competitionimageurl,
      datecreated,
      lastupdated) <> (Game.tupled, Game.unapply)

    /** Database column opponents SqlType(varchar), Length(128,true) */
    val opponents: Rep[String] = column[String]("opponents", O.Length(128,varying=true))
    /** Database column academymembers SqlType(timestamp), Default(None) */
    val academymembers: Rep[Option[DateTime]] = column[Option[DateTime]]("academymembers", O.Default(None))
    /** Database column bondholders SqlType(timestamp), Default(None) */
    val bondholders: Rep[Option[DateTime]] = column[Option[DateTime]]("bondholders", O.Default(None))
    /** Database column attended SqlType(bool), Default(None) */
    val attended: Rep[Boolean] = column[Boolean]("attended", O.Default(false))
    /** Database column result SqlType(varchar), Length(128,true), Default(None) */
    val result: Rep[Option[GameResult]] = column[Option[GameResult]]("result", O.Length(128,varying=true), O.Default(None))
    /** Database column competition SqlType(varchar), Length(128,true) */
    val competition: Rep[Competition] = column[Competition]("competition", O.Length(128,varying=true))
    /** Database column season SqlType(int4) */
    val season: Rep[Int] = column[Int]("season")
    /** Database column attendence SqlType(int4), Default(None) */
    val attendence: Rep[Option[Int]] = column[Option[Int]]("attendence", O.Default(None))
    /** Database column prioritypoint SqlType(timestamp), Default(None) */
    val prioritypoint: Rep[Option[DateTime]] = column[Option[DateTime]]("prioritypoint", O.Default(None))
    /** Database column at SqlType(timestamp), Default(None) */
    val at: Rep[Option[DateTime]] = column[Option[DateTime]]("at", O.Default(None))
    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    /** Database column location SqlType(varchar), Length(128,true) */
    val location: Rep[Location] = column[Location]("location", O.Length(128,varying=true))
    /** Database column tvchannel SqlType(varchar), Length(128,true), Default(None) */
    val tvchannel: Rep[Option[String]] = column[Option[String]]("tvchannel", O.Length(128,varying=true), O.Default(None))
    /** Database column report SqlType(text), Default(None) */
    val report: Rep[Option[String]] = column[Option[String]]("report", O.Default(None))
    /** Database column seasontickets SqlType(timestamp), Default(None) */
    val seasontickets: Rep[Option[DateTime]] = column[Option[DateTime]]("seasontickets", O.Default(None))
    /** Database column generalsale SqlType(timestamp), Default(None) */
    val generalsale: Rep[Option[DateTime]] = column[Option[DateTime]]("generalsale", O.Default(None))
    /** Database column datecreated SqlType(timestamp) */
    val datecreated: Rep[DateTime] = column[DateTime]("datecreated")
    /** Database column lastupdated SqlType(timestamp) */
    val lastupdated: Rep[DateTime] = column[DateTime]("lastupdated")
    /** Database column academymemberspostal SqlType(timestamp), Default(None) */
    val hometeamimageurl: Rep[Option[String]] = column[Option[String]]("hometeamimageurl", O.Default(None))
    val awayteamimageurl: Rep[Option[String]] = column[Option[String]]("awayteamimageurl", O.Default(None))
    val competitionimageurl: Rep[Option[String]] = column[Option[String]]("competitionimageurl", O.Default(None))
    /** Uniqueness Index over (competition,location,opponents,season) (database name idxgameKeyComposite) */
    val index1 = index("idxgameKeyComposite", (competition, location, opponents, season), unique=true)
  }
  /** Collection-like TableQuery object for table Game */
  lazy val games = new TableQuery(tag => new TGame(tag))

  /**
    * Find a game by its {@link Competition}, {@link Location}, opponents and season. Together, these are guaranteed to
    * uniquely define a {@link Game}.
    *
    * @param competition The { @link Competition} to search for.
    * @param location The { @link Location} to search for.
    * @param opponents The opponents to search for.
    * @param season    The season to search for.
    * @return The uniquely defined { @link Game} if it exists or null otherwise.
    */
  override def findByBusinessKey(competition: Competition, location: Location, opponents: String, season: Int): Future[Option[Game]] = dbConfig.db.run {
    games.
      filter(_.location === location).
      filter(_.opponents === opponents).
      filter(_.season === season).
      filter(_.competition === competition).
      result.headOption
  }

  /**
    * Get the latest known season.
    *
    * @return The latest known season.
    */
  override def getLatestSeason: Future[Option[Int]] = dbConfig.db.run {
    games.map(_.season).max.result
  }

  /**
    * Persist a game
    */
  override def store(game: Game)(implicit nowService: NowService): Future[Game] = {
    store_(game.copy(lastUpdated = nowService.now))
  }

  def store_(game: Game): Future[Game] = dbConfig.db.run {
    (games returning games).insertOrUpdate(game)
  }.map(_.getOrElse(game))

  /**
    * Find a game by its ID.
    */
  override def findById(id: Long): Future[Option[Game]] = dbConfig.db.run {
    games.filter(_.id === id).result.headOption
  }

  /**
    * Get all the {@link Game}s for a given season.
    *
    * @param season The season to search for.
    * @return All the { @link Game}s during the given season.
    */
  override def getAllForSeason(season: Int): Future[List[Game]] = dbConfig.db.run {
    val x = games.filter(_.season === season)
    games.filter(_.season === season).sortBy(_.at).result
  }.map(_.toList)

  /**
    * Find all the seasons known so far.
    *
    * @return All the known seasons.
    */
  override def getAllSeasons: Future[SortedSet[Int]] = dbConfig.db.run {
    games.map(_.season).distinct.result
  }.map(SortedSet(_ :_*))

  /**
    * Search for games that match all the search options provided.
    */
  override def search(
                       attendedSearchOption: AttendedSearchOption,
                       locationSearchOption: LocationSearchOption,
                       gameOrTicketSearchOption: GameOrTicketSearchOption): Future[List[Game]] = dbConfig.db.run {
    Seq[SearchOption](attendedSearchOption, locationSearchOption, gameOrTicketSearchOption).foldLeft(games.asInstanceOf[Query[TGame, Game, Seq]]) {
      (q, so) => so match {
        case AttendedSearchOption.ATTENDED => q.filter(_.attended === true)
        case AttendedSearchOption.UNATTENDED => q.filter(g => g.attended === false)
        case LocationSearchOption.HOME =>
          val home: Location = Location.HOME
          q.filter(_.location === home)
        case LocationSearchOption.AWAY =>
          val away: Location = Location.AWAY
          q.filter(_.location === away)
        case GameOrTicketSearchOption.BONDHOLDERS => q.filter(_.bondholders.isDefined)
        case GameOrTicketSearchOption.PRIORITY_POINT => q.filter(_.prioritypoint.isDefined)
        case GameOrTicketSearchOption.SEASON => q.filter(_.seasontickets.isDefined)
        case GameOrTicketSearchOption.ACADEMY => q.filter(_.academymembers.isDefined)
        case GameOrTicketSearchOption.GENERAL_SALE => q.filter(_.generalsale.isDefined)
        case _ => q
      }
    }.sortBy(_.at).result
  }.map(_.toList)

  /**
    * Get all {@link Game}s for a given season and {@link Location}.
    *
    * @param season   The season to search for.
    * @param location The { @link Location} to search for.
    * @return All { @link Game}s with the given season and { @link Location}.
    */
  override def getAllForSeasonAndLocation(season: Int, location: Location): Future[List[Game]] = dbConfig.db.run{
    games.filter(_.season === season).filter(_.location === location).sortBy(_.at).result
  }.map(_.toList)

  /**
    * Get all known games.
    */
  override def getAll: Future[List[Game]] = dbConfig.db.run {
    games.sortBy(_.at).result
  }.map(_.toList)

  /**
    * Find a game by the {@link DateTime} it was played.
    *
    * @param datePlayed The { @link DateTime} to search for.
    * @return The { @link Game} played at the given { @link DateTime} or null if one could not be found.
    */
  override def findByDatePlayed(datePlayed: DateTime): Future[Option[Game]] = dbConfig.db.run {
    games.filter(_.at === datePlayed).sortBy(_.at).result.headOption
  }

  /**
    * Get all competition logos.
    *
    * @return
    */
  override def getAllCompetitionLogos: Future[Set[String]] = dbConfig.db.run {
    games.map(_.competitionimageurl).distinct.result
  }.map(_.flatten.toSet)

  /**
    * Get all home and away logos.
    *
    * @return
    */
  override def getAllTeamLogos: Future[Set[String]] = dbConfig.db.run {
    games.map(_.hometeamimageurl).union(games.map(_.awayteamimageurl)).distinct.result
  }.map(_.flatten.toSet)
}
