package json

import argonaut.{DecodeJson, Parse}

import scala.io.Source
import scalaz.\/-
import scalaz.-\/

/**
 * Created by alex on 12/02/15.
 */
object ConfigurationReader {

  private object Implicits {

    implicit class OrImplicit[A](a: A) {

      def or[V](v: V) = Option(a) match {
        case Some(x) => \/-(x)
        case None => -\/(v)
      }
    }
  }

  import Implicits._

  def apply[V](resource: String)(implicit decoder: DecodeJson[V]): V = {
    val result = for {
      stream <- ConfigurationReader.getClass.getClassLoader.getResourceAsStream(resource).or(s"Cannot find resource $resource")
      json <- Parse.decodeEither(Source.fromInputStream(stream).getLines.mkString)
    }
    yield json
    result.fold(msg => throw new IllegalStateException(msg), identity)
  }

}
