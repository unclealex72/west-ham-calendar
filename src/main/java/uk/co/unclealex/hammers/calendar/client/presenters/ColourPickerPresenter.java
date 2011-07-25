/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import java.util.Map;

import uk.co.unclealex.hammers.calendar.client.presenters.ColourPickerPresenter.Display;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
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
public class ColourPickerPresenter extends AbstractPopupPresenter<Display> {

	public static interface Display extends AbstractPopupPresenter.Display, HasValue<CalendarColour> {
		Map<CalendarColour, IsWidget> getWidgetsByCalendarColour();
		void markAsUsed(IsWidget isWidget);
	}

	private final Display i_display;
	private final CalendarColour[] i_usedCalendarColours;
	private final CalendarColour i_calendarColour;
	
	@Inject
	public ColourPickerPresenter(Display display, @Assisted CalendarColour[] usedCalendarColours, @Assisted CalendarColour calendarColour) {
		super();
		i_display = display;
		i_usedCalendarColours = usedCalendarColours;
		i_calendarColour = calendarColour;
	}

	@Override
	protected void prepare(Display display) {
		display.setValue(getCalendarColour());
		for (CalendarColour usedCalendarColour : getUsedCalendarColours()) {
			display.markAsUsed(display.getWidgetsByCalendarColour().get(usedCalendarColour));
		}
	}

	@Override
	public Display getDisplay() {
		return i_display;
	}
	
	public CalendarColour[] getUsedCalendarColours() {
		return i_usedCalendarColours;
	}
	
	public CalendarColour getCalendarColour() {
		return i_calendarColour;
	}
}
