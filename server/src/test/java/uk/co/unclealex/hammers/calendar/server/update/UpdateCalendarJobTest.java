/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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

package uk.co.unclealex.hammers.calendar.server.update;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UpdateCalendarJobTest.
 * 
 * @author alex
 */
public class UpdateCalendarJobTest {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(UpdateCalendarJobTest.class);
	
	/**
	 * Test running does not throw any exceptions.
	 * 
	 * @throws SchedulerException
	 *           the scheduler exception
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	@Test
	public void testRunningDoesNotThrowAnyExceptions() throws SchedulerException, InterruptedException {
		final AtomicInteger atomicInteger = new AtomicInteger();
		
		UpdateCalendarJob updateCalendarJob = new UpdateCalendarJob();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				atomicInteger.incrementAndGet();
			}
		};
		updateCalendarJob.setRunnable(runnable);
		updateCalendarJob.setCronString("* * * * * ?");
		try {
			updateCalendarJob.initialise();
			Thread.sleep(2000);
			Assert.assertFalse("The counter was not updated.", atomicInteger.get() == 0);
		}
		finally {
			updateCalendarJob.destroy();
		}
	}

	/**
	 * Test run now.
	 * 
	 * @throws InterruptedException
	 *           the interrupted exception
	 * @throws SchedulerException
	 *           the scheduler exception
	 */
	@Test
	public void testRunNow() throws InterruptedException, SchedulerException {
		final AtomicInteger atomicInteger = new AtomicInteger();
		
		UpdateCalendarJob updateCalendarJob = new UpdateCalendarJob();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				atomicInteger.incrementAndGet();
			}
		};
		updateCalendarJob.setRunnable(runnable);
		try {
			updateCalendarJob.initialise();
			updateCalendarJob.scheduleNow();
			Thread.sleep(500);
			Assert.assertEquals("The counter was not correct.", 1, atomicInteger.get());
		}
		finally {
			updateCalendarJob.destroy();
		}
	}
	
	/**
	 * Test jobs cannot run at same time.
	 * 
	 * @throws SchedulerException
	 *           the scheduler exception
	 */
	@Test
	public void testJobsCannotRunAtSameTime() throws SchedulerException {
		final AtomicInteger atomicInteger = new AtomicInteger();
		
		UpdateCalendarJob updateCalendarJob = new UpdateCalendarJob();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					log.error("A thread was interrupted.", e);
					Assert.fail("A thread was interrupted.");
				}
				atomicInteger.incrementAndGet();
			}
		};
		updateCalendarJob.setRunnable(runnable);
		try {
			updateCalendarJob.initialise();
			Callable<Void> callable = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					while (atomicInteger.get() < 2) {
						Thread.sleep(100);
					}
					return null;
				}
			};
			long then = System.currentTimeMillis();
			updateCalendarJob.scheduleNow();
			updateCalendarJob.scheduleNow();
			Future<Void> future = Executors.newCachedThreadPool().submit(callable);
			try {
				future.get(1500, TimeUnit.MILLISECONDS);
			}
			catch (Throwable t) {
				log.error("Oh no!", t);
				Assert.fail("The computation did not complete successfully.");
			}
			Assert.assertEquals("The counter was not correct.", 2, atomicInteger.get());
			long timeToComplete = System.currentTimeMillis() - then;
			log.info("Jobs took " + timeToComplete + "ms to complete.");
			Assert.assertFalse("The two jobs took less than a second to complete.", timeToComplete <= 1000);
		}
		finally {
			updateCalendarJob.destroy();
		}
	}
}
