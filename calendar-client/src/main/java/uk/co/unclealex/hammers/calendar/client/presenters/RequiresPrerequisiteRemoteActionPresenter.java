/**
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
import com.google.gwt.event.shared.EventBus;
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
public abstract class RequiresPrerequisiteRemoteActionPresenter extends AbstractActivity {

	private final AsyncCallbackExecutor asyncCallbackExecutor;
	
	public RequiresPrerequisiteRemoteActionPresenter(AsyncCallbackExecutor asyncCallbackExecutor) {
		super();
		this.asyncCallbackExecutor = asyncCallbackExecutor;
	}


	@Override
	public final void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				afterActionPerformed(panel, eventBus);
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Void> callback) {
				performPrerequisiteAction(anonymousAttendanceService, userAttendanceService, adminAttendanceService, callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	protected abstract void afterActionPerformed(AcceptsOneWidget panel, EventBus eventBus);


	protected abstract void performPrerequisiteAction(AnonymousAttendanceServiceAsync anonymousAttendanceService,
			UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
			AsyncCallback<Void> callback);

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return asyncCallbackExecutor;
	}

}
