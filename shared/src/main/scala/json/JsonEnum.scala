package json

import enumeratum._
import io.circe.{Decoder, Encoder, KeyDecoder, KeyEncoder}

/**
  * Circe custom serialiser and deserialiser for enums.
  *
  * Created by alex on 11/02/16.
  */
trait JsonEnum[E <: EnumEntry] extends Enum[E] {

  implicit val entryEncoder: Encoder[E] = Encoder.encodeString.contramap(_.entryName)
  implicit val entryDecoder: Decoder[E] = Decoder.decodeString.emap { en =>
    values.find(_.entryName == en).toRight(s"$en is not a valid ${getClass.getName}")
  }

  implicit val entryKeyEncoder: KeyEncoder[E] = KeyEncoder.encodeKeyString.contramap(_.entryName)
  implicit val entryKeyDecoder: KeyDecoder[E] = (key: String) => values.find(_.entryName == key)
}
