package sprites

import java.awt.image.BufferedImage
import java.awt.{Dimension, Point}

/**
  * A trait to create a CSS sprite from a set of URLs.
  * Created by alex on 03/04/16.
  */
trait SpriteService {

  /**
    * Generate an image containing all the input images scaled to a max width and height
    * @param imageURLs The URLs to combine
    * @param maxSize The maximum size of each image
    * @return The combined image and a map of the URLs to their position in the new image.
    */
  def generate(imageURLs: Set[String], maxSize: Dimension): Option[(BufferedImage, Map[String, Point])]
}
