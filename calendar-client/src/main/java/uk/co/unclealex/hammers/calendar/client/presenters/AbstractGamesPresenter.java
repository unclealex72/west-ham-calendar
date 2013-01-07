/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.AbstractGamesPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.CanWaitSupport;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.views.AbstractGameTableRow;
import uk.co.unclealex.hammers.calendar.shared.model.GameView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
public abstract class AbstractGamesPresenter<D extends Display<V>, V extends AbstractGameTableRow> extends AbstractTablePresenter<GameView, V, D> {

	public static interface Display<V> extends AbstractTablePresenter.Display<GameView, V> {
	  // No extra methods
	}
	
	private final AsyncCallbackExecutor asyncCallbackExecutor;
	private final Provider<CanWaitSupport> canWaitSupportProvider;
	
	public AbstractGamesPresenter(
			PlaceController placeController,
			D display, HammersMessages hammersMessages,
			AsyncCallbackExecutor asyncCallbackExecutor,
			Provider<CanWaitSupport> canWaitSupportProvider) {
		super(placeController, display, hammersMessages);
		this.asyncCallbackExecutor = asyncCallbackExecutor;
		this.canWaitSupportProvider = canWaitSupportProvider;
	}

	@Override
	protected void start(final D display, final int season) {
		final boolean showMonthBreaks = showMonthBreaks();
		ExecutableAsyncCallback<GameView[]> callback = new FailureAsPopupExecutableAsyncCallback<GameView[]>() {
			String previousMonth = null;
			@Override
			public void onSuccess(GameView[] games) {
				for (final GameView gameView : games) {
					if (showMonthBreaks) {
						String month = getHammersMessages().month(gameView.getDatePlayed());
						if (!month.equals(previousMonth)) {
							display.addSubHeader(month);
							previousMonth = month;
						}
					}
					final V view = display.addRow(gameView);
					ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							attendanceChanged(event.getValue(), view, gameView);
						};
					};
					view.addValueChangeHandler(handler);
				}
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<GameView[]> callback) {
				executeCallback(anonymousAttendanceService, season, callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	protected void attendanceChanged(final boolean attended, final V view, final GameView gameView) {
		ExecutableAsyncCallback<GameView> callback = new FailureAsPopupExecutableAsyncCallback<GameView>() {
			@Override
			public void onSuccess(GameView result) {
				//
			}
			@Override
			public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
					UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
					AsyncCallback<GameView> callback) {
				Integer id = gameView.getId();
				if (attended) {
					userAttendanceService.attendGame(id, callback);
				}
				else {
					userAttendanceService.unattendGame(id, callback);
				}
			}
		};
		CanWaitSupport canWait = getCanWaitSupportProvider().get().wrap(view);
    getAsyncCallbackExecutor().executeAndWait(callback, canWait);
	}
	
	protected abstract void executeCallback(AnonymousAttendanceServiceAsync anonymousAttendanceService, int season, AsyncCallback<GameView[]> callback);
	
	protected abstract boolean showMonthBreaks();
	
	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return asyncCallbackExecutor;
	}

	public Provider<CanWaitSupport> getCanWaitSupportProvider() {
		return canWaitSupportProvider;
	}
}
