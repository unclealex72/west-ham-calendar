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

import org.junit.Test;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author alex
 * 
 */
public class UpdateCalendarJobLoadingTest {

	@Test
	public void testLoad() throws SchedulerException {
		ApplicationContext ctxt = new ClassPathXmlApplicationContext("/application-contexts/update/context.xml",
				"/application-contexts/update/cron.xml", "/application-contexts/dao/context.xml",
				"/application-contexts/dao/test-db.xml");
		UpdateCalendarJob updateCalendarJob = ctxt.getBean("updateCalendarJob", UpdateCalendarJob.class);
		updateCalendarJob.destroy();
	}

}
