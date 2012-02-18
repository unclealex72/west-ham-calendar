/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.gin;

import javax.inject.Singleton;

import uk.co.unclealex.hammers.calendar.client.factories.GameTableRowFactory;
import uk.co.unclealex.hammers.calendar.client.factories.GoogleAuthenticationPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.factories.LeagueTableRowFactory;
import uk.co.unclealex.hammers.calendar.client.factories.LoginPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.factories.LoginPresenterFactoryImpl;
import uk.co.unclealex.hammers.calendar.client.factories.TeamTableRowFactory;
import uk.co.unclealex.hammers.calendar.client.presenters.AdminPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.AuthenticationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.ChangePasswordPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.GamesPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.GoogleAuthenticationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.LeaguePresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.LoginPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.NavigationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.SeasonsPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.SelectTicketCalendarPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.TeamsPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.UpdateUsersPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.UserPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.WaitingPresenter;
import uk.co.unclealex.hammers.calendar.client.util.ClickHelper;
import uk.co.unclealex.hammers.calendar.client.util.ClickHelperImpl;
import uk.co.unclealex.hammers.calendar.client.views.Admin;
import uk.co.unclealex.hammers.calendar.client.views.Authentication;
import uk.co.unclealex.hammers.calendar.client.views.ChangePassword;
import uk.co.unclealex.hammers.calendar.client.views.GameTableRow;
import uk.co.unclealex.hammers.calendar.client.views.GamesTable;
import uk.co.unclealex.hammers.calendar.client.views.GoogleAuthentication;
import uk.co.unclealex.hammers.calendar.client.views.LeagueTable;
import uk.co.unclealex.hammers.calendar.client.views.LeagueTableRow;
import uk.co.unclealex.hammers.calendar.client.views.Login;
import uk.co.unclealex.hammers.calendar.client.views.Navigation;
import uk.co.unclealex.hammers.calendar.client.views.Seasons;
import uk.co.unclealex.hammers.calendar.client.views.SelectTicketCalendar;
import uk.co.unclealex.hammers.calendar.client.views.TableRow;
import uk.co.unclealex.hammers.calendar.client.views.TeamTableRow;
import uk.co.unclealex.hammers.calendar.client.views.TeamsTable;
import uk.co.unclealex.hammers.calendar.client.views.UpdateUsers;
import uk.co.unclealex.hammers.calendar.client.views.User;
import uk.co.unclealex.hammers.calendar.client.views.Waiting;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

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
public class HammersClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(SeasonsPresenter.Display.class).to(Seasons.class).in(Singleton.class);
		bind(SeasonsPresenter.class).in(Singleton.class);
		
		bind(NavigationPresenter.Display.class).to(Navigation.class).in(Singleton.class);
		bind(NavigationPresenter.class).asEagerSingleton();
		
		bind(AuthenticationPresenter.Display.class).to(Authentication.class).in(Singleton.class);
		bind(AuthenticationPresenter.class).asEagerSingleton();
		
		bind(ChangePasswordPresenter.Display.class).to(ChangePassword.class).in(Singleton.class);
		bind(ChangePasswordPresenter.class).in(Singleton.class);
		
		bind(GoogleAuthenticationPresenter.Display.class).to(GoogleAuthentication.class);
		install(new GinFactoryModuleBuilder().implement(GoogleAuthenticationPresenter.class, GoogleAuthenticationPresenter.class).
				build(GoogleAuthenticationPresenterFactory.class));

		bind(LoginPresenter.Display.class).to(Login.class);
		bind(LoginPresenterFactory.class).to(LoginPresenterFactoryImpl.class);
		
		bind(GamesPresenter.Display.class).to(GamesTable.class).in(Singleton.class);
		bind(GamesPresenter.class).in(Singleton.class);
		install(new GinFactoryModuleBuilder().implement(TableRow.class, GameTableRow.class).
				build(GameTableRowFactory.class));
		
		bind(TeamsPresenter.Display.class).to(TeamsTable.class).in(Singleton.class);
		bind(TeamsPresenter.class).in(Singleton.class);
		install(new GinFactoryModuleBuilder().implement(TableRow.class, TeamTableRow.class).
				build(TeamTableRowFactory.class));
				
		bind(LeaguePresenter.Display.class).to(LeagueTable.class).in(Singleton.class);
		bind(LeaguePresenter.class).in(Singleton.class);
		install(new GinFactoryModuleBuilder().implement(TableRow.class, LeagueTableRow.class).
				build(LeagueTableRowFactory.class));

		bind(AdminPresenter.Display.class).to(Admin.class).in(Singleton.class);
		bind(AdminPresenter.class).in(Singleton.class);
    
    bind(UpdateUsersPresenter.Display.class).to(UpdateUsers.class).in(Singleton.class);
    bind(UpdateUsersPresenter.class).in(Singleton.class);
    bind(UserPresenter.Display.class).to(User.class);

    bind(SelectTicketCalendarPresenter.Display.class).to(SelectTicketCalendar.class);
    bind(SelectTicketCalendarPresenter.class).in(Singleton.class);
    
    bind(WaitingPresenter.Display.class).to(Waiting.class).in(Singleton.class);
		bind(WaitingPresenter.class).asEagerSingleton();
		
		bind(ClickHelper.class).to(ClickHelperImpl.class);
	}

}
