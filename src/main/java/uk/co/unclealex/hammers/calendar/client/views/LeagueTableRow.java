/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.shared.model.LeagueRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
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
public class LeagueTableRow extends TableRow {

  @UiTemplate("LeagueTableRow.ui.xml")
	public interface Binder extends UiBinder<Widget, LeagueTableRow> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField HasText opponents;
	@UiField HasText played;
	@UiField HasText won;
	@UiField HasText drawn;
	@UiField HasText lost;
	@UiField HasText goalsFor;
	@UiField HasText goalsAgainst;
	@UiField HasText points;

	/**
	 * @param game
	 * @param hammersMessages
	 */
	@Inject
	public LeagueTableRow(@Assisted LeagueRow leagueRow, HammersMessages hammersMessages) {
		initWidget(binder.createAndBindUi(this));
		if (leagueRow != null) {
	  	opponents.setText(leagueRow.getTeam());
	  	setInteger(played, leagueRow.getPlayed());
	  	setInteger(won, leagueRow.getWon());
	  	setInteger(drawn, leagueRow.getWon());
	  	setInteger(lost, leagueRow.getLost());
	  	setInteger(goalsFor, leagueRow.getFor());
	  	setInteger(goalsAgainst, leagueRow.getAgainst());
	  	setInteger(points, leagueRow.getPoints());
		}
	}

	protected void setInteger(HasText label, int value) {
		label.setText(Integer.toString(value));
	}


}
