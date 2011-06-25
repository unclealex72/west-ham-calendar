/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;

public class SkySportsDecorator extends TelevisionDecorator {

	private static final Logger log = Logger.getLogger(SkySportsDecorator.class);
	
	public static void main(String[] args) throws IOException {
		SkySportsDecorator skySportsDecorator = new SkySportsDecorator();
		URL fixturesUrl = skySportsDecorator.findFixturesUrl();
		Map<Date, String> channelsByGameTime = skySportsDecorator.findChannelsByGameTime(fixturesUrl);
		for (Entry<Date, String> entry : channelsByGameTime.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	protected URL findFixturesUrl() throws IOException {
		URL mainUrl = new URL("http://www.skysports.com/football/");
		BufferedReader reader = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
		URL fixturesUrl = null;
		String line;
		Pattern fixturesUrlPattern = Pattern.compile("<a href='(.+?)'>Live on Sky</a>");
		while (fixturesUrl == null && ((line = reader.readLine()) != null)) {
			Matcher matcher = fixturesUrlPattern.matcher(line);
			if (matcher.find()) {
				fixturesUrl = new URL("http://www.skysports.com" + matcher.group(1));
			}
		}
		reader.close();
		if (fixturesUrl == null) {
			throw new IOException("Could not find the Sky Sports fixtures URL.");
		}
		return fixturesUrl;
	}

	/* (non-Javadoc)
	 * @see uk.co.unclealex.hammers.calendar.html.TelevisionDecorator#findChannelsByGameTime()
	 */
	@Override
	protected Map<Date, String> findChannelsByGameTime(URL fixturesUrl) throws IOException {
		DateFormat format = new SimpleDateFormat("hh.mma dd MMMM yyyy");
		String monthAndYear = null;
		Map<Date, String> channelsByGameTime = new HashMap<Date, String>();
		SkySportsFixture skySportsFixture = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(fixturesUrl.openStream()));
		Pattern monthAndYearPattern = Pattern.compile("<td colspan=\"4\"><strong>(.+)</strong></td>");
		Action dateAction = new MatchAction("<td>(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun) (\\d+)</td>", "date") {
			@Override
			public void execute(SkySportsFixture skySportsFixture, String match) {
				skySportsFixture.setDate(match);
			}
		};
		Action gameAndTimeAction = new MatchAction("<td>.*West Ham.*\\((\\d{1,2}\\.\\d{1,2}(?:a|p)m)\\)</td>", "time") {
			@Override
			public void execute(SkySportsFixture skySportsFixture, String match) {
				skySportsFixture.setTime(match);
			}
		};
		Action knownChannelAction = new FindAction("title=\"(Sky Sports.*?)\"", "known channel") {
			@Override
			public void execute(SkySportsFixture skySportsFixture, String match) {
				skySportsFixture.setChannel(match.replace("Sky Sports ", "SS"));
			}			
		};
		Action unknownChannelAction = new MatchAction("<td>(TBC)</td>", "unknown channel") {
			@Override
			public void execute(SkySportsFixture skySportsFixture, String match) {
				skySportsFixture.setChannel("SS");
			}			
		};
		String line;
		while ((line = reader.readLine()) != null) {
			Matcher monthAndYearMatcher = monthAndYearPattern.matcher(line);
			if (monthAndYearMatcher.matches()) {
				monthAndYear = monthAndYearMatcher.group(1);
				log.debug("Found month and year " + monthAndYear);
				skySportsFixture = new SkySportsFixture(monthAndYear);
			}
			else if (monthAndYear != null) {
				attemptMatch(dateAction, line, skySportsFixture);
				attemptMatch(gameAndTimeAction, line, skySportsFixture);
				boolean channelMatched = 
					attemptMatch(knownChannelAction, line, skySportsFixture) ||
					attemptMatch(unknownChannelAction, line, skySportsFixture);
				if (channelMatched && skySportsFixture.isComplete()) {
					String date = 
						String.format(
								"%s %s %s", skySportsFixture.getTime(), skySportsFixture.getDate(), skySportsFixture.getMonthAndYear());
					try {
						Date gameTime = format.parse(date);
						String channel = skySportsFixture.getChannel();
						channelsByGameTime.put(gameTime, channel);
						log.debug(String.format("Added channel %s for game at %s", gameTime, channel));
					}
					catch (ParseException e) {
						log.warn("Could not parse date " + date, e);
					}
					skySportsFixture = new SkySportsFixture(monthAndYear);
				}
			}
		}
		reader.close();
		return channelsByGameTime;
	}

	/**
	 * @param dateAction
	 * @param line
	 * @param skySportsFixture
	 */
	protected boolean attemptMatch(Action action, String line, SkySportsFixture skySportsFixture) {
		Matcher matcher = action.getPattern().matcher(line);
		if (action.evaluate(matcher)) {
			String match = matcher.group(1);
			log.debug(String.format("Found %s '%s'", action.getName(), match));
			action.execute(skySportsFixture, match);
			return true;
		}
		return false;
	}

	protected abstract class Action implements Predicate<Matcher> {
		private Pattern i_pattern;
		private String i_name;
		
		public Action(String pattern, String name) {
			super();
			i_pattern = Pattern.compile(pattern);
			i_name = name;
		}

		public abstract void execute(SkySportsFixture skySportsFixture, String match);
		
		/**
		 * @return the pattern
		 */
		public Pattern getPattern() {
			return i_pattern;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return i_name;
		}
	}
	
	protected abstract class FindAction extends Action {

		public FindAction(String pattern, String name) {
			super(pattern, name);
		}

		public boolean evaluate(Matcher matcher) {
			return matcher.find();
		}
	}

	protected abstract class MatchAction extends Action {

		public MatchAction(String pattern, String name) {
			super(pattern, name);
		}

		public boolean evaluate(Matcher matcher) {
			return matcher.matches();
		}
	}
}
