/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */

package uk.co.unclealex.hammers.calendar.server.html;

import java.util.List;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * A class to walk and filter {@link TagNode}s. Basically, this class wraps a {@link TagNodeVisitor} but only visits
 * {@link TagNode}s and always walks the entire tree.
 * @author alex
 *
 */
public abstract class TagNodeFilter implements Predicate<TagNode> {

	/**
	 * Walk an entire {@link TagNode} tree.
	 * @param tagNode The {@link TagNode} to list.
	 */
	public List<TagNode> list(TagNode tagNode) {
		final List<TagNode> tagNodes = Lists.newArrayList();
		TagNodeVisitor visitor = new TagNodeVisitor() {
			@Override
			public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
				if (htmlNode instanceof TagNode) {
					TagNode tagNode = (TagNode) htmlNode;
					if (apply(tagNode)) {
						tagNodes.add(tagNode);
					}
				}
				return true;
			}
		};
		tagNode.traverse(visitor);
		return tagNodes;
	}
}
