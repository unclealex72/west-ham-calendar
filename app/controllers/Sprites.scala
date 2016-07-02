package controllers

import java.awt.Dimension
import java.io.ByteArrayInputStream
import javax.inject.Inject

import akka.stream.scaladsl.{Source, StreamConverters}
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import sprites.{LogoSizes, Sprite, SpriteHolder}

import scala.concurrent.ExecutionContext

class Sprites @Inject() (val spriteHolder: SpriteHolder,
              val logoSizes: LogoSizes,
              implicit val ec: ExecutionContext) extends Controller with Etag {

  def spriteAndLastUpdated(f: SpriteHolder => Option[Sprite])(action: Sprite => Action[AnyContent]): Action[AnyContent] = {
    val optionalResult = for {
      sprite <- f(spriteHolder)
      lastUpdated <- spriteHolder.lastUpdated
    } yield {
      ETag(ISODateTimeFormat.basicDateTime().print(lastUpdated))(action(sprite))
    }
    optionalResult.getOrElse(Action { implicit request => NotFound })
  }

  def css(f: SpriteHolder => Option[Sprite], mainClass: String, size: Dimension, call: Call) = spriteAndLastUpdated(f) { sprite =>
    Action { implicit request =>
      Ok(views.html.sprites(size, mainClass, call.absoluteURL(), sprite.positionsByClassName.toSeq)).as("text/css")
    }
  }

  def image(f: SpriteHolder => Option[Sprite]) = spriteAndLastUpdated(f) { sprite =>
      Action { implicit request =>
        Ok.chunked(StreamConverters.fromInputStream(() => new ByteArrayInputStream(sprite.image))).as("image/png")
      }
  }

  def teamCss = css(_.teams, "team-logo", logoSizes.teamLogoSize, routes.Sprites.teamImage())

  def teamImage = image(_.teams)

  def competitionCss = css(_.competitions, "competition-logo", logoSizes.competionLogoSize, routes.Sprites.competitionImage())

  def competitionImage = image(_.competitions)
}