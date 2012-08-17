/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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

import org.htmlcleaner.CommentNode;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;


/**
 * Utilities for getting text from {@link TagNode}s.
 * 
 * @author alex
 * 
 */
public final class TagNodeUtils {

	/**
	 * Instantiates a new tag node utils.
	 */
	private TagNodeUtils() {
		// Empty constructor
	}

	/**
	 * Get the text of a {@link TagNode} but not of it's children.
	 * 
	 * @param tagNode
	 *          The {@link TagNode} of whose text to get.
	 * @return The text of the {@link TagNode}.
	 */
	public static String textOf(TagNode tagNode) {
		Function<Object, String> toStringFunction = new Function<Object, String>() {
			@Override
			public String apply(Object obj) {
				if (obj instanceof ContentNode) {
					return ((ContentNode) obj).getContent().toString();
				}
				else if (obj instanceof CommentNode) {
					return ((CommentNode) obj).getContent().toString();
				}
				else {
					return "";
				}
			}
		};
		Predicate<Object> isContentPredicate = Predicates.or(Predicates.instanceOf(ContentNode.class),
				Predicates.instanceOf(CommentNode.class));
		@SuppressWarnings("unchecked")
		Iterable<String> strs = Iterables.transform(Iterables.filter(tagNode.getChildren(), isContentPredicate),
				toStringFunction);
		return Joiner.on(' ').join(strs).replace('\u00a0', ' ').replace("&nbsp;", " ");

	}

	/**
	 * Normalise all whitespace (including non-breaking) and trim.
	 * 
	 * @param tagNode
	 *          The node whose text should be normalised.
	 * @return The normalised text of the element.
	 */
	public static String normaliseText(TagNode tagNode) {
		return Strings.nullToEmpty(textOf(tagNode)).replace("\\s+", " ").trim();
	}

	/**
	 * Normalise all whitespace (including non-breaking) and trim.
	 * 
	 * @param tagNode
	 *          The node whose text should be normalised.
	 * @return The normalised text of the node or null if the normalised text is
	 *         empty.
	 */
	public static String normaliseTextToNull(TagNode tagNode) {
		return Strings.emptyToNull(normaliseText(tagNode));
	}

}
