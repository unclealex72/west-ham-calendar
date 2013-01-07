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

package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

/**
 * @author alex
 *
 */
public class NoGamesPresenter extends AbstractActivity {

	public static interface Display extends IsWidget {

		HasClickHandlers getRunJobButton();
		HasClickHandlers getCreateCalendarsButton();
		
	}
	
	private final Display display;
	private final AsyncCallbackExecutor asyncCallbackExecutor;
	
	@Inject
	public NoGamesPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor) {
		super();
		this.display = display;
		this.asyncCallbackExecutor = asyncCallbackExecutor;
	}


	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		panel.setWidget(getDisplay());
		getDisplay().getRunJobButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						// Do nothing
					}
					@Override
					public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
							UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
							AsyncCallback<Void> callback) {
						adminAttendanceService.updateCalendars(callback);
					}
				};
				getAsyncCallbackExecutor().execute(callback);
        Window.alert("Calendars will now be updated. Please wait a few minutes and refresh your browser.");
			}
		});
		getDisplay().getCreateCalendarsButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						// Do nothing
					}
					@Override
					public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
							UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
							AsyncCallback<Void> callback) {
						adminAttendanceService.createCalendars(callback);
					}
				};
				getAsyncCallbackExecutor().execute(callback);
        Window.alert("Calendars will now be created.");
			}
		});

	}

	/**
	 * @return the display
	 */
	public Display getDisplay() {
		return display;
	}


	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return asyncCallbackExecutor;
	}
}
