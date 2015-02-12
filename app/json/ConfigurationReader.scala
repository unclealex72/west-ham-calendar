package json

import argonaut.{DecodeJson, Parse}

import scala.io.Source

/**
 * Created by alex on 12/02/15.
 */
object ConfigurationReader {

  def apply[V](resource: String)(implicit decoder: DecodeJson[V]): V = {
    val result = for {
      stream <- Option(ConfigurationReader.getClass.getClassLoader.getResourceAsStream(resource)).toRight(s"Cannot find resource $resource").right
      json <- Parse.decodeEither(Source.fromInputStream(stream).getLines.mkString).toEither.right
    }
    yield json
    result.fold(msg => throw new IllegalStateException(msg), identity)
  }
}
