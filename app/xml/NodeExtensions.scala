package xml

import scala.xml.Node

/**
  * Created by alex on 05/03/16.
  */
trait NodeExtensions {

  implicit class NodeHasAttribute(node: Node) {
    def hasClass(value: String) = hasAttr("class", value)

    def hasAttr(name: String, value: String): Boolean = node.attributes.asAttrMap.get(name).contains(value)

    def hasId(id: String): Boolean = hasAttr("id", id)

    def trimmed: String = node.text.trim
  }
}
