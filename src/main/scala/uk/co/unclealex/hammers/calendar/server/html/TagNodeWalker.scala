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

package uk.co.unclealex.hammers.calendar.server.html;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

/**
 * A class to walk {@link TagNode}s. Basically, class wraps a {@link TagNodeVisitor} but only visits
 * {@link TagNode}s and always walks the entire tree.
 * @author alex
 *
 */
abstract class TagNodeWalker(
  /**
   * The tag node to walk.
   */
  tagNode: TagNode) {

  val visitor = new TagNodeVisitor() {
    def visit(parentNode: TagNode, htmlNode: HtmlNode) = {
      if (htmlNode.isInstanceOf[TagNode]) {
        execute(htmlNode.asInstanceOf[TagNode])
      }
      true
    }
  }
  tagNode.traverse(visitor)

  /**
   * Do something in response to finding a {@link TagNode}.
   * @param tagNode The current {@link TagNode}.
   */
  def execute(tagNode: TagNode)
}
