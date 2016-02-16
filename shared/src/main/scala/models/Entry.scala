package models

/**
  * Created by alex on 16/02/16.
  */
case class Entry(user: Option[String], links: Links[EntryRel])

sealed trait EntryRel extends Rel
object EntryRel extends RelEnum[EntryRel] {
  val values = findValues
}