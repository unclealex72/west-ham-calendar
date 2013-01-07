/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.places.SeasonAwarePlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

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
public abstract class SeasonAwarePresenter extends AbstractActivity {

	private final PlaceController placeController;
	
	public SeasonAwarePresenter(PlaceController placeController) {
		super();
		this.placeController = placeController;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Place where = getPlaceController().getWhere();
		if (where instanceof SeasonAwarePlace) {
			start(panel, ((SeasonAwarePlace) where).getSeason());
		}
	}

	protected abstract void start(AcceptsOneWidget panel, int season);
	
	public PlaceController getPlaceController() {
		return placeController;
	}

}
