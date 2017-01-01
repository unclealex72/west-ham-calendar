package xml

import scala.xml.{Node, NodeSeq}

/**
  * Created by alex on 05/03/16.
  */
trait NodeExtensions {

  implicit class NodeHasAttribute(node: Node) {
    def hasClass(value: String): Boolean = attr("class") match {
      case Some(classText) =>
        classText.split("""\s+""").contains(value)
      case None => false
    }

    def attr(name: String): Option[String] = node.attribute(name).map(nodes => nodes.map(_.text).mkString(" "))

    def hasAttr(name: String, value: String): Boolean = node.attributes.asAttrMap.get(name).contains(value)

    def hasId(id: String): Boolean = hasAttr("id", id)

    def trimmed: String = node.text.trim
  }

  implicit class NodeSeqClassFilter(nodeSeq: NodeSeq) {

    def \\~(name: String, `class`: String): NodeSeq = (nodeSeq \\ name).filter(_.hasClass(`class`))
    def \~(name: String, `class`: String): NodeSeq = (nodeSeq \ name).filter(_.hasClass(`class`))
  }
}
