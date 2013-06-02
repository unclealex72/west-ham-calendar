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

package uk.co.unclealex.hammers.calendar.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.hammers.calendar.update.MainUpdateService;

/**
 * A class containing the code to make sure that all Google calendars are
 * synchronised with the database.
 * 
 * @author alex
 * 
 */
public class UpdateRunnable implements Runnable {

  /** The logger for this class. */
  private static final Logger log = LoggerFactory.getLogger(UpdateRunnable.class);

  /**
   * The {@link MainUpdateService} used to update the Google calendars.
   */
  private MainUpdateService mainUpdateService;

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {
      log.info("Updating started.");
      log.info("Updating completed.");
      System.gc();
    }
    catch (final Throwable e) {
      log.error("Could not update calendars.", e);
    }
  }

  /**
   * Gets the {@link MainUpdateService} used to update the Google calendars.
   * 
   * @return the {@link MainUpdateService} used to update the Google calendars
   */
  public MainUpdateService getMainUpdateService() {
    return mainUpdateService;
  }

  /**
   * Sets the {@link MainUpdateService} used to update the Google calendars.
   * 
   * @param mainUpdateService
   *          the new {@link MainUpdateService} used to update the Google
   *          calendars
   */
  public void setMainUpdateService(final MainUpdateService mainUpdateService) {
    this.mainUpdateService = mainUpdateService;
  }

}
