/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

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
public class SelectableListPanel extends UnorderedListPanel {

	private String i_selectedStyleName = "selected";
	private Widget i_selectedWidget;

	public void select(Widget widget) {
		Widget selectedWidget = getSelectedWidget();
		if (!getChildren().contains(widget) || widget.equals(selectedWidget) ) {
			return;
		}
		if (selectedWidget != null) {
			DOM.getParent(selectedWidget.getElement()).setClassName("");
		}
		DOM.getParent(widget.getElement()).setClassName(getSelectedStyleName());
		setSelectedWidget(widget);
	}

	public Widget getSelectedWidget() {
		return i_selectedWidget;
	}

	protected void setSelectedWidget(Widget selectedWidget) {
		i_selectedWidget = selectedWidget;
	}

	public String getSelectedStyleName() {
		return i_selectedStyleName;
	}

	public void setSelectedStyleName(String selectedStyleName) {
		i_selectedStyleName = selectedStyleName;
	}
}
