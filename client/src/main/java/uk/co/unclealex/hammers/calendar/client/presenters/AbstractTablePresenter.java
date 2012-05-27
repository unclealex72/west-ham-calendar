/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.presenters.AbstractTablePresenter.Display;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

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
public abstract class AbstractTablePresenter<M, V, D extends Display<M, V>> extends SeasonAwarePresenter {

	public static interface Display<M, V> extends IsWidget {

		void clear();
		void addSubHeader(String header);
		V addRow(M model);
		void setTitleText(String text);
	}
	
	private final D i_display;
	private final HammersMessages i_hammersMessages;
	
	public AbstractTablePresenter(PlaceController placeController, D display, HammersMessages hammersMessages) {
		super(placeController);
		i_display = display;
		i_hammersMessages = hammersMessages;
	}

	@Override
	protected final void start(AcceptsOneWidget panel, int season) {
		D display = getDisplay();
		display.setTitleText(getHammersMessages().season(season, season + 1));
		display.clear();
		start(display, season);
		panel.setWidget(display);
	}
	
	protected abstract void start(D display, int season);

	public D getDisplay() {
		return i_display;
	}

	public HammersMessages getHammersMessages() {
		return i_hammersMessages;
	}
}
