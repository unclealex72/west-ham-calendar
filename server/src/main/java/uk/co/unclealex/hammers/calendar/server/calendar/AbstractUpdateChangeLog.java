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

import java.util.Comparator;
import java.util.Objects;

import uk.co.unclealex.hammers.calendar.server.calendar.google.GoogleCalendar;


/**
 * The base class for all {@link UpdateChangeLog}s.
 * 
 * @author alex
 * 
 */
public abstract class AbstractUpdateChangeLog implements UpdateChangeLog {

	/**
	 * The {@link GoogleCalendar} that was changed.
	 */
	private final GoogleCalendar i_googleCalendar;

	/**
	 * Instantiates a new abstract update change log.
	 * 
	 * @param googleCalendar
	 *          the google calendar
	 */
	public AbstractUpdateChangeLog(GoogleCalendar googleCalendar) {
		super();
		i_googleCalendar = googleCalendar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(UpdateChangeLog o) {
		int cmp = getGoogleCalendar().getCalendarTitle().compareTo(o.getGoogleCalendar().getCalendarTitle());
		if (cmp == 0) {
			cmp = createCompareVisitor().compare(this, o);
		}
		return cmp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {
		return obj instanceof UpdateChangeLog && compareTo((UpdateChangeLog) obj) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(getClass(), gameToHashCode(), getGoogleCalendar());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("{Calendar: %s, Game: %s, Action: %s}", getGoogleCalendar(), gameToString(), action());
	}

	/**
	 * Produce a hash code for the field that represents the game that was
	 * changed.
	 * 
	 * @return The game's hash code.
	 */
	protected abstract int gameToHashCode();

	/**
	 * Produce a printable string for the field that represents the game that was
	 * changed.
	 * 
	 * @return The game as a string.
	 */
	protected abstract String gameToString();

	/**
	 * Produce a string that describes what action was taken in i_update.
	 * 
	 * @return The action as a string.
	 */
	protected abstract String action();

	/**
	 * Create a comparator that can be used to compare i_instance to another.
	 * 
	 * @return A comparator that can be used to compare i_instance to another
	 *         {@link UpdateChangeLog}. {@link UpdateChangeLog}.
	 */
	protected abstract UpdateChangeLogComparator createCompareVisitor();

	/**
	 * A comparator that uses an {@link UpdateChangeLogVisitor} to compare different types
	 * of change logs.
	 * @author alex
	 *
	 */
	abstract class UpdateChangeLogComparator implements Comparator<UpdateChangeLog> {

		/**
		 * The result of the comparison.
		 */
		private int cmp;

		/**
		 * Compare i_{@link UpdateChangeLog} to an {@link AddedChangeLog}.
		 * @param addedChangeLog The change log to compare to.
		 * @return {@see Comparable#compareTo(Object)}.
		 */
		protected abstract int compareTo(AddedChangeLog addedChangeLog);

		/**
		 * Compare i_{@link UpdateChangeLog} to an {@link UpdatedChangeLog}.
		 * @param updatedChangeLog The change log to compare to.
		 * @return {@see Comparable#compareTo(Object)}.
		 */
		protected abstract int compareTo(UpdatedChangeLog updatedChangeLog);

		/**
		 * Compare i_{@link UpdateChangeLog} to an {@link RemovedChangeLog}.
		 * @param removedChangeLog The change log to compare to.
		 * @return {@see Comparable#compareTo(Object)}.
		 */
		protected abstract int compareTo(RemovedChangeLog removedChangeLog);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int compare(UpdateChangeLog o1, UpdateChangeLog o2) {
			UpdateChangeLogVisitor.Default visitor = new UpdateChangeLogVisitor.Default() {

				@Override
				public void visit(AddedChangeLog addedChangeLog) {
					cmp = compareTo(addedChangeLog);
				}

				@Override
				public void visit(UpdatedChangeLog updatedChangeLog) {
					cmp = compareTo(updatedChangeLog);
				}

				@Override
				public void visit(RemovedChangeLog removedChangeLog) {
					cmp = compareTo(removedChangeLog);
				}
			};
			o1.accept(visitor);
			return cmp;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final GoogleCalendar getGoogleCalendar() {
		return i_googleCalendar;
	}

}
