package json

import enumeratum._
import upickle.Js
import upickle.default.{Reader, Writer}
/**
  * Upickle custom serialiser and deserialiser to enums.
  *
  * Created by alex on 11/02/16.
  */
trait JsonEnum[E <: EnumEntry] extends Enum[E] {

  def enumToJson(e: E): Js.Value = Js.Str(e.entryName)

  def jsonToEnum(js: Js.Value): Either[String, E] = {
    js match {
      case Js.Str(str) => withNameOption(str).toRight(s"$str is not a valid ${getClass.getName}")
      case _ => Left(s"Expected a string when trying to parse a ${getClass.getName}")
    }
  }
}
