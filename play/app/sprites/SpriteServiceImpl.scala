package sprites
import java.awt.{Dimension, Graphics2D, Point}
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.imgscalr.Scalr
import org.imgscalr.Scalr.{Method, Mode}

/**
  * Created by alex on 03/04/16.
  */
class SpriteServiceImpl extends SpriteService with StrictLogging {

  /**
    * Generate an image containing all the input images scaled to a max width and height
    *
    * @param imageURLs The URLs to combine
    * @param maxSize  The maximum size of each image
    * @return The combined image and a map of the URLs to their position in the new image.
    */
  override def generate(imageURLs: Set[String], maxSize: Dimension): (BufferedImage, Map[String, Point]) = {
    val image = new BufferedImage(maxSize.width * imageURLs.size, maxSize.height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.getGraphics.asInstanceOf[Graphics2D]
    val coordinatesByUrl = imageURLs.zipWithIndex.foldLeft(Map.empty[String, Point]) { (coordinatesByUrl, indexedUrl) =>
      val (url, index) = indexedUrl
      val coordinate = new Point(index * maxSize.width, 0)
      drawTo(graphics, url, coordinate, maxSize)
      coordinatesByUrl + (url -> coordinate)
    }
    graphics.dispose()
    image.flush()
    (image, coordinatesByUrl)
  }

  def drawTo(graphics: Graphics2D, url: String, coordinate: Point, maxSize: Dimension): Unit = {
    try {
      val componentImage = ImageIO.read(new URL(url))
      val optionalResizedImage = Stream(Mode.FIT_TO_HEIGHT, Mode.FIT_TO_WIDTH).flatMap { mode =>
        val resizedImage = Scalr.resize(componentImage, Method.ULTRA_QUALITY, mode, maxSize.width, maxSize.height)
        if (resizedImage.getWidth > maxSize.width || resizedImage.getHeight > maxSize.height) {
          resizedImage.flush()
          None
        }
        else {
          Some(resizedImage)
        }
      }.headOption
      optionalResizedImage match {
        case Some(resizedImage) =>
          val offset = new Point((maxSize.width - resizedImage.getWidth) / 2, (maxSize.height - resizedImage.getHeight) / 2)
          graphics.drawImage(resizedImage, coordinate.x + offset.x, coordinate.y + offset.y, null)
          resizedImage.flush()
        case None =>
          logger.warn(s"Could not resize image url $url")
      }
      componentImage.flush()
    }
    catch {
      case e: Exception => logger.error(s"Could not read image url $url", e)
    }
  }
}
