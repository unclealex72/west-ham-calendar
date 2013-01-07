/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.places.AdminPlace;
import uk.co.unclealex.hammers.calendar.client.places.GamesPlace;
import uk.co.unclealex.hammers.calendar.client.places.HammersPlace;
import uk.co.unclealex.hammers.calendar.client.places.HammersPlaceVisitor;
import uk.co.unclealex.hammers.calendar.client.places.LeaguePlace;
import uk.co.unclealex.hammers.calendar.client.places.NoGamesPlace;
import uk.co.unclealex.hammers.calendar.client.places.TeamsPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
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
public class HammersActivityMapper implements ActivityMapper {

	private final Provider<GamesPresenter> gamesPresenterProvider;
	private final Provider<TeamsPresenter> teamsPresenterProvider;
	private final Provider<LeaguePresenter> leaguePresenterProvider;
	private final Provider<AdminPresenter> adminPresenterProvider;
	private final Provider<NoGamesPresenter> noGamesPresenterProvider;
	private final SeasonsPresenter seasonsPresenter;

	@Inject
	public HammersActivityMapper(Provider<GamesPresenter> gamesPresenterProvider,
			Provider<TeamsPresenter> teamsPresenterProvider, Provider<LeaguePresenter> leaguePresenterProvider,
			Provider<AdminPresenter> adminPresenterProvider,
			Provider<NoGamesPresenter> noGamesPresenterProvider,
			SeasonsPresenter seasonsPresenter) {
		this.gamesPresenterProvider = gamesPresenterProvider;
		this.teamsPresenterProvider = teamsPresenterProvider;
		this.leaguePresenterProvider = leaguePresenterProvider;
		this.adminPresenterProvider = adminPresenterProvider;
		this.noGamesPresenterProvider = noGamesPresenterProvider;
		this.seasonsPresenter = seasonsPresenter;
	}

	@Override
	public Activity getActivity(Place place) {
		getSeasonsPresenter().go();
		return new ActivityProvider(place).asActivity();
	}
	
	protected class ActivityProvider implements HammersPlaceVisitor, Activity {

		private final Place place;
		private Activity activity;
		
		public ActivityProvider(Place place) {
			super();
			this.place = place;
		}

		public Activity asActivity() {
			Place place = getPlace();
			if (place instanceof HammersPlace) {
				((HammersPlace) place).accept(this);
			}
			else {
				asDefault();
			}
			return this;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ActivityProvider) && (getPlace().equals(((ActivityProvider) obj).getPlace()));
		}
		
		@Override
		public void visit(HammersPlace hammersPlace) {
			asDefault();
		}
		
		@Override
		public void visit(TeamsPlace teamsPlace) {
			setActivity(getTeamsPresenterProvider().get());
		}

		@Override
		public void visit(LeaguePlace leaguePlace) {
			setActivity(getLeaguePresenterProvider().get());
		}

		@Override
		public void visit(GamesPlace gamesPlace) {
			setActivity(getGamesPresenterProvider().get());
		}

		@Override
		public void visit(AdminPlace adminPlace) {
			setActivity(getAdminPresenterProvider().get());
		}

		@Override
		public void visit(NoGamesPlace noGamesPlace) {
			setActivity(getNoGamesPresenterProvider().get());
		}

		public void asDefault() {
			// Do nothing
		}
		
		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		@Override
		public String mayStop() {
			return getActivity().mayStop();
		}

		@Override
		public void onCancel() {
			getActivity().onCancel();
		}

		@Override
		public void onStop() {
			getActivity().onStop();
		}

		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			getActivity().start(panel, eventBus);
		}

		public Place getPlace() {
			return place;
		}
		
	}

	public Provider<GamesPresenter> getGamesPresenterProvider() {
		return gamesPresenterProvider;
	}

	public Provider<TeamsPresenter> getTeamsPresenterProvider() {
		return teamsPresenterProvider;
	}

	public Provider<LeaguePresenter> getLeaguePresenterProvider() {
		return leaguePresenterProvider;
	}

	public Provider<AdminPresenter> getAdminPresenterProvider() {
		return adminPresenterProvider;
	}

	public SeasonsPresenter getSeasonsPresenter() {
		return seasonsPresenter;
	}

	public Provider<NoGamesPresenter> getNoGamesPresenterProvider() {
		return noGamesPresenterProvider;
	}
}
