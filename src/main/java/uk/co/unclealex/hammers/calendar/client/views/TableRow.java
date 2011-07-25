/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
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
public abstract class TableRow extends Composite {

	@UiField HasWidgets container;
	
	void addToTable(FlexTable table) {
		List<Widget> widgets = new ArrayList<Widget>();
		for (Iterator<Widget> iter = container.iterator(); iter.hasNext(); ) {
			widgets.add(iter.next());
		}
		int row = table.getRowCount();
		for (ListIterator<Widget> iter = widgets.listIterator(); iter.hasNext(); ) {
			int column = iter.nextIndex();
			Widget widget = iter.next();
			widget.removeFromParent();
			table.setWidget(row, column, widget);
		}
		String stylePrimaryName = getStylePrimaryName();
		if (stylePrimaryName != null && !stylePrimaryName.isEmpty()) {
			table.getRowFormatter().setStylePrimaryName(row, stylePrimaryName);
		}
	}

}
