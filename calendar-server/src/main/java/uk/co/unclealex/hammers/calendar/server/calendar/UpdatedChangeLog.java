/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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
 */

package uk.co.unclealex.hammers.calendar.server.calendar;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;
import uk.co.unclealex.hammers.calendar.server.model.Game;



/**
 * An {@link UpdateChangeLog} that indicates a game was updared.
 * @author alex
 *
 */
public class UpdatedChangeLog extends AbstractGameUpdateChangeLog {

	
	/**
	 * Create an updated change log.
	 * @param googleCalendar The calendar that was changed.
	 * @param game The game that was changed.
	 */
	public UpdatedChangeLog(GoogleCalendar googleCalendar, Game game) {
		super(googleCalendar, game);
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
	protected String action() {
		return "Updated";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UpdateChangeLogComparator createCompareVisitor() {
		return new UpdateChangeLogComparator() {
			
			
			/**
			 * Compare two {@link UpdatedChangeLog}s by comparing games.
			 * @return The result of comparing both games.
			 */
			@Override
			protected int compareTo(UpdatedChangeLog updatedChangeLog) {
				return getGame().compareTo(updatedChangeLog.getGame());
			}
			
			/**
			 * {@link UpdatedChangeLog}s are always smaller than {@link AddedChangeLog}s.
			 * @return -1
			 */
			@Override
			protected int compareTo(AddedChangeLog addedChangeLog) {
				return -1;
			}

			/**
			 * {@link UpdatedChangeLog}s are always larger than {@link RemovedChangeLog}s.
			 * @return 1
			 */
			@Override
			protected int compareTo(RemovedChangeLog removedChangeLog) {
				return 1;
			}
		};
	}

}
