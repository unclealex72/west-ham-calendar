package json

import upickle.Invalid.Data
import upickle.Js

import scala.language.implicitConversions
import scalaz.Scalaz._
import scalaz._
/**
  * Helper methods for deserialising JSON and returning Eithers and Options
  * Created by alex on 14/02/16.
  */
trait JsonConverters[A] extends JsonCodecs {

  // Required reader and writer

  def serialise(a: A): Js.Value
  def deserialise(value: Js.Value): ValidationNel[String, A]

  implicit val validationReader: upickle.default.Reader[ValidationNel[String, A]] =
    upickle.default.Reader[ValidationNel[String, A]] {
      case value => deserialise(value)
  }

  implicit val eitherReader: upickle.default.Reader[\/[NonEmptyList[String], A]] =
    upickle.default.Reader[\/[NonEmptyList[String], A]] {
      case value => deserialise(value).disjunction
  }

  implicit val reader: upickle.default.Reader[A] = upickle.default.Reader[A] { case value =>
    deserialise(value).valueOr { msgs =>
      throw new Data(value, msgs.toList.mkString("\n"))
    }
  }

  implicit val writer: upickle.default.Writer[A] = upickle.default.Writer[A](serialise)

}
