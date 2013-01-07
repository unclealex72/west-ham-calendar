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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.html.GameUpdateCommand.Type;
import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.server.model.GameKey;
import uk.co.unclealex.hammers.calendar.shared.model.Competition;
import uk.co.unclealex.hammers.calendar.shared.model.Location;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * The Class GameUpdateCommandTest.
 * 
 * @author alex
 */
public class GameUpdateCommandTest {

	/**
	 * Date of.
	 * 
	 * @param day
	 *          the day
	 * @param month
	 *          the month
	 * @param year
	 *          the year
	 * @return the date time
	 */
	static final DateTime dateOf(int day, int month, int year) {
		return new DateTime(year, month, day, 15, 0, 0, 0, DateTimeZone.forID("Europe/London"));
	}

	/** The Constant DEFAULT_COMPETITION. */
	private static final Competition DEFAULT_COMPETITION = Competition.FACP;
	
	/** The Constant DEFAULT_LOCATION. */
	private static final Location DEFAULT_LOCATION = Location.HOME;
	
	/** The Constant DEFAULT_OPPONENTS. */
	private static final String DEFAULT_OPPONENTS = "Them";
	
	/** The Constant DEFAULT_SEASON. */
	private static final int DEFAULT_SEASON = 2012;
	
	/** The Constant DEFAULT_DATE_PLAYED. */
	private static final DateTime DEFAULT_DATE_PLAYED = dateOf(5, 9, 1972);
	
	/** The Constant DEFAULT_BONDHOLDERS_AVAILABLE. */
	private static final DateTime DEFAULT_BONDHOLDERS_AVAILABLE = dateOf(5, 9, 1973);
	
	/** The Constant DEFAULT_PRIORITY_POINT_POST_AVAILABLE. */
	private static final DateTime DEFAULT_PRIORITY_POINT_POST_AVAILABLE = dateOf(5, 9, 1974);
	
	/** The Constant DEFAULT_SEASON_TICKETS_AVAILABLE. */
	private static final DateTime DEFAULT_SEASON_TICKETS_AVAILABLE = dateOf(5, 9, 1975);
	
	/** The Constant DEFAULT_ACADEMY_TICKETS_AVAILABLE. */
	private static final DateTime DEFAULT_ACADEMY_TICKETS_AVAILABLE = dateOf(5, 9, 1976);
	
	/** The Constant DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE. */
	private static final DateTime DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE = dateOf(5, 9, 1977);
	
	/** The Constant DEFAULT_RESULT. */
	private static final String DEFAULT_RESULT = "1-0";
	
	/** The Constant DEFAULT_ATTENDENCE. */
	private static final Integer DEFAULT_ATTENDENCE = 100000;
	
	/** The Constant DEFAULT_MATCH_REPORT. */
	private static final String DEFAULT_MATCH_REPORT = "Good";
	
	/** The Constant DEFAULT_TELEVISION_CHANNEL. */
	private static final String DEFAULT_TELEVISION_CHANNEL = "BBC";
	
	/** The Constant DEFAULT_ATTENDED. */
	private static final boolean DEFAULT_ATTENDED = false;

	/**
	 * Test there is a 1-1 mapping between orderings and game update command
	 * instances.
	 */
	@Test
	public void testOrderingAndInstancesAreBijective() {
		Predicate<Method> isCreateGameUpdateCommandMethodPredicate = new Predicate<Method>() {
			@Override
			public boolean apply(Method method) {
				return GameUpdateCommand.class.equals(method.getReturnType());
			}
		};
		Function<Method, GameUpdateCommand> factory = new Function<Method, GameUpdateCommand>() {
			@Override
			public GameUpdateCommand apply(Method method) {
				try {
					return (GameUpdateCommand) method.invoke(null,
							GameLocator.gameKeyLocator(new GameKey(Competition.FACP, Location.HOME, "Them", 2012)), null);
				}
				catch (Throwable t) {
					Assert.fail("Could not invoke method " + method.getName());
					return null;
				}
			}
		};
		List<GameUpdateCommand> gameUpdateCommands = Lists
				.newArrayList(Iterables.transform(Iterables.filter(Arrays.asList(GameUpdateCommand.class.getMethods()),
						isCreateGameUpdateCommandMethodPredicate), factory));
		Function<GameUpdateCommand, GameUpdateCommand.Type> typeFunction = new Function<GameUpdateCommand, GameUpdateCommand.Type>() {
			@Override
			public Type apply(GameUpdateCommand gameUpdateCommand) {
				return gameUpdateCommand.getType();
			}
		};
		List<GameUpdateCommand.Type> missingTypes = Lists.newArrayList(GameUpdateCommand.Type.values());
		missingTypes.removeAll(Lists.newArrayList(Iterables.transform(gameUpdateCommands, typeFunction)));
		Assert.assertTrue("The following orderings are not covered: " + Joiner.on(", ").join(missingTypes),
				missingTypes.isEmpty());
		Assert.assertEquals("The wrong number of game update commands were found.", GameUpdateCommand.Type.values().length,
				gameUpdateCommands.size());
	}

	/**
	 * The Interface GameUpdateCommandTestCase.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface GameUpdateCommandTestCase {
	}

	/**
	 * Test all game update commands are tested.
	 */
	@Test
	public void testAllGameUpdateCommandsAreTested() {
		Predicate<Method> isGameUpdateCommandTestCasePredicate = new Predicate<Method>() {
			@Override
			public boolean apply(Method method) {
				return method.getAnnotation(Test.class) != null
						&& method.getAnnotation(GameUpdateCommandTestCase.class) != null;
			}
		};
		Assert.assertEquals("The wrong number of game update command test cases were found.",
				GameUpdateCommand.Type.values().length,
				Iterables.size(Iterables.filter(Arrays.asList(getClass().getMethods()), isGameUpdateCommandTestCasePredicate)));
	}

	/**
	 * A factory for creating GameUpdateCommand objects.
	 * 
	 * @param <E>
	 *          the element type
	 */
	abstract class GameUpdateCommandFactory<E> {
		
		/**
		 * Creates a new GameUpdateCommand object.
		 * 
		 * @param gameLocator
		 *          the game locator
		 * @param value
		 *          the value
		 * @return the game update command
		 */
		public abstract GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, E value);

		/**
		 * Creates a new GameUpdateCommand object.
		 * 
		 * @param gameKey
		 *          the game key
		 * @param value
		 *          the value
		 * @return the game update command
		 */
		public GameUpdateCommand createGameUpdateCommand(GameKey gameKey, E value) {
			return createGameUpdateCommand(GameLocator.gameKeyLocator(gameKey), value);
		}
	}

	/**
	 * Test game update command.
	 * 
	 * @param <E>
	 *          the element type
	 * @param gameUpdateCommandFactory
	 *          the game update command factory
	 * @param valueFunction
	 *          the value function
	 * @param currentValue
	 *          the current value
	 * @param newValue
	 *          the new value
	 */
	public <E> void testGameUpdateCommand(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E currentValue, E newValue) {
		testNoChangeForNull(gameUpdateCommandFactory, valueFunction);
		testNullIsHandledCorrectly(gameUpdateCommandFactory, valueFunction, newValue);
		testNoChangeForEqualValue(gameUpdateCommandFactory, valueFunction, currentValue);
		testChangeForDifferentValues(gameUpdateCommandFactory, valueFunction, newValue);
	}

	/**
	 * Test null is handled correctly.
	 * 
	 * @param <E>
	 *          the element type
	 * @param gameUpdateCommandFactory
	 *          the game update command factory
	 * @param valueFunction
	 *          the value function
	 * @param newValue
	 *          the new value
	 */
	protected <E> void testNullIsHandledCorrectly(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E newValue) {
		Game game = new Game(1, DEFAULT_COMPETITION, DEFAULT_LOCATION, DEFAULT_OPPONENTS, DEFAULT_SEASON, null, null, null,
				null, null, null, null, null, null, null, false);
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), newValue);
		Assert.assertTrue("A change was not made to a null value when one was expected.", gameUpdateCommand.update(game));
		Assert.assertNotNull("The changed value was null.", valueFunction.apply(game));
	}

	/**
	 * Test no change for null.
	 * 
	 * @param <E>
	 *          the element type
	 * @param gameUpdateCommandFactory
	 *          the game update command factory
	 * @param valueFunction
	 *          the value function
	 */
	protected <E> void testNoChangeForNull(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), null);
		Assert.assertFalse("A change was made for null when one was not expected.", gameUpdateCommand.update(game));
		Assert.assertNotNull("The changed value was null.", valueFunction.apply(game));
	}

	/**
	 * Test no change for equal value.
	 * 
	 * @param <E>
	 *          the element type
	 * @param gameUpdateCommandFactory
	 *          the game update command factory
	 * @param valueFunction
	 *          the value function
	 * @param currentValue
	 *          the current value
	 */
	protected <E> void testNoChangeForEqualValue(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E currentValue) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(),
				currentValue);
		Assert.assertFalse("A change was made for the current value when one was not expected.",
				gameUpdateCommand.update(game));
		Assert.assertEquals("The changed value was incorrect.", currentValue, valueFunction.apply(game));
	}

	/**
	 * Test change for different values.
	 * 
	 * @param <E>
	 *          the element type
	 * @param gameUpdateCommandFactory
	 *          the game update command factory
	 * @param valueFunction
	 *          the value function
	 * @param newValue
	 *          the new value
	 */
	protected <E> void testChangeForDifferentValues(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E newValue) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), newValue);
		Assert.assertTrue("A change was not made to the current value when one was expected.",
				gameUpdateCommand.update(game));
		Assert.assertEquals("The changed value was incorrect.", newValue, valueFunction.apply(game));
	}

	/**
	 * Creates the fully populated game.
	 * 
	 * @return the game
	 */
	protected Game createFullyPopulatedGame() {
		return new Game(1, DEFAULT_COMPETITION, DEFAULT_LOCATION, DEFAULT_OPPONENTS, DEFAULT_SEASON, DEFAULT_DATE_PLAYED,
				DEFAULT_BONDHOLDERS_AVAILABLE, DEFAULT_PRIORITY_POINT_POST_AVAILABLE, DEFAULT_SEASON_TICKETS_AVAILABLE,
				DEFAULT_ACADEMY_TICKETS_AVAILABLE, DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE, DEFAULT_RESULT, DEFAULT_ATTENDENCE,
				DEFAULT_MATCH_REPORT, DEFAULT_TELEVISION_CHANNEL, DEFAULT_ATTENDED);
	}

	/**
	 * Test date played.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testDatePlayed() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDatePlayed) {
				return GameUpdateCommand.datePlayed(gameLocator, newDatePlayed);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimePlayed();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_DATE_PLAYED,
				DEFAULT_DATE_PLAYED.plusHours(1));
	}

	/**
	 * Test result.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testResult() {
		GameUpdateCommandFactory<String> gameUpdateCommandFactory = new GameUpdateCommandFactory<String>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, String newResult) {
				return GameUpdateCommand.result(gameLocator, newResult);
			}
		};
		Function<Game, String> valueFunction = new Function<Game, String>() {
			@Override
			public String apply(Game game) {
				return game.getResult();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_RESULT, "1" + DEFAULT_RESULT);
	}

	/**
	 * Test attendence.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testAttendence() {
		GameUpdateCommandFactory<Integer> gameUpdateCommandFactory = new GameUpdateCommandFactory<Integer>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, Integer newAttendence) {
				return GameUpdateCommand.attendence(gameLocator, newAttendence);
			}
		};
		Function<Game, Integer> valueFunction = new Function<Game, Integer>() {
			@Override
			public Integer apply(Game game) {
				return game.getAttendence();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_ATTENDENCE, DEFAULT_ATTENDENCE * 2);
	}

	/**
	 * Test match report.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testMatchReport() {
		GameUpdateCommandFactory<String> gameUpdateCommandFactory = new GameUpdateCommandFactory<String>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, String newMatchReport) {
				return GameUpdateCommand.matchReport(gameLocator, newMatchReport);
			}
		};
		Function<Game, String> valueFunction = new Function<Game, String>() {
			@Override
			public String apply(Game game) {
				return game.getMatchReport();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_MATCH_REPORT, DEFAULT_MATCH_REPORT + "!");
	}

	/**
	 * Test television channel.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testTelevisionChannel() {
		GameUpdateCommandFactory<String> gameUpdateCommandFactory = new GameUpdateCommandFactory<String>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, String newTelevisionChannel) {
				return GameUpdateCommand.televisionChannel(gameLocator, newTelevisionChannel);
			}
		};
		Function<Game, String> valueFunction = new Function<Game, String>() {
			@Override
			public String apply(Game game) {
				return game.getTelevisionChannel();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_TELEVISION_CHANNEL,
				DEFAULT_TELEVISION_CHANNEL + "!");
	}

	/**
	 * Test attended.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testAttended() {
		GameUpdateCommandFactory<Boolean> gameUpdateCommandFactory = new GameUpdateCommandFactory<Boolean>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, Boolean newAttended) {
				return GameUpdateCommand.attended(gameLocator, newAttended);
			}
		};
		Function<Game, Boolean> valueFunction = new Function<Game, Boolean>() {
			@Override
			public Boolean apply(Game game) {
				return game.isAttended();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_ATTENDED, !DEFAULT_ATTENDED);
	}

	/**
	 * Test bond holder.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testBondHolder() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDateTime) {
				return GameUpdateCommand.bondHolderTickets(gameLocator, newDateTime);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimeBondholdersAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_BONDHOLDERS_AVAILABLE,
				DEFAULT_BONDHOLDERS_AVAILABLE.plusHours(1));
	}

	/**
	 * Test priority tickets.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testPriorityTickets() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDateTime) {
				return GameUpdateCommand.priorityPointTickets(gameLocator, newDateTime);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimePriorityPointPostAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_PRIORITY_POINT_POST_AVAILABLE,
				DEFAULT_PRIORITY_POINT_POST_AVAILABLE.plusHours(1));
	}

	/**
	 * Test season tickets.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testSeasonTickets() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDateTime) {
				return GameUpdateCommand.seasonTickets(gameLocator, newDateTime);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimeSeasonTicketsAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_SEASON_TICKETS_AVAILABLE,
				DEFAULT_SEASON_TICKETS_AVAILABLE.plusHours(1));
	}

	/**
	 * Test academy tickets.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testAcademyTickets() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDateTime) {
				return GameUpdateCommand.academyTickets(gameLocator, newDateTime);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimeAcademyMembersAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_ACADEMY_TICKETS_AVAILABLE,
				DEFAULT_ACADEMY_TICKETS_AVAILABLE.plusHours(1));
	}

	/**
	 * Test general sale tickets.
	 */
	@Test
	@GameUpdateCommandTestCase
	public void testGeneralSaleTickets() {
		GameUpdateCommandFactory<DateTime> gameUpdateCommandFactory = new GameUpdateCommandFactory<DateTime>() {
			@Override
			public GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, DateTime newDateTime) {
				return GameUpdateCommand.generalSaleTickets(gameLocator, newDateTime);
			}
		};
		Function<Game, DateTime> valueFunction = new Function<Game, DateTime>() {
			@Override
			public DateTime apply(Game game) {
				return game.getDateTimeGeneralSaleAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE,
				DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE.plusHours(1));
	}
}
