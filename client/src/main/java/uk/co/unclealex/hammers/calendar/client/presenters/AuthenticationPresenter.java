/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationEvent;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationEventListener;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;

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
public class AuthenticationPresenter implements AuthenticationEventListener {

	public interface Display extends IsWidget {

		HasText getMessage();
    Anchor getChangePasswordLink();
	}

	private final HammersMessages i_hammersMessages;
	private final ChangePasswordPresenter i_changePasswordPresenter;
	private final Display i_display;
	
	@Inject
	public AuthenticationPresenter(
	    ChangePasswordPresenter changePasswordPresenter,
	    Display display, HammersMessages hammersMessages, AuthenticationManager authenticationManager) {
	  i_changePasswordPresenter = changePasswordPresenter;
		i_hammersMessages = hammersMessages;
		i_display = display;
    authenticationManager.addAuthenticationEventListener(this);
		bind();
	}

	protected void bind() {
	  ClickHandler showPopupHandler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        getChangePasswordPresenter().showRelativeTo((UIObject) event.getSource());
      }
    };
    getDisplay().getChangePasswordLink().addClickHandler(showPopupHandler);
  }
	
	@Override
	public void onAuthenticationChanged(AuthenticationEvent event) {
		String username = event.getUsername();
		HammersMessages hammersMessages = getHammersMessages();
		boolean loggedIn = username != null;
    String message = loggedIn?hammersMessages.loggedIn(username):hammersMessages.notLoggedIn();
		Display display = getDisplay();
    display.getMessage().setText(message);
		display.getChangePasswordLink().setVisible(loggedIn);
	}

	public HammersMessages getHammersMessages() {
		return i_hammersMessages;
	}

	public Display getDisplay() {
		return i_display;
	}

  public ChangePasswordPresenter getChangePasswordPresenter() {
    return i_changePasswordPresenter;
  }

}
