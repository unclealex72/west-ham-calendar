package models

import json.JsonCodecs
import upickle.Js

/**
  * Created by alex on 14/02/16.
  */
case class Links(links: Map[String, String] = Map.empty) {

  def withSelf(href: String) = withLink("self", href)

  def withLink(rel: String, href: String) = Links(links + (rel -> href))

  def withLinks(moreLinks: Map[String, String]) = Links(links ++ moreLinks)
}

object Links extends JsonCodecs {

  def self(href: String): Links = Links().withSelf(href)

  def linksToJson(links: Links): Js.Value = {
    val jsonLinks = links.links.toSeq.map { case (rel, href) =>
      Js.Obj(Map("rel" -> rel, "href" -> href).mapValues(Js.Str).toSeq :_*)
    }
    Js.Arr(jsonLinks :_*)
  }

  private def jsonToLink(value: Js.Value): Either[String, (String, String)] = value.jsObj { fields =>
    for {
      rel <- fields.mandatory("rel", "Cannot find a rel property for links")(_.jsStr).right
      href <- fields.mandatory("href", "Cannot find a href property for links")(_.jsStr).right
    } yield (rel, href)
  }

  def jsonToLinks(value: Js.Value): Either[String, Links] = {
    value.jsArr(jsonToLink).right.map { links =>
      Links(links.toMap)
    }
  }
}