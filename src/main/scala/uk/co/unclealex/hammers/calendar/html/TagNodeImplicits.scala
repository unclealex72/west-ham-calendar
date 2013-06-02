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

package uk.co.unclealex.hammers.calendar.html

import org.htmlcleaner.CommentNode
import org.htmlcleaner.ContentNode
import org.htmlcleaner.TagNode
import scala.collection.JavaConversions._

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Implicit methods for getting text from {@link TagNode}s.
 *
 * @author alex
 *
 */
object TagNodeImplicits {

  implicit class Implicits(tagNode: TagNode) {

    /**
     * Get the text of a {@link TagNode} but not of it's children.
     *
     * @param tagNode
     *          The {@link TagNode} of whose text to get.
     * @return The text of the {@link TagNode}.
     */
    def text: String = {
      val textExtractor: PartialFunction[Any, String] = { obj =>
        obj match {
          case cn: ContentNode => cn.getContent.toString
          case cn: CommentNode => cn.getContent.toString
        }
      }
      val texts = tagNode.getChildren() collect textExtractor
      texts.mkString(" ").replace('\u00a0', ' ').replace("&nbsp;", " ")
    }

    /**
     * Normalise all whitespace (including non-breaking) and trim.
     *
     * @param tagNode
     *          The node whose text should be normalised.
     * @return The normalised text of the element.
     */
    def normalisedText: String =
      Strings.nullToEmpty(text).replace("\\s+", " ").trim()

    /**
     * Normalise all whitespace (including non-breaking) and trim.
     *
     * @param tagNode
     *          The node whose text should be normalised.
     * @return The normalised text of the element or None if the text was empty.
     */
    def optionalNormalisedText: Option[String] = Option(Strings.emptyToNull(normalisedText))
  }

}
