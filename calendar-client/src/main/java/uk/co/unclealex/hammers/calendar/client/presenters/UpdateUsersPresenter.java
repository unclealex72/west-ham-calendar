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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.factories.UserPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.model.User;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * @author aj016368
 *
 */
public class UpdateUsersPresenter implements RefreshablePresenter, CloseHandler<PopupPanel> {

  public static interface Display extends IsWidget {
    HasWidgets getUsersPanel();
    HasClickHandlers getAddNewUserButton();
  }
  
  private final Display i_display;
  private final AsyncCallbackExecutor i_asyncCallbackExecutor;
  private final UserPresenterFactory i_updatePresenterFactory;
  private HandlerRegistration i_newUserHandlerRegistration;
  
  @Inject
  public UpdateUsersPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor,
      UserPresenterFactory updatePresenterFactory) {
    super();
    i_display = display;
    i_asyncCallbackExecutor = asyncCallbackExecutor;
    i_updatePresenterFactory = updatePresenterFactory;
  }
  
  public void show(HasWidgets container) {
    Display display = getDisplay();
    container.add(display.asWidget());
    refresh();
  }

  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    refresh();
  }
  
  @Override
  public void refresh() {
    Display display = getDisplay();
    display.getUsersPanel().clear();
    HandlerRegistration newUserHandlerRegistration = getNewUserHandlerRegistration();
    if (newUserHandlerRegistration != null) {
      newUserHandlerRegistration.removeHandler();
    }
    ExecutableAsyncCallback<User[]> callback = new FailureAsPopupExecutableAsyncCallback<User[]>() {
      @Override
      public void onSuccess(User[] users) {
        createList(users);
      }
      @Override
      public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
          UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
          AsyncCallback<User[]> callback) {
        adminAttendanceService.getAllUsers(callback);
      }
    };
    getAsyncCallbackExecutor().execute(callback);
  }
  
  protected void createList(User[] users) {
    Display display = getDisplay();
    HasWidgets usersPanel = display.getUsersPanel();
    HasClickHandlers addNewUserButton = display.getAddNewUserButton();
    final List<String> usernames = new ArrayList<String>(users.length);
    for (User user : users) {
      usernames.add(user.getUsername());
      if (!user.isLoggedIn()) {
        Anchor anchor = new Anchor(true);
        anchor.setText(user.getUsername());
        anchor.addClickHandler(createUserHandler(usernames, user));
        usersPanel.add(anchor);
      }
    }
    HandlerRegistration handlerRegistration = addNewUserButton.addClickHandler(createUserHandler(usernames, null));
    setNewUserHandlerRegistration(handlerRegistration);
  }

  protected ClickHandler createUserHandler(final List<String> allUsernames, final User user) {
    return new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        UserPresenter userPresenter = getUpdatePresenterFactory().createUserPresenter(user, allUsernames);
        userPresenter.getDisplay().getPopupPanel().addCloseHandler(UpdateUsersPresenter.this);
        userPresenter.showRelativeTo((UIObject) event.getSource());
      }
    };
  }

  public UserPresenterFactory getUpdatePresenterFactory() {
    return i_updatePresenterFactory;
  }

  public Display getDisplay() {
    return i_display;
  }

  public AsyncCallbackExecutor getAsyncCallbackExecutor() {
    return i_asyncCallbackExecutor;
  }

  public HandlerRegistration getNewUserHandlerRegistration() {
    return i_newUserHandlerRegistration;
  }

  public void setNewUserHandlerRegistration(HandlerRegistration newUserHandlerRegistration) {
    i_newUserHandlerRegistration = newUserHandlerRegistration;
  }
}
