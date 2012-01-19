/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.LeaguePresenter.Display;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.views.LeagueTableRow;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;
import uk.co.unclealex.hammers.calendar.shared.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.shared.remote.UserAttendanceServiceAsync;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
public class LeaguePresenter extends AbstractTablePresenter<LeagueRow, LeagueTableRow, Display> {

	public static interface Display extends AbstractTablePresenter.Display<LeagueRow, LeagueTableRow> {
    // No extra method
  }
	
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	
	@Inject
	public LeaguePresenter(
			PlaceController placeController,
			Display display, HammersMessages hammersMessages, AsyncCallbackExecutor asyncCallbackExecutor) {
		super(placeController, display, hammersMessages);
		i_asyncCallbackExecutor = asyncCallbackExecutor;
	}

	@Override
	protected void start(final Display display, final int season) {
		ExecutableAsyncCallback<LeagueRow[]> callback = new FailureAsPopupExecutableAsyncCallback<LeagueRow[]>() {
			@Override
			public void onSuccess(LeagueRow[] leagueRows) {
				for (LeagueRow leagueRow : leagueRows) {
					display.addRow(leagueRow);
				}
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<LeagueRow[]> callback) {
				anonymousAttendanceService.getLeagueForSeason(season, callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}
	
	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}
}
