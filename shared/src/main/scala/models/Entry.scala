package models

import json.JsonConverters
import upickle.Js
import Links._

import scalaz._
import Scalaz._
import scala.collection.SortedSet
/**
  * Created by alex on 16/02/16.
  */
case class Entry(user: Option[String], seasons: SortedSet[Season], links: Links[EntryRel])

sealed trait EntryRel extends Rel
object EntryRel extends RelEnum[EntryRel] {
  val values = findValues

  object LOGIN extends Rel_("login") with EntryRel
  object LOGOUT extends Rel_("logout") with EntryRel
}

object Entry extends JsonConverters[Entry] {

  def serialise(entry: Entry): Js.Value = {
    Js.Obj(entry.user.map(u => "user" -> Js.Str(u)).toSeq ++ Seq(
      "seasons" -> jsSorted(Season.serialise)(entry.seasons),
      "links" -> linksToJson(entry.links)) :_*)
  }

  def deserialise(value: Js.Value): ValidationNel[String, Entry] = value.jsObj("Entry") { fields =>
    (fields.optional("user")(_.jsStr) |@|
      fields.mandatory("seasons")(_.jsSorted(Season.deserialise)) |@|
      fields.optionalDefault("links")(jsonToLinks(EntryRel))(Links[EntryRel]()))(Entry.apply)
  }
}