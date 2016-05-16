package sprites

import java.awt.Point

/**
  * A class to hold a CSS sprite consisting of a PNG image, a map of class names to positions and a map of URLs to class names
  * Created by alex on 03/04/16.
  */
case class Sprite(image: Array[Byte], positionsByClassName: Map[String, Point], classNamesByUrl: Map[String, String])