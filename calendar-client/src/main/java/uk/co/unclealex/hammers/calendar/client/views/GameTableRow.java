/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.views;

import java.util.Date;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.HammersMessages;
import uk.co.unclealex.hammers.calendar.shared.model.GameView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
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
public class GameTableRow extends AbstractGameTableRow {

  @UiTemplate("GameTableRow.ui.xml")
	public interface Binder extends UiBinder<Widget, GameTableRow> {
    // No extra method
  }
	
	private static final Binder binder = GWT.create(Binder.class);

	/**
	 * @param gameView
	 * @param hammersMessages
	 */
	@Inject
	public GameTableRow(@Assisted GameView gameView, HammersMessages hammersMessages) {
		super(gameView, hammersMessages);
	}

	@Override
	protected void bind() {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	protected String formatDatePlayed(Date datePlayed) {
		return getHammersMessages().datePlayed(datePlayed);
	}
}
