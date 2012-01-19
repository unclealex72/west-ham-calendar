/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.ChangePasswordPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationManager;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

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
public class ChangePasswordPresenter extends AbstractPopupPresenter<PopupPanel, Display> {

	public static interface Display extends AbstractPopupPresenter.Display<PopupPanel>, CanWait {

    Button getChange();
    Button getCancel();
    TextBox getNewPassword2();
    TextBox getNewPassword1();
    HasText getPasswordsDontMatch();
	}

	private final HammersMessages i_messages;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final Display i_display;
	
	@Inject
	public ChangePasswordPresenter(
	    Display display, AuthenticationManager authenticationManager,
	    AsyncCallbackExecutor asyncCallbackExecutor, HammersMessages messages) {
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_messages = messages;
		bind();
	}

	protected void bind() {
	  final Display display = getDisplay();
    final TextBox newPassword1 = display.getNewPassword1();
    TextBox newPassword2 = display.getNewPassword2();
    KeyUpHandler textsChangedHandler = new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        onTextsChanged();
      }
    };
    for (TextBox newPassword : new TextBox[] { newPassword1, newPassword2 }) {
      newPassword.addKeyUpHandler(textsChangedHandler);
      newPassword.setText("");
    }
    ClickHandler cancelHandler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    };
    display.getCancel().addClickHandler(cancelHandler);
    final ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        hide();
      }
      @Override
      public void onFailure(Throwable cause) {
        hide();
        super.onFailure(cause);
      }
      @Override
      public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
          UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
          AsyncCallback<Void> callback) {
        userAttendanceService.changePassword(newPassword1.getText(), callback);
      }
    };
    ClickHandler changePasswordHandler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        getAsyncCallbackExecutor().executeAndWait(callback, display);
      }
    };
    display.getChange().addClickHandler(changePasswordHandler);
	}

	@Override
	protected void prepare(final Display display) {
    for (TextBox newPassword : new TextBox[] { display.getNewPassword1(), display.getNewPassword2() }) {
      newPassword.setText("");
    }
    onTextsChanged();
  }
	
  protected void onTextsChanged() {
    final Display display = getDisplay();
    final TextBox newPassword1 = display.getNewPassword1();
    final TextBox newPassword2 = display.getNewPassword2();
    String text1 = newPassword1.getText();
    String text2 = newPassword2.getText();
    boolean textsEqual = text1.equals(text2);
    boolean text1Empty = text1.isEmpty();
    String passwordMessage;
    HammersMessages messages = getMessages();
    if (text1Empty) {
      passwordMessage = messages.passwordCannotBeEmpty();
    }
    else if (textsEqual) {
      passwordMessage = messages.passwordsMatch();
    }
    else {
      passwordMessage = messages.passwordsDontMatch();
    }
    display.getPasswordsDontMatch().setText(passwordMessage);
    display.getChange().setEnabled(textsEqual && !text1Empty);
  }
  
	public Display getDisplay() {
		return i_display;
	}

  public AsyncCallbackExecutor getAsyncCallbackExecutor() {
    return i_asyncCallbackExecutor;
  }

  public HammersMessages getMessages() {
    return i_messages;
  }

}
