/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.presenters.AbstractPopupPresenter.Display;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

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
public abstract class AbstractPopupPresenter<P extends PopupPanel, D extends Display<P>> {

	public static interface Display<P> extends IsWidget, HasWidgets {
	
		public P getPopupPanel();
	}

	public void center() {
		prepare().center();
	}
	
	public void showRelativeTo(UIObject uiObject) {
		prepare().showRelativeTo(uiObject);
	}
	
	public void hide() {
		getDisplay().getPopupPanel().hide();
	}
	
	protected PopupPanel prepare() {
		D display = getDisplay();
		prepare(display);
		PopupPanel popupPanel = display.getPopupPanel();
		RootPanel.get().add(popupPanel);
		return popupPanel;
	}
	
	protected abstract void prepare(D display);
	
	protected abstract D getDisplay();
}
