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

package uk.co.unclealex.hammers.calendar.server.calendar;

/**
 * A {@link GameUpdateInformation} bean that can be used to say whether a game was updated or not.
 * @author alex
 *
 */
public class GameWasCreatedInformation implements GameUpdateInformation {

	/**
	 * The Google Calendar event id of the game that was created.
	 */
	private final String i_eventId;
	
	/**
	 * Create a new instance of this class.
	 * @param eventId The id of the created event.
	 */
	public GameWasCreatedInformation(String eventId) {
		super();
		i_eventId = eventId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(GameUpdateInformationVisitor visitor) {
		visitor.visit(this);
	}

	public String getEventId() {
		return i_eventId;
	}
}
