/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.LoginPresenter.Display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
public class Login extends SimplePanel implements Display {

	@UiTemplate("Login.ui.xml")
	public interface Binder extends UiBinder<Widget, Login> {
    // No extra method
  }
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField PopupPanel popupPanel;
	@UiField Label failureLabel;
	@UiField HasText password;
	@UiField HasText username;
	@UiField Button login;
	@UiField Button cancel;
	
	@Inject
	public Login() {
		add(binder.createAndBindUi(this));
	}

	public HasText getPassword() {
		return password;
	}

	public HasText getUsername() {
		return username;
	}

	public Button getLogin() {
		return login;
	}

	public Button getCancel() {
		return cancel;
	}

	public PopupPanel getPopupPanel() {
		return popupPanel;
	}

	public Label getFailureLabel() {
		return failureLabel;
	}


}
