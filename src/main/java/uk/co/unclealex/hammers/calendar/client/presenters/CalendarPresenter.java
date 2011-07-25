/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.factories.ColourPickerPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.presenters.CalendarPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.NumericListBoxAdaptor;
import uk.co.unclealex.hammers.calendar.client.views.CalendarCaption;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarConfiguration;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
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
public class CalendarPresenter extends AbstractPopupPresenter<Display> {

	public static interface Display extends AbstractPopupPresenter.Display, CanWait {

		Button getRemove();
		Button getSave();
		Button getCancel();
		Button getUndo();
		HasClickHandlers getChangeColour();
		HasValue<Boolean> getSelected();
		HasValue<Boolean> getShare();
		HasValue<Boolean> getBusy();
		ListBox getReminderHours();
		ListBox getReminderMinutes();
		CalendarCaption getCalendarCaption();
	}
	
	private final Display i_display;
	private final NumericListBoxAdaptor i_reminderHoursAdaptor;
	private final NumericListBoxAdaptor i_reminderMinutesAdaptor;
	private CalendarConfiguration i_calendarConfiguration;
	private final ColourPickerPresenterFactory i_colourPickerPresenterFactory;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private CalendarColour i_originalCalendarColour;
	private CalendarColour i_currentCalendarColour;
	
	@Inject
	public CalendarPresenter(
			@Assisted CalendarConfiguration calendarConfiguration, Display display, 
			ColourPickerPresenterFactory colourPickerPresenterFactory, AsyncCallbackExecutor asyncCallbackExecutor) {
		super();
		i_calendarConfiguration = calendarConfiguration;
		i_display = display;
		i_reminderHoursAdaptor = new NumericListBoxAdaptor(display.getReminderHours());
		i_reminderMinutesAdaptor = new NumericListBoxAdaptor(display.getReminderMinutes());
		i_colourPickerPresenterFactory = colourPickerPresenterFactory;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		setCalendarConfiguration(calendarConfiguration);
	}

	@Override
	protected void prepare(Display display) {
		NumericListBoxAdaptor hoursAdaptor = getReminderHoursAdaptor();
		NumericListBoxAdaptor minutesAdaptor = getReminderMinutesAdaptor();
		addValues(hoursAdaptor, 24, 24, 48, 72, 96);
		addValues(minutesAdaptor, 60);
		registerForUpdates(display.getSelected());
		registerForUpdates(display.getShare());
		registerForUpdates(display.getBusy());
		registerForUpdates(hoursAdaptor);
		registerForUpdates(minutesAdaptor);
		addConfirmableAction(
			display.getCancel(),
			"cancel", 
			new ConfirmationRequired() { public boolean isConfirmationRequired() { return isUpdated(); }},
			new Runnable() { public void run() { hide(); }});
		addConfirmableAction(
			display.getUndo(), 
			"undo changes", 
			ConfirmationRequiredAlways.INSTANCE, 
			new Runnable() { public void run() { resetValues(); }});
		addConfirmableAction(display.getSave(), "save", ConfirmationRequiredAlways.INSTANCE, createSaveRunnable());
		addConfirmableAction(display.getRemove(), "delete", ConfirmationRequiredAlways.INSTANCE, createRemoveRunnable());
		display.getRemove().setEnabled(!getCalendarConfiguration().getCalendarType().isMandatory());
		display.getChangeColour().addClickHandler(createColourPickerHandler());
		resetValues();
	}

	protected void addValues(NumericListBoxAdaptor adaptor, int upTo, int... extraValues) {
		for (int value = 0; value < upTo; value++) {
			adaptor.addValue(value);
		}
		for (int value : extraValues) {
			adaptor.addValue(value);
		}
	}

	protected ClickHandler createColourPickerHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Widget source = (Widget) event.getSource();
				ExecutableAsyncCallback<CalendarColour[]> popupCallback = new FailureAsPopupExecutableAsyncCallback<CalendarColour[]>() {

					@Override
					public void onSuccess(final CalendarColour[] usedCalendarColours) {
						final ColourPickerPresenter colourPickerPresenter = 
								getColourPickerPresenterFactory().createColourPickerPresenter(getCurrentCalendarColour(), usedCalendarColours);
						ValueChangeHandler<CalendarColour> colourHandler = new ValueChangeHandler<CalendarColour>() {
							@Override
							public void onValueChange(ValueChangeEvent<CalendarColour> event) {
								CalendarColour calendarColour = event.getValue();
								updateColour(getCurrentCalendarColour(), calendarColour);
								setCurrentCalendarColour(calendarColour);
								colourPickerPresenter.hide();
								checkIfUpdated();
							}
						};
						colourPickerPresenter.getDisplay().addValueChangeHandler(colourHandler);
						colourPickerPresenter.showRelativeTo(source);
					}

					@Override
					public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
							UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
							AsyncCallback<CalendarColour[]> callback) {
						adminAttendanceService.getUsedCalendarColours(callback);
					}
				};
				getAsyncCallbackExecutor().execute(popupCallback);
			}
		};
	}

	protected void updateColour(CalendarColour originalColour, CalendarColour newColour) {
		CalendarCaption calendarCaption = getDisplay().getCalendarCaption();
		calendarCaption.removeStyleName(originalColour.asStyle());
		calendarCaption.addStyleName(newColour.asStyle());
	}

	protected AsyncRunnable<Void> createRemoveRunnable() {
		return new AsyncRunnable<Void>(true) {
			@Override
			public void onSuccess(Void result) {
				// Do nothing
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Void> callback) {
				adminAttendanceService.remove(getCalendarConfiguration().getCalendarType(), callback);
			}
		};
	}

	protected AsyncRunnable<Void> createSaveRunnable() {
		return new AsyncRunnable<Void>(true) {
			@Override
			public void onSuccess(Void result) {
				setCalendarConfiguration(getCalendarConfiguration());
				resetValues();
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<Void> callback) {
				injectValues();
				adminAttendanceService.createOrUpdate(getCalendarConfiguration(), callback);
			}
		};
	}
	
	protected void resetValues() {
		Display display = getDisplay();
		CalendarConfiguration calendarConfiguration = getCalendarConfiguration();
		CalendarColour originalCalendarColour = getOriginalCalendarColour();
		updateColour(getCurrentCalendarColour(), originalCalendarColour);
		display.getBusy().setValue(calendarConfiguration.isBusy());
		Integer reminderInMinutes = calendarConfiguration.getReminderInMinutes();
		if (reminderInMinutes == null) {
			reminderInMinutes = 0;
		}
		getReminderMinutesAdaptor().setValue(reminderInMinutes % 60);
		getReminderHoursAdaptor().setValue(reminderInMinutes / 60);
		display.getSelected().setValue(calendarConfiguration.isSelected());
		display.getShare().setValue(calendarConfiguration.isShared());
		CalendarCaption calendarCaption = display.getCalendarCaption();
		calendarCaption.getCalendarTitle().setText(calendarConfiguration.getCalendarTitle());
		calendarCaption.getCalendarDescription().setText(calendarConfiguration.getDescription());
		setCurrentCalendarColour(originalCalendarColour);
		checkIfUpdated();
	}

	protected void injectValues() {
		Display display = getDisplay();
		CalendarConfiguration calendarConfiguration = getCalendarConfiguration();
		calendarConfiguration.setBusy(display.getBusy().getValue());
		int reminderInMinutes = 
				getReminderHoursAdaptor().getValue() * 60 + getReminderMinutesAdaptor().getValue();
		calendarConfiguration.setColour(getCurrentCalendarColour());
		calendarConfiguration.setReminderInMinutes(reminderInMinutes == 0?null:reminderInMinutes);
		calendarConfiguration.setSelected(display.getSelected().getValue());
		calendarConfiguration.setShared(display.getShare().getValue());
	}

	protected abstract class AsyncRunnable<T> extends FailureAsPopupExecutableAsyncCallback<T> implements Runnable {

		private final boolean i_hideOnExit;
		
		public AsyncRunnable(boolean hideOnExit) {
			super();
			i_hideOnExit = hideOnExit;
		}


		public void run() {
			ExecutableAsyncCallback<T> callback = new FailureAsPopupExecutableAsyncCallback<T>() {
				public void onSuccess(T result) {
					AsyncRunnable.this.onSuccess(result);
					if (isHideOnExit()) {
						hide();
					}
				}
				@Override
				public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
						UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
						AsyncCallback<T> callback) {
					AsyncRunnable.this.execute(anonymousAttendanceService, userAttendanceService, adminAttendanceService, callback);					
				}
			};
      getAsyncCallbackExecutor().executeAndWait(callback, getDisplay());
		}


		public boolean isHideOnExit() {
			return i_hideOnExit;
		}
	}
	
	protected <T> void registerForUpdates(HasValue<T> hasValue) {
		ValueChangeHandler<T> handler = new ValueChangeHandler<T>() {
			@Override
			public void onValueChange(ValueChangeEvent<T> event) {
				checkIfUpdated();
			}
		};
		hasValue.addValueChangeHandler(handler);
	}

	protected void checkIfUpdated() {
		boolean isUpdated = isUpdated();
		getDisplay().getUndo().setEnabled(isUpdated);
		getDisplay().getSave().setEnabled(isUpdated);
	}

	public boolean isUpdated() {
		CalendarConfiguration calendarConfiguration = getCalendarConfiguration();
		Display display = getDisplay();
		Integer originalReminderInMinutes = calendarConfiguration.getReminderInMinutes();
		boolean isAsOriginal;
		int currentReminderHours = getReminderHoursAdaptor().getValue();
		int currentReminderMinutes = getReminderMinutesAdaptor().getValue();
		if (originalReminderInMinutes == null) {
			isAsOriginal = currentReminderMinutes == 0 && currentReminderHours == 0;
		}
		else {
			isAsOriginal = 
					originalReminderInMinutes / 60 == currentReminderHours &&
					originalReminderInMinutes % 60 == currentReminderMinutes;
		}
		isAsOriginal &= 
				calendarConfiguration.isBusy() == display.getBusy().getValue() &&
				calendarConfiguration.isSelected() == display.getSelected().getValue() &&
				calendarConfiguration.isShared() == display.getShare().getValue() &&
				getCurrentCalendarColour() == getOriginalCalendarColour();
		return !isAsOriginal;
	}
	
	protected interface ConfirmationRequired {
		public boolean isConfirmationRequired();
	}
	
	protected static class ConfirmationRequiredAlways implements ConfirmationRequired {
		
		public static ConfirmationRequiredAlways INSTANCE = new ConfirmationRequiredAlways();
		@Override
		public boolean isConfirmationRequired() {
			return true;
		}
	}
	
	protected void addConfirmableAction(
			HasClickHandlers target, final String action, final ConfirmationRequired confirmationRequired, final Runnable runnable) {
		ClickHandler handler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (!confirmationRequired.isConfirmationRequired() || Window.confirm("Are you sure you want to " + action + "?")) {
					runnable.run();
				}
			}
		};
		target.addClickHandler(handler);
	}

	public Display getDisplay() {
		return i_display;
	}

	public CalendarConfiguration getCalendarConfiguration() {
		return i_calendarConfiguration;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public CalendarColour getOriginalCalendarColour() {
		return i_originalCalendarColour;
	}

	protected void setCalendarConfiguration(CalendarConfiguration calendarConfiguration) {
		i_calendarConfiguration = calendarConfiguration;
		CalendarColour colour = calendarConfiguration.getColour();
		setOriginalCalendarColour(colour);
		setCurrentCalendarColour(colour);
	}

	protected void setOriginalCalendarColour(CalendarColour originalCalendarColour) {
		i_originalCalendarColour = originalCalendarColour;
	}

	public NumericListBoxAdaptor getReminderHoursAdaptor() {
		return i_reminderHoursAdaptor;
	}

	public NumericListBoxAdaptor getReminderMinutesAdaptor() {
		return i_reminderMinutesAdaptor;
	}

	public CalendarColour getCurrentCalendarColour() {
		return i_currentCalendarColour;
	}

	public void setCurrentCalendarColour(CalendarColour currentCalendarColour) {
		i_currentCalendarColour = currentCalendarColour;
	}

	public ColourPickerPresenterFactory getColourPickerPresenterFactory() {
		return i_colourPickerPresenterFactory;
	}
}
