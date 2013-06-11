/**
 * Copyright 2013 Alex Jones
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
package uk.co.unclealex.hammers.calendar.schedule

import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import com.google.inject.Guice
import module.CalendarModule
import uk.co.unclealex.hammers.calendar.update.MainUpdateService
import org.quartz.JobDetail
import org.quartz.JobBuilder._
import org.quartz.TriggerBuilder._
import org.quartz.CronScheduleBuilder._
import org.quartz.impl.StdSchedulerFactory
import com.typesafe.scalalogging.slf4j.Logging

/**
 * The class used to schedule the main update job. Note that this class is not injected by Guice but instead
 * uses a Guice module instead. This is a bit of a hack but it quickly gets stateful jobs working with Guice.
 * @author alex
 *
 */
@DisallowConcurrentExecution
class UpdateJob extends Job with Logging {

  def execute(context: JobExecutionContext): Unit = {
    logger info "Update job execution has started"
    try {
      val injector = Guice.createInjector(new CalendarModule())
      val mainUpdateService = injector.getInstance(classOf[MainUpdateService])
      mainUpdateService.processDatabaseUpdates()
    } catch {
      case e: Exception => logger error ("Updating failed unexpectedly", e)
    } finally {
      logger info "Update job execution has finished"
    }
  }
}

object UpdateJob {

  val scheduler = StdSchedulerFactory.getDefaultScheduler

  def schedule: Unit = {
    val job = newJob(classOf[UpdateJob]) withIdentity ("job1", "group1") build ()
    val cronTrigger = newTrigger() withIdentity ("cron_trigger", "group1") withSchedule (cronSchedule("0 0 * * * ?")) build ()
    scheduler scheduleJob (job, cronTrigger)
    scheduler.start()
    scheduler triggerJob (job.getKey)
  }

  def close: Unit = {
    scheduler.shutdown()
  }
}