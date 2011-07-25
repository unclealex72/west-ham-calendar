/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
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
public class CalendarCaption extends FocusPanel implements Caption {

  @UiTemplate("CalendarCaption.ui.xml")
	public interface Binder extends UiBinder<Widget, CalendarCaption> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Label calendarTitle;
	@UiField Label calendarDescription;

	@Inject
	public CalendarCaption() {
		add(binder.createAndBindUi(this));
	}

	public Label getCalendarTitle() {
		return calendarTitle;
	}

	public Label getCalendarDescription() {
		return calendarDescription;
	}

	@Override
	public String getHTML() {
		return getElement().getInnerHTML();
	}

	@Override
	public void setHTML(String html) {
		throw new UnsupportedOperationException("setHTML");
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException("setText");
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.safehtml.client.HasSafeHtml#setHTML(com.google.gwt.safehtml.shared.SafeHtml)
	 */
	@Override
	public void setHTML(SafeHtml html) {
		// TODO Auto-generated method stub
		
	}
}
