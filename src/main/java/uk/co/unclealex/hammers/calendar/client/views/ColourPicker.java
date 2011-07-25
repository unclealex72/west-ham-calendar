/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.presenters.ColourPickerPresenter.Display;
import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
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
public class ColourPicker extends SimplePanel implements Display {

	public interface Style extends CssResource {
		String colourPicker();
		String alreadyUsed();
	}
	
  @UiTemplate("ColourPicker.ui.xml")
	public interface Binder extends UiBinder<Widget, ColourPicker> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private CalendarColour i_value;
	private Map<IsWidget, CalendarColour> i_calendarColoursByWidget = new HashMap<IsWidget, CalendarColour>();
	private Map<CalendarColour, IsWidget> i_widgetsByCalendarColour = new HashMap<CalendarColour, IsWidget>();
	
	@UiField FlexTable mainTable;
	@UiField Style style;
	@UiField PopupPanel popupPanel;
	
	@Inject
	public ColourPicker() {
		add(binder.createAndBindUi(this));
		build();
	}
	
	/**
	 * @return
	 */
	protected void build() {
		FlexTable table = getMainTable();
		CalendarColour[] calendarColours = CalendarColour.values();
		final Map<Widget, CalendarColour> coloursBySource = new HashMap<Widget, CalendarColour>();
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				setValue(coloursBySource.get(source), true);
			}
		};
		Map<CalendarColour, IsWidget> widgetsByCalendarColour = getWidgetsByCalendarColour();
		for (int idx = 0; idx < calendarColours.length; idx++) {
			Anchor a = new Anchor(true);
			CalendarColour calendarColour = calendarColours[idx];
			a.addStyleName(getStyle().colourPicker());
			a.addStyleName(calendarColour.asStyle());
			a.setTitle(calendarColour.getName());
			coloursBySource.put(a, calendarColour);
			widgetsByCalendarColour.put(calendarColour, a);
			table.setWidget(idx / 7, idx % 7, a);
			a.addClickHandler(handler);
		}
	}

	@Override
	public void markAsUsed(IsWidget isWidget) {
		markAsUsed(isWidget.asWidget());
	}

	public void markAsUsed(Widget widget) {
		widget.setTitle(widget.getTitle() + " (used)");
		widget.addStyleName(getStyle().alreadyUsed());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CalendarColour> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public CalendarColour getValue() {
		return i_value;
	}

	@Override
	public void setValue(CalendarColour value) {
		setValue(value, false);
	}

	@Override
	public void setValue(CalendarColour value, boolean fireEvents) {
		CalendarColour oldValue = i_value;
		i_value = value;
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
    }
	}

	@Override
	public Map<CalendarColour, IsWidget> getWidgetsByCalendarColour() {
		return i_widgetsByCalendarColour;
	}
	
	public Map<IsWidget, CalendarColour> getCalendarColoursByWidget() {
		return i_calendarColoursByWidget;
	}

	public FlexTable getMainTable() {
		return mainTable;
	}

	public Style getStyle() {
		return style;
	}

	public PopupPanel getPopupPanel() {
		return popupPanel;
	}
}
