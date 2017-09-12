package models

import enumeratum.EnumEntry
import io.circe.{Decoder, Encoder, KeyDecoder, KeyEncoder}
import json.JsonEnum

/**
  * Type safe links object
  * Created by alex on 14/02/16.
  */
case class Links[R <: Rel](private val links: Map[R, String] = Map.empty[R, String], private val _self: Option[String] = None) {

  def withSelf(href: String): Links[R] = Links(links, Some(href))

  def withLink(rel: R, href: String): Links[R] = Links(links + (rel -> href), _self)

  def withLink(rel: R, maybeHref: Option[String]): Links[R] = withLinks(maybeHref.map(rel -> _).toMap)

  def withLinks(moreLinks: Map[R, String]): Links[R] = Links(links ++ moreLinks, _self)

  def self: Option[String] = _self

  def apply(rel: R): Option[String] = links.get(rel)

  def required(rel: R): String = apply(rel).get
}

trait Rel extends EnumEntry {
  val rel: String

  final override val entryName: String = rel

  final override def toString: String = rel
}

abstract class Rel_(val rel: String) extends Rel

trait RelEnum[R <: Rel] extends JsonEnum[R] with (String => Option[R]) {

  def apply(rel: String): Option[R] = withNameOption(rel)

  implicit def relFromString(str: String): Either[String, R] =
    apply(str).toRight(s"$str is not a valid ${getClass.getName}")
}

object Links {

  def withSelf[R <: Rel](href: String): Links[R] = Links[R]().withSelf(href)

  def withLink[R <: Rel](rel: R, href: String): Links[R] = Links[R]().withLink(rel, href)

  def withLinks[R <: Rel](moreLinks: Map[R, String]): Links[R] = Links[R]().withLinks(moreLinks)

  // Classes to facilitate serialising and deserialising links

  private case class JsonLink(rel: String, href: String)

  private case class JsonLinks(links: Seq[JsonLink])

  private def linksToJsonLinks[R <: Rel](links: Links[R]): JsonLinks = {
    val jsonLinks: Seq[JsonLink] = links.links.toSeq.map {
      case (rel, href) => JsonLink(rel.entryName, href)
    }
    val selfLinks: Seq[JsonLink] = links._self.toSeq.map { href =>
      JsonLink("_self", href)
    }
    JsonLinks(jsonLinks ++ selfLinks)
  }

  private def jsonLinksToLinks[R <: Rel](jsonLinks: JsonLinks)(implicit ev: KeyDecoder[R]): Either[String, Links[R]] = {
    val emptyLinks: Either[String, Links[R]] = Right(Links[R]())
    jsonLinks.links.foldLeft(emptyLinks) { (eitherLinks, jsonLink) =>
      eitherLinks.flatMap { links =>
        val href = jsonLink.href
        val rel = jsonLink.rel
        if (rel == "_self") {
          Right(links.withSelf(href))
        }
        else {
          val rel_ = ev(rel).toRight(s"$rel is not a valid relation")
          rel_.map { r =>
            links.withLink(r, href)
          }
        }
      }
    }
  }

  private implicit def jsonLinkEncoder[R <: Rel]: Encoder[JsonLink] =
    Encoder.forProduct2("rel", "href")(jl => (jl.rel, jl.href))

  private def jsonLinksEncoder[R <: Rel]: Encoder[JsonLinks] =
    Encoder.forProduct1("links")(ls => ls.links)

  private implicit def jsonLinkDecoder[R <: Rel](implicit ev: KeyDecoder[R]): Decoder[JsonLink] =
    Decoder.forProduct2("rel", "href")(JsonLink.apply)

  private def jsonLinksDecoder[R <: Rel](implicit ev: KeyDecoder[R]): Decoder[JsonLinks] =
    Decoder.forProduct1("links")(JsonLinks.apply)

  implicit def linksEncoder[R <: Rel]: Encoder[Links[R]] =
    jsonLinksEncoder.contramap(linksToJsonLinks)

  implicit def linksDecoder[R <: Rel](implicit ev: KeyDecoder[R]): Decoder[Links[R]] = {
    jsonLinksDecoder.emap(jl => jsonLinksToLinks(jl))
  }
}