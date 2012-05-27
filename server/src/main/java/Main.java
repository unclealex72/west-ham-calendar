import java.io.IOException;
import java.util.List;

import uk.co.unclealex.hammers.calendar.server.calendar.CalendarFactory;
import uk.co.unclealex.hammers.calendar.server.calendar.ConstantOauthCalendarFactory;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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
 * @author unclealex72
 *
 */

/**
 * @author alex
 * 
 */
public class Main {

	public static void main(String[] args) throws IOException, GoogleAuthenticationFailedException {
		ConstantOauthCalendarFactory calendarFactory = new ConstantOauthCalendarFactory() {
			public String getRefreshToken() {
				return "1/xINUaqv1xpf1BJ_IJx7USj8fCDkhfHXbj8lqC44rs5Q";
			}
		};
		calendarFactory.setAccessToken("ya29.AHES6ZQTz9xMqhlcMv0sNlhHlCRkp_Iqfha3a2GPUF97BudH");
		Calendar calendar = calendarFactory.createCalendar();
		String[] calendarIds = new String[] { "gvlfj3o1gt6qkbjh3re7059ovk@group.calendar.google.com",
				"cnitefp2m9epk0prt2fgr9tvro@group.calendar.google.com", "kb2nn05vhvmmt9rcdd8arkj0jg@group.calendar.google.com",
				"709a8qimgsjhj7aq87b4ofd3s0@group.calendar.google.com", "k7b6psuhbqueaaqtcfglvjk3gc@group.calendar.google.com",
				"sl4gtp1o2a8nf8jlcbo4n237k4@group.calendar.google.com", "gn6t63cv72p8od3dhdrvqkvpug@group.calendar.google.com",
				"pf4f1vat7io0iicsefduo02e7k@group.calendar.google.com", "tt7o01aoihprtiau5283cu8r60@group.calendar.google.com",
				"6mkgld1nke52okp5nlm8rgsm28@group.calendar.google.com", "rrud6a8jlpiqnt2dfcskbmvrqo@group.calendar.google.com" };
		for (String calendarId : calendarIds) {
			String pageToken = null;
			List<String> gameIds = Lists.newArrayList();
			int duplicateCount = 0;
			do {
				Events events = calendar.events().list(calendarId).setMaxResults(Integer.MAX_VALUE).setPageToken(pageToken)
						.execute();
				for (Event event : events.getItems()) {
					String summary = event.getSummary();
					String description = event.getDescription();
					System.out.println(summary + ": " + description);
					String gameId = event.getExtendedProperties().getShared().get("hammersId");
					if (gameIds.contains(gameId)) {
						System.out.println("Found duplicate. Deleting.");
						calendar.events().delete(calendarId, event.getId()).execute();
						duplicateCount++;
					}
					else {
						gameIds.add(gameId);
					}
				}
				pageToken = events.getNextPageToken();
			} while (pageToken != null);
			System.out.println("Found " + gameIds.size() + " unique games and " + duplicateCount + " duplicates.");
		}
	}
}
