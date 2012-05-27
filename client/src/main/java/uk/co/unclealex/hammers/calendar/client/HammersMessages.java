/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client;

import java.util.Date;

import com.google.gwt.i18n.client.Messages;

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
public interface HammersMessages extends Messages {

	@DefaultMessage("The {0}/{1} season")
	String season(int season, int nextYear);
	
	@DefaultMessage("{0,date,MMMM yyyy}")
	String month(Date date);
	
	@DefaultMessage("{0,date,EEE dd HH:mm}")
	String datePlayed(Date datePlayed);

	@DefaultMessage("{0,date,EEE dd MMM yyyy}")
	String fullDatePlayed(Date datePlayed);

	@DefaultMessage("Home")
	String home();
	@DefaultMessage("Away")
	String away();
	
	@DefaultMessage("{0,date,EEE dd MMM}")
	String ticketsAvailable(Date date);

	@DefaultMessage("Welcome, {0}")
	String loggedIn(String username);
	
	@DefaultMessage("You are not logged in.")
	String notLoggedIn();
	
	@DefaultMessage("Login")
	String login();
	
	@DefaultMessage("Logoff")
	String logoff();

	@DefaultMessage("Passwords cannot be empty")
  String passwordCannotBeEmpty();

	@DefaultMessage("Passwords match")
  String passwordsMatch();

	@DefaultMessage("Passwords do not match")
  String passwordsDontMatch();
	
	@DefaultMessage("New user")
	String newUser();
	
	@DefaultMessage("Update user")
	String updateUser();
}
