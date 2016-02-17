package models

import json.JsonCodecs
import upickle.Js
import Links._

/**
  * Created by alex on 16/02/16.
  */
case class Entry(user: Option[String], links: Links[EntryRel])

sealed trait EntryRel extends Rel
object EntryRel extends RelEnum[EntryRel] {
  val values = findValues

  object SEASONS extends Rel_("seasons") with EntryRel
  object LOGIN extends Rel_("login") with EntryRel
  object LOGOUT extends Rel_("logout") with EntryRel
}

object Entry extends JsonCodecs {

  private def entryToJson(entry: Entry): Js.Value = {
    Js.Obj(entry.user.map(u => "user" -> Js.Str(u)).toSeq :+ "links" -> linksToJson(entry.links) :_*)
  }

  implicit val entry2Writer: upickle.default.Writer[Entry] = upickle.default.Writer[Entry] { case entry =>
    entryToJson(entry)
  }

  private def jsonToEntry(value: Js.Value): Either[String, Entry] = value.jsObj { fields =>
    for {
      user <- fields.optional("user")(_.jsStr).right
      links <- fields.optionalDefault("links")(jsonToLinks(EntryRel))(Links[EntryRel]()).right
    } yield Entry(user, links)
  }

  implicit val entry2Reader: upickle.default.Reader[Either[String, Entry]] = upickle.default.Reader[Either[String, Entry]] { case value =>
    jsonToEntry(value)
  }
}