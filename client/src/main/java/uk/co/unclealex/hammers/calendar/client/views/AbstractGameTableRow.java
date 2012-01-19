/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import java.util.Date;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.shared.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

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
public abstract class AbstractGameTableRow extends TableRow implements HasValue<Boolean>, HasEnabled {

	@UiField Label date;
	@UiField Label location;
	@UiField Label competition;
	@UiField Label opponents;
	@UiField Label result;
	@UiField Label televisionChannel;
	@UiField Label ticketsAvailable;
	@UiField CheckBox attended;

	private final HammersMessages i_hammersMessages;
	
	public AbstractGameTableRow(Game game, HammersMessages hammersMessages) {
		i_hammersMessages = hammersMessages;
		bind();
		boolean visible = false;
		boolean enabled = false;
		if (game != null) {
			date.setText(formatDatePlayed(game.getDatePlayed()));
			location.setText(Location.HOME == game.getLocation()?hammersMessages.home():hammersMessages.away());
			competition.setText(game.getCompetition().getName());
			opponents.setText(game.getOpponents());
			result.setText(game.getResult());
			televisionChannel.setText(game.getTelevisionChannel());
			Date dateTicketsAvailable = game.getTicketsAvailable();
			String tickets;
			if (dateTicketsAvailable != null) {
				tickets = hammersMessages.ticketsAvailable(dateTicketsAvailable);
			}
			else {
				tickets = "";
			}
			ticketsAvailable.setText(tickets);
			attended.setValue(game.isAttended());
			visible = true;
			enabled = game.isEnabled();
			if (game.isNonStandardWeekendGame()) {
				setStylePrimaryName("weekend");
			}
			else if (game.isWeekGame()) {
				setStylePrimaryName("week");
			}
		}
		attended.setVisible(visible);
		attended.setEnabled(enabled);
	}

	@Override
	public Boolean getValue() {
		return attended.getValue();
	}

	@Override
	public void setValue(Boolean value) {
		attended.setValue(value);
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		attended.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return attended.addValueChangeHandler(handler);
	}

	@Override
	public boolean isEnabled() {
		return attended.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		attended.setEnabled(enabled);
	}

	protected abstract void bind();

	protected abstract String formatDatePlayed(Date datePlayed);

	public HammersMessages getHammersMessages() {
		return i_hammersMessages;
	}

}
