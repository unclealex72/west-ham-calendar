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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.unclealex.hammers.calendar.server.testing.CountingDataSource;

/**
 * @author alex
 * 
 */
public class UpdateCalendarJobTransactionalTest {

	private static final Logger log = LoggerFactory.getLogger(UpdateCalendarJobTransactionalTest.class);

	@Test
	public void testConnectionsGetClosed() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				ApplicationContext ctxt = new ClassPathXmlApplicationContext("/application-contexts/dao/context.xml",
						"/application-contexts/dao/test-db.xml", "/application-contexts/update/context.xml",
						"/application-contexts/update/transactional.xml");
				UpdateCalendarJob updateCalendarJob = ctxt.getBean(UpdateCalendarJob.class);
				updateCalendarJob.execute();
			}
		};
		int originalTotalConnections = CountingDataSource.TOTAL_CONNECTION_COUNT.get();
		int originalOpenConnections = CountingDataSource.OPEN_CONNECTION_COUNT.get();
		Future<?> future = executor.submit(runnable);
		future.get();
		int finalTotalConnections = CountingDataSource.TOTAL_CONNECTION_COUNT.get();
		int finalOpenConnections = CountingDataSource.OPEN_CONNECTION_COUNT.get();
		int openedConnections = finalTotalConnections - originalTotalConnections;
		log.info(openedConnections + " connections were opened.");
		Assert.assertTrue("Not enough connections were opened to correctly test.", openedConnections >= 2);
		Assert.assertEquals("The number of open connections was wrong.", originalOpenConnections, finalOpenConnections);
	}
}
