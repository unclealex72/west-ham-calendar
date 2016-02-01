/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package html

import org.htmlcleaner.{HtmlNode, TagNode, TagNodeVisitor}

import scala.collection.mutable

/**
 * A class to walk and filter {@link TagNode}s. Basically, class wraps a {@link TagNodeVisitor} but only visits
 * {@link TagNode}s and always walks the entire tree.
  *
  * @author alex
 *
 */
class TagNodeFilter(
  /**
   * The predicate used to decide whether a tag node should be included or not.
   */
  p: TagNode => Boolean) {

  /**
   * Walk an entire {@link TagNode} tree.
    *
    * @param tagNode The {@link TagNode} to walk.
   * @return A list of all child {@link TagNodes} that adhere to i_{@link Predicate}.
   */
  def list(tagNode: TagNode): List[TagNode] = {
    val tagNodes: mutable.Buffer[TagNode] = mutable.Buffer()
    val visitor = new TagNodeVisitor() {
      def visit(parentNode: TagNode, htmlNode: HtmlNode) = {
        htmlNode match {
          case tagNode: TagNode =>
            if (p(tagNode)) {
              tagNodes += tagNode
            }
          case _ =>
        }
        true
      }
    }
    tagNode.traverse(visitor)
    tagNodes.toList
  }
}
