/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.factories.CalendarPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarType;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

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
public abstract class CalendarsPresenter implements CloseHandler<PopupPanel>, RefreshablePresenter {

	public static interface Display extends IsWidget {
		HasWidgets getCalendarListPanel();
		ListBox getOtherCalendarNames();
		HasClickHandlers getAddNewCalendar();
	}
	
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final CalendarPresenterFactory i_calendarPresenterFactory;
	private final boolean i_tickets;
	private HandlerRegistration i_handlerRegistration;
	
	public CalendarsPresenter(Display display, AsyncCallbackExecutor asyncCallbackExecutor, CalendarPresenterFactory calendarPresenterFactory, boolean tickets) {
		super();
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_calendarPresenterFactory = calendarPresenterFactory;
		i_tickets = tickets;
	}
	
	public void show(HasWidgets container) {
		ClickHandler addNewCalendarHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAddNewCalendar();
			}
		};
		HasClickHandlers addNewCalendar = getDisplay().getAddNewCalendar();
		HandlerRegistration handlerRegistration = getHandlerRegistration();
		if (handlerRegistration != null) {
			handlerRegistration.removeHandler();
		}
		setHandlerRegistration(addNewCalendar.addClickHandler(addNewCalendarHandler));
		refresh();
		container.add(getDisplay().asWidget());
	}
	
	@Override
	public void refresh() {
		HasWidgets calendarListPanel = getDisplay().getCalendarListPanel();
		calendarListPanel.clear();
		getDisplay().getOtherCalendarNames().clear();
		ExecutableAsyncCallback<CalendarConfiguration[]> callback = 
				new FailureAsPopupExecutableAsyncCallback<CalendarConfiguration[]>() {
			@Override
			public void onSuccess(CalendarConfiguration[] calendarConfigurations) {
				for (CalendarConfiguration calendarConfiguration : calendarConfigurations) {
					if (calendarConfiguration.isPersisted()) {
						addPersistedCalendar(calendarConfiguration);						
					}
					else {
						addNotPersistedCalendar(calendarConfiguration);
					}
				}
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<CalendarConfiguration[]> callback) {
				adminAttendanceService.getCalendarConfigurations(isTickets(), callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	protected void addNotPersistedCalendar(CalendarConfiguration calendarConfiguration) {
		ListBox otherCalendarNames = getDisplay().getOtherCalendarNames();
		otherCalendarNames.addItem(calendarConfiguration.getCalendarTitle(), calendarConfiguration.getCalendarType().name());
	}

	protected void addPersistedCalendar(CalendarConfiguration calendarConfiguration) {
		Anchor calendarLink = new Anchor(true);
		calendarLink.setText(calendarConfiguration.getCalendarTitle());
		final CalendarPresenter calendarPresenter = createCalendarPresenter(calendarConfiguration);
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				calendarPresenter.center();
			}
		};
		calendarLink.addClickHandler(handler);
		getDisplay().getCalendarListPanel().add(calendarLink);
	}

	public CalendarPresenter createCalendarPresenter(CalendarConfiguration calendarConfiguration) {
		final CalendarPresenter calendarPresenter = 
				getCalendarPresenterFactory().createCalendarPresenter(calendarConfiguration);
		calendarPresenter.getDisplay().getPopupPanel().addCloseHandler(this);
		return calendarPresenter;
	}

	protected void onAddNewCalendar() {
		ListBox otherCalendarNames = getDisplay().getOtherCalendarNames();
		int idx = otherCalendarNames.getSelectedIndex();
		if (idx >= 0) {
			final CalendarType calendarType = CalendarType.valueOf(otherCalendarNames.getValue(idx));
			ExecutableAsyncCallback<CalendarConfiguration> callback = new FailureAsPopupExecutableAsyncCallback<CalendarConfiguration>() {
				@Override
				public void onSuccess(CalendarConfiguration calendarConfiguration) {
					createCalendarPresenter(calendarConfiguration).center();
				}
				@Override
				public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
						UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
						AsyncCallback<CalendarConfiguration> callback) {
					adminAttendanceService.createNewCalendarConfiguration(calendarType, callback);
				}
			};
			getAsyncCallbackExecutor().execute(callback);
		}
	}

	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		refresh();
	}
	
	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public CalendarPresenterFactory getCalendarPresenterFactory() {
		return i_calendarPresenterFactory;
	}

	public boolean isTickets() {
		return i_tickets;
	}

	public HandlerRegistration getHandlerRegistration() {
		return i_handlerRegistration;
	}

	public void setHandlerRegistration(HandlerRegistration handlerRegistration) {
		i_handlerRegistration = handlerRegistration;
	}
}
