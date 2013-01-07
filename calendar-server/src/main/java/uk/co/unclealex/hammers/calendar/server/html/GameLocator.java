/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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

package uk.co.unclealex.hammers.calendar.server.html;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

import uk.co.unclealex.hammers.calendar.server.model.GameKey;


/**
 * A game locator is an abstract class that presents a visitor so services can
 * decide how to locate an existing game.
 * 
 * @author alex
 * 
 */
public abstract class GameLocator implements Comparable<GameLocator> {

	/**
	 * Create a new game locator that locates games by their {@link GameKey}.
	 * @param gameKey The game key to use.
	 * @return A new game locator.
	 */
	public static GameLocator gameKeyLocator(GameKey gameKey) {
		return new GameKeyLocator(gameKey);
	}

	/**
	 * Create a new game locator that locates games by their date played.
	 * @param datePlayed The date the game was played.
	 * @return A new game locator.
	 */
	public static GameLocator datePlayedLocator(DateTime datePlayed) {
		return new DatePlayedLocator(datePlayed);
	}

	/**
	 * A visitor that can be used to make decisions depending on the
	 * type of {@link GameLocator} being presented.
	 * 
	 * @author alex
	 *
	 */
	public abstract static class GameLocatorVisitor {

		/**
		 * Visit an unknown type of {@link GameLocator}.
		 * @param gameLocator The {@link GameLocator} to visit.
		 * @throws IllegalArgumentException This is always thrown.
		 */
		public final void visit(GameLocator gameLocator) throws IllegalArgumentException {
			throw new IllegalArgumentException(gameLocator.getClass() + " is invalid.");
		}

		/**
		 * Visit a {@link GameKeyLocator}.
		 * @param gameKeyLocator The {@link GameKeyLocator} to visit.
		 */
		public abstract void visit(GameKeyLocator gameKeyLocator);

		/**
		 * Visit a {@link DatePlayedLocator}.
		 * @param datePlayedLocator The {@link DatePlayedLocator} to visit.
		 */
		public abstract void visit(DatePlayedLocator datePlayedLocator);
	}

	/**
	 * The base class for both types of game locator.
	 * 
	 * @param <E>
	 *          The type of object that will be used to locate the game.
	 * @author alex
	 */
	@SuppressWarnings("rawtypes")
	abstract static class InternalGameLocator<E extends Comparable> extends GameLocator {
		
		/**
		 * The object used to locate a game.
		 */
		private final E locator;

		/**
		 * Instantiates a new internal game locator.
		 * 
		 * @param locator
		 *          the locator
		 */
		protected InternalGameLocator(E locator) {
			this.locator = locator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(getClass(), getLocator());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return getLocator().toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			return obj instanceof GameLocator && compareTo((GameLocator) obj) == 0;
		}

		/**
		 * Gets the object used to locate a game.
		 * 
		 * @return the locator
		 */
		public E getLocator() {
			return locator;
		}
	}

	/**
	 * A {@link GameLocator} that locates games using a {@link GameKey}.
	 * @author alex
	 *
	 */
	public static class GameKeyLocator extends InternalGameLocator<GameKey> {

		/**
		 * Instantiates a new game key locator.
		 * 
		 * @param gameKey
		 *          the game key
		 */
		protected GameKeyLocator(GameKey gameKey) {
			super(gameKey);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(GameLocator o) {
			class ComparingVisitor extends GameLocatorVisitor {
				int cmp;

				@Override
				public void visit(DatePlayedLocator datePlayedLocator) {
					cmp = -1;
				}

				@Override
				public void visit(GameKeyLocator gameKeyLocator) {
					cmp = getLocator().compareTo(gameKeyLocator.getLocator());
				}
			}
			ComparingVisitor visitor = new ComparingVisitor();
			o.accept(visitor);
			return visitor.cmp;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(GameLocatorVisitor visitor) {
			visitor.visit(this);
		}
	}

	/**
	 * A {@link GameLocator} that locates games using the date they were played.
	 * @author alex
	 *
	 */
	public static class DatePlayedLocator extends InternalGameLocator<DateTime> {

		/**
		 * Instantiates a new date played locator.
		 * 
		 * @param datePlayed
		 *          the date played
		 */
		protected DatePlayedLocator(DateTime datePlayed) {
			super(datePlayed);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(GameLocator o) {
			class ComparingVisitor extends GameLocatorVisitor {
				int cmp;

				@Override
				public void visit(DatePlayedLocator datePlayedLocator) {
					cmp = getLocator().compareTo(datePlayedLocator.getLocator());
				}

				@Override
				public void visit(GameKeyLocator gameKeyLocator) {
					cmp = 1;
				}
			}
			ComparingVisitor visitor = new ComparingVisitor();
			o.accept(visitor);
			return visitor.cmp;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(GameLocatorVisitor visitor) {
			visitor.visit(this);
		}
	}

	/**
	 * Accept a {@link GameLocatorVisitor}.
	 * @param visitor The {@link GameLocatorVisitor} to accept.
	 */
	public abstract void accept(GameLocatorVisitor visitor);

}
