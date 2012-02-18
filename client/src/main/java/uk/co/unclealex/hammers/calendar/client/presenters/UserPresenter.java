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

import java.util.Collection;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.UserPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.RoleListBoxAdaptor;
import uk.co.unclealex.hammers.calendar.shared.model.Role;
import uk.co.unclealex.hammers.calendar.shared.model.User;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.assistedinject.Assisted;

/**
 * @author aj016368
 *
 */
public class UserPresenter extends AbstractCallbackPopupPresenter<DialogBox, Display> implements CanWait {

  public static interface Display extends AbstractCallbackPopupPresenter.Display<DialogBox> {
    TextBox getUsername();
    TextBox getPassword();
    ListBox getRoles();
    Button getRemove();
    Button getUpdate();
    Button getCancel();
  }
  
  private final Display i_display;
  private final User i_user;
  private final Collection<String> i_existingUsernames;
  private final RoleListBoxAdaptor i_roleListBoxAdaptor;
  private final HammersMessages i_messages;
  
  @Inject
  public UserPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor, HammersMessages messages,
      @Assisted User user, @Assisted Collection<String> existingUsernames) {
    super(asyncCallbackExecutor);
    i_display = display;
    i_user = user;
    i_existingUsernames = existingUsernames;
    i_roleListBoxAdaptor = new RoleListBoxAdaptor(display.getRoles());
    i_messages = messages;
  }

  @Override
  protected void prepare(final Display display) {
    RoleListBoxAdaptor roleListBoxAdaptor = getRoleListBoxAdaptor();
    roleListBoxAdaptor.addValues(Role.values());
    User user = getUser();
    HammersMessages messages = getMessages();
    String caption = user==null?messages.newUser():messages.updateUser();
    display.getPopupPanel().getCaption().setText(caption);
    TextBox password = display.getPassword();
    TextBox username = display.getUsername();
    KeyUpHandler keyUpHandler = new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        updateButtons();
      }
    };
    password.addKeyUpHandler(keyUpHandler);
    if (user == null) {
      username.addKeyUpHandler(keyUpHandler);
    }
    else {
      username.setEnabled(false);
      username.setValue(user.getUsername());
      roleListBoxAdaptor.setValue(user.getRole());
    }
    addConfirmableAction(
      display.getCancel(), "cancel", 
      ConfirmationRequiredAlways.INSTANCE, new Runnable() { public void run() { hide(); }});
    Runnable updateUserRunnable = new AsyncRunnable<Void>(true) {
      @Override
      public void onSuccess(Void result) {
        // Do nothing
      }
      @Override
      public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
          UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
          AsyncCallback<Void> callback) {
        String username = getDisplay().getUsername().getText();
        String password = getDisplay().getPassword().getText();
        Role role = getRoleListBoxAdaptor().getValue();
        if (getUser() == null) {
          adminAttendanceService.addUser(username, password, role, callback);
        }
        else {
          adminAttendanceService.alterUser(username, password, role, callback);
        }
      }
    };
    addConfirmableAction(display.getUpdate(), "update", ConfirmationRequiredAlways.INSTANCE, updateUserRunnable);
    Button remove = display.getRemove();
    if (user == null) {
      remove.setEnabled(false);
    }
    else {
      Runnable removeRunnable = new AsyncRunnable<Void>(true) {
        @Override
        public void onSuccess(Void result) {
          // Do nothing.          
        }
        @Override
        public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
            UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
            AsyncCallback<Void> callback) {
          adminAttendanceService.removeUser(display.getUsername().getText(), callback);
        }
      };
      addConfirmableAction(remove, "remove this user", ConfirmationRequiredAlways.INSTANCE, removeRunnable);
    }
  }
  
  protected void updateButtons() {
    Display display = getDisplay();
    String username = display.getUsername().getText();
    boolean isEntryValid = 
      !display.getPassword().getText().isEmpty() &&
      (getUser() == null || (!username.isEmpty() && !getExistingUsernames().contains(username)));
    display.getUpdate().setEnabled(isEntryValid);
  }

  @Override
  public void startWaiting() {
    getDisplay().startWaiting();
  }
  
  @Override
  public void stopWaiting() {
    getDisplay().stopWaiting();
  }

  public Display getDisplay() {
    return i_display;
  }

  public User getUser() {
    return i_user;
  }

  public Collection<String> getExistingUsernames() {
    return i_existingUsernames;
  }

  public RoleListBoxAdaptor getRoleListBoxAdaptor() {
    return i_roleListBoxAdaptor;
  }

  public HammersMessages getMessages() {
    return i_messages;
  }
}
