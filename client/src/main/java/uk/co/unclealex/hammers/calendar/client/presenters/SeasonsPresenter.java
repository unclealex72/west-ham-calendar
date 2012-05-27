/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import java.util.Arrays;
import java.util.Comparator;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.places.SeasonAwarePlace;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.ui.ShapeableAnchor;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
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
public class SeasonsPresenter {

	public interface Display extends IsWidget {

		void clear();
		HasWidgets getListPanel();
		
	}

	private boolean i_initialised;
	private final Display i_display;
	private final PlaceController i_placeController;
	private final HammersMessages i_hammersMessages;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	
	@Inject
	public SeasonsPresenter(Display display, PlaceController placeController,
			HammersMessages hammersMessages,
			AsyncCallbackExecutor asyncCallbackExecutor) {
		super();
		i_display = display;
		i_placeController = placeController;
		i_hammersMessages = hammersMessages;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		bind();
	}

	protected void bind() {
		// No binding required.
	}

	public void go() {
		if (isInitialised()) {
			return;
		}
		setInitialised(true);
		final Display display = getDisplay();
		display.clear();
		final Comparator<Integer> reverseIntegerComparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		};
		ExecutableAsyncCallback<Integer[]> allSeasonsCallback = new FailureAsPopupExecutableAsyncCallback<Integer[]>() {
			@Override
			public void onSuccess(Integer[] seasons) {
				Arrays.sort(seasons, reverseIntegerComparator);
				for (final Integer season : seasons) {
					HasWidgets listPanel = display.getListPanel();
					Anchor anchor = new ShapeableAnchor(getHammersMessages().season(season, season + 1));
					ClickHandler handler = new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							PlaceController placeController = getPlaceController();
							Place where = placeController.getWhere();
							if (where instanceof SeasonAwarePlace) {
								Place newPlace = ((SeasonAwarePlace) where).withSeason(season);
								placeController.goTo(newPlace);
							}
						}
					};
					anchor.addClickHandler(handler);
					listPanel.add(anchor);
				}
			}
			/* (non-Javadoc)
			 * @see uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback#execute(uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync, uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync, uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync, com.google.gwt.user.client.rpc.AsyncCallback)
			 */
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Integer[]> callback) {
				anonymousAttendanceService.getAllSeasons(callback);
			}
		};
		getAsyncCallbackExecutor().execute(allSeasonsCallback);
	}

	public Display getDisplay() {
		return i_display;
	}

	public PlaceController getPlaceController() {
		return i_placeController;
	}

	public HammersMessages getHammersMessages() {
		return i_hammersMessages;
	}

	protected boolean isInitialised() {
		return i_initialised;
	}

	protected void setInitialised(boolean initialised) {
		i_initialised = initialised;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}
}
