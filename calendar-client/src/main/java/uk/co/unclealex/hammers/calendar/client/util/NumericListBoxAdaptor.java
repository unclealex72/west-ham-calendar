/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.util;

import com.google.gwt.user.client.ui.ListBox;

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
public class NumericListBoxAdaptor extends ValueListBoxAdaptor<Integer> {

	private int maxDigitsSoFar = 0;
	
	/**
	 * @param listBox
	 */
	public NumericListBoxAdaptor(ListBox listBox) {
		super(listBox, null);
	}

	@Override
	protected Integer parse(String value) {
		return Integer.parseInt(value);
	}

	@Override
	protected String toString(Integer value) {
		return Integer.toString(value);
	}
	
	@Override
	protected String toDisplayableString(Integer value) {
		String text = Integer.toString(value);
		return pad(text);
	}
	
	@Override
	public void addValue(Integer value) {
		String text = toDisplayableString(value);
		int len = text.length();
		int maxDigitsSoFar = getMaxDigitsSoFar();
		ListBox listBox = getListBox();
		if (len > maxDigitsSoFar) {
			setMaxDigitsSoFar(len);
			for (int idx = 0; idx < listBox.getItemCount(); idx++) {
				listBox.setItemText(idx, pad(listBox.getItemText(idx)));
			}
		}
		super.addValue(value);
	}
	
	/**
	 * @param value
	 * @return
	 */
	protected String pad(String value) {
		int maxDigitsSoFar = getMaxDigitsSoFar();
		while (value.length() < maxDigitsSoFar) {
			value = "0" + value;
		}
		return value;
	}

	@Override
	public void clear() {
		super.clear();
		setMaxDigitsSoFar(0);
	}
	
	protected int getMaxDigitsSoFar() {
		return maxDigitsSoFar;
	}

	protected void setMaxDigitsSoFar(int maxDigitsSoFar) {
		this.maxDigitsSoFar = maxDigitsSoFar;
	}

	
}
