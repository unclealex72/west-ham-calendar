/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.gin;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.AuthenticationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.NavigationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.SeasonsPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.WaitingPresenter;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.SimplePanel;

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
@GinModules({ HammersClientModule.class, HammersInternalModule.class })
public interface HammersGinjector extends Ginjector {

	SimplePanel getMainPanel();

	SeasonsPresenter.Display getSeasonsView();
	NavigationPresenter.Display getNavigationView();
	AuthenticationPresenter.Display getUserPanel();
  WaitingPresenter.Display getWaitingView();

	PlaceHistoryHandler getPlaceHistoryHandler();
	AsyncCallbackExecutor getAsyncCallbackExecutor();

}
