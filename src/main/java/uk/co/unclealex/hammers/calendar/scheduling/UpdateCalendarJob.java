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
package uk.co.unclealex.hammers.calendar.scheduling;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import uk.co.unclealex.hammers.calendar.google.GoogleCalendarService;
import uk.co.unclealex.hammers.calendar.google.GoogleConfiguration;
import uk.co.unclealex.hammers.calendar.html.FixtureBuilderService;

public class UpdateCalendarJob implements StatefulJob, JobFactory {

	private static final Logger log = Logger.getLogger(UpdateCalendarJob.class);
	
	private FixtureBuilderService i_onlineFixtureBuilderService;
	private GoogleCalendarService i_googleCalendarService;
	private GoogleConfiguration i_googleConfiguration;
	private Scheduler i_scheduler;
	
	public void initialise() throws SchedulerException, ParseException {
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    setScheduler(scheduler);
    scheduler.setJobFactory(this);
    scheduler.start();
    JobDetail job = new JobDetail("job1", "group1", getClass());
		String cronString = getGoogleConfiguration().getCronString();
		Matcher matcher = Pattern.compile("\"(.+)\"").matcher(cronString);
		if (matcher.matches()) {
			cronString = matcher.group(1);
		}
		CronTrigger trigger = new CronTrigger("trigger1", "group1", cronString);
		scheduler.scheduleJob(job, trigger);
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
		}
		catch (IOException e) {
			log.error("Could not update calendars.", e);
		}
		catch (RuntimeException e) {
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

	public GoogleConfiguration getGoogleConfiguration() {
		return i_googleConfiguration;
	}

	public void setGoogleConfiguration(GoogleConfiguration googleConfiguration) {
		i_googleConfiguration = googleConfiguration;
	}

}
