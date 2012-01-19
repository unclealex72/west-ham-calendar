/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;

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
public class ShapeableAnchor extends Anchor {

	public ShapeableAnchor() {
		this(true);
	}

	public ShapeableAnchor(boolean useDefaultHref) {
		super(useDefaultHref);
	}

	public ShapeableAnchor(SafeHtml html) {
		super(html);
	}

	public ShapeableAnchor(String text) {
		super(text);
	}

	public ShapeableAnchor(Element element) {
		super(element);
	}

	public ShapeableAnchor(SafeHtml html, Direction dir) {
		super(html, dir);
	}

	public ShapeableAnchor(SafeHtml html, DirectionEstimator directionEstimator) {
		super(html, directionEstimator);
	}

	public ShapeableAnchor(String text, Direction dir) {
		super(text, dir);
	}

	public ShapeableAnchor(String text, DirectionEstimator directionEstimator) {
		super(text, directionEstimator);
	}

	public ShapeableAnchor(String text, boolean asHtml) {
		super(text, asHtml);
	}

	public ShapeableAnchor(SafeHtml html, String href) {
		super(html, href);
	}

	public ShapeableAnchor(String text, String href) {
		super(text, href);
	}

	public ShapeableAnchor(SafeHtml html, Direction dir, String href) {
		super(html, dir, href);
	}

	public ShapeableAnchor(SafeHtml html, DirectionEstimator directionEstimator, String href) {
		super(html, directionEstimator, href);
	}

	public ShapeableAnchor(String text, Direction dir, String href) {
		super(text, dir, href);
	}

	public ShapeableAnchor(String text, DirectionEstimator directionEstimator, String href) {
		super(text, directionEstimator, href);
	}

	public ShapeableAnchor(String text, boolean asHTML, String href) {
		super(text, asHTML, href);
	}

	public ShapeableAnchor(SafeHtml html, String href, String target) {
		super(html, href, target);
	}

	public ShapeableAnchor(String text, String href, String target) {
		super(text, href, target);
	}

	public ShapeableAnchor(String text, boolean asHtml, String href, String target) {
		super(text, asHtml, href, target);
	}

	public void setShape(String shape) {
		getElement().setAttribute("shape", shape);
	}
}
