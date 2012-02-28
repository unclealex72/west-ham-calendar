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
package uk.co.unclealex.hammers.calendar.server.update;

import org.quartz.CronScheduleBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that schedules and executes runnable jobs.
 * @author alex
 *
 */
public class UpdateCalendarJob implements JobFactory {

	private final Logger log = LoggerFactory.getLogger(UpdateCalendarJob.class);
	
	/**
	 * The {@link Runnable} job to execute.
	 */
	private Runnable i_runnable;
	
	/**
	 * The cron string used to control job scheduling or null if no job is to be scheduled.
	 */
	private String i_cronString;
	
	/**
	 * A Quartz {@link Scheduler}.
	 */
	private Scheduler i_scheduler;
	
	/**
	 * A Quartz {@link JobKey}.
	 */
	private JobKey i_jobKey;
	
	/**
	 * Initialise the {@link Scheduler} and {@link JobKey}.
	 * @throws SchedulerException Thrown if there is an error configuring the Quartz {@link Scheduler}.
	 */
	public void initialise() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.setJobFactory(this);
		scheduler.start();
		setScheduler(scheduler);
		JobDetail job = JobBuilder.newJob(MyJob.class).storeDurably().build();
		setJobKey(job.getKey());
		String cronString = getCronString();
		if (cronString != null) {
			log.info("Calendar updates have been scheduled with the following cron string: " + cronString);
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronString)).build();
			scheduler.scheduleJob(job, trigger);
		}
		else {
			log.warn("No cron string was set so there will be no automatic updates.");
			scheduler.addJob(job, false);
		}
	}

	/**
	 * Schedule the job to run now.
	 * @throws SchedulerException Thrown if there is an error configuring the Quartz {@link Scheduler}.
	 */
	public void scheduleNow() throws SchedulerException {
		Scheduler scheduler = getScheduler();
		scheduler.triggerJob(getJobKey());
	}

	/**
	 * Destroy the {@link Scheduler}.
	 * @throws SchedulerException Thrown if there is an error shutting down the Quartz {@link Scheduler}.
	 */
	public void destroy() throws SchedulerException {
		getScheduler().shutdown();
	}

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		return new MyJob();
	}

	/**
	 * A class that encapsulates the job to be run, making sure that no two jobs can run concurrently.
	 * @author alex
	 */
	@DisallowConcurrentExecution
	class MyJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			UpdateCalendarJob.this.execute();
		}
	}

	/**
	 * Execute the current job.
	 */
	public void execute() {
		Thread currentThread = Thread.currentThread();
		String currentThreadName = currentThread.getName();
		try {
			currentThread.setName("Update Calendar");
			getRunnable().run();
		}
		finally {
			currentThread.setName(currentThreadName);
		}
	}

	public Scheduler getScheduler() {
		return i_scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		i_scheduler = scheduler;
	}

	public String getCronString() {
		return i_cronString;
	}

	public void setCronString(String cronString) {
		i_cronString = cronString;
	}

	public Runnable getRunnable() {
		return i_runnable;
	}

	public void setRunnable(Runnable runnable) {
		i_runnable = runnable;
	}

	public JobKey getJobKey() {
		return i_jobKey;
	}

	public void setJobKey(JobKey jobKey) {
		i_jobKey = jobKey;
	}
}
