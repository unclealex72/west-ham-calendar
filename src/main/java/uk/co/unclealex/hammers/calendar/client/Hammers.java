/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client;

import uk.co.unclealex.hammers.calendar.client.gin.HammersGinjector;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

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
public class Hammers implements EntryPoint {

	@Override
	public void onModuleLoad() {

		final HammersGinjector injector = GWT.create(HammersGinjector.class);

		RootPanel.get("seasons").add(injector.getSeasonsView());
		RootPanel.get("welcome").add(injector.getMainPanel());
		RootPanel.get("user").add(injector.getUserPanel());
		RootPanel.get("nav1").add(injector.getNavigationView());
		RootPanel.get().add(injector.getWaitingView());
		
		ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Goes to the place represented on URL else default place
				injector.getPlaceHistoryHandler().handleCurrentHistory();
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Void> callback) {
				anonymousAttendanceService.initialise(callback);
			}
		};
		injector.getAsyncCallbackExecutor().execute(callback);
	}

}
