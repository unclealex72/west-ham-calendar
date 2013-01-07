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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

import uk.co.unclealex.hammers.calendar.server.dates.DateServiceImpl;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;


/**
 * The Class SeasonHtmlGamesScannerTest.
 * 
 * @author alex
 */
public class SeasonHtmlGamesScannerTest {

	/**
	 * Test season.
	 * 
	 * @param season
	 *          the season
	 * @param expectedUpdates
	 *          the expected updates
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	public void testSeason(int season, String[] expectedUpdates) throws IOException, URISyntaxException {
		SeasonHtmlGamesScanner seasonHtmlGamesScanner = new SeasonHtmlGamesScanner();
		seasonHtmlGamesScanner.setHtmlPageLoader(new HtmlPageLoaderImpl());
		seasonHtmlGamesScanner.setDateService(new DateServiceImpl());
		SortedSet<GameUpdateCommand> gameUpdateCommands = seasonHtmlGamesScanner.scan(getClass().getClassLoader()
				.getResource("html/fixtures-" + season + ".html").toURI());
		String[] actualUpdates = Iterables.toArray(Iterables.transform(gameUpdateCommands, Functions.toStringFunction()),
				String.class);
		Assert.assertArrayEquals("The wrong updates were returned for season " + season, expectedUpdates, actualUpdates);
	}

	/**
	 * Test2007.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void test2007() throws IOException, URISyntaxException {
		String[] expectedUpdates = new String[] { "{2007 PREM Arsenal HOME: DATE_PLAYED <- 2007-09-29T15:00:00.000+01:00}",
				"{2007 PREM Arsenal HOME: RESULT <- 0-1}", "{2007 PREM Arsenal HOME: ATTENDENCE <- 34966}",
				"{2007 PREM Arsenal HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37988,00.html}",
				"{2007 PREM Arsenal AWAY: DATE_PLAYED <- 2008-01-01T15:00:00.000Z}", "{2007 PREM Arsenal AWAY: RESULT <- 0-2}",
				"{2007 PREM Arsenal AWAY: ATTENDENCE <- 60102}",
				"{2007 PREM Arsenal AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38839,00.html}",
				"{2007 PREM Aston Villa HOME: DATE_PLAYED <- 2008-05-11T15:00:00.000+01:00}",
				"{2007 PREM Aston Villa HOME: RESULT <- 2-2}", "{2007 PREM Aston Villa HOME: ATTENDENCE <- 34969}",
				"{2007 PREM Aston Villa HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~36759,00.html}",
				"{2007 PREM Aston Villa AWAY: DATE_PLAYED <- 2007-10-06T15:00:00.000+01:00}",
				"{2007 PREM Aston Villa AWAY: RESULT <- 0-1}", "{2007 PREM Aston Villa AWAY: ATTENDENCE <- 40842}",
				"{2007 PREM Aston Villa AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38052,00.html}",
				"{2007 PREM Birmingham HOME: DATE_PLAYED <- 2008-02-09T15:00:00.000Z}",
				"{2007 PREM Birmingham HOME: RESULT <- 1-1}", "{2007 PREM Birmingham HOME: ATTENDENCE <- 34884}",
				"{2007 PREM Birmingham HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~39110,00.html}",
				"{2007 PREM Birmingham AWAY: DATE_PLAYED <- 2007-08-18T15:00:00.000+01:00}",
				"{2007 PREM Birmingham AWAY: RESULT <- 1-0}", "{2007 PREM Birmingham AWAY: ATTENDENCE <- 24961}",
				"{2007 PREM Birmingham AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38147,00.html}",
				"{2007 PREM Blackburn HOME: DATE_PLAYED <- 2008-03-15T15:00:00.000Z}",
				"{2007 PREM Blackburn HOME: RESULT <- 2-1}", "{2007 PREM Blackburn HOME: ATTENDENCE <- 34006}",
				"{2007 PREM Blackburn HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38286,00.html}",
				"{2007 PREM Blackburn AWAY: DATE_PLAYED <- 2007-12-09T16:00:00.000Z}",
				"{2007 PREM Blackburn AWAY: RESULT <- 1-0}", "{2007 PREM Blackburn AWAY: ATTENDENCE <- 20870}",
				"{2007 PREM Blackburn AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37977,00.html}",
				"{2007 PREM Bolton HOME: DATE_PLAYED <- 2007-11-04T16:00:00.000Z}", "{2007 PREM Bolton HOME: RESULT <- 1-1}",
				"{2007 PREM Bolton HOME: ATTENDENCE <- 33867}",
				"{2007 PREM Bolton HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37743,00.html}",
				"{2007 PREM Bolton AWAY: DATE_PLAYED <- 2008-04-12T15:00:00.000+01:00}",
				"{2007 PREM Bolton AWAY: RESULT <- 0-1}", "{2007 PREM Bolton AWAY: ATTENDENCE <- 23043}",
				"{2007 PREM Bolton AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38482,00.html}",
				"{2007 PREM Chelsea HOME: DATE_PLAYED <- 2008-03-01T15:00:00.000Z}", "{2007 PREM Chelsea HOME: RESULT <- 0-4}",
				"{2007 PREM Chelsea HOME: ATTENDENCE <- 34969}",
				"{2007 PREM Chelsea HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37177,00.html}",
				"{2007 PREM Chelsea AWAY: DATE_PLAYED <- 2007-12-01T12:45:00.000Z}", "{2007 PREM Chelsea AWAY: RESULT <- 0-1}",
				"{2007 PREM Chelsea AWAY: ATTENDENCE <- 41830}",
				"{2007 PREM Chelsea AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37921,00.html}",
				"{2007 PREM Derby County HOME: DATE_PLAYED <- 2008-04-19T15:00:00.000+01:00}",
				"{2007 PREM Derby County HOME: RESULT <- 2-1}", "{2007 PREM Derby County HOME: ATTENDENCE <- 34612}",
				"{2007 PREM Derby County HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38533,00.html}",
				"{2007 PREM Derby County AWAY: DATE_PLAYED <- 2007-11-10T15:00:00.000Z}",
				"{2007 PREM Derby County AWAY: RESULT <- 5-0}", "{2007 PREM Derby County AWAY: ATTENDENCE <- 32440}",
				"{2007 PREM Derby County AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37819,00.html}",
				"{2007 PREM Everton HOME: DATE_PLAYED <- 2007-12-15T15:00:00.000Z}", "{2007 PREM Everton HOME: RESULT <- 0-2}",
				"{2007 PREM Everton HOME: ATTENDENCE <- 34430}",
				"{2007 PREM Everton HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37301,00.html}",
				"{2007 PREM Everton AWAY: DATE_PLAYED <- 2008-03-22T17:15:00.000Z}", "{2007 PREM Everton AWAY: RESULT <- 1-1}",
				"{2007 PREM Everton AWAY: ATTENDENCE <- null}",
				"{2007 PREM Everton AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38326,00.html}",
				"{2007 PREM Fulham HOME: DATE_PLAYED <- 2008-01-12T15:00:00.000Z}", "{2007 PREM Fulham HOME: RESULT <- 2-1}",
				"{2007 PREM Fulham HOME: ATTENDENCE <- 34947}",
				"{2007 PREM Fulham HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38914,00.html}",
				"{2007 PREM Fulham AWAY: DATE_PLAYED <- 2008-02-23T15:00:00.000Z}", "{2007 PREM Fulham AWAY: RESULT <- 1-0}",
				"{2007 PREM Fulham AWAY: ATTENDENCE <- 25280}",
				"{2007 PREM Fulham AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38688,00.html}",
				"{2007 PREM Liverpool HOME: DATE_PLAYED <- 2008-01-30T19:45:00.000Z}",
				"{2007 PREM Liverpool HOME: RESULT <- 1-0}", "{2007 PREM Liverpool HOME: ATTENDENCE <- 34977}",
				"{2007 PREM Liverpool HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38551,00.html}",
				"{2007 PREM Liverpool AWAY: DATE_PLAYED <- 2008-03-05T20:00:00.000Z}",
				"{2007 PREM Liverpool AWAY: RESULT <- 0-4}", "{2007 PREM Liverpool AWAY: ATTENDENCE <- 42954}",
				"{2007 PREM Liverpool AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38144,00.html}",
				"{2007 PREM Man Utd HOME: DATE_PLAYED <- 2007-12-29T15:00:00.000Z}", "{2007 PREM Man Utd HOME: RESULT <- 2-1}",
				"{2007 PREM Man Utd HOME: ATTENDENCE <- 34966}",
				"{2007 PREM Man Utd HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38802,00.html}",
				"{2007 PREM Man Utd AWAY: DATE_PLAYED <- 2008-05-03T12:45:00.000+01:00}",
				"{2007 PREM Man Utd AWAY: RESULT <- 1-4}", "{2007 PREM Man Utd AWAY: ATTENDENCE <- 76013}",
				"{2007 PREM Man Utd AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37408,00.html}",
				"{2007 PREM Manchester City HOME: DATE_PLAYED <- 2007-08-11T15:00:00.000+01:00}",
				"{2007 PREM Manchester City HOME: RESULT <- 0-2}", "{2007 PREM Manchester City HOME: ATTENDENCE <- 34921}",
				"{2007 PREM Manchester City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37428,00.html}",
				"{2007 PREM Manchester City AWAY: DATE_PLAYED <- 2008-01-20T16:00:00.000Z}",
				"{2007 PREM Manchester City AWAY: RESULT <- 1-1}", "{2007 PREM Manchester City AWAY: ATTENDENCE <- 39042}",
				"{2007 PREM Manchester City AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38955,00.html}",
				"{2007 PREM Middlesbrough HOME: DATE_PLAYED <- 2007-09-15T15:00:00.000+01:00}",
				"{2007 PREM Middlesbrough HOME: RESULT <- 3-0}", "{2007 PREM Middlesbrough HOME: ATTENDENCE <- 34351}",
				"{2007 PREM Middlesbrough HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37580,00.html}",
				"{2007 PREM Middlesbrough AWAY: DATE_PLAYED <- 2007-12-22T15:00:00.000Z}",
				"{2007 PREM Middlesbrough AWAY: RESULT <- 2-1}", "{2007 PREM Middlesbrough AWAY: ATTENDENCE <- 26007}",
				"{2007 PREM Middlesbrough AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38776,00.html}",
				"{2007 PREM Newcastle HOME: DATE_PLAYED <- 2008-04-26T15:00:00.000+01:00}",
				"{2007 PREM Newcastle HOME: RESULT <- 2-2}", "{2007 PREM Newcastle HOME: ATTENDENCE <- 34980}",
				"{2007 PREM Newcastle HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37364,00.html}",
				"{2007 PREM Newcastle AWAY: DATE_PLAYED <- 2007-09-23T13:30:00.000+01:00}",
				"{2007 PREM Newcastle AWAY: RESULT <- 1-3}", "{2007 PREM Newcastle AWAY: ATTENDENCE <- 50104}",
				"{2007 PREM Newcastle AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37583,00.html}",
				"{2007 PREM Portsmouth HOME: DATE_PLAYED <- 2008-04-08T19:45:00.000+01:00}",
				"{2007 PREM Portsmouth HOME: RESULT <- 0-1}", "{2007 PREM Portsmouth HOME: ATTENDENCE <- 33629}",
				"{2007 PREM Portsmouth HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38445,00.html}",
				"{2007 PREM Portsmouth AWAY: DATE_PLAYED <- 2007-10-27T17:15:00.000+01:00}",
				"{2007 PREM Portsmouth AWAY: RESULT <- 0-0}", "{2007 PREM Portsmouth AWAY: ATTENDENCE <- 20525}",
				"{2007 PREM Portsmouth AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37696,00.html}",
				"{2007 PREM Reading HOME: DATE_PLAYED <- 2007-12-26T13:00:00.000Z}", "{2007 PREM Reading HOME: RESULT <- 1-1}",
				"{2007 PREM Reading HOME: ATTENDENCE <- 34277}",
				"{2007 PREM Reading HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38747,00.html}",
				"{2007 PREM Reading AWAY: DATE_PLAYED <- 2007-09-01T15:00:00.000+01:00}",
				"{2007 PREM Reading AWAY: RESULT <- 3-0}", "{2007 PREM Reading AWAY: ATTENDENCE <- 23533}",
				"{2007 PREM Reading AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38214,00.html}",
				"{2007 PREM Sunderland HOME: DATE_PLAYED <- 2007-10-21T16:00:00.000+01:00}",
				"{2007 PREM Sunderland HOME: RESULT <- 3-1}", "{2007 PREM Sunderland HOME: ATTENDENCE <- 34913}",
				"{2007 PREM Sunderland HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38126,00.html}",
				"{2007 PREM Sunderland AWAY: DATE_PLAYED <- 2008-03-29T15:00:00.000Z}",
				"{2007 PREM Sunderland AWAY: RESULT <- 1-2}", "{2007 PREM Sunderland AWAY: ATTENDENCE <- 45690}",
				"{2007 PREM Sunderland AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~38399,00.html}",
				"{2007 PREM Tottenham HOME: DATE_PLAYED <- 2007-11-25T13:30:00.000Z}",
				"{2007 PREM Tottenham HOME: RESULT <- 1-1}", "{2007 PREM Tottenham HOME: ATTENDENCE <- 34966}",
				"{2007 PREM Tottenham HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37870,00.html}",
				"{2007 PREM Tottenham AWAY: DATE_PLAYED <- 2008-03-09T15:00:00.000Z}",
				"{2007 PREM Tottenham AWAY: RESULT <- 0-4}", "{2007 PREM Tottenham AWAY: ATTENDENCE <- 36062}",
				"{2007 PREM Tottenham AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37233,00.html}",
				"{2007 PREM Wigan Athletic HOME: DATE_PLAYED <- 2007-08-25T15:00:00.000+01:00}",
				"{2007 PREM Wigan Athletic HOME: RESULT <- 1-1}", "{2007 PREM Wigan Athletic HOME: ATTENDENCE <- 33793}",
				"{2007 PREM Wigan Athletic HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~37470,00.html}",
				"{2007 PREM Wigan Athletic AWAY: DATE_PLAYED <- 2008-02-02T15:00:00.000Z}",
				"{2007 PREM Wigan Athletic AWAY: RESULT <- 0-1}", "{2007 PREM Wigan Athletic AWAY: ATTENDENCE <- 20525}",
				"{2007 PREM Wigan Athletic AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~39064,00.html}",
				"{2007 LGCP Bristol Rovers AWAY: DATE_PLAYED <- 2007-08-28T19:45:00.000+01:00}",
				"{2007 LGCP Bristol Rovers AWAY: RESULT <- 2-1}", "{2007 LGCP Bristol Rovers AWAY: ATTENDENCE <- 10831}",
				"{2007 LGCP Bristol Rovers AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~42975,00.html}",
				"{2007 LGCP Coventry City AWAY: DATE_PLAYED <- 2007-10-30T19:45:00.000Z}",
				"{2007 LGCP Coventry City AWAY: RESULT <- 2-1}", "{2007 LGCP Coventry City AWAY: ATTENDENCE <- 23968}",
				"{2007 LGCP Coventry City AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~43087,00.html}",
				"{2007 LGCP Everton HOME: DATE_PLAYED <- 2007-12-12T19:45:00.000Z}", "{2007 LGCP Everton HOME: RESULT <- 1-2}",
				"{2007 LGCP Everton HOME: ATTENDENCE <- 28777}",
				"{2007 LGCP Everton HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~43165,00.html}",
				"{2007 LGCP Plymouth HOME: DATE_PLAYED <- 2007-09-26T19:45:00.000+01:00}",
				"{2007 LGCP Plymouth HOME: RESULT <- 1-0}", "{2007 LGCP Plymouth HOME: ATTENDENCE <- 25774}",
				"{2007 LGCP Plymouth HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~43058,00.html}",
				"{2007 FACP Manchester City HOME: DATE_PLAYED <- 2008-01-05T15:00:00.000Z}",
				"{2007 FACP Manchester City HOME: RESULT <- 0-0}", "{2007 FACP Manchester City HOME: ATTENDENCE <- 33806}",
				"{2007 FACP Manchester City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~43214,00.html}",
				"{2007 FACP Manchester City AWAY: DATE_PLAYED <- 2008-01-16T20:05:00.000Z}",
				"{2007 FACP Manchester City AWAY: RESULT <- 0-1}", "{2007 FACP Manchester City AWAY: ATTENDENCE <- 27809}",
				"{2007 FACP Manchester City AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~43288,00.html}" };
		testSeason(2007, expectedUpdates);
	}

	/**
	 * Test2011.
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *           the uRI syntax exception
	 */
	@Test
	public void test2011() throws IOException, URISyntaxException {
		String[] expectedUpdates = new String[] {
				"{2011 LGCP Aldershot Town HOME: DATE_PLAYED <- 2011-08-24T19:45:00.000+01:00}",
				"{2011 LGCP Aldershot Town HOME: RESULT <- 1-2}", "{2011 LGCP Aldershot Town HOME: ATTENDENCE <- 19879}",
				"{2011 LGCP Aldershot Town HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~60962,00.html}",
				"{2011 FACP Sheffield Wed AWAY: DATE_PLAYED <- 2012-01-08T15:00:00.000Z}",
				"{2011 FACP Sheffield Wed AWAY: RESULT <- 0-1}", "{2011 FACP Sheffield Wed AWAY: ATTENDENCE <- 17916}",
				"{2011 FACP Sheffield Wed AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~61383,00.html}",
				"{2011 FLC Barnsley HOME: DATE_PLAYED <- 2011-12-17T15:00:00.000Z}", "{2011 FLC Barnsley HOME: RESULT <- 1-0}",
				"{2011 FLC Barnsley HOME: ATTENDENCE <- 34749}",
				"{2011 FLC Barnsley HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58614,00.html}",
				"{2011 FLC Barnsley AWAY: DATE_PLAYED <- 2012-04-07T15:00:00.000+01:00}",
				"{2011 FLC Barnsley AWAY: RESULT <- null}", "{2011 FLC Barnsley AWAY: ATTENDENCE <- null}",
				"{2011 FLC Barnsley AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Birmingham HOME: DATE_PLAYED <- 2012-04-09T15:00:00.000+01:00}",
				"{2011 FLC Birmingham HOME: RESULT <- null}", "{2011 FLC Birmingham HOME: ATTENDENCE <- null}",
				"{2011 FLC Birmingham HOME: MATCH_REPORT <- null}",
				"{2011 FLC Birmingham AWAY: DATE_PLAYED <- 2011-12-26T17:30:00.000Z}",
				"{2011 FLC Birmingham AWAY: RESULT <- 1-1}", "{2011 FLC Birmingham AWAY: ATTENDENCE <- 20214}",
				"{2011 FLC Birmingham AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58336,00.html}",
				"{2011 FLC Blackpool HOME: DATE_PLAYED <- 2011-10-15T15:00:00.000+01:00}",
				"{2011 FLC Blackpool HOME: RESULT <- 4-0}", "{2011 FLC Blackpool HOME: ATTENDENCE <- 31448}",
				"{2011 FLC Blackpool HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57649,00.html}",
				"{2011 FLC Blackpool AWAY: DATE_PLAYED <- 2012-02-18T15:00:00.000Z}",
				"{2011 FLC Blackpool AWAY: RESULT <- null}", "{2011 FLC Blackpool AWAY: ATTENDENCE <- null}",
				"{2011 FLC Blackpool AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Brighton HOME: DATE_PLAYED <- 2012-04-14T15:00:00.000+01:00}",
				"{2011 FLC Brighton HOME: RESULT <- null}", "{2011 FLC Brighton HOME: ATTENDENCE <- null}",
				"{2011 FLC Brighton HOME: MATCH_REPORT <- null}",
				"{2011 FLC Brighton AWAY: DATE_PLAYED <- 2011-10-24T19:45:00.000+01:00}",
				"{2011 FLC Brighton AWAY: RESULT <- 1-0}", "{2011 FLC Brighton AWAY: ATTENDENCE <- 20686}",
				"{2011 FLC Brighton AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57697,00.html}",
				"{2011 FLC Bristol City HOME: DATE_PLAYED <- 2011-11-01T19:45:00.000Z}",
				"{2011 FLC Bristol City HOME: RESULT <- 0-0}", "{2011 FLC Bristol City HOME: ATTENDENCE <- 27980}",
				"{2011 FLC Bristol City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58215,00.html}",
				"{2011 FLC Bristol City AWAY: DATE_PLAYED <- 2012-04-17T19:45:00.000+01:00}",
				"{2011 FLC Bristol City AWAY: RESULT <- null}", "{2011 FLC Bristol City AWAY: ATTENDENCE <- null}",
				"{2011 FLC Bristol City AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Burnley HOME: DATE_PLAYED <- 2011-12-03T15:00:00.000Z}", "{2011 FLC Burnley HOME: RESULT <- 1-2}",
				"{2011 FLC Burnley HOME: ATTENDENCE <- 26274}",
				"{2011 FLC Burnley HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58547,00.html}",
				"{2011 FLC Burnley AWAY: DATE_PLAYED <- 2012-03-24T15:00:00.000Z}", "{2011 FLC Burnley AWAY: RESULT <- null}",
				"{2011 FLC Burnley AWAY: ATTENDENCE <- null}", "{2011 FLC Burnley AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Cardiff City HOME: DATE_PLAYED <- 2011-08-07T13:00:00.000+01:00}",
				"{2011 FLC Cardiff City HOME: RESULT <- 0-1}", "{2011 FLC Cardiff City HOME: ATTENDENCE <- 25680}",
				"{2011 FLC Cardiff City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58440,00.html}",
				"{2011 FLC Cardiff City AWAY: DATE_PLAYED <- 2012-03-04T13:00:00.000Z}",
				"{2011 FLC Cardiff City AWAY: RESULT <- null}", "{2011 FLC Cardiff City AWAY: ATTENDENCE <- null}",
				"{2011 FLC Cardiff City AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Coventry City HOME: DATE_PLAYED <- 2012-01-02T15:00:00.000Z}",
				"{2011 FLC Coventry City HOME: RESULT <- 1-0}", "{2011 FLC Coventry City HOME: ATTENDENCE <- 34936}",
				"{2011 FLC Coventry City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~59324,00.html}",
				"{2011 FLC Coventry City AWAY: DATE_PLAYED <- 2011-11-19T15:00:00.000Z}",
				"{2011 FLC Coventry City AWAY: RESULT <- 2-1}", "{2011 FLC Coventry City AWAY: ATTENDENCE <- 20524}",
				"{2011 FLC Coventry City AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58275,00.html}",
				"{2011 FLC Crystal Palace HOME: DATE_PLAYED <- 2012-02-25T12:45:00.000Z}",
				"{2011 FLC Crystal Palace HOME: RESULT <- null}", "{2011 FLC Crystal Palace HOME: ATTENDENCE <- null}",
				"{2011 FLC Crystal Palace HOME: MATCH_REPORT <- null}",
				"{2011 FLC Crystal Palace AWAY: DATE_PLAYED <- 2011-10-01T15:00:00.000+01:00}",
				"{2011 FLC Crystal Palace AWAY: RESULT <- 2-2}", "{2011 FLC Crystal Palace AWAY: ATTENDENCE <- 20074}",
				"{2011 FLC Crystal Palace AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57571,00.html}",
				"{2011 FLC Derby County HOME: DATE_PLAYED <- 2011-11-26T17:20:00.000Z}",
				"{2011 FLC Derby County HOME: RESULT <- 3-1}", "{2011 FLC Derby County HOME: ATTENDENCE <- 27864}",
				"{2011 FLC Derby County HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58490,00.html}",
				"{2011 FLC Derby County AWAY: DATE_PLAYED <- 2011-12-31T13:00:00.000Z}",
				"{2011 FLC Derby County AWAY: RESULT <- 1-2}", "{2011 FLC Derby County AWAY: ATTENDENCE <- 28067}",
				"{2011 FLC Derby County AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58385,00.html}",
				"{2011 FLC Doncaster HOME: DATE_PLAYED <- 2012-03-10T15:00:00.000Z}",
				"{2011 FLC Doncaster HOME: RESULT <- null}", "{2011 FLC Doncaster HOME: ATTENDENCE <- null}",
				"{2011 FLC Doncaster HOME: MATCH_REPORT <- null}",
				"{2011 FLC Doncaster AWAY: DATE_PLAYED <- 2011-08-13T15:00:00.000+01:00}",
				"{2011 FLC Doncaster AWAY: RESULT <- 1-0}", "{2011 FLC Doncaster AWAY: ATTENDENCE <- 11344}",
				"{2011 FLC Doncaster AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57839,00.html}",
				"{2011 FLC Hull City HOME: DATE_PLAYED <- 2012-04-28T12:30:00.000+01:00}",
				"{2011 FLC Hull City HOME: RESULT <- null}", "{2011 FLC Hull City HOME: ATTENDENCE <- null}",
				"{2011 FLC Hull City HOME: MATCH_REPORT <- null}",
				"{2011 FLC Hull City AWAY: DATE_PLAYED <- 2011-11-05T15:00:00.000Z}",
				"{2011 FLC Hull City AWAY: RESULT <- 2-0}", "{2011 FLC Hull City AWAY: ATTENDENCE <- 21756}",
				"{2011 FLC Hull City AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58231,00.html}",
				"{2011 FLC Ipswich Town HOME: DATE_PLAYED <- 2011-09-27T19:45:00.000+01:00}",
				"{2011 FLC Ipswich Town HOME: RESULT <- 0-1}", "{2011 FLC Ipswich Town HOME: ATTENDENCE <- 27709}",
				"{2011 FLC Ipswich Town HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57558,00.html}",
				"{2011 FLC Ipswich Town AWAY: DATE_PLAYED <- 2012-01-31T19:45:00.000Z}",
				"{2011 FLC Ipswich Town AWAY: RESULT <- 1-5}", "{2011 FLC Ipswich Town AWAY: ATTENDENCE <- 22185}",
				"{2011 FLC Ipswich Town AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~59496,00.html}",
				"{2011 FLC Leeds United HOME: DATE_PLAYED <- 2011-08-21T13:15:00.000+01:00}",
				"{2011 FLC Leeds United HOME: RESULT <- 2-2}", "{2011 FLC Leeds United HOME: ATTENDENCE <- 28252}",
				"{2011 FLC Leeds United HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57927,00.html}",
				"{2011 FLC Leeds United AWAY: DATE_PLAYED <- 2012-03-17T15:00:00.000Z}",
				"{2011 FLC Leeds United AWAY: RESULT <- null}", "{2011 FLC Leeds United AWAY: ATTENDENCE <- null}",
				"{2011 FLC Leeds United AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Leicester City HOME: DATE_PLAYED <- 2011-10-29T15:00:00.000+01:00}",
				"{2011 FLC Leicester City HOME: RESULT <- 3-2}", "{2011 FLC Leicester City HOME: ATTENDENCE <- 30410}",
				"{2011 FLC Leicester City HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58179,00.html}",
				"{2011 FLC Leicester City AWAY: DATE_PLAYED <- 2012-04-21T15:00:00.000+01:00}",
				"{2011 FLC Leicester City AWAY: RESULT <- null}", "{2011 FLC Leicester City AWAY: ATTENDENCE <- null}",
				"{2011 FLC Leicester City AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Middlesbrough HOME: DATE_PLAYED <- 2012-03-20T19:45:00.000Z}",
				"{2011 FLC Middlesbrough HOME: RESULT <- null}", "{2011 FLC Middlesbrough HOME: ATTENDENCE <- null}",
				"{2011 FLC Middlesbrough HOME: MATCH_REPORT <- null}",
				"{2011 FLC Middlesbrough AWAY: DATE_PLAYED <- 2011-11-29T19:45:00.000Z}",
				"{2011 FLC Middlesbrough AWAY: RESULT <- 2-0}", "{2011 FLC Middlesbrough AWAY: ATTENDENCE <- 18457}",
				"{2011 FLC Middlesbrough AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58520,00.html}",
				"{2011 FLC Millwall HOME: DATE_PLAYED <- 2012-02-04T12:30:00.000Z}", "{2011 FLC Millwall HOME: RESULT <- 2-1}",
				"{2011 FLC Millwall HOME: ATTENDENCE <- 27774}",
				"{2011 FLC Millwall HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~59529,00.html}",
				"{2011 FLC Millwall AWAY: DATE_PLAYED <- 2011-09-17T12:30:00.000+01:00}",
				"{2011 FLC Millwall AWAY: RESULT <- 0-0}", "{2011 FLC Millwall AWAY: ATTENDENCE <- 16078}",
				"{2011 FLC Millwall AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58107,00.html}",
				"{2011 FLC Nottm Forest HOME: DATE_PLAYED <- 2012-01-21T15:00:00.000Z}",
				"{2011 FLC Nottm Forest HOME: RESULT <- 2-1}", "{2011 FLC Nottm Forest HOME: ATTENDENCE <- null}",
				"{2011 FLC Nottm Forest HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~59439,00.html}",
				"{2011 FLC Nottm Forest AWAY: DATE_PLAYED <- 2011-08-28T13:15:00.000+01:00}",
				"{2011 FLC Nottm Forest AWAY: RESULT <- 4-1}", "{2011 FLC Nottm Forest AWAY: ATTENDENCE <- 21379}",
				"{2011 FLC Nottm Forest AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57971,00.html}",
				"{2011 FLC Peterborough HOME: DATE_PLAYED <- 2011-09-24T15:00:00.000+01:00}",
				"{2011 FLC Peterborough HOME: RESULT <- 1-0}", "{2011 FLC Peterborough HOME: ATTENDENCE <- 29895}",
				"{2011 FLC Peterborough HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57522,00.html}",
				"{2011 FLC Peterborough AWAY: DATE_PLAYED <- 2012-02-11T15:00:00.000Z}",
				"{2011 FLC Peterborough AWAY: RESULT <- null}", "{2011 FLC Peterborough AWAY: ATTENDENCE <- null}",
				"{2011 FLC Peterborough AWAY: MATCH_REPORT <- null}",
				"{2011 FLC Portsmouth HOME: DATE_PLAYED <- 2011-09-10T15:00:00.000+01:00}",
				"{2011 FLC Portsmouth HOME: RESULT <- 4-3}", "{2011 FLC Portsmouth HOME: ATTENDENCE <- null}",
				"{2011 FLC Portsmouth HOME: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58042,00.html}",
				"{2011 FLC Portsmouth AWAY: DATE_PLAYED <- 2012-01-14T15:00:00.000Z}",
				"{2011 FLC Portsmouth AWAY: RESULT <- 1-0}", "{2011 FLC Portsmouth AWAY: ATTENDENCE <- 18492}",
				"{2011 FLC Portsmouth AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~59392,00.html}",
				"{2011 FLC Reading HOME: DATE_PLAYED <- 2012-03-31T15:00:00.000+01:00}",
				"{2011 FLC Reading HOME: RESULT <- null}", "{2011 FLC Reading HOME: ATTENDENCE <- null}",
				"{2011 FLC Reading HOME: MATCH_REPORT <- null}",
				"{2011 FLC Reading AWAY: DATE_PLAYED <- 2011-12-10T15:00:00.000Z}", "{2011 FLC Reading AWAY: RESULT <- 0-3}",
				"{2011 FLC Reading AWAY: ATTENDENCE <- 24026}",
				"{2011 FLC Reading AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~58567,00.html}",
				"{2011 FLC Southampton HOME: DATE_PLAYED <- 2012-02-14T19:45:00.000Z}",
				"{2011 FLC Southampton HOME: RESULT <- null}", "{2011 FLC Southampton HOME: ATTENDENCE <- null}",
				"{2011 FLC Southampton HOME: MATCH_REPORT <- null}",
				"{2011 FLC Southampton AWAY: DATE_PLAYED <- 2011-10-18T19:45:00.000+01:00}",
				"{2011 FLC Southampton AWAY: RESULT <- 0-1}", "{2011 FLC Southampton AWAY: ATTENDENCE <- 32152}",
				"{2011 FLC Southampton AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57685,00.html}",
				"{2011 FLC Watford HOME: DATE_PLAYED <- 2012-03-07T19:45:00.000Z}", "{2011 FLC Watford HOME: RESULT <- null}",
				"{2011 FLC Watford HOME: ATTENDENCE <- null}", "{2011 FLC Watford HOME: MATCH_REPORT <- null}",
				"{2011 FLC Watford AWAY: DATE_PLAYED <- 2011-08-16T19:45:00.000+01:00}",
				"{2011 FLC Watford AWAY: RESULT <- 4-0}", "{2011 FLC Watford AWAY: ATTENDENCE <- 14747}",
				"{2011 FLC Watford AWAY: MATCH_REPORT <- file:/page/MatchReport/0,,12562~57881,00.html}" };
		testSeason(2011, expectedUpdates);
	}
}
