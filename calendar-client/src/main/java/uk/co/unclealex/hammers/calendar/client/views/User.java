/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.UserPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.CanWaitSupport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
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
public class User extends SimplePanel implements Display {

  @UiTemplate("User.ui.xml")
	public interface Binder extends UiBinder<Widget, User> {
    // No extra method
  }
	
	private static final Binder binder = GWT.create(Binder.class);

	private final CanWaitSupport canWaitSupport;
	
	@UiField DialogBox popupPanel;
	@UiField TextBox username;
	@UiField TextBox password;
	@UiField ListBox roles;
	@UiField Button remove;
	@UiField Button cancel;
	@UiField Button update;
	
	@Inject
	public User(CanWaitSupport canWaitSupport) {
		add(binder.createAndBindUi(this));
		this.canWaitSupport = canWaitSupport;
	}

	@Override
	public void startWaiting() {
	  getCanWaitSupport().startWaiting();
	}

  @Override
  public void stopWaiting() {
    getCanWaitSupport().stopWaiting();
  }

	public static Binder getBinder() {
    return binder;
  }

  public DialogBox getPopupPanel() {
    return popupPanel;
  }

  public TextBox getUsername() {
    return username;
  }

  public TextBox getPassword() {
    return password;
  }

  public ListBox getRoles() {
    return roles;
  }

  public Button getCancel() {
    return cancel;
  }

  public Button getUpdate() {
    return update;
  }

  public CanWaitSupport getCanWaitSupport() {
    return canWaitSupport;
  }

  public Button getRemove() {
    return remove;
  }	
}
