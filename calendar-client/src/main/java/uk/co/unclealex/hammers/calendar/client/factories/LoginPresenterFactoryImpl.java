/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.factories;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.LoginPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.LoginPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationManager;
import uk.co.unclealex.hammers.calendar.client.util.ClickHelper;

import com.google.inject.Provider;

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
public class LoginPresenterFactoryImpl implements LoginPresenterFactory {

	private final Provider<Display> i_displayProvider;
	private final Provider<AnonymousAttendanceServiceAsync> i_anonymousAttendanceServiceProvider;
	private final Provider<AuthenticationManager> i_authenticationManagerProvider;
	private final Provider<ClickHelper> i_clickHelperProvider;
	@Inject
	public LoginPresenterFactoryImpl(Provider<Display> displayProvider,
			Provider<AnonymousAttendanceServiceAsync> anonymousAttendanceServiceProvider,
			Provider<AuthenticationManager> authenticationManagerProvider, Provider<ClickHelper> clickHelperProvider) {
		super();
		i_displayProvider = displayProvider;
		i_anonymousAttendanceServiceProvider = anonymousAttendanceServiceProvider;
		i_authenticationManagerProvider = authenticationManagerProvider;
		i_clickHelperProvider = clickHelperProvider;
	}


	@Override
	public LoginPresenter createLoginPresenter(Runnable originalAction) {
		return new LoginPresenter(
				getDisplayProvider().get(), 
				getAnonymousAttendanceServiceProvider().get(), 
				getAuthenticationManagerProvider().get(), 
				getClickHelperProvider().get(),
				originalAction);
	}


	public Provider<Display> getDisplayProvider() {
		return i_displayProvider;
	}


	public Provider<AnonymousAttendanceServiceAsync> getAnonymousAttendanceServiceProvider() {
		return i_anonymousAttendanceServiceProvider;
	}


	public Provider<AuthenticationManager> getAuthenticationManagerProvider() {
		return i_authenticationManagerProvider;
	}


  public Provider<ClickHelper> getClickHelperProvider() {
    return i_clickHelperProvider;
  }

}
