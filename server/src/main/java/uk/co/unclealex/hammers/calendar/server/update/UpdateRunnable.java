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

package uk.co.unclealex.hammers.calendar.server.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alex
 *
 */
public class UpdateRunnable implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(UpdateRunnable.class);
	
	private MainUpdateService i_mainUpdateService;
	
	@Override
	public void run() {
		try {
			log.info("Updating started.");
			getMainUpdateService().updateAllCalendars();
			log.info("Updating completed.");
			System.gc();
		}
		catch (Throwable e) {
			log.error("Could not update calendars.", e);
		}
	}

	public MainUpdateService getMainUpdateService() {
		return i_mainUpdateService;
	}

	public void setMainUpdateService(MainUpdateService mainUpdateService) {
		i_mainUpdateService = mainUpdateService;
	}

}
