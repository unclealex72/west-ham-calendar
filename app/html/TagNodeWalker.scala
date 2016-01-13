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

package html;

import org.htmlcleaner.HtmlNode
import org.htmlcleaner.TagNode
import org.htmlcleaner.TagNodeVisitor;
import scala.collection.mutable.Buffer

/**
 * A class to walk {@link TagNode}s. Basically, class wraps a {@link TagNodeVisitor} but only visits
 * {@link TagNode}s and always walks the entire tree.
 * @author alex
 *
 */
object TagNodeWalker {

  /**
   * Walk a tag node.
   * @param f A function that transforms a tag node into a list of results.
   * @param tagNode the node to walk.
   */
  def walk[E](f: TagNode => Traversable[E])(tagNode: TagNode): List[E] = {
    val buffer = Buffer.empty[E]
    val visitor = new TagNodeVisitor() {
      def visit(parentNode: TagNode, htmlNode: HtmlNode) = {
        if (htmlNode.isInstanceOf[TagNode]) {
          buffer ++= f(htmlNode.asInstanceOf[TagNode])
        }
        true
      }
    }
    tagNode.traverse(visitor)
    buffer.toList
  }
}
