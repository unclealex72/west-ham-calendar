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

package uk.co.unclealex.hammers.calendar.server.html;

import org.joda.time.DateTime;

import uk.co.unclealex.hammers.calendar.server.model.Game;

/**
 * 
 * A class used to encapsulate a possible update to a game as directed by an
 * {@link HtmlGamesScanner}.
 * 
 * @author alex
 * 
 */
public abstract class GameUpdateCommand implements Comparable<GameUpdateCommand> {

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's date played value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newDatePlayed
	 *          The new date played value.
	 * @return A {@link GameUpdateCommand} that updates a game's date played
	 *         value.
	 */
	public static GameUpdateCommand datePlayed(GameLocator gameLocator, DateTime newDatePlayed) {
		return new InternalGameUpdateCommand<DateTime>(Type.DATE_PLAYED, gameLocator, newDatePlayed) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getDatePlayed();
			}

			@Override
			protected void setNewValue(Game game, DateTime newDatePlayed) {
				game.setDatePlayed(newDatePlayed);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's result value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newResult
	 *          The new result.
	 * @return A {@link GameUpdateCommand} that updates a game's result value.
	 */
	public static GameUpdateCommand result(GameLocator gameLocator, String newResult) {
		return new InternalGameUpdateCommand<String>(Type.RESULT, gameLocator, newResult) {
			@Override
			protected String getCurrentValue(Game game) {
				return game.getResult();
			}

			@Override
			protected void setNewValue(Game game, String newResult) {
				game.setResult(newResult);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's attendence value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newAttendence
	 *          The new attendence.
	 * @return A {@link GameUpdateCommand} that updates a game's attendence value.
	 */
	public static GameUpdateCommand attendence(GameLocator gameLocator, Integer newAttendence) {
		return new InternalGameUpdateCommand<Integer>(Type.ATTENDENCE, gameLocator, newAttendence) {
			@Override
			protected Integer getCurrentValue(Game game) {
				return game.getAttendence();
			}

			@Override
			protected void setNewValue(Game game, Integer newAttendence) {
				game.setAttendence(newAttendence);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's match report
	 * value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newMatchReport
	 *          The new match report.
	 * @return A {@link GameUpdateCommand} that updates a game's match report
	 *         value.
	 */
	public static GameUpdateCommand matchReport(GameLocator gameLocator, String newMatchReport) {
		return new InternalGameUpdateCommand<String>(Type.MATCH_REPORT, gameLocator, newMatchReport) {
			@Override
			protected String getCurrentValue(Game game) {
				return game.getMatchReport();
			}

			@Override
			protected void setNewValue(Game game, String newMatchReport) {
				game.setMatchReport(newMatchReport);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's television channel
	 * value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newTelevisionChannel
	 *          The new television channel.
	 * @return A {@link GameUpdateCommand} that updates a game's television channel
	 *         value.
	 */
	public static GameUpdateCommand televisionChannel(GameLocator gameLocator, String newTelevisionChannel) {
		return new InternalGameUpdateCommand<String>(Type.TELEVISION_CHANNEL, gameLocator, newTelevisionChannel) {
			@Override
			protected String getCurrentValue(Game game) {
				return game.getTelevisionChannel();
			}

			@Override
			protected void setNewValue(Game game, String newTelevisionChannel) {
				game.setTelevisionChannel(newTelevisionChannel);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's attended value.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newAttended
	 *          The new attended value.
	 * @return A {@link GameUpdateCommand} that updates a game's attended value.
	 */
	public static GameUpdateCommand attended(GameLocator gameLocator, Boolean newAttended) {
		return new InternalGameUpdateCommand<Boolean>(Type.ATTENDED, gameLocator, newAttended) {
			@Override
			protected Boolean getCurrentValue(Game game) {
				return game.isAttended();
			}

			@Override
			protected void setNewValue(Game game, Boolean newAttended) {
				game.setAttended(newAttended);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's bondholder tickets availability date.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newBondHolderTicketsAvailable
	 *          The new date for bond holder ticket availabilty.
	 * @return A {@link GameUpdateCommand} that updates a game's bondholder availability tickets date.
	 */
	public static GameUpdateCommand bondHolderTickets(GameLocator gameLocator, DateTime newBondHolderTicketsAvailable) {
		return new InternalGameUpdateCommand<DateTime>(Type.BONDHOLDER_TICKETS, gameLocator, newBondHolderTicketsAvailable) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getBondholdersAvailable();
			}

			@Override
			protected void setNewValue(Game game, DateTime newBondHolderTicketsAvailable) {
				game.setBondholdersAvailable(newBondHolderTicketsAvailable);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's priority points tickets availability date.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newPriorityPointTicketsAvailable
	 *          The new date for priority point ticket availabilty.
	 * @return A {@link GameUpdateCommand} that updates a game's priority points tickets availability date.
	 */
	public static GameUpdateCommand priorityPointTickets(GameLocator gameLocator, DateTime newPriorityPointTicketsAvailable) {
		return new InternalGameUpdateCommand<DateTime>(Type.PRIORITY_POINT_POST_TICKETS, gameLocator, newPriorityPointTicketsAvailable) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getPriorityPointPostAvailable();
			}

			@Override
			protected void setNewValue(Game game, DateTime newPriorityPointTicketsAvailable) {
				game.setPriorityPointPostAvailable(newPriorityPointTicketsAvailable);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's season tickets availability date.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newSeasonTicketsAvailable
	 *          The new date for season ticket availabilty.
	 * @return A {@link GameUpdateCommand} that updates a game's season ticket availability date.
	 */
	public static GameUpdateCommand seasonTickets(GameLocator gameLocator, DateTime newSeasonTicketsAvailable) {
		return new InternalGameUpdateCommand<DateTime>(Type.SEASON_TICKETS, gameLocator, newSeasonTicketsAvailable) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getSeasonTicketsAvailable();
			}

			@Override
			protected void setNewValue(Game game, DateTime newSeasonTicketsAvailable) {
				game.setSeasonTicketsAvailable(newSeasonTicketsAvailable);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's academy tickets availability date.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newAcademyTicketsAvailable
	 *          The new date for academy ticket availabilty.
	 * @return A {@link GameUpdateCommand} that updates a game's academy ticket availability date.
	 */
	public static GameUpdateCommand academyTickets(GameLocator gameLocator, DateTime newAcademyTicketsAvailable) {
		return new InternalGameUpdateCommand<DateTime>(Type.ACADEMY_TICKETS, gameLocator, newAcademyTicketsAvailable) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getAcademyMembersAvailable();
			}

			@Override
			protected void setNewValue(Game game, DateTime newAcademyAvailable) {
				game.setAcademyMembersAvailable(newAcademyAvailable);
			}
		};
	}

	/**
	 * Create a {@link GameUpdateCommand} that updates a game's general sale tickets availability date.
	 * 
	 * @param gameLocator
	 *          The locator to use to locate the game.
	 * @param newGeneralSaleTicketsAvailable
	 *          The new date for general sale ticket availabilty.
	 * @return A {@link GameUpdateCommand} that updates a game's general sale ticket availability date.
	 */
	public static GameUpdateCommand generalSaleTickets(GameLocator gameLocator, DateTime newGeneralSaleTicketsAvailable) {
		return new InternalGameUpdateCommand<DateTime>(Type.GENERAL_SALE_TICKETS, gameLocator, newGeneralSaleTicketsAvailable) {
			@Override
			protected DateTime getCurrentValue(Game game) {
				return game.getGeneralSaleAvailable();
			}

			@Override
			protected void setNewValue(Game game, DateTime newAcademyAvailable) {
				game.setGeneralSaleAvailable(newAcademyAvailable);
			}
		};
	}

	/**
	 * An enumeration to allow the difference {@link GameUpdateCommand}s to be ordered in a sorted set.
	 * @author alex
	 *
	 */
	static enum Type {
		DATE_PLAYED, RESULT, ATTENDENCE, MATCH_REPORT, TELEVISION_CHANNEL, ATTENDED, 
		BONDHOLDER_TICKETS, PRIORITY_POINT_POST_TICKETS, SEASON_TICKETS, ACADEMY_TICKETS, GENERAL_SALE_TICKETS
	}

	private final GameLocator i_gameLocator;
	private final Type i_type;

	protected GameUpdateCommand(Type type, GameLocator gameLocator) {
		super();
		i_gameLocator = gameLocator;
		i_type = type;
	}

	/**
	 * Update a game. No check is made to see if the correct game is being updated.
	 * @param game The game to update.
	 * @return True if the game was updated, false otherwise.
	 */
	public abstract boolean update(Game game);
	
	@Override
	public int compareTo(GameUpdateCommand o) {
		int cmp = getGameLocator().compareTo(o.getGameLocator());
		if (cmp == 0) {
			cmp = getType().compareTo(o.getType());
		}
		return cmp;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GameUpdateCommand && compareTo((GameUpdateCommand) obj) == 0;
	}
	
	/**
	 * A subclass of {@link GameUpdateCommand} that actually attempts to update a game.
	 * @author alex
	 *
	 * @param <V> The type of the value that may be updated.
	 */
	protected static abstract class InternalGameUpdateCommand<V> extends GameUpdateCommand {

		private final V i_newValue;

		protected InternalGameUpdateCommand(Type type, GameLocator gameLocator, V newValue) {
			super(type, gameLocator);
			i_newValue = newValue;
		}

		@Override
		public String toString() {
			return String.format("{%s: %s <- %s}", getGameLocator(), getType(), getNewValue());
		}
		
		public boolean update(Game game) {
			if (getNewValue() == null || getNewValue().equals(getCurrentValue(game))) {
				return false;
			}
			setNewValue(game, getNewValue());
			return true;
		}

		/**
		 * @param game The game to check.
		 * @return The current value of the current game.
		 */
		protected abstract V getCurrentValue(Game game);

		/**
		 * Alter a game.
		 * @param game The game to alter.
		 * @param newValue The new value to assign to the game.
		 */
		protected abstract void setNewValue(Game game, V newValue);

		/**
		 * @return the newValue
		 */
		public V getNewValue() {
			return i_newValue;
		}
	}

	/**
	 * @return The required {@link GameLocator} of any game to change.
	 */
	public GameLocator getGameLocator() {
		return i_gameLocator;
	}

	/**
	 * @return The ordering of this {@link GameUpdateCommand}.
	 */
	protected Type getType() {
		return i_type;
	}
}
