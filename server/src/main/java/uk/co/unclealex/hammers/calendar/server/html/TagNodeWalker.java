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

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

/**
 * A class to walk {@link TagNode}s. Basically, this class wraps a {@link TagNodeVisitor} but only visits
 * {@link TagNode}s and always walks the entire tree.
 * @author alex
 *
 */
public abstract class TagNodeWalker {

	/**
	 * Walk an entire {@link TagNode} tree.
	 * @param tagNode The {@link TagNode} to walk.
	 */
	public TagNodeWalker(TagNode tagNode) {
		doWalk(tagNode);
	}

	/**
	 * Walk an entire {@link TagNode} tree.
	 * @param tagNode The {@link TagNode} to walk.
	 */
	protected void doWalk(TagNode tagNode) {
		TagNodeVisitor visitor = new TagNodeVisitor() {
			@Override
			public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
				if (htmlNode instanceof TagNode) {
					execute((TagNode) htmlNode);
				}
				return true;
			}
		};
		tagNode.traverse(visitor);
	}
	
	/**
	 * Do something in response to finding a {@link TagNode}.
	 * @param tagNode The current {@link TagNode}.
	 */
	public abstract void execute(TagNode tagNode);
}
