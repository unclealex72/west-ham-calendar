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
package uk.co.unclealex.hammers.calendar.server.scheduling;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import uk.co.unclealex.hammers.calendar.server.html.FixtureBuilderService;
import uk.co.unclealex.hammers.calendar.server.service.GoogleCalendarService;

public class UpdateCalendarJob implements StatefulJob, JobFactory {

	private static final Logger log = Logger.getLogger(UpdateCalendarJob.class);
	
	private FixtureBuilderService i_onlineFixtureBuilderService;
	private GoogleCalendarService i_googleCalendarService;
	private String i_cronString;
	private Scheduler i_scheduler;
	private JobDetail i_jobDetail;
	
	public void initialise() throws SchedulerException, ParseException {
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    setScheduler(scheduler);
    scheduler.setJobFactory(this);
    scheduler.start();
    JobDetail job = new JobDetail("job1", "group1", getClass());
    setJobDetail(job);
		String cronString = getCronString();
		if (cronString != null) {
			scheduler.scheduleJob(job, new CronTrigger("trigger1", "group1", cronString));
		}
	}

	public void scheduleNow() throws SchedulerException {
		getScheduler().scheduleJob(getJobDetail(), TriggerUtils.makeImmediateTrigger("trigger2", 0, 0));
	}
	
	@Override
	public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException {
		return this;
	}
	public void destroy() throws SchedulerException {
		getScheduler().shutdown();
	}
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		try {
			getOnlineFixtureBuilderService().buildAll();
			getGoogleCalendarService().updateCalendars();
			log.info("Updating completed.");
		}
		catch (Throwable e) {
			log.error("Could not update calendars.", e);
		}
	}

	public FixtureBuilderService getOnlineFixtureBuilderService() {
		return i_onlineFixtureBuilderService;
	}

	public void setOnlineFixtureBuilderService(FixtureBuilderService onlineFixtureBuilderService) {
		i_onlineFixtureBuilderService = onlineFixtureBuilderService;
	}

	public GoogleCalendarService getGoogleCalendarService() {
		return i_googleCalendarService;
	}

	public void setGoogleCalendarService(GoogleCalendarService googleCalendarService) {
		i_googleCalendarService = googleCalendarService;
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

	public JobDetail getJobDetail() {
		return i_jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		i_jobDetail = jobDetail;
	}
}
