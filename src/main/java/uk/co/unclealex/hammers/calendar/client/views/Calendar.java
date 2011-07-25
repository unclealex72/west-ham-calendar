/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.CalendarPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.CanWaitSupport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
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
public class Calendar extends SimplePanel implements Display {

  @UiTemplate("Calendar.ui.xml")
	public interface Binder extends UiBinder<Widget, Calendar> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private final CalendarCaption i_calendarCaption;
	private final CanWaitSupport i_canWaitSupport;
	
	@UiField DialogBox popupPanel;
	@UiField ListBox reminderHours;
	@UiField ListBox reminderMinutes;
	@UiField CheckBox busy;
	@UiField CheckBox share;
	@UiField CheckBox selected;
	@UiField Button changeColour;
	@UiField Button undo;
	@UiField Button cancel;
	@UiField Button save;
	@UiField Button remove;

	@Inject
	public Calendar(CalendarCaption calendarCaption, CanWaitSupport canWaitSupport) {
		i_calendarCaption = calendarCaption;
		add(binder.createAndBindUi(this));
		i_canWaitSupport = canWaitSupport;
		canWaitSupport.wrap(
		    getReminderHours(), getReminderMinutes(), getBusy(), getShare(), getSelected(), 
		    getChangeColour(), getUndo(), getCancel(), getSave(), getRemove());
	}

	@UiFactory
	public DialogBox createDialogBox(boolean animationEnabled, boolean glassEnabled) {
		DialogBox dialogBox = new DialogBox(getCalendarCaption());
		dialogBox.setAnimationEnabled(animationEnabled);
		dialogBox.setGlassEnabled(glassEnabled);
		return dialogBox;
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
	public CheckBox getBusy() {
		return busy;
	}

	@Override
	public CheckBox getShare() {
		return share;
	}

	@Override
	public CheckBox getSelected() {
		return selected;
	}

	@Override
	public Button getChangeColour() {
		return changeColour;
	}

	@Override
	public Button getUndo() {
		return undo;
	}

	@Override
	public Button getCancel() {
		return cancel;
	}
	@Override
	public Button getSave() {
		return save;
	}

	@Override
	public Button getRemove() {
		return remove;
	}

	public ListBox getReminderHours() {
		return reminderHours;
	}

	public ListBox getReminderMinutes() {
		return reminderMinutes;
	}

	public PopupPanel getPopupPanel() {
		return popupPanel;
	}

	public CalendarCaption getCalendarCaption() {
		return i_calendarCaption;
	}

  public CanWaitSupport getCanWaitSupport() {
    return i_canWaitSupport;
  }
}
