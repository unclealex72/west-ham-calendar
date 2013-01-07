/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.ChangePasswordPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.CanWaitSupport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
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
public class ChangePassword extends SimplePanel implements Display {

  @UiTemplate("ChangePassword.ui.xml")
	public interface Binder extends UiBinder<Widget, ChangePassword> {
    // No extra methods
	}
  
	private static final Binder binder = GWT.create(Binder.class);

	private final CanWaitSupport canWaitSupport;
	
  public interface Style extends CssResource {
    String colourPicker();
    String alreadyUsed();
  }

  @UiField PopupPanel popupPanel;
  @UiField TextBox newPassword1;
  @UiField TextBox newPassword2;
  @UiField Label passwordsDontMatch;
  @UiField Button cancel;
  @UiField Button change;
  
  @Inject
  public ChangePassword(CanWaitSupport canWaitSupport) {
    add(binder.createAndBindUi(this));
    this.canWaitSupport = canWaitSupport;
    canWaitSupport.wrap(newPassword1, newPassword2, change, cancel);
  }

  @Override
  public void startWaiting() {
    getCanWaitSupport().startWaiting();
  }
  
  @Override
  public void stopWaiting() {
    getCanWaitSupport().stopWaiting();
  }
  
  @Override
  public PopupPanel getPopupPanel() {
    return popupPanel;
  }

  @Override
  public TextBox getNewPassword1() {
    return newPassword1;
  }

  @Override
  public TextBox getNewPassword2() {
    return newPassword2;
  }

  @Override
  public Label getPasswordsDontMatch() {
    return passwordsDontMatch;
  }

  @Override
  public Button getCancel() {
    return cancel;
  }

  @Override
  public Button getChange() {
    return change;
  }

  public CanWaitSupport getCanWaitSupport() {
    return canWaitSupport;
  }
}