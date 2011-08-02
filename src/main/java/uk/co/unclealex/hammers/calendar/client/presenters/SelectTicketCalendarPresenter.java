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

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.util.CalendarTypeListBoxAdaptor;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author aj016368
 *
 */
public class SelectTicketCalendarPresenter {

  public static interface Display extends IsWidget, CanWait {
    ListBox getTicketCalendarsListBox();
  }
  
  private final Display i_display;
  private final AsyncCallbackExecutor i_asyncCallbackExecutor;
  private boolean i_initialised = false;
  
  @Inject
  public SelectTicketCalendarPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor) {
    super();
    i_display = display;
    i_asyncCallbackExecutor = asyncCallbackExecutor;
  }
  
  protected void prepare() {
    ExecutableAsyncCallback<CalendarType> callback = new FailureAsPopupExecutableAsyncCallback<CalendarType>() {
      @Override
      public void onSuccess(CalendarType calendarType) {
        prepare(calendarType);
      }
      @Override
      public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
          UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
          AsyncCallback<CalendarType> callback) {
        adminAttendanceService.getSelectedTicketingCalendar(callback);
      }
    };
    getAsyncCallbackExecutor().execute(callback);
  }
  
  protected void prepare(CalendarType selectedCalendarType) {
    ListBox ticketCalendarsListBox = getDisplay().getTicketCalendarsListBox();
    CalendarTypeListBoxAdaptor adaptor = 
        new CalendarTypeListBoxAdaptor(ticketCalendarsListBox, "Do not show ticket dates");
    adaptor.addValue(null);
    for (CalendarType calendarType : CalendarType.values()) {
      if (calendarType.isTicketCalendar()) {
        adaptor.addValue(calendarType);
      }
    }
    adaptor.setValue(selectedCalendarType);
    ValueChangeHandler<CalendarType> handler = new ValueChangeHandler<CalendarType>() {
      @Override
      public void onValueChange(final ValueChangeEvent<CalendarType> event) {
        ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
          @Override
          public void onSuccess(Void result) {
            // Do nothing
          }
          @Override
          public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
              UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
              AsyncCallback<Void> callback) {
            adminAttendanceService.setSelectedTicketingCalendar(event.getValue(), callback);
          }
        };
        getAsyncCallbackExecutor().executeAndWait(callback, getDisplay());
      }
    };
    adaptor.addValueChangeHandler(handler);
  }

  public void show(HasWidgets container) {
    if (!isInitialised()) {
      prepare();
      setInitialised(true);
    }
    container.add(getDisplay().asWidget());
  }

  public Display getDisplay() {
    return i_display;
  }

  public AsyncCallbackExecutor getAsyncCallbackExecutor() {
    return i_asyncCallbackExecutor;
  }

  public boolean isInitialised() {
    return i_initialised;
  }

  public void setInitialised(boolean initialised) {
    i_initialised = initialised;
  }
}
