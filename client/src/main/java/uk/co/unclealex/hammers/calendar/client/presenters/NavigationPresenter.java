/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.places.AdminPlace;
import uk.co.unclealex.hammers.calendar.client.places.GamesPlace;
import uk.co.unclealex.hammers.calendar.client.places.LeaguePlace;
import uk.co.unclealex.hammers.calendar.client.places.SeasonAwarePlace;
import uk.co.unclealex.hammers.calendar.client.places.TeamsPlace;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationEvent;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationEventListener;
import uk.co.unclealex.hammers.calendar.client.security.AuthenticationManager;
import uk.co.unclealex.hammers.calendar.client.ui.SelectableListPanel;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;

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
public class NavigationPresenter implements AuthenticationEventListener {

	public interface Display extends IsWidget {

		Anchor getGamesOption();
		Anchor getTeamsOption();
		Anchor getLeagueOption();
		Anchor getAdminOption();
	  Anchor getAuthenticateOption();

		SelectableListPanel getOptions();
	}

	private final PlaceController i_placeController;
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final HammersMessages i_hammersMessages;
	private final AuthenticationManager i_authenticationManager;
	
	@Inject
	public NavigationPresenter(
			PlaceController placeController, Display display, AuthenticationManager authenticationManager,
			AsyncCallbackExecutor asyncCallbackExecutor, HammersMessages hammersMessages) {
		super();
		i_placeController = placeController;
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_hammersMessages = hammersMessages;
		i_authenticationManager = authenticationManager;
		getAuthenticationManager().addAuthenticationEventListener(this);
		bind();
	}

	protected void bind() {
		Display display = getDisplay();
		bind(display.getGamesOption(), new PlaceFactory() { public Place createPlace(int season) { return new GamesPlace(season); } });
		bind(display.getTeamsOption(), new PlaceFactory() { public Place createPlace(int season) { return new TeamsPlace(season); } });
		bind(display.getLeagueOption(), new PlaceFactory() { public Place createPlace(int season) { return new LeaguePlace(season); } });
		bind(display.getAdminOption(), new PlaceFactory() { public Place createPlace(int season) { return new AdminPlace(season); } });
		final Anchor authenticateOption = display.getAuthenticateOption();
		ClickHandler authenticateHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final boolean loginRequested = getHammersMessages().login().equals(authenticateOption.getText());
				ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						PlaceController placeController = getPlaceController();
						Place currentPlace = placeController.getWhere();
						placeController.goTo(Place.NOWHERE);
						placeController.goTo(currentPlace);
					}
					@Override
					public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
							UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
							AsyncCallback<Void> callback) {
						if (loginRequested) {
							userAttendanceService.forceLogin(callback);
						}
						else {
							anonymousAttendanceService.logout(callback);
							getAuthenticationManager().unauthenticated();
						}
					}
				};
				getAsyncCallbackExecutor().execute(callback);
			}
		};
		authenticateOption.addClickHandler(authenticateHandler);
	}
	
	protected void bind(final Anchor anchor, final PlaceFactory placeFactory) {
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PlaceController placeController = getPlaceController();
				Place where = placeController.getWhere();
				if (where instanceof SeasonAwarePlace) {
					getDisplay().getOptions().select(anchor);
					Place newPlace = placeFactory.createPlace(((SeasonAwarePlace) where).getSeason());
					placeController.goTo(newPlace);
				}
			}
		};
		anchor.addClickHandler(handler);
	}
	
	@Override
	public void onAuthenticationChanged(AuthenticationEvent event) {
		String username = event.getUsername();
		HammersMessages hammersMessages = getHammersMessages();
		String anchorText = username==null?hammersMessages.login():hammersMessages.logoff();
		getDisplay().getAuthenticateOption().setText(anchorText);
	}
	
	protected interface PlaceFactory {
		public Place createPlace(int season);
	}
	
	public PlaceController getPlaceController() {
		return i_placeController;
	}
	
	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public HammersMessages getHammersMessages() {
		return i_hammersMessages;
	}

	public AuthenticationManager getAuthenticationManager() {
		return i_authenticationManager;
	}
	
}
