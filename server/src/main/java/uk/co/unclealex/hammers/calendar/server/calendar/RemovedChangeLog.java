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

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;

/**
 * An {@link UpdateChangeLog} that indicates a game was removed.
 * @author alex
 *
 */
public class RemovedChangeLog extends AbstractIdUpdateChangeLog {

	/**
	 * Create an removed change log.
	 * @param googleCalendar The calendar that was changed.
	 * @param gameId The id of the game that was changed.
	 */
	public RemovedChangeLog(GoogleCalendar googleCalendar, String gameId) {
		super(googleCalendar, gameId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String action() {
		return "Removed";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(UpdateChangeLogVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UpdateChangeLogComparator createCompareVisitor() {
		return new UpdateChangeLogComparator() {
			
			/**
			 * Compare this {@link RemovedChangeLog} to another by comparing game ids.
			 * @return The result of comparing the two game ids.
			 */
			@Override
			protected int compareTo(RemovedChangeLog removedChangeLog) {
				return getGameId().compareTo(getGameId());
			}
			
			/**
			 * {@link RemovedChangeLog}s are always smaller than {@link UpdatedChangeLog}s.
			 * @return -1
			 */
			@Override
			protected int compareTo(UpdatedChangeLog updatedChangeLog) {
				return -1;
			}
			
			/**
			 * {@link RemovedChangeLog}s are always smaller than {@link AddedChangeLog}s.
			 * @return -1
			 */
			@Override
			protected int compareTo(AddedChangeLog addedChangeLog) {
				return -1;
			}
		};
	}
}
