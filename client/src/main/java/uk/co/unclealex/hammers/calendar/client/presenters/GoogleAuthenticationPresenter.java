/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.GoogleAuthenticationPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.assistedinject.Assisted;

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
public class GoogleAuthenticationPresenter extends AbstractPopupPresenter<PopupPanel, Display> {

	public static interface Display extends AbstractPopupPresenter.Display<PopupPanel>, CanWait {
		
		HasText getSuccessCode();
		HasClickHandlers getSubmitButton();
    Anchor getAuthenticationAnchor();
	}
	
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final String i_authenticationUrl;
	private final Runnable i_originalAction;
	
	@Inject
	public GoogleAuthenticationPresenter(
	    Display display, AsyncCallbackExecutor asyncCallbackExecutor, 
	    @Assisted String authenticationUrl, @Assisted Runnable originalAction) {
		super();
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_authenticationUrl = authenticationUrl;
		i_originalAction = originalAction;
	}

	@Override
	protected void prepare(final Display display) {
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ExecutableAsyncCallback<Void> successTokenCallback = new FailureAsPopupExecutableAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						hide();
						getOriginalAction().run();
					}
					@Override
					public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
							UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
							AsyncCallback<Void> callback) {
						adminAttendanceService.authenticate(display.getSuccessCode().getText(), callback);
					}
				};
				getAsyncCallbackExecutor().executeAndWait(successTokenCallback, getDisplay());
			}
		};
		display.getSubmitButton().addClickHandler(handler);
		String authenticationUrl = getAuthenticationUrl();
    display.getAuthenticationAnchor().setHref(authenticationUrl);
		Window.open(authenticationUrl, "google-oauth", "width=800,height=600");
	}

	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public String getAuthenticationUrl() {
		return i_authenticationUrl;
	}

  public Runnable getOriginalAction() {
    return i_originalAction;
  }
}
