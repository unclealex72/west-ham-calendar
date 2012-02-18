/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.GamesPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.CanWaitSupport;
import uk.co.unclealex.hammers.calendar.client.views.GameTableRow;
import uk.co.unclealex.hammers.calendar.shared.model.Game;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Provider;

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
public class GamesPresenter extends AbstractGamesPresenter<Display, GameTableRow> {

	public static interface Display extends AbstractGamesPresenter.Display<GameTableRow> {
    // No extra method
  }


	@Inject
	public GamesPresenter(PlaceController placeController,
			Display display, HammersMessages hammersMessages,
			AsyncCallbackExecutor asyncCallbackExecutor,
			Provider<CanWaitSupport> canWaitSupportProvider) {
		super(placeController, display, hammersMessages, asyncCallbackExecutor, canWaitSupportProvider);
	}

  @Override
  protected void executeCallback(AnonymousAttendanceServiceAsync anonymousAttendanceService, int season,
  		AsyncCallback<Game[]> callback) {
  	anonymousAttendanceService.getAllGamesChronologicallyForSeason(season, callback);
  }
	
	@Override
	protected boolean showMonthBreaks() {
		return true;
	}
}
