/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import uk.co.unclealex.hammers.calendar.client.factories.TableRowFactory;
import uk.co.unclealex.hammers.calendar.client.presenters.AbstractTablePresenter;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;

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
public abstract class Table<M, V extends TableRow, F extends TableRowFactory<M, V>> extends Composite implements AbstractTablePresenter.Display<M, V> {

	private boolean nextRowOdd = true;
	private final F tableRowFactory;
	
	public Table(F tableRowFactory) {
		this.tableRowFactory = tableRowFactory;
		bind();
		table.setStylePrimaryName(getTableStyleName());
	}

	@UiField FlexTable table;
	@UiField HasText title;
	
	public void setTitleText(String titleText) {
		title.setText(titleText);
	}

	@Override
	public void clear() {
		while (table.getRowCount() > 0) {
		   table.removeRow(0);
		}
		TableRow headerTableRow = getTableRowFactory().createTableRow(null);
		headerTableRow.addToTable(table);
		table.getRowFormatter().addStyleName(0, "header");
	}

	@Override
	public void addSubHeader(String subHeader) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(subHeader));
		table.getRowFormatter().addStyleName(row, "subheader");
		table.getFlexCellFormatter().setColSpan(row, 0, table.getCellCount(0));
	}

	@Override
	public V addRow(M model) {
		boolean nextRowOdd = isNextRowOdd();
		V tableRow = getTableRowFactory().createTableRow(model);
		int row = table.getRowCount();
		tableRow.addToTable(table);
		table.getRowFormatter().addStyleName(row, nextRowOdd?"odd":"even");
		setNextRowOdd(!nextRowOdd);
		return tableRow;
	}

	protected abstract void bind();
	
	protected abstract String getTableStyleName();
	
	public boolean isNextRowOdd() {
		return nextRowOdd;
	}

	public void setNextRowOdd(boolean nextRowIsOdd) {
		nextRowOdd = nextRowIsOdd;
	}

	public F getTableRowFactory() {
		return tableRowFactory;
	}
	
}
