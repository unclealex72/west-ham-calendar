/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.places.GamesPlace;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class MainPresenter extends AbstractActivity {

	private final PlaceController i_placeController;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	
	@Inject
	public MainPresenter(
			PlaceController placeController, AsyncCallbackExecutor asyncCallbackExecutor) {
		super();
		i_placeController = placeController;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		ExecutableAsyncCallback<Integer> callback = new FailureAsPopupExecutableAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer latestSeason) {
				getPlaceController().goTo(new GamesPlace(latestSeason));
			}
			@Override
			public void execute(
					AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Integer> callback) {
				anonymousAttendanceService.getLatestSeason(callback);				
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}


	protected PlaceController getPlaceController() {
		return i_placeController;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}
}
