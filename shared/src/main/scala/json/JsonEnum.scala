package json

import upickle.{default, Js}
import upickle.default.{Reader, Writer}
import enumeratum._

import scalaz.{Failure, Success, ValidationNel}

/**
  * Upickle custom serialiser and deserialiser to enums.
  *
  * Created by alex on 11/02/16.
  */
trait JsonEnum[E <: EnumEntry] extends Enum[E] with JsonConverters[E] {

  def enumToJson(e: E): Js.Value = Js.Str(e.entryName)

  override def serialise(e: E) = enumToJson(e)

  def jsonToEnum(js: Js.Value): ValidationNel[String, E] = {
    val enum = js match {
      case Js.Str(str) => withNameOption(str).map(Success(_)).getOrElse(Failure(s"$str is not a valid ${getClass.getName}"))
      case _ => Failure(s"Expected a string when trying to parse a ${getClass.getName}")
    }
    enum.toValidationNel
  }

  override def deserialise(js: Js.Value) = deserialise(js)
}
