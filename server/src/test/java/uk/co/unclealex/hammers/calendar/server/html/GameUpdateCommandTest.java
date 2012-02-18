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
 * @author alex
 * 
 */
public class GameUpdateCommandTest {

	static final DateTime dateOf(int day, int month, int year) {
		return new DateTime(year, month, day, 15, 0, 0, 0, DateTimeZone.forID("Europe/London"));
	}

	private static final Competition DEFAULT_COMPETITION = Competition.FACP;
	private static final Location DEFAULT_LOCATION = Location.HOME;
	private static final String DEFAULT_OPPONENTS = "Them";
	private static final int DEFAULT_SEASON = 2012;
	private static final DateTime DEFAULT_DATE_PLAYED = dateOf(5, 9, 1972);
	private static final DateTime DEFAULT_BONDHOLDERS_AVAILABLE = dateOf(5, 9, 1973);
	private static final DateTime DEFAULT_PRIORITY_POINT_POST_AVAILABLE = dateOf(5, 9, 1974);
	private static final DateTime DEFAULT_SEASON_TICKETS_AVAILABLE = dateOf(5, 9, 1975);
	private static final DateTime DEFAULT_ACADEMY_TICKETS_AVAILABLE = dateOf(5, 9, 1976);
	private static final DateTime DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE = dateOf(5, 9, 1977);
	private static final String DEFAULT_RESULT = "1-0";
	private static final Integer DEFAULT_ATTENDENCE = 100000;
	private static final String DEFAULT_MATCH_REPORT = "Good";
	private static final String DEFAULT_TELEVISION_CHANNEL = "BBC";
	private static final boolean DEFAULT_ATTENDED = false;

	/**
	 * Test there is a 1-1 mapping between orderings and game update command
	 * instances
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface GameUpdateCommandTestCase {
	}

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

	abstract class GameUpdateCommandFactory<E> {
		public abstract GameUpdateCommand createGameUpdateCommand(GameLocator gameLocator, E value);

		public GameUpdateCommand createGameUpdateCommand(GameKey gameKey, E value) {
			return createGameUpdateCommand(GameLocator.gameKeyLocator(gameKey), value);
		}
	}

	public <E> void testGameUpdateCommand(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E currentValue, E newValue) {
		testNoChangeForNull(gameUpdateCommandFactory, valueFunction);
		testNullIsHandledCorrectly(gameUpdateCommandFactory, valueFunction, newValue);
		testNoChangeForEqualValue(gameUpdateCommandFactory, valueFunction, currentValue);
		testChangeForDifferentValues(gameUpdateCommandFactory, valueFunction, newValue);
	}

	protected <E> void testNullIsHandledCorrectly(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E newValue) {
		Game game = new Game(1, DEFAULT_COMPETITION, DEFAULT_LOCATION, DEFAULT_OPPONENTS, DEFAULT_SEASON, null, null, null,
				null, null, null, null, null, null, null, false);
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), newValue);
		Assert.assertTrue("A change was not made to a null value when one was expected.", gameUpdateCommand.update(game));
		Assert.assertNotNull("The changed value was null.", valueFunction.apply(game));
	}

	protected <E> void testNoChangeForNull(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), null);
		Assert.assertFalse("A change was made for null when one was not expected.", gameUpdateCommand.update(game));
		Assert.assertNotNull("The changed value was null.", valueFunction.apply(game));
	}

	protected <E> void testNoChangeForEqualValue(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E currentValue) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(),
				currentValue);
		Assert.assertFalse("A change was made for the current value when one was not expected.",
				gameUpdateCommand.update(game));
		Assert.assertEquals("The changed value was incorrect.", currentValue, valueFunction.apply(game));
	}

	protected <E> void testChangeForDifferentValues(GameUpdateCommandFactory<E> gameUpdateCommandFactory,
			Function<Game, E> valueFunction, E newValue) {
		Game game = createFullyPopulatedGame();
		GameUpdateCommand gameUpdateCommand = gameUpdateCommandFactory.createGameUpdateCommand(game.getGameKey(), newValue);
		Assert.assertTrue("A change was not made to the current value when one was expected.",
				gameUpdateCommand.update(game));
		Assert.assertEquals("The changed value was incorrect.", newValue, valueFunction.apply(game));
	}

	protected Game createFullyPopulatedGame() {
		return new Game(1, DEFAULT_COMPETITION, DEFAULT_LOCATION, DEFAULT_OPPONENTS, DEFAULT_SEASON, DEFAULT_DATE_PLAYED,
				DEFAULT_BONDHOLDERS_AVAILABLE, DEFAULT_PRIORITY_POINT_POST_AVAILABLE, DEFAULT_SEASON_TICKETS_AVAILABLE,
				DEFAULT_ACADEMY_TICKETS_AVAILABLE, DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE, DEFAULT_RESULT, DEFAULT_ATTENDENCE,
				DEFAULT_MATCH_REPORT, DEFAULT_TELEVISION_CHANNEL, DEFAULT_ATTENDED);
	}

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
				return game.getDatePlayed();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_DATE_PLAYED,
				DEFAULT_DATE_PLAYED.plusHours(1));
	}

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
				return game.getBondholdersAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_BONDHOLDERS_AVAILABLE,
				DEFAULT_BONDHOLDERS_AVAILABLE.plusHours(1));
	}

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
				return game.getPriorityPointPostAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_PRIORITY_POINT_POST_AVAILABLE,
				DEFAULT_PRIORITY_POINT_POST_AVAILABLE.plusHours(1));
	}

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
				return game.getSeasonTicketsAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_SEASON_TICKETS_AVAILABLE,
				DEFAULT_SEASON_TICKETS_AVAILABLE.plusHours(1));
	}

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
				return game.getAcademyMembersAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_ACADEMY_TICKETS_AVAILABLE,
				DEFAULT_ACADEMY_TICKETS_AVAILABLE.plusHours(1));
	}

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
				return game.getGeneralSaleAvailable();
			}
		};
		testGameUpdateCommand(gameUpdateCommandFactory, valueFunction, DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE,
				DEFAULT_GENERATE_SALE_TICKETS_AVAILABLE.plusHours(1));
	}
}
