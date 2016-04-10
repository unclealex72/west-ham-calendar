package sprites
import java.awt.{Dimension, Point}
import java.io.ByteArrayOutputStream
import javax.annotation.PostConstruct
import javax.imageio.ImageIO
import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.slf4j.StrictLogging
import dao.GameDao
import dates.NowService
import org.joda.time.DateTime
import play.api.Application
import play.api.cache.{Cache, CacheApi}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.concurrent.duration._
/**
  * Created by alex on 03/04/16.
  */
@Singleton
class PlayCacheSpriteHolder @Inject() (val cacheApi: CacheApi, val logoSizes: LogoSizes, val spriteService: SpriteService, val gameDao: GameDao, val nowService: NowService, implicit val ec: ExecutionContext) extends SpriteHolder with StrictLogging {

  sealed abstract class Key[T: ClassTag](val name: String) {
    def toKey = s"${classOf[PlayCacheSpriteHolder]}.$name"

    def get: Option[T] = {
      cacheApi.get[T](toKey)
    }
    def put(value: T) = cacheApi.set(toKey, value)
  }

  object COMPETITIONS extends Key[Sprite]("competitions")
  object TEAMS extends Key[Sprite]("teams")
  object LAST_UPDATED extends Key[DateTime]("lastUpdated")

  override def competitions: Option[Sprite] = COMPETITIONS.get

  override def teams: Option[Sprite] = TEAMS.get

  override def lastUpdated: Option[DateTime] = LAST_UPDATED.get

  override def update: Future[Unit] = {
    for {
      _ <- update(TEAMS, logoSizes.teamLogoSize, gameDao.getAllTeamLogos)
      _ <- update(COMPETITIONS, logoSizes.competionLogoSize, gameDao.getAllCompetitionLogos)
    } yield {
      LAST_UPDATED.put(nowService.now)
    }
  }

  def update(key: Key[Sprite], size: Dimension, urls: Future[Set[String]])(implicit ec: ExecutionContext): Future[Unit] = urls.map { urls =>
    logger.info(s"Updating logo cache ${key.name}")
    val (image, coordinatesByUrl) = spriteService.generate(urls, size)
    val out = new ByteArrayOutputStream
    ImageIO.write(image, "png", out)
    def positionToClassName(coordinate: Point): String = s"sprite-${key.name}-${coordinate.x}-${coordinate.y}"
    val classNamesByUrl = coordinatesByUrl.mapValues(positionToClassName)
    val positionsByClassName = coordinatesByUrl.values.foldLeft(Map.empty[String, Point]) { (positionsByClassName, coordinate) =>
      positionsByClassName + (positionToClassName(coordinate) -> coordinate)
    }
    key.put(Sprite(out.toByteArray, positionsByClassName, classNamesByUrl))
  }
}
