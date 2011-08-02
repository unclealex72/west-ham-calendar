/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.AdminPresenter.Display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
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
public class Admin extends Composite implements Display {

	@UiField HasWidgets gameCalendarsPanel;
	@UiField HasWidgets ticketCalendarsPanel;
	@UiField HasWidgets selectTicketCalendarPanel;
	@UiField HasClickHandlers runJobButton;
	@UiField HasWidgets updateUsersPanel;
	
  @UiTemplate("Admin.ui.xml")
	public interface Binder extends UiBinder<Widget, Admin> {
    // No extra methods
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@Inject
	public Admin() {
		initWidget(binder.createAndBindUi(this));
	}

	public HasWidgets getGameCalendarsPanel() {
		return gameCalendarsPanel;
	}

	public HasWidgets getTicketCalendarsPanel() {
		return ticketCalendarsPanel;
	}

	public HasClickHandlers getRunJobButton() {
		return runJobButton;
	}

  public HasWidgets getUpdateUsersPanel() {
    return updateUsersPanel;
  }

  public HasWidgets getSelectTicketCalendarPanel() {
    return selectTicketCalendarPanel;
  }

}
