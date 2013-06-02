/**
 * Copyright 2012 Alex Jones
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
 * @author alex
 *
 */

package uk.co.unclealex.hammers.calendar.html;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;

import uk.co.unclealex.hammers.calendar.dates.DateServiceImpl;
import uk.co.unclealex.hammers.calendar.html.DatePlayedLocator;
import uk.co.unclealex.hammers.calendar.html.GameLocator;
import uk.co.unclealex.hammers.calendar.html.GameUpdateCommand;
import uk.co.unclealex.hammers.calendar.html.HtmlPageLoaderImpl;
import uk.co.unclealex.hammers.calendar.html.TicketsHtmlSingleGameScanner;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * @author alex
 * 
 */
public abstract class AbstractTicketsHtmlGamesScannerTest {

  private final int year;

  /**
   * 
   */
  public AbstractTicketsHtmlGamesScannerTest(final int year) {
    this.year = year;
  }

  /**
   * Test.
   * 
   * @param resourceName
   *          the resource name
   * @param dateTime
   *          the date time
   * @param expectedGameUpdateCommandsFunction
   *          the expected game update commands function
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the uRI syntax exception
   */
  protected void test(
      final String resourceName,
      final DateTime dateTime,
      final Function<GameLocator, GameUpdateCommand[]> expectedGameUpdateCommandsFunction)
      throws IOException,
      URISyntaxException {
    final TicketsHtmlSingleGameScanner ticketsHtmlSingleGameScanner =
        new TicketsHtmlSingleGameScanner(new HtmlPageLoaderImpl(), new DateServiceImpl(), new Integer(year));
    final URL url = getClass().getClassLoader().getResource(Joiner.on('/').join("html", "tickets", year, resourceName));
    final Iterable<GameUpdateCommand> actualGameUpdateCommands = ticketsHtmlSingleGameScanner.scan(url.toURI());
    Assert.assertThat(
        "The wrong updates were returned for " + resourceName,
        actualGameUpdateCommands,
        Matchers.containsInAnyOrder(expectedGameUpdateCommandsFunction.apply(new DatePlayedLocator(dateTime))));
  }

  /**
   * Date of.
   * 
   * @param day
   *          the day
   * @param month
   *          the month
   * @param year
   *          the year
   * @param hour
   *          the hour
   * @param minute
   *          the minute
   * @return the date time
   */
  protected DateTime dateOf(final int day, final int month, final int year, final int hour, final int minute) {
    return new DateTime(year, month, day, hour, minute).withZone(DateTimeZone.forID("Europe/London"));
  }

}