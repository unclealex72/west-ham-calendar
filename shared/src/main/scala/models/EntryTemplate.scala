package models

import io.circe.{Decoder, Encoder}

import scala.collection.immutable
/**
  * Created by alex on 16/02/16.
  */
case class EntryTemplate[D](user: Option[String], seasons: Set[SeasonTemplate[D]], links: Links[EntryRel] = Links[EntryRel]())

sealed trait EntryRel extends Rel
object EntryRel extends RelEnum[EntryRel] {
  val values: immutable.IndexedSeq[EntryRel] = findValues

  object LOGIN extends Rel_("login") with EntryRel
  object LOGOUT extends Rel_("logout") with EntryRel
}

object EntryTemplate {

  implicit def entryEncoder[D](implicit ev: Encoder[D]): Encoder[EntryTemplate[D]] =
    Encoder.forProduct3("user", "seasons", "links")(entry => (entry.user, entry.seasons, entry.links))

  implicit def entryDecoder[D](implicit ev: Decoder[D]): Decoder[EntryTemplate[D]] =
    Decoder.forProduct3("user", "seasons", "links")(EntryTemplate.apply[D])
}