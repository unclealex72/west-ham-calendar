/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.security;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.factories.GoogleAuthenticationPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
public class AuthenticationManagerImpl implements AuthenticationManager {

	private final Set<AuthenticationEventListener> i_authenticationEventListeners = new HashSet<AuthenticationEventListener>();
	
	@Inject
	public AuthenticationManagerImpl(
			final AnonymousAttendanceServiceAsync anonymousAttendanceService,
			final AsyncCallbackExecutor asyncCallbackExecutor,
			final GoogleAuthenticationPresenterFactory googleAuthenticationPresenterFactory) {
		ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// Do nothing
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Void> callback) {
				anonymousAttendanceService.ensureDefaultsExist(callback);
			}
		};
		asyncCallbackExecutor.execute(callback);
		class AsyncCallbackTimer extends Timer implements AsyncCallback<String> {
			String currentUsername = "";
			@Override
			public void run() {
				anonymousAttendanceService.getUserPrincipal(this);
			}
			@Override
			public void onSuccess(String username) {
				if ((currentUsername == null && username != null) || (currentUsername != null && !currentUsername.equals(username))) {
					authenticated(username);
					currentUsername = username;
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				// Do nothing
			}
		}
		Timer timer = new AsyncCallbackTimer();
		timer.run();
		timer.scheduleRepeating(60 * 1000);
	}
	
	@Override
	public void addAuthenticationEventListener(AuthenticationEventListener authenticationEventListener) {
		getAuthenticationEventListeners().add(authenticationEventListener);
	}

	@Override
	public void removeAuthenticationEventListener(AuthenticationEventListener authenticationEventListener) {
		getAuthenticationEventListeners().remove(authenticationEventListener);
	}

	@Override
	public void authenticated(String username) {
		AuthenticationEvent event = new AuthenticationEvent(username);
		for (AuthenticationEventListener authenticationEventListener : getAuthenticationEventListeners()) {
			authenticationEventListener.onAuthenticationChanged(event);
		}
	}

	@Override
	public void unauthenticated() {
		authenticated(null);
	}
	
	public Set<AuthenticationEventListener> getAuthenticationEventListeners() {
		return i_authenticationEventListeners;
	}
}
