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
package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException
import java.net.URI
import org.htmlcleaner.TagNode
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.Set
import scala.collection.JavaConverters._
import java.lang.{ Iterable => JIterable }
/**
 * An {@link ElementLinkHarvester} is a {@link LinkHarvester} that searches a page for HTML elements with
 * a given name and then processes it's inner text.
 * @author alex
 *
 */
abstract class ElementLinkHarvester(
  /**
   * The name of the element to look for.
   */
  elementName: String) extends LinkHarvester {

  @Override
  override def harvestLinks(pageUri: URI, tagNode: TagNode): JIterable[URI] = {
    val links: Set[URI] = LinkedHashSet()
    new TagNodeWalker(tagNode) {
      def execute(tagNode: TagNode) = {
        if (elementName.equals(tagNode.getName())) {
          val linkUri = checkForLink(pageUri, tagNode)
          links ++= linkUri
        }
      }
    }
    links.asJava
  }

  /**
   * Once a suitable element has been found check to see if a link can be found.
   * @param uri The URI of the page being harvested.
   * @param tagNode The {@link TagNode} that had the correct element name.
   * @return A new child link, or null if none was found.
   */
  protected def checkForLink(uri: URI, tagNode: TagNode): Option[URI]

}
